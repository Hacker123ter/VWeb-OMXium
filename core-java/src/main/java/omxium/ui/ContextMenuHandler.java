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
import omxium.i18n.I18n;

public class ContextMenuHandler {

    private final ContextMenu contextMenu = new ContextMenu();

    public ContextMenuHandler(WebView webView) {
        Label copyLabel = new Label(I18n.t("context.copy"));
        copyLabel.setTextFill(Color.BLACK);

        Label shortcutLabel = new Label(I18n.t("context.copy.shortcut"));
        shortcutLabel.setTextFill(Color.GRAY);

        HBox copyBox = new HBox(10, copyLabel, shortcutLabel);
        copyBox.setStyle("-fx-padding: 5 10 5 10; -fx-cursor: hand;");

        CustomMenuItem copyItem = new CustomMenuItem(copyBox);
        copyItem.setHideOnClick(true);

        copyBox.setOnMouseClicked(e -> {
            Object result = webView.getEngine().executeScript("window.getSelection().toString()");
            if (result instanceof String text && !text.isEmpty()) {
                ClipboardContent content = new ClipboardContent();
                content.putString(text);
                Clipboard.getSystemClipboard().setContent(content);
            }
            contextMenu.hide();
        });

        Label reloadPageLabel = new Label(I18n.t("context.reload"));
        reloadPageLabel.setTextFill(Color.BLACK);

        Label reloadPageShortcut = new Label(I18n.t("context.reload.shortcut"));
        reloadPageShortcut.setTextFill(Color.GRAY);

        HBox reloadPageBox = new HBox(10, reloadPageLabel, reloadPageShortcut);
        reloadPageBox.setStyle("-fx-padding: 5 10 5 10; -fx-cursor: hand;");

        CustomMenuItem reloadPage = new CustomMenuItem(reloadPageBox);
        reloadPage.setHideOnClick(true);

        reloadPageBox.setOnMouseClicked(e -> {
            webView.getEngine().reload();
            contextMenu.hide();
        });

        contextMenu.getItems().addAll(copyItem, reloadPage);

        webView.setOnMousePressed(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                Object result = webView.getEngine().executeScript("window.getSelection().toString()");
                contextMenu.getItems().clear();

                if (result instanceof String text && !text.isEmpty()) {
                    contextMenu.getItems().addAll(copyItem, reloadPage);
                } else {
                    contextMenu.getItems().add(reloadPage);
                }

                contextMenu.show(webView, e.getScreenX(), e.getScreenY());
            } else {
                contextMenu.hide();
            }
        });
    }

    public void attachToScene(Scene scene) {
        scene.setOnMousePressed(e -> contextMenu.hide());
    }
}