package ru.example.monitoring.visualprocessing;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import ru.example.monitoring.data.AreaData;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Класс ImageTextExtractor содержит методы для обработки изображений с текстом.
 */
public class ImageTextExtractor {

    /**
     * Подготавливает изображение для извлечения текста.
     */
    public static AreaData prepareImage(AreaData areaData) {
        Mat originalImage = areaData.getMat();
        // --- imageStep1: Вырезали область с зелёными цифрами ---
        Mat greenImage = extractGreenChannel(originalImage); // закрасили всё кроме зелёного
        Mat binaryImage = convertToBinary(greenImage); // преобразовали в чёрно- белый
        Mat imageStep1 = extractRegion(originalImage, findBoundingRect(binaryImage)); //получили область с оригинала, где были белые пиксели
        // --- imageStep2: Выделили точку ярче ---
        Mat imageStep2 = removeNoise(imageStep1); //убрали тусклые пиксели
        Mat removeNoisePoint = removeNoisePoint(imageStep1);// нашли точку
        Rect rect = findLargestContour(removeNoisePoint);
        drawFilledRectangle(imageStep2, rect); // выделили точку зелёным
        // --- imageStep3: Вырезали все области и вставили в новый Mat по очереди с равными интервалами. ---
        Mat binaryImage2 = convertToBinary2(imageStep2);
        List<Rect> contours = splitByContours(binaryImage2);
        List<Mat> regions = extractRegionsFromImage(contours, imageStep2);
        List<Mat> preparedImages = imageProcessing(regions);
        Mat combineImages = combineImages(preparedImages);

        areaData.setPreparedImage(combineImages);
        return areaData;
    }

    private static Mat combineImages(List<Mat> images) {
        if (images.isEmpty()) {
            return new Mat();
        }
        int resultWidth = images.stream().mapToInt(Mat::cols).sum();
        int resultHeight = images.stream().mapToInt(Mat::rows).max().orElse(1);
        Mat resultMat = new Mat(resultHeight, resultWidth, images.get(0).type());
        resultMat.setTo(new Scalar(0, 0, 0));
        int x = 0;
        for (Mat image : images) {
            Rect roi = new Rect(x, resultHeight - image.rows(), image.cols(), image.rows());
            image.copyTo(resultMat.submat(roi));
            x += image.cols();
        }
        return resultMat;
    }

    private static List<Mat> imageProcessing(List<Mat> regions) {
        List<Mat> result = new LinkedList<>();
        for (Mat region : regions) {
            Mat blackBorder = addBlackBorder(region, 3); // добавили рамку
            Mat increaseImageSize = increaseImageSize(blackBorder, 7); // увеличили масштаб. сгладили границы
            Mat dilatedImage = addDilation(increaseImageSize, 5, 5); // увеличиваем яркость и размер изображения
            Mat smoothedImage = addSmoothing(dilatedImage, 5, 5); // размываем изображение и уменьшаем шумы
            Mat erodedImage = addErosion(smoothedImage, 3, 3); // сужаем границы и уменьшаем яркость
            Mat convertToBinary = convertToBinary(erodedImage); // преобразовали в чёрно-белый
            result.add(convertToBinary);
        }
        return result;
    }

