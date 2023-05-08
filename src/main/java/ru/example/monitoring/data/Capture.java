package ru.example.monitoring.data;

import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

public class Capture {

    private final String captureId;

    private final TextField x1TextField;
    private final TextField y1TextField;
    private final TextField x2TextField;
    private final TextField y2TextField;

    private EventHandler<MouseEvent> mouseReleasedHandler;
    private EventHandler<MouseEvent> mouseDraggedHandler;
    private EventHandler<MouseEvent> mouseMovedHandler;

    private Rectangle rectangle;

    public Capture(String captureId, Button clickedButton) {
        this.captureId = captureId;
        VBox vBoxControl = (VBox) clickedButton.getParent().getParent().lookup("#vBoxControl-" + captureId);
        this.x1TextField = (TextField) vBoxControl.lookup("#" + captureId + "|textField|X1");
        this.y1TextField = (TextField) vBoxControl.lookup("#" + captureId + "|textField|Y1");
        this.x2TextField = (TextField) vBoxControl.lookup("#" + captureId + "|textField|X2");
        this.y2TextField = (TextField) vBoxControl.lookup("#" + captureId + "|textField|Y2");
    }

    public String getCaptureId() {
        return captureId;
    }

    public TextField getX1TextField() {
        return x1TextField;
    }

    public TextField getY1TextField() {
        return y1TextField;
    }

    public TextField getX2TextField() {
        return x2TextField;
    }

    public TextField getY2TextField() {
        return y2TextField;
    }

    public EventHandler<MouseEvent> getMouseReleasedHandler() {
        return mouseReleasedHandler;
    }

    public void setMouseReleasedHandler(EventHandler<MouseEvent> mouseReleasedHandler) {
        this.mouseReleasedHandler = mouseReleasedHandler;
    }

    public EventHandler<MouseEvent> getMouseDraggedHandler() {
        return mouseDraggedHandler;
    }

    public void setMouseDraggedHandler(EventHandler<MouseEvent> mouseDraggedHandler) {
        this.mouseDraggedHandler = mouseDraggedHandler;
    }

    public EventHandler<MouseEvent> getMouseMovedHandler() {
        return mouseMovedHandler;
    }

    public void setMouseMovedHandler(EventHandler<MouseEvent> mouseMovedHandler) {
        this.mouseMovedHandler = mouseMovedHandler;
    }

    public Rectangle getRectangle() {
        return rectangle;
    }

    public void setRectangle(Rectangle rectangle) {
        this.rectangle = rectangle;
    }
}
