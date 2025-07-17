package omxium.ui;

import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.web.WebView;

public class ContextMenuHandler {

    private final ContextMenu contextMenu = new ContextMenu();

    public ContextMenuHandler(WebView webView) {
        Label copyLabel = new Label("Скопировать");
        copyLabel.setTextFill(Color.BLACK);

        Label shortcutLabel = new Label("Ctrl + C");
        shortcutLabel.setTextFill(Color.GRAY);

        HBox itemBox = new HBox(10, copyLabel, shortcutLabel);
        itemBox.setStyle("-fx-padding: 5 10 5 10; -fx-cursor: hand;");

        CustomMenuItem item = new CustomMenuItem(itemBox);
        item.setHideOnClick(true);

        itemBox.setOnMouseClicked(e -> {
            Object result = webView.getEngine().executeScript("window.getSelection().toString()");
            if (result instanceof String text && !text.isEmpty()) {
                ClipboardContent content = new ClipboardContent();
                content.putString(text);
                Clipboard.getSystemClipboard().setContent(content);
            }
            contextMenu.hide();
        });

        contextMenu.getItems().add(item);

        webView.setOnMousePressed(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                Object result = webView.getEngine().executeScript("window.getSelection().toString()");
                if (result instanceof String text && !text.isEmpty()) {
                    contextMenu.show(webView, e.getScreenX(), e.getScreenY());
                } else {
                    contextMenu.hide();
                }
            } else {
                contextMenu.hide();
            }
        });
    }

    public void attachToScene(Scene scene) {
        scene.setOnMousePressed(e -> contextMenu.hide());
    }
}