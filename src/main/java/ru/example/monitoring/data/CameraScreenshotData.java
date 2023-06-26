package ru.example.monitoring.data;

import javafx.geometry.Bounds;
import javafx.scene.image.ImageView;
import org.opencv.core.Mat;

import java.util.List;

public class CameraScreenshotData {

    private final Mat screenshot;
    private final ImageView imageView;
    private final List<Capture> captures;

    public CameraScreenshotData(Mat screenshot, ImageView imageView, List<Capture> captures) {
        this.screenshot = screenshot;
        this.imageView = imageView;
        this.captures = captures;
    }

    public Mat getScreenshot() {
        return screenshot;
    }

    public List<Capture> getCaptures() {
        return captures;
    }

    public Bounds getCameraPaneBounds() {
        return imageView.getBoundsInParent();
    }
}
