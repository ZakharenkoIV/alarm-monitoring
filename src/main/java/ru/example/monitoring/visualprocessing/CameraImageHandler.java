package ru.example.monitoring.visualprocessing;

import javafx.geometry.Bounds;
import javafx.scene.shape.Rectangle;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import ru.example.monitoring.data.AreaData;
import ru.example.monitoring.data.CameraScreenshotData;
import ru.example.monitoring.data.Capture;

import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class CameraImageHandler {

    private final LinkedBlockingQueue<CameraScreenshotData> imageQueue;
    private final LinkedBlockingQueue<AreaData> cutAreaQueue;
    private final ExecutorService imageProducerExecutors;
    private final ExecutorService imageConsumerExecutors;
    private final ExecutorService cutAreaConsumerExecutors;

    public CameraImageHandler() {
        this.imageQueue = new LinkedBlockingQueue<>();
        this.cutAreaQueue = new LinkedBlockingQueue<>();
        this.imageProducerExecutors = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        this.imageConsumerExecutors = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        this.cutAreaConsumerExecutors = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    public void imageProducerHandle(Runnable imageTask) {
        imageProducerExecutors.submit(imageTask);
    }

    public void putCameraImage(CameraScreenshotData imageData) {
        try {
            imageQueue.put(imageData);
            imageConsumerHandle();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void imageConsumerHandle() {
        imageConsumerExecutors.submit(() -> {
            try {
                CameraScreenshotData imageData = imageQueue.take();
                for (Capture capture : imageData.getCaptures()) {
                    cutAreaQueue.put(cutArea(imageData, capture));
                    areaConsumerHandle();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    private void areaConsumerHandle() {
        cutAreaConsumerExecutors.submit(() -> {
            try {
                AreaData area = cutAreaQueue.take();
                AreaData result = ImageTextExtractor.prepareImage(area);
                String resultString = ImageTextExtractor.recognizeText(result);

                System.out.println(resultString);
                Imgcodecs.imwrite("src/main/resources/"
                        .concat(area.getAreaName().replace("|", "-"))
                        .concat(".jpg"), result.getPreparedImage()
                );
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    private AreaData cutArea(CameraScreenshotData imageData, Capture capture) {
        Rectangle rectangle = capture.getRectangle();
        ;
        String areaName = capture.getCaptureId();
        String sensorName = capture.getSensor().getText().toUpperCase(Locale.ROOT);
        int originScreenWidth = imageData.getScreenshot().width();
        int originScreenHeight = imageData.getScreenshot().height();

        Bounds cameraPaneBounds = imageData.getCameraPaneBounds();
        double windowScreenWidth = cameraPaneBounds.getWidth();
        double windowScreenHeight = cameraPaneBounds.getHeight();

        double windowRectX = rectangle.getX();
        double windowRectY = rectangle.getY();
        double originRectX = (windowRectX / windowScreenWidth) * originScreenWidth;
        double originRectY = (windowRectY / windowScreenHeight) * originScreenHeight;

        double widthRatio = originScreenWidth / windowScreenWidth;
        double heightRatio = originScreenHeight / windowScreenHeight;
        double originRectWidth = rectangle.getWidth() * widthRatio;
        double originRectHeight = rectangle.getHeight() * heightRatio;

        Rect roi = new Rect(
                (int) originRectX,
                (int) originRectY,
                (int) originRectWidth,
                (int) originRectHeight
        );
        Mat areaMat = imageData.getScreenshot().submat(roi);

        return new AreaData(areaMat, areaName, sensorName);
    }
}
