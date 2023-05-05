package ru.example.monitoring.controller;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class ButtonCaptureController {

    @FXML
    private TextField x1TextField;
    @FXML
    private TextField y1TextField;
    @FXML
    private TextField x2TextField;
    @FXML
    private TextField y2TextField;

    @FXML
    private Pane camera;

    private EventHandler<MouseEvent> mouseReleasedHandler;
    private EventHandler<MouseEvent> mouseDraggedHandler;
    private EventHandler<MouseEvent> mouseMovedHandler;
    Boolean existMouseMovedHandler = false;
    private Rectangle rectangle;

    @FXML
    public void handleCaptureButtonAction() {
        if (!existMouseMovedHandler) {
            mouseMovedHandler = event -> {
                savePoint(event, x1TextField, y1TextField);
            };
            mouseDraggedHandler = event -> {
                camera.removeEventHandler(MouseEvent.MOUSE_MOVED, mouseMovedHandler);
                existMouseMovedHandler = false;
                camera.addEventHandler(MouseEvent.MOUSE_RELEASED, mouseReleasedHandler);
                savePoint(event, x2TextField, y2TextField);
                drawRectangle();
            };
            if (mouseReleasedHandler == null) {
                mouseReleasedHandler = event -> {
                    camera.removeEventHandler(MouseEvent.MOUSE_DRAGGED, mouseDraggedHandler);
                };
            }
            camera.addEventHandler(MouseEvent.MOUSE_MOVED, mouseMovedHandler);
            existMouseMovedHandler = true;
            camera.addEventHandler(MouseEvent.MOUSE_DRAGGED, mouseDraggedHandler);
        }
    }

    private void savePoint(MouseEvent event, TextField xTextField, TextField yTextField) {
        int x = Math.min((int) event.getX(), ((int) camera.getWidth()));
        x = Math.max(x, 0);
        int y = Math.min((int) event.getY(), ((int) camera.getHeight()));
        y = Math.max(y, 0);
        String axisX = String.valueOf(x);
        String axisY = String.valueOf(y);
        xTextField.setText(axisX);
        yTextField.setText(axisY);
    }

    private void drawRectangle() {
        if (rectangle != null) {
            camera.getChildren().remove(rectangle);
        }
        double x1 = Double.parseDouble(x1TextField.getText());
        double y1 = Double.parseDouble(y1TextField.getText());
        double x2 = Double.parseDouble(x2TextField.getText());
        double y2 = Double.parseDouble(y2TextField.getText());
        rectangle = new Rectangle(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x2 - x1), Math.abs(y2 - y1));
        rectangle.setFill(null);
        rectangle.setStroke(Color.RED);
        rectangle.setStrokeWidth(2);
        camera.getChildren().add(rectangle);
    }
}