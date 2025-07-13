package omxium.ui;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebHistory;

import java.net.URI;

public class UrlBlocker {

    private final WebEngine engine;
    private boolean blocking = false;

    public UrlBlocker(WebEngine engine) {
        this.engine = engine;
        init();
    }

    private void init() {
        ChangeListener<String> listener = (obs, oldUrl, newUrl) -> {
            if (blocking || newUrl == null) return;

            if (isBlocked(newUrl)) {
                blocking = true;
                Platform.runLater(() -> {
                    engine.getLoadWorker().cancel();

                    WebHistory history = engine.getHistory();
                    int idx = history.getCurrentIndex();
                    String prevUrl = (idx > 0)
                            ? history.getEntries().get(idx - 1).getUrl()
                            : null;

                    if (prevUrl != null && !prevUrl.equals("about:blank")) {
                        history.go(-1);
                    }

                    blocking = false;
                });
            }
        };
        engine.locationProperty().addListener(listener);
    }

    private boolean isBlocked(String url) {
        try {
            URI uri = new URI(url);
            String scheme = uri.getScheme();
            String host   = uri.getHost();
            if (("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme))
                    && host != null
                    && !host.equals("localhost")
                    && !host.equals("127.0.0.1")
            ) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}