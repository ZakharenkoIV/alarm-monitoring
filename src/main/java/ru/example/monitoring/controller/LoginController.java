package ru.example.monitoring.controller;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import ru.example.monitoring.handler.HandlerManager;

public class LoginController {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;

    private final HandlerManager handlerManager;

    public LoginController(HandlerManager handlerManager) {
        this.handlerManager = handlerManager;
    }

    @FXML
    public void handleLoginButtonAction() {
        if (usernameField.getText().equals("q") && passwordField.getText().equals("q")) {
            handlerManager.getHomeButton().handle();
        }
    }
}
