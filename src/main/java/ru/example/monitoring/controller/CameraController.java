package ru.example.monitoring.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import ru.example.monitoring.data.Capture;
import ru.example.monitoring.data.VBoxCaptureFactory;
import ru.example.monitoring.handler.HandlerManager;
import ru.example.monitoring.handler.button.CaptureButtonHandler;
import ru.example.monitoring.handler.button.LockButtonHandler;
import ru.example.monitoring.repository.mem.CaptureMem;

import java.util.Optional;

public class CameraController {
    @FXML
    private BorderPane borderPaneControl;
    @FXML
    private Button lockButton;
    @FXML
    private VBox scrollVBoxControl;

    private final VBoxCaptureFactory vBoxCaptureFactory;
    private final HandlerManager handlerManager;

    public CameraController(HandlerManager handlerManager) {
        this.handlerManager = handlerManager;
        this.vBoxCaptureFactory = new VBoxCaptureFactory(this);
    }

    @FXML
    public void handleCaptureButtonAction(ActionEvent buttonEvent) {
        Button clickedButton = (Button) buttonEvent.getSource();
        String captureId = clickedButton.getId().substring(14);
        Capture capture = CaptureMem.getCapture(captureId);
        new CaptureButtonHandler().handle(capture);
    }

    @FXML
    public void handleAddNewCaptureButtonAction(ActionEvent buttonEvent) {
        Button clickedButton = (Button) buttonEvent.getSource();
        VBox newCaptureControlBox = vBoxCaptureFactory.createNewControlBox(clickedButton);
        scrollVBoxControl.getChildren().add(newCaptureControlBox);
        String captureId = newCaptureControlBox.getId().substring(12);
        Optional<Capture> optionalCapture = Optional.ofNullable(CaptureMem.getCapture(captureId));
        if (optionalCapture.isEmpty()) {
            CaptureMem.putCapture(new Capture(captureId, clickedButton));
        }
    }

    @FXML
    public void handleLockButtonAction() {
        new LockButtonHandler().handle(lockButton, borderPaneControl);
    }

    @FXML
    public void handleHomeButtonAction() {
        handlerManager.getHomeButton().handle();
    }
}