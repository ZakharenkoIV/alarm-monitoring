package ru.example.monitoring;

import javafx.application.Application;
import javafx.stage.Stage;
import org.opencv.core.Core;
import ru.example.monitoring.camera.CameraManager;
import ru.example.monitoring.handler.HandlerManager;
import ru.example.monitoring.loader.PageLoader;
import ru.example.monitoring.navigation.NavigationManager;
import ru.example.monitoring.visualprocessing.CameraImageHandler;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    @Override
    public void start(Stage primaryStage) {

        primaryStage.setTitle("Alarm monitoring");
        primaryStage.setMinWidth(817);
        primaryStage.setMinHeight(639);

        HandlerManager handlerManager = new HandlerManager();
        CameraImageHandler cameraImageHandler = new CameraImageHandler();
        CameraManager cameraManager = new CameraManager(cameraImageHandler);
        PageLoader pageLoader = new PageLoader(handlerManager, cameraManager);
        NavigationManager navigation = new NavigationManager(pageLoader);
        navigation.loadStartScene("login");
        cameraManager.setNavigation(navigation);
        handlerManager.loadButtonHandlers(navigation);

        primaryStage.setScene(navigation.getScene());
        primaryStage.show();
        primaryStage.setOnCloseRequest(e -> System.exit(0));
    }
}