package omxium.ui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import omxium.i18n.I18n;

public class TopBar extends HBox {

    private final Label timerLabel;

    public TopBar() {
        setPrefHeight(32);
        setStyle("""
            -fx-background-color: white;
            -fx-border-color: transparent transparent rgba(0, 0, 0, 0.1) transparent;
            -fx-border-width: 0 0 1px 0;
        """);

        Label titleLabel = new Label(I18n.t("topbar.title"));
        titleLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: black; -fx-font-smoothing-type: lcd;");

        timerLabel = new Label("0:00");
        timerLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: black; -fx-font-smoothing-type: lcd;");

        VBox timerBox = new VBox(titleLabel, timerLabel);
        timerBox.setAlignment(Pos.CENTER);
        timerBox.setPadding(new Insets(2, 10, 2, 10));

        timerBox.setStyle("""
            -fx-background-color: white;
            -fx-padding: 6 12 6 12;
        """);

        setAlignment(Pos.CENTER_RIGHT);
        getChildren().add(timerBox);
    }

    public void updateTimerActivity(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        String timeStr = String.format("%d:%02d", minutes, seconds);

        Platform.runLater(() -> timerLabel.setText(timeStr));
    }
}