package ru.example.monitoring.camera;

import javafx.scene.image.Image;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfInt;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

public class CameraStreamer {

    private final List<CameraObserver> observers = new ArrayList<>();

    public CameraStreamer(int cameraIndex) {
        this.captureVideo(cameraIndex);
    }

    public void attach(CameraObserver observer) {
        observers.add(observer);
    }

    private void notifyObservers(Image image) {
        for (CameraObserver observer : observers) {
            observer.update(image);
        }
    }

    public void captureVideo(int cameraIndex) {
        VideoCapture videoCapture = new VideoCapture(cameraIndex);
        videoCapture.set(Videoio.CAP_PROP_FRAME_WIDTH, 1280);
        videoCapture.set(Videoio.CAP_PROP_FRAME_HEIGHT, 720);
        if (!videoCapture.isOpened()) {
            System.out.println("Не удалось открыть веб-камеру.");
            return;
        }
        subscribeToBroadcast(videoCapture);
    }

    private void subscribeToBroadcast(VideoCapture capture) {
        if (capture.isOpened()) {
            Thread captureThread = new Thread(() -> {
                long startTime1 = System.currentTimeMillis();
                long a = 0;
                Mat frame = new Mat();
                while (true) {
                    long startTime = System.currentTimeMillis();
                    capture.read(frame);
                    Image image = matToImage(frame);
                    notifyObservers(image);
                    a++;
                    if (startTime - startTime1 > 1000) {
                        System.out.println(a);
                        a = 0;
                        startTime1 = System.currentTimeMillis();
                    }
                }
            });
            captureThread.setDaemon(true);
            captureThread.start();
        }
    }

    private Image matToImage(Mat frame) {
        MatOfByte buffer = new MatOfByte();
        MatOfInt params = new MatOfInt(Imgcodecs.IMWRITE_PXM_BINARY, 1);
        Imgcodecs.imencode(".bmp", frame, buffer, params);
        return new Image(new ByteArrayInputStream(buffer.toArray()));
    }
}
