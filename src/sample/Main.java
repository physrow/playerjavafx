package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.media.AudioSpectrumListener;
import javafx.scene.media.MediaPlayer;
import javafx.scene.web.WebView;
import javafx.stage.Stage;


public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Player");
        primaryStage.setScene(new Scene(root, 1400, 900));
        primaryStage.show();
//        WebView webview = new WebView();
//        webview.getEngine().load(
//                "https://www.youtube.com/"
//        );
//        webview.setPrefSize(640, 390);
//        primaryStage.setScene(new Scene(webview));
//        primaryStage.show();

    }

    public static class VideoPlayer extends Application {
        public static void main(String[] args) { launch(args); }
        @Override public void start(Stage stage) throws Exception {
            WebView webview = new WebView();
            webview.getEngine().load(
                    "https://www.youtube.com/"
            );
            webview.setPrefSize(640, 390);
            stage.setScene(new Scene(webview));
            stage.show();
        }


    }

}