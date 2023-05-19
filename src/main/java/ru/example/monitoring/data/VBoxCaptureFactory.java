package ru.example.monitoring.data;

import javafx.beans.InvalidationListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import ru.example.monitoring.controller.CameraController;
import ru.example.monitoring.repository.mem.CaptureMem;

import static ru.example.monitoring.util.CaptureUtil.drawRectangle;

public class VBoxCaptureFactory {

    private int controlCount = 0;
    private final int cameraPageCount;

    private final CameraController cameraController;

    public VBoxCaptureFactory(CameraController cameraController, int cameraPageCount) {
        this.cameraController = cameraController;
        this.cameraPageCount = cameraPageCount;
    }

    public VBox createNewControlBox(Button clickedButton) {
        controlCount++;
        String captureId = cameraPageCount + "|" + controlCount;

        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        int maxWidth = (int) bounds.getWidth();
        int maxHeight = (int) bounds.getHeight();

        TextField x1TextField = new TextField();
        x1TextField.setPromptText("X1");
        x1TextField.setAlignment(Pos.CENTER);
        x1TextField.setId(captureId + "|textField|X1");
        x1TextField.setTextFormatter(getLimitToFourDigitsFormatter(maxWidth));

        TextField y1TextField = new TextField();
        y1TextField.setPromptText("Y1");
        y1TextField.setAlignment(Pos.CENTER);
        y1TextField.setId(captureId + "|textField|Y1");
        y1TextField.setTextFormatter(getLimitToFourDigitsFormatter(maxHeight));

        TextField x2TextField = new TextField();
        x2TextField.setPromptText("X2");
        x2TextField.setAlignment(Pos.CENTER);
        x2TextField.setId(captureId + "|textField|X2");
        x2TextField.setTextFormatter(getLimitToFourDigitsFormatter(maxWidth));

        TextField y2TextField = new TextField();
        y2TextField.setPromptText("Y2");
        y2TextField.setAlignment(Pos.CENTER);
        y2TextField.setId(captureId + "|textField|Y2");
        y2TextField.setTextFormatter(getLimitToFourDigitsFormatter(maxHeight));

        Pane camera = (Pane) clickedButton.getScene().lookup("#camera");
        x1TextField.focusedProperty().addListener(getChangeTextFieldListener(captureId, x1TextField, y1TextField, x2TextField, y2TextField, camera));
        y1TextField.focusedProperty().addListener(getChangeTextFieldListener(captureId, x1TextField, y1TextField, x2TextField, y2TextField, camera));
        x2TextField.focusedProperty().addListener(getChangeTextFieldListener(captureId, x1TextField, y1TextField, x2TextField, y2TextField, camera));
        y2TextField.focusedProperty().addListener(getChangeTextFieldListener(captureId, x1TextField, y1TextField, x2TextField, y2TextField, camera));

        HBox coordinatesBox = new HBox();
        coordinatesBox.getChildren().addAll(x1TextField, y1TextField, x2TextField, y2TextField);

        Button captureButton = new Button("Захватить");
        captureButton.setId("captureButton-" + captureId);
        captureButton.setPrefWidth(149);
        captureButton.setOnAction(cameraController::handleCaptureButtonAction);

        TextField sensorTextField = new TextField();
        sensorTextField.setPromptText("Датчик");
        sensorTextField.setPrefWidth(149);
        sensorTextField.setAlignment(Pos.CENTER);
        sensorTextField.setId(captureId + "|textField|sensor");

        HBox associationBox = new HBox();
        associationBox.getChildren().addAll(captureButton, sensorTextField);

        VBox controlBox = new VBox();
        controlBox.getChildren().addAll(coordinatesBox, associationBox);
        controlBox.setPadding(new Insets(15, 5, 15, 5));
        controlBox.setAlignment(Pos.CENTER);
        controlBox.setStyle("-fx-border-color: black; -fx-border-width: 1px;");
        controlBox.setId("vBoxControl-" + captureId);
        return controlBox;
    }

    public TextFormatter<String> getLimitToFourDigitsFormatter(int maxValue) {
        return new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            int maxValueLength = String.valueOf(maxValue).length();
            if (newText.matches(String.format("\\d{0,%d}", maxValueLength)) && (newText.isEmpty() || Integer.parseInt(newText) <= maxValue)) {
                return change;
            }
            return null;
        });
    }

    private InvalidationListener getChangeTextFieldListener(String captureId,
                                                            TextField x1TextField,
                                                            TextField y1TextField,
                                                            TextField x2TextField,
                                                            TextField y2TextField,
                                                            Pane camera) {
        return observable -> {
            if (!x1TextField.getText().equals("")
                    && !y1TextField.getText().equals("")
                    && !x2TextField.getText().equals("")
                    && !y2TextField.getText().equals("")) {
                Capture capture = CaptureMem.getCapture(captureId, cameraController.getCameraPageName());
                drawRectangle(capture, camera);
            }
        };
    }
}
