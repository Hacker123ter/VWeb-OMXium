package omxium.ui;

import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.scene.control.Alert;
import javafx.scene.web.WebEngine;

public class UrlBlocker {

    private final WebEngine engine;
    private final String allowedStartPage;

    public UrlBlocker(WebEngine engine, String allowedStartPage) {
        this.engine = engine;
        this.allowedStartPage = allowedStartPage;
        init();
    }

    private void init() {
        engine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SCHEDULED) {
                String url = engine.getLocation();

                if (isBlocked(url)) {
                    Platform.runLater(() -> {
                        showBlockedAlert(url);
                        engine.load(allowedStartPage);
                    });
                }
            }
        });
    }

    private boolean isBlocked(String url) {
        return url.startsWith("http://") || url.startsWith("https://");
    }

    private void showBlockedAlert(String url) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Загрузка заблокирована");
        alert.setHeaderText(null);
        alert.setContentText("Попытка загрузить внешний URL заблокирована:\n" + url);
        alert.showAndWait();
    }
}