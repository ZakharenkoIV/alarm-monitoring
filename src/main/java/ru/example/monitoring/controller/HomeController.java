package ru.example.monitoring.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ru.example.monitoring.camera.CameraManager;
import ru.example.monitoring.handler.HandlerManager;

import java.util.List;

public class HomeController {

    @FXML
    VBox cameraVBox;

    private final HandlerManager handlerManager;
    private final CameraManager cameraManager;

    public HomeController(HandlerManager handlerManager, CameraManager cameraManager) {
        this.handlerManager = handlerManager;
        this.cameraManager = cameraManager;
    }

    @FXML
    public void handleSettingButtonAction() {
        handlerManager.getSettingButtonHandler().handle();
    }

    @FXML
    public void handleCameraButtonAction() {
        handlerManager.getCameraButtonHandler().handle();
    }

    @FXML
    public void handleAddCameraHBoxButtonAction() {
        cameraVBox.getChildren().add(createCameraHBox());
    }

    @FXML
    public void handleStartButtonAction() {
    }

    private HBox createCameraHBox() {
        HBox cameraBox = new HBox();
        cameraBox.setAlignment(Pos.CENTER);
        cameraBox.setPrefSize(778, 75);
        cameraBox.setSpacing(30);
        cameraBox.setPadding(new Insets(30, 10, 0, 10));

        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setPrefWidth(500);
        List<String> cameras = cameraManager.getCamerasNames();
        comboBox.getItems().addAll(FXCollections.observableArrayList(cameras));
        HBox.setMargin(comboBox, new Insets(0, 40, 0, 0));
        comboBox.setOnAction(event -> cameraManager.addCameraPage(comboBox.getValue()));

        Button loadButton = new Button("Загрузить");
        loadButton.setAlignment(Pos.CENTER);
        loadButton.setPrefWidth(75);

        Button saveButton = new Button("Сохранить");
        saveButton.setAlignment(Pos.CENTER);
        saveButton.setPrefWidth(75);

        HBox buttonBox = new HBox();
        buttonBox.setSpacing(15);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(loadButton, saveButton);

        cameraBox.getChildren().addAll(comboBox, buttonBox);
        cameraManager.addCameraComboBox(comboBox);
        return cameraBox;
    }
}
