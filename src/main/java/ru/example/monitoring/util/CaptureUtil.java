package ru.example.monitoring.util;

import javafx.geometry.Bounds;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import ru.example.monitoring.data.Capture;
import ru.example.monitoring.repository.mem.CaptureMem;

import java.util.List;

public class CaptureUtil {

    public static void drawRectangle(Capture capture, Pane camera) {
        camera.getChildren().remove(capture.getRectangle());
        createRectangle(capture, camera);
    }

    public static void drawRectanglesWithNewWidth(String cameraPageName, Bounds bounds, Pane camera, Image image) {
        double[] newPictureSize = calculateProportionalSize(bounds.getWidth(), bounds.getHeight(), image);
        List<Capture> captureList = CaptureMem.getCaptureListForCameraPageName(cameraPageName);
        for (Capture capture : captureList) {
            updateCaptureRectangle(capture, newPictureSize[0], newPictureSize[1], camera);
        }
    }

    public static void drawRectanglesWithNewHeight(String cameraPageName, Bounds bounds, Pane camera, Image image) {
        double[] newPictureSize = calculateProportionalSize(bounds.getWidth(), bounds.getHeight(), image);
        List<Capture> captureList = CaptureMem.getCaptureListForCameraPageName(cameraPageName);
        for (Capture capture : captureList) {
            updateCaptureRectangle(capture, newPictureSize[0], newPictureSize[1], camera);
        }
    }

    public static double[] calculateProportionalSize(double cameraPaneWidth, double cameraPaneHeight, Image image) {
        double aspectRatio;
        if (image != null) {
            aspectRatio = image.getWidth() / image.getHeight();
        } else {
            aspectRatio = 1280.0 / 720.0;
        }
        double pictureWidth;
        double pictureHeight;
        if (cameraPaneWidth / aspectRatio <= cameraPaneHeight) {
            pictureWidth = cameraPaneWidth;
            pictureHeight = (int) Math.round((cameraPaneWidth / aspectRatio));
        } else {
            pictureWidth = (int) Math.round(cameraPaneHeight * aspectRatio);
            pictureHeight = cameraPaneHeight;
        }
        return new double[]{pictureWidth, pictureHeight};
    }

    private static void createRectangle(Capture capture, Pane camera) {
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
            setBasicRectangleSettings(rectangle);
            capture.setRectangle(rectangle);
            camera.getChildren().add(rectangle);
        }
    }

    private static void setBasicRectangleSettings(Rectangle rectangle) {
        rectangle.setFill(null);
        rectangle.setStroke(Color.RED);
        rectangle.setStrokeWidth(2);
    }

    private static void updateCaptureRectangle(Capture capture, double newWidth, double newHeight, Pane camera) {
        Rectangle rectangle = capture.getRectangle();
        if (rectangle == null) {
            return;
        }
        camera.getChildren().remove(rectangle);

        double oldPixelX = rectangle.getX();
        double oldPixelY = rectangle.getY();
        double oldWidth = rectangle.getWidth();
        double oldHeight = rectangle.getHeight();

        double widthRatio = newWidth / capture.getPictureWidth();
        double heightRatio = newHeight / capture.getPictureHeight();

        double newPixelX = oldPixelX / capture.getPictureWidth() * newWidth;
        double newPixelY = oldPixelY / capture.getPictureHeight() * newHeight;
        double newPixelWidth = oldWidth * widthRatio;
        double newPixelHeight = oldHeight * heightRatio;

        recreateRectangle(capture, newPixelX, newPixelY, newPixelWidth, newPixelHeight, camera);
        capture.setPictureWidth(newWidth);
        capture.setPictureHeight(newHeight);
    }

    private static void recreateRectangle(Capture capture, double x, double y, double width, double height, Pane camera) {
        updateCaptureTextFieldsTexts(capture, x, y, x + width, y + height);
        Rectangle rectangle = new Rectangle(x, y, width, height);
        setBasicRectangleSettings(rectangle);
        capture.setRectangle(rectangle);
        camera.getChildren().add(rectangle);
    }

    private static void updateCaptureTextFieldsTexts(Capture capture, double x1, double y1, double x2, double y2) {
        capture.getX1TextField().setText(String.valueOf((int) x1));
        capture.getY1TextField().setText(String.valueOf((int) y1));
        capture.getX2TextField().setText(String.valueOf((int) x2));
        capture.getY2TextField().setText(String.valueOf((int) y2));
    }
}
