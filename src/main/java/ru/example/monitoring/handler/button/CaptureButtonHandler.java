package ru.example.monitoring.handler.button;

import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import ru.example.monitoring.data.Capture;

public class CaptureButtonHandler {

    private static Pane camera;

    public CaptureButtonHandler() {
    }

    public void handle(Capture capture) {
        camera = (Pane) capture.getX1TextField().getScene().lookup("#camera");
        deleteAllCameraCaptureHandlers(capture);
        loadCaptureHandlers(capture);
        startCaptureOnCamera(capture);
    }

    private void startCaptureOnCamera(Capture handlersSource) {
        camera.addEventHandler(MouseEvent.MOUSE_MOVED, handlersSource.getMouseMovedHandler());
        camera.addEventHandler(MouseEvent.MOUSE_DRAGGED, handlersSource.getMouseDraggedHandler());
    }

    private void loadCaptureHandlers(Capture capture) {
        EventHandler<MouseEvent> mouseMovedHandler = getMouseMovedHandler(capture);
        capture.setMouseMovedHandler(mouseMovedHandler);
        EventHandler<MouseEvent> mouseDraggedHandler = getMouseDraggedHandler(capture);
        capture.setMouseDraggedHandler(mouseDraggedHandler);
        EventHandler<MouseEvent> mouseReleasedHandler = getMouseReleasedHandler(capture);
        capture.setMouseReleasedHandler(mouseReleasedHandler);
    }

    private EventHandler<MouseEvent> getMouseMovedHandler(Capture capture) {
        return event -> {
            savePoint((int) event.getX(), (int) camera.getWidth(), capture.getX1TextField());
            savePoint((int) event.getY(), (int) camera.getHeight(), capture.getY1TextField());
        };
    }

    private EventHandler<MouseEvent> getMouseDraggedHandler(Capture capture) {
        return event -> {
            camera.removeEventHandler(MouseEvent.MOUSE_MOVED, capture.getMouseMovedHandler());
            camera.addEventHandler(MouseEvent.MOUSE_RELEASED, capture.getMouseReleasedHandler());
            savePoint((int) event.getX(), (int) camera.getWidth(), capture.getX2TextField());
            savePoint((int) event.getY(), (int) camera.getHeight(), capture.getY2TextField());
            drawRectangle(capture);
        };
    }

    private EventHandler<MouseEvent> getMouseReleasedHandler(Capture capture) {
        return event -> {
            camera.removeEventHandler(MouseEvent.MOUSE_DRAGGED, capture.getMouseDraggedHandler());
            camera.removeEventHandler(MouseEvent.MOUSE_RELEASED, capture.getMouseReleasedHandler());
        };
    }

    private void deleteAllCameraCaptureHandlers(Capture handlersSource) {
        if (handlersSource.getMouseMovedHandler() != null) {
            camera.removeEventHandler(MouseEvent.MOUSE_MOVED, handlersSource.getMouseMovedHandler());
        }
        if (handlersSource.getMouseDraggedHandler() != null) {
            camera.removeEventHandler(MouseEvent.MOUSE_DRAGGED, handlersSource.getMouseDraggedHandler());
        }
        if (handlersSource.getMouseDraggedHandler() != null) {
            camera.removeEventHandler(MouseEvent.MOUSE_RELEASED, handlersSource.getMouseReleasedHandler());
        }
    }

    private void savePoint(int axis, int maxValue, TextField xTextField) {
        int value = Math.min(axis, maxValue);
        value = Math.max(value, 0);
        xTextField.setText(String.valueOf(value));
    }

    public static void drawRectangle(Capture capture) {
        camera.getChildren().remove(capture.getRectangle());
        createRectangle(capture);
    }

    private static void createRectangle(Capture capture) {
        String stringX1 = capture.getX1TextField().getText();
        String stringY1 = capture.getY1TextField().getText();
        String stringX2 = capture.getX2TextField().getText();
        String stringY2 = capture.getY2TextField().getText();
        if (!stringX1.equals("") && !stringY1.equals("") && !stringX2.equals("") && !stringY2.equals("")) {
            int x1 = Integer.parseInt(stringX1);
            int y1 = Integer.parseInt(stringY1);
            int x2 = Integer.parseInt(stringX2);
            int y2 = Integer.parseInt(stringY2);
            Rectangle rectangle = new Rectangle(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x2 - x1), Math.abs(y2 - y1));
            rectangle.setFill(null);
            rectangle.setStroke(Color.RED);
            rectangle.setStrokeWidth(2);
            capture.setRectangle(rectangle);
            camera.getChildren().add(rectangle);
        }
    }
}