package ru.example.monitoring.camera;

import javafx.scene.image.Image;

public interface CameraObserver {
    void update(Image image);
}
