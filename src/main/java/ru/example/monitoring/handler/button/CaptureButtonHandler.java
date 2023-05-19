package ru.example.monitoring.handler.button;

import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import ru.example.monitoring.data.Capture;

import static ru.example.monitoring.util.CaptureUtil.drawRectangle;

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
            drawRectangle(capture, camera);
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
}