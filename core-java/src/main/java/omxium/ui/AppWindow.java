package omxium.ui;

import javafx.concurrent.Worker;
import javafx.scene.image.Image;
import omxium.rpc.DiscordRPCModule;
import omxium.server.LocalServer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.apache.commons.codec.digest.HmacUtils;

import java.util.Objects;
import java.util.UUID;

public class AppWindow extends Application {

    private LocalServer server;
    private static final String SECRET_KEY = UUID.randomUUID().toString();
    public static volatile String AUTH_TOKEN;
    private Thread tokenRefreshThread;

    @Override
    public void start(Stage primaryStage) throws Exception {
        regenerateToken();

        server = new LocalServer(1739);
        DiscordRPCModule.start();

        WebView webView = new WebView();
        webView.setContextMenuEnabled(false);
        new UrlBlocker(webView.getEngine());

        webView.getEngine().locationProperty().addListener((obs, o, n) -> {
            if (n != null && n.startsWith("gmp://")) {
                String tail = n.substring("gmp://".length());
                Platform.runLater(() -> webView.getEngine().load("http://" + tail));
            }
        });

        webView.getEngine().setUserAgent("OmxiumWebView/1.0");
        webView.getEngine().getLoadWorker().stateProperty().addListener((obs, o, state) -> {
            if (state == Worker.State.SUCCEEDED) {
                String script = """
                    window.__omxiumToken = '%s';
                    (function(){
                        const origFetch = window.fetch;
                        window.fetch = function(resource, init = {}) {
                            init.headers = Object.assign({}, init.headers || {}, {
                                'X-Omxium-Token': window.__omxiumToken
                            });
                            return origFetch(resource, init);
                        };
                        const origOpen = XMLHttpRequest.prototype.open;
                        XMLHttpRequest.prototype.open = function(method, url) {
                            this.setRequestHeader('X-Omxium-Token', window.__omxiumToken);
                            origOpen.apply(this, arguments);
                        };
                    })();
                """.formatted(AUTH_TOKEN);
                webView.getEngine().executeScript(script);
            }
        });

        webView.getEngine().load(
                String.format("gmp://localhost:1739/start.html?token=%s", AUTH_TOKEN)
        );

        ContextMenuHandler contextHandler = new ContextMenuHandler(webView);

        BorderPane root = new BorderPane(webView);
        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("VWeb Omxium");

        primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icon.png"))));

        primaryStage.setMaximized(true);

        primaryStage.setOnCloseRequest(evt -> {
            if (server != null) {
                server.stop();
            }
            if (tokenRefreshThread != null) {
                tokenRefreshThread.interrupt();
            }
            Platform.exit();
        });

        primaryStage.show();

        tokenRefreshThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(5 * 60 * 1000);
                } catch (InterruptedException e) {
                    break;
                }
                regenerateToken();
                Platform.runLater(() -> {
                    String js = "window.__omxiumToken = '" + AUTH_TOKEN + "';";
                    webView.getEngine().executeScript(js);
                });
            }
        });
        tokenRefreshThread.setDaemon(true);
        tokenRefreshThread.start();

    }

    @Override
    public void stop() throws Exception {
        DiscordRPCModule.stop();
        super.stop();
        if (server != null) {
            server.stop();
        }
        if (tokenRefreshThread != null) {
            tokenRefreshThread.interrupt();
        }
    }

    private static void regenerateToken() {
        String raw = UUID.randomUUID().toString();
        AUTH_TOKEN = HmacUtils.hmacSha256Hex(SECRET_KEY, raw);
    }
}