    private static Mat addDilation(Mat image, int width, int height) {
        Mat dilatedImage = new Mat();
        Imgproc.dilate(image, dilatedImage, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(width, height)));
        return dilatedImage;
    }

    private static Mat addErosion(Mat image, int width, int height) {
        Mat erodedImage = new Mat();
        Imgproc.erode(image, erodedImage, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(width, height)));
        return erodedImage;
    }

    private static Mat addSmoothing(Mat image, int width, int height) {
        Mat smoothedIimage = new Mat();
        Imgproc.GaussianBlur(image, smoothedIimage, new Size(width, height), 0);
        return smoothedIimage;
    }

    private static List<Mat> extractRegionsFromImage(List<Rect> contours, Mat imageStep2) {
        List<Mat> result = new LinkedList<>();
        for (Rect rect : contours) {
            result.add(new Mat(imageStep2, rect));
        }
        return result;
    }

    private static Mat convertToBinary2(Mat image) {
        Mat grayImage = new Mat();
        Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_BGR2GRAY);

        Mat binaryImage = new Mat();
        Imgproc.threshold(grayImage, binaryImage, 1, 255, Imgproc.THRESH_BINARY);

        return binaryImage;
    }

    private static List<Rect> splitByContours(Mat binaryImage) {
        List<Rect> result = new LinkedList<>();
        List<MatOfPoint> contours = findContours1(binaryImage);
        contours.sort((contour1, contour2) -> {
            Rect rect1 = Imgproc.boundingRect(contour1);
            Rect rect2 = Imgproc.boundingRect(contour2);
            return Integer.compare(rect1.x, rect2.x);
        });

        for (MatOfPoint matOfPoint : contours) {
            Rect boundingRect = Imgproc.boundingRect(matOfPoint);
            if (boundingRect.height >= boundingRect.width) {
                result.add(boundingRect);
            } else {
                int halfRectWidth = boundingRect.width / 2;
                Rect leftRect = new Rect(boundingRect.x, boundingRect.y, halfRectWidth, boundingRect.height);
                result.add(leftRect);
                Rect rightRect = new Rect(boundingRect.x + halfRectWidth, boundingRect.y, halfRectWidth, boundingRect.height);
                result.add(rightRect);
            }
        }
        return result;
    }

    private static Mat removeNoisePoint(Mat image) {
        Scalar lowerBlueThreshold = new Scalar(0, 0, 60);
        Scalar upperBlueThreshold = new Scalar(30, 100, 100);
        Scalar lowerBlackThreshold = new Scalar(30, 0, 50);
        Scalar upperBlackThreshold = new Scalar(60, 100, 100);
        Scalar lowerRedThreshold = new Scalar(15, 100, 100);
        Scalar upperRedThreshold = new Scalar(20, 200, 255);
        Scalar lowerGreenThreshold = new Scalar(20, 100, 100);
        Scalar upperGreenThreshold = new Scalar(30, 200, 255);
        Mat hsvImage = new Mat();
        Imgproc.cvtColor(image, hsvImage, Imgproc.COLOR_RGB2HSV);
        Mat blueMask = new Mat();
        Core.inRange(hsvImage, lowerBlueThreshold, upperBlueThreshold, blueMask);
        Mat blueComponentImage = new Mat();
        Core.bitwise_and(image, image, blueComponentImage, blueMask);
        Mat blackMask = new Mat();
        Core.inRange(hsvImage, lowerBlackThreshold, upperBlackThreshold, blackMask);
        Mat blackComponentImage = new Mat();
        Core.bitwise_and(image, image, blackComponentImage, blackMask);
        Mat redMask = new Mat();
        Core.inRange(hsvImage, lowerRedThreshold, upperRedThreshold, redMask);
        Mat redComponentImage = new Mat();
        Core.bitwise_and(image, image, redComponentImage, redMask);
        Mat greenMask = new Mat();
        Core.inRange(hsvImage, lowerGreenThreshold, upperGreenThreshold, greenMask);
        Mat greenComponentImage = new Mat();
        Core.bitwise_and(image, image, greenComponentImage, greenMask);
        Mat resultImage = new Mat();
        Core.addWeighted(blueComponentImage, 1.0, blackComponentImage, 1.0, 0.0, resultImage);
        Core.addWeighted(resultImage, 1.0, redComponentImage, 1.0, 0.0, resultImage);
        Core.addWeighted(resultImage, 1.0, greenComponentImage, 1.0, 0.0, resultImage);
        return resultImage;
    }

    private static Rect findLargestContour(Mat mat) {
        Mat convertToBinary = convertToBinary(mat);
        Mat hierarchy = new Mat();
        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(convertToBinary, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        double maxArea = 0;
        int maxContourIndex = -1;
        for (int i = 0; i < contours.size(); i++) {
            double area = Imgproc.contourArea(contours.get(i));
            if (area > maxArea) {
                maxArea = area;
                maxContourIndex = i;
            }
        }
        Rect rect = new Rect();
        if (maxContourIndex >= 0) {
            rect = Imgproc.boundingRect(contours.get(maxContourIndex));
        }
        return rect;
    }

    /**
     * Рисует заполненный прямоугольник на извлеченном регионе изображения.
     *
     * @param extractedRegion    извлеченный регион изображения
     * @param largestContourRect ограничивающий прямоугольник наибольшего контура
     */
    private static void drawFilledRectangle(Mat extractedRegion, Rect largestContourRect) {
        Point topLeft = new Point(largestContourRect.tl().x, largestContourRect.tl().y);
        Point bottomRight = new Point(largestContourRect.tl().x + 1, largestContourRect.tl().y + 1);
        Imgproc.rectangle(extractedRegion, topLeft, bottomRight, new Scalar(0, 255, 0), -1);
    }

    private static List<MatOfPoint> findContours1(Mat image) {
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(image, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        return contours;
    }

    /**
     * Находит контуры на бинарном изображении.
     *
     * @param image бинарное изображение
     * @return список контуров
     */
    private static List<MatOfPoint> findContours(Mat image) {
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(image, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        return contours;
    }

    private static Mat increaseImageSize(Mat image, int size) {
        Mat resizedImage = new Mat();
        Imgproc.resize(image, resizedImage, new Size(image.cols() * size, image.rows() * size));
        return resizedImage;
    }

    private static Mat removeNoise(Mat image) {
        Scalar lowerGreenThreshold = new Scalar(30, 0, 60);
        Scalar upperGreenThreshold = new Scalar(70, 240, 255);
        Mat hsvImage = new Mat();
        Imgproc.cvtColor(image, hsvImage, Imgproc.COLOR_RGB2HSV);
        Mat greenMask = new Mat();
        Core.inRange(hsvImage, lowerGreenThreshold, upperGreenThreshold, greenMask);
        Mat greenComponentImage = new Mat();
        Core.bitwise_and(image, image, greenComponentImage, greenMask);
        return processContours(greenComponentImage);
    }

    private static Mat processContours(Mat coloredImage) {
        Mat grayImage = new Mat();
        Imgproc.cvtColor(coloredImage, grayImage, Imgproc.COLOR_BGR2GRAY);
        Mat binaryImage = new Mat();
        Imgproc.threshold(grayImage, binaryImage, 0, 255, Imgproc.THRESH_BINARY);
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(binaryImage, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        Mat result = coloredImage.clone();
        for (MatOfPoint contour : contours) {
            if (contour.total() < 7) {
                Imgproc.drawContours(result, Collections.singletonList(contour), 0, new Scalar(0, 0, 0), -1);
            }
        }
        return result;
    }

    private static Mat addBlackBorder(Mat image, int size) {
        int newWidth = image.cols() + 2 * size;
        int newHeight = image.rows() + 2 * size * 2;
        Mat newImage = new Mat(newHeight, newWidth, image.type(), Scalar.all(0));
        Mat roi = newImage.submat(size * 2, size * 2 + image.rows(), size, size + image.cols());
        image.copyTo(roi);
        return newImage;
    }

    /**
     * Извлекает прямоугольную область из исходного изображения.
     *
     * @param sourceMat исходное изображение
     * @param rect      прямоугольная область для извлечения
     * @return извлеченная область
     */
    private static Mat extractRegion(Mat sourceMat, Rect rect) {
        return new Mat(sourceMat, rect.clone());
    }

    /**
     * Находит ограничивающий прямоугольник вокруг контуров на изображении.
     *
     * @param image изображение
     * @return ограничивающий прямоугольник
     */
    private static Rect findBoundingRect(Mat image) {
        List<MatOfPoint> contours = findContours(image);
        return getBoundingRect(contours);
    }

    /**
     * Преобразует изображение в бинарное изображение.
     *
     * @param image исходное изображение
     * @return бинарное изображение
     */
    private static Mat convertToBinary(Mat image) {
        Mat grayImage = new Mat();
        Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_BGR2GRAY);
        Mat binaryImage = new Mat();
        Imgproc.threshold(grayImage, binaryImage, 0, 255, Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);
        return binaryImage;
    }

    /**
     * Извлекает зеленую цветовую компоненту из изображения.
     *
     * @param image исходное изображение
     * @return изображение, содержащее только зеленую цветовую компоненту
     */
    private static Mat extractGreenChannel(Mat image) {
        Scalar lowerGreenThreshold = new Scalar(30, 50, 100);
        Scalar upperGreenThreshold = new Scalar(60, 155, 200);
        Mat hsvImage = new Mat();
        Imgproc.cvtColor(image, hsvImage, Imgproc.COLOR_RGB2HSV);
        Mat greenMask = new Mat();
        Core.inRange(hsvImage, lowerGreenThreshold, upperGreenThreshold, greenMask);
        Mat greenComponentImage = new Mat();
        Core.bitwise_and(image, image, greenComponentImage, greenMask);
        return greenComponentImage;
    }

    /**
     * Получает ограничивающий прямоугольник из списка контуров.
     *
     * @param contours список контуров
     * @return ограничивающий прямоугольник
     */
    private static Rect getBoundingRect(List<MatOfPoint> contours) {
        Rect rect = new Rect();
        if (!contours.isEmpty()) {
            MatOfPoint mergedContour = new MatOfPoint();
            contours.forEach(mergedContour::push_back);
            rect = Imgproc.boundingRect(mergedContour);
        }
        return rect.clone();
    }

    /**
     * Распознает текст на изображении с помощью библиотеки Tesseract.
     */
    public static String recognizeText(AreaData areaData, Tesseract tesseract) {
        Mat image = areaData.getPreparedImage();
        BufferedImage bufferedImage = matToBufferedImage(image);
        File tempFile;
        try {
            tempFile = File.createTempFile("temp", ".jpg");
            ImageIO.write(bufferedImage, "jpg", tempFile);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        String recognizedText;
        try {
            String regex = areaData.getParameter().getRegex();
            tesseract.setLanguage("digits1");
            recognizedText = tesseract.doOCR(tempFile).replace("\n", "");
            if (checkPattern(recognizedText, regex)) {
                tesseract.setLanguage("digits");
                recognizedText = tesseract.doOCR(tempFile).replace("\n", "");
                if (checkPattern(recognizedText, regex)) {
                    tesseract.setLanguage("digits_comma");
                    recognizedText = tesseract.doOCR(tempFile).replace("\n", "");
                    if (checkPattern(recognizedText, regex)) {
                        tesseract.setLanguage("rus");
                        recognizedText = tesseract.doOCR(tempFile).replace("\n", "");
                        if (checkPattern(recognizedText, regex)) {
                            tesseract.setLanguage("eng");
                            recognizedText = tesseract.doOCR(tempFile).replace("\n", "");
                            if (checkPattern(recognizedText, regex)) {
                                recognizedText = "-";
                            }
                        }
                    }
                }
            }
        } catch (TesseractException e) {
            e.printStackTrace();
            return null;
        }
        return recognizedText;
    }

    private static boolean checkPattern(String input, String regex) {
        return !Pattern.matches(regex, input);
    }

    /**
     * Преобразует матрицу OpenCV в объект BufferedImage.
     *
     * @param mat матрица OpenCV
     * @return объект BufferedImage
     */
    private static BufferedImage matToBufferedImage(Mat mat) {
        int type = mat.channels() > 1 ? BufferedImage.TYPE_3BYTE_BGR : BufferedImage.TYPE_BYTE_GRAY;
        BufferedImage image = new BufferedImage(mat.cols(), mat.rows(), type);
        WritableRaster raster = image.getRaster();
        DataBufferByte dataBuffer = (DataBufferByte) raster.getDataBuffer();
        byte[] data = dataBuffer.getData();
        mat.get(0, 0, data);
        return image;
    }
}