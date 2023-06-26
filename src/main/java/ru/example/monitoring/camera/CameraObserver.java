package ru.example.monitoring.camera;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public interface CameraObserver {
    ImageView update(Image image);
}
