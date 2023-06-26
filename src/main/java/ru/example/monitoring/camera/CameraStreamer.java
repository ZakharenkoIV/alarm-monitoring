package ru.example.monitoring.camera;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfInt;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;
import ru.example.monitoring.data.CameraScreenshotData;
import ru.example.monitoring.repository.mem.CaptureMem;
import ru.example.monitoring.visualprocessing.CameraImageHandler;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

public class CameraStreamer {

    private final List<CameraObserver> observers;
    private final CameraImageHandler cameraImageHandler;
    private final String cameraName;

    public CameraStreamer(String cameraName, CameraImageHandler cameraImageHandler, int cameraIndex) {
        this.cameraName = cameraName;
        this.observers = new ArrayList<>();
        this.cameraImageHandler = cameraImageHandler;
        this.captureVideo(cameraIndex);
    }

    public void attach(CameraObserver observer) {
        observers.add(observer);
    }

    public void captureVideo(int cameraIndex) {
        subscribeToBroadcast(new VideoCapture(cameraIndex));
    }

    /**
     * Опрашивает камеру, отправляет скриншот на страницу отображения
     * и помещает скриншот в очередь на обработку.
     *
     * @param capture Объект подключения к камере.
     */
    private void subscribeToBroadcast(VideoCapture capture) {
        if (capture.isOpened()) {
            Thread captureThread = new Thread(() -> {
                while (true) {
                    Mat frame = new Mat();
                    //capture.read(frame);
                    frame = Imgcodecs.imread("src/main/resources/test.jpg");
                    ImageView imageView = notifyObservers(frame);
                    CameraScreenshotData screenshotData = new CameraScreenshotData(
                            frame,
                            imageView,
                            CaptureMem.getCaptureListForCameraName(cameraName)
                    );
                    cameraImageHandler.imageProducerHandle(() ->
                            cameraImageHandler.putCameraImage(screenshotData));
                }
            });
            captureThread.setDaemon(true);
            captureThread.start();
        } else {
            System.out.println("Не удалось открыть веб-камеру.");
        }
    }

    private ImageView notifyObservers(Mat frame) {
        ImageView imageView = null;
        for (CameraObserver observer : observers) {
            imageView = observer.update(matToImage(frame));
        }
        return imageView;
    }

    private Image matToImage(Mat frame) {
        MatOfByte buffer = new MatOfByte();
        MatOfInt params = new MatOfInt(Imgcodecs.IMWRITE_PXM_BINARY, 1);
        Imgcodecs.imencode(".bmp", frame, buffer, params);
        return new Image(new ByteArrayInputStream(buffer.toArray()));
    }
}
