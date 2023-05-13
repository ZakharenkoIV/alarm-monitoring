package ru.example.monitoring;

import javafx.application.Application;
import javafx.stage.Stage;
import ru.example.monitoring.handler.HandlerManager;
import ru.example.monitoring.loader.PageLoader;
import ru.example.monitoring.navigation.NavigationManager;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Alarm monitoring");
        primaryStage.setMinWidth(817);
        primaryStage.setMinHeight(639);

        HandlerManager handlerManager = new HandlerManager();
        PageLoader pageLoader = new PageLoader(handlerManager);
        NavigationManager navigation = new NavigationManager(pageLoader);
        navigation.loadStartScene("login");
        handlerManager.loadButtonHandlers(navigation);

        primaryStage.setScene(navigation.getScene());
        primaryStage.show();
        primaryStage.setOnCloseRequest(e -> System.exit(0));
    }
}