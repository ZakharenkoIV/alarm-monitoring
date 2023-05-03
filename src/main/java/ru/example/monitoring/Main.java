package ru.example.monitoring;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        primaryStage.setTitle("Alarm monitoring");
        primaryStage.setMinWidth(817);
        primaryStage.setMinHeight(639);

        FXMLLoader loader = new FXMLLoader();
        URL xmlUrl = getClass().getResource("/fxml/camera.fxml");
        loader.setLocation(xmlUrl);
        Parent root = loader.load();

        // Отображаем сцену
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setOnCloseRequest(e -> System.exit(0));
    }
}