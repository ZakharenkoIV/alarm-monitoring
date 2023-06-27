package ru.example.monitoring.visualprocessing;

import javafx.geometry.Bounds;
import javafx.scene.shape.Rectangle;
import net.sourceforge.tess4j.ITessAPI;
import net.sourceforge.tess4j.Tesseract;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import ru.example.monitoring.data.AreaData;
import ru.example.monitoring.data.CameraScreenshotData;
import ru.example.monitoring.data.Capture;
import ru.example.monitoring.integration.ClientMessageDispatcher;

import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class CameraImageHandler {

    private final LinkedBlockingQueue<CameraScreenshotData> imageQueue;
    private final LinkedBlockingQueue<AreaData> cutAreaQueue;
    private final ExecutorService imageProducerExecutors;
    private final ExecutorService imageConsumerExecutors;
    private final ExecutorService cutAreaConsumerExecutors;
    private final ConditionChecker checker;
    private final ClientMessageDispatcher clientMessageDispatcher;

    public CameraImageHandler(ConditionChecker checker, ClientMessageDispatcher clientMessageDispatcher) {
        this.imageQueue = new LinkedBlockingQueue<>();
        this.cutAreaQueue = new LinkedBlockingQueue<>();
        this.imageProducerExecutors = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        this.imageConsumerExecutors = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        this.cutAreaConsumerExecutors = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        this.checker = checker;
        this.clientMessageDispatcher = clientMessageDispatcher;
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
                    AreaData areaData = cutArea(imageData, capture);
                    if (areaData.getParameter().isActive()) {
                        cutAreaQueue.put(areaData);
                        areaConsumerHandle();
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    private void areaConsumerHandle() {
        cutAreaConsumerExecutors.submit(() -> {
            try {
                AreaData areaData = cutAreaQueue.take();
                Mat preparedImage = ImageTextExtractor.prepareImage(areaData.getMat());
                areaData.setPreparedImage(preparedImage);
                String recognizedValue = ImageTextExtractor.recognizeText(areaData, tesseractThreadLocal.get());
                areaData.setActualValue(recognizedValue);
                Optional<String> jsonWarningMessage = checker.check(areaData);
                jsonWarningMessage.ifPresent(clientMessageDispatcher::send);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    private final ThreadLocal<Tesseract> tesseractThreadLocal = ThreadLocal.withInitial(() -> {
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath("src/main/resources/tessdata");
        tesseract.setVariable("tessedit_char_whitelist", "0123456789.");
        tesseract.setPageSegMode(ITessAPI.TessPageSegMode.PSM_SINGLE_LINE);
        tesseract.setOcrEngineMode(ITessAPI.TessOcrEngineMode.OEM_DEFAULT);
        return tesseract;
    });

    private AreaData cutArea(CameraScreenshotData imageData, Capture capture) {
        Rectangle rectangle = capture.getRectangle();
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
