package omxium.ui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class TopBar extends HBox {

    private final Label timerLabel;

    public TopBar() {
        setPrefHeight(32);
        setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #e0e0e0;" +
                        "-fx-border-width: 1 0 0 0;"
        );

        Label titleLabel = new Label("Time activity");
        titleLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: gray;");

        timerLabel = new Label("0:00");
        timerLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: gray;");

        VBox timerBox = new VBox(titleLabel, timerLabel);
        timerBox.setAlignment(Pos.CENTER);
        timerBox.setPadding(new Insets(2, 10, 2, 10));

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