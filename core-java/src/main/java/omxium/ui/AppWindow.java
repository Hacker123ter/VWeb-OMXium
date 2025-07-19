package omxium.ui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import omxium.rpc.DiscordRPCModule;
import omxium.runtime.UrlBlocker;
import omxium.server.LocalServer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.util.Objects;

public class AppWindow extends Application {

    private LocalServer server;
    private TopBar topBar;

    private int secondsElapsed = 0;

    @Override
    public void start(Stage primaryStage) throws Exception {
        topBar = new TopBar();

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

        webView.getEngine().load("gmp://localhost:1739/start.html");

        new ContextMenuHandler(webView);

        TopBar topBar = new TopBar();

        VBox root = new VBox(topBar, webView);
        root.setSpacing(0);
        root.setFillWidth(true);
        VBox.setVgrow(webView, javafx.scene.layout.Priority.ALWAYS);

        root.setStyle("-fx-background-color: white;");
        webView.setStyle("-fx-background-color: white;");

        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("VWeb Omxium");

        primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icon.png"))));

        primaryStage.setMaximized(true);

        primaryStage.setOnCloseRequest(evt -> {
            if (server != null) {
                server.stop();
            }
            Platform.exit();
        });

        primaryStage.show();

        startTimerActivity();

    }

    private void startTimerActivity() {
        Timeline timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            secondsElapsed++;
            topBar.updateTimerActivity(secondsElapsed);
        }));
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
    }

    @Override
    public void stop() throws Exception {
        DiscordRPCModule.stop();
        super.stop();
        if (server != null) {
            server.stop();
        }
    }
}