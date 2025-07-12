package omxium.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.util.Objects;

public class AppWindow extends Application {

    @Override
    public void start(Stage primaryStage) {
        String startPage = Objects.requireNonNull(getClass().getResource("html/start.html")).toExternalForm();
        BorderPane root = new BorderPane();
        WebView webView = new WebView();

        new UrlBlocker(webView.getEngine(), startPage);

        webView.setContextMenuEnabled(false);
        webView.getEngine().load(startPage);

        root.setCenter(webView);

        Scene scene = new Scene(root);

        primaryStage.setTitle("VWeb Omxium");
        primaryStage.setScene(scene);

        primaryStage.setMaximized(true);

        primaryStage.show();
    }
}