package ru.example.monitoring.camera;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.io.ByteArrayInputStream;

public class CameraPane implements CameraObserver {

    private final ImageView imageView;

    public CameraPane(Pane pane) {
        this.imageView = new ImageView(new Image(new ByteArrayInputStream(new byte[0])));
        this.imageView.setPreserveRatio(true);
        this.imageView.fitWidthProperty().bind(pane.widthProperty());
        this.imageView.fitHeightProperty().bind(pane.heightProperty());
        this.imageView.setSmooth(true);
        this.imageView.setId("cameraImageView");
        pane.getChildren().add(this.imageView);
    }

    @Override
    public void update(Image image) {
        Platform.runLater(() -> imageView.setImage(image));
    }
}
