package ru.example.monitoring.loader;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import ru.example.monitoring.camera.CameraManager;
import ru.example.monitoring.controller.CameraController;
import ru.example.monitoring.controller.HomeController;
import ru.example.monitoring.controller.LoginController;
import ru.example.monitoring.controller.SettingController;
import ru.example.monitoring.handler.HandlerManager;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PageLoader {
    private static final Logger logger = Logger.getLogger(PageLoader.class.getName());
    private final static String PAGE_URL_TEMPLATE = "/fxml/%s.fxml";

    private final HandlerManager handlerManager;
    private final CameraManager cameraManager;

    public PageLoader(HandlerManager handlerManager, CameraManager cameraManager) {
        this.handlerManager = handlerManager;
        this.cameraManager = cameraManager;
    }

    public Parent loadPage(String pageName) {
        if (pageName == null || pageName.isEmpty()) {
            logger.warning("Имя страницы не может быть пустым.");
            return null;
        }
        String pageUrl = String.format(PAGE_URL_TEMPLATE, pageName);
        FXMLLoader loader = new FXMLLoader(getClass().getResource(pageUrl));
        switch (pageName) {
            case "login" -> loader.setControllerFactory(c -> new LoginController(handlerManager));
            case "home" -> loader.setControllerFactory(c -> new HomeController(handlerManager, cameraManager));
            case "setting" -> loader.setControllerFactory(c -> new SettingController(handlerManager));
            case "camera" -> loader.setControllerFactory(c -> new CameraController(handlerManager, cameraManager));
            default -> {
                logger.warning("Неизвестное имя страницы: " + pageName);
                return null;
            }
        }
        try {
            return loader.load();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Произошла ошибка при создании страницы \"" + pageName + "\".", e);
            return null;
        }
    }
}