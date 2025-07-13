package omxium.ui;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebHistory;

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
        return url.startsWith("http://") || url.startsWith("https://");
    }
}