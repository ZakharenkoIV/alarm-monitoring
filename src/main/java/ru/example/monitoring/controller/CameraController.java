package ru.example.monitoring.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import ru.example.monitoring.camera.CameraManager;
import ru.example.monitoring.data.Capture;
import ru.example.monitoring.data.VBoxCaptureFactory;
import ru.example.monitoring.handler.HandlerManager;
import ru.example.monitoring.repository.mem.CaptureMem;

import java.util.Optional;

import static ru.example.monitoring.util.CaptureUtil.*;

public class CameraController {
    @FXML
    private BorderPane borderPaneControl;
    @FXML
    private Button lockButton;
    @FXML
    private VBox scrollVBoxControl;
    @FXML
    private Pane camera;
    @FXML
    private Label cameraPageName;

    private final VBoxCaptureFactory vBoxCaptureFactory;
    private final HandlerManager handlerManager;

    public CameraController(HandlerManager handlerManager, CameraManager cameraManager) {
        this.handlerManager = handlerManager;
        this.vBoxCaptureFactory = new VBoxCaptureFactory(this, cameraManager.getCameraPageCount());
    }

    @FXML
    public void initialize() {
        ImageView imageView = new ImageView();
        imageView.fitWidthProperty().bind(camera.widthProperty());
        imageView.fitHeightProperty().bind(camera.heightProperty());
        camera.getChildren().add(imageView);
    }

    @FXML
    public void handleCaptureButtonAction(ActionEvent buttonEvent) {
        Button clickedButton = (Button) buttonEvent.getSource();
        String captureId = clickedButton.getId().substring(14);
        Capture capture = CaptureMem.getCapture(captureId, cameraPageName.getText());
        handlerManager.getCaptureButtonHandler().handle(capture);
        addRedrawRectangleListenerForChangeSizeWindow();
    }

    @FXML
    public void handleAddNewCaptureButtonAction(ActionEvent buttonEvent) {
        Button clickedButton = (Button) buttonEvent.getSource();
        VBox newCaptureControlBox = vBoxCaptureFactory.createNewControlBox(clickedButton);
        scrollVBoxControl.getChildren().add(newCaptureControlBox);
        String captureId = newCaptureControlBox.getId().substring(12);
        Optional<Capture> optionalCapture = Optional.ofNullable(CaptureMem.getCapture(captureId, cameraPageName.getText()));
        if (optionalCapture.isEmpty()) {
            Capture capture = new Capture(captureId, clickedButton);
            double[] pictureSize = calculateProportionalSize(camera.getWidth(), camera.getHeight());
            capture.setPictureWidth(pictureSize[0]);
            capture.setPictureHeight(pictureSize[1]);
            CaptureMem.putCapture(capture, cameraPageName.getText());
        }
    }

    @FXML
    public void handleLockButtonAction() {
        handlerManager.getLockButtonHandler().handle(lockButton, borderPaneControl);
    }

    @FXML
    public void handleHomeButtonAction() {
        handlerManager.getHomeButtonHandler().handle();
    }

    @FXML
    public void handleForwardButtonAction() {
        handlerManager.getForwardButtonHandler().handle(cameraPageName.getText());
    }

    @FXML
    public void handleBackButtonAction() {
        handlerManager.getBackButtonHandler().handle(cameraPageName.getText());
    }

    private void addRedrawRectangleListenerForChangeSizeWindow() {
        camera.widthProperty().addListener(
                (observable, oldValue, newValue) -> drawRectanglesWithNewWidth(
                        cameraPageName.getText(), oldValue.intValue(), newValue.intValue(), camera));

        camera.heightProperty().addListener(
                (observable, oldValue, newValue) -> drawRectanglesWithNewHeight(
                        cameraPageName.getText(), oldValue.intValue(), newValue.intValue(), camera));
    }

    public String getCameraPageName() {
        return cameraPageName.getText();
    }
}