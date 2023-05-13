package ru.example.monitoring.controller;

import javafx.fxml.FXML;
import ru.example.monitoring.handler.HandlerManager;

public class HomeController {

    private final HandlerManager handlerManager;

    public HomeController(HandlerManager handlerManager) {
        this.handlerManager = handlerManager;
    }

    @FXML
    public void handleSettingButtonAction() {
        handlerManager.getSettingButton().handle();
    }

    @FXML
    public void handleCameraButtonAction() {
        handlerManager.getCameraButton().handle();
    }
}
