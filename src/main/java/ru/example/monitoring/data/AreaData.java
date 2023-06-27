package ru.example.monitoring.data;

import org.opencv.core.Mat;
import ru.example.monitoring.repository.mem.ParameterMem;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class AreaData {

    private final Mat area;
    private final Parameter parameter;
    private Mat preparedImage;
    private final String areaName;
    private final String sensor;
    private String actualValue;
    private final String createTime;

    public AreaData(Mat area, String areaName, String sensor) {
        String adaptiveSensor = sensor.toUpperCase(Locale.ROOT).replace(",", ".");
        this.parameter = ParameterMem.getParameter(adaptiveSensor);
        this.area = area;
        this.areaName = areaName;
        this.sensor = adaptiveSensor;
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        createTime = now.format(formatter);
    }

    public String getCreateTime() {
        return createTime;
    }

    public String getActualValue() {
        return actualValue;
    }

    public void setActualValue(String actualValue) {
        this.actualValue = actualValue;
    }

    public Parameter getParameter() {
        return parameter;
    }

    public Mat getPreparedImage() {
        return preparedImage;
    }

    public void setPreparedImage(Mat preparedImage) {
        this.preparedImage = preparedImage;
    }

    public Mat getMat() {
        return area;
    }

    public String getAreaName() {
        return areaName;
    }

    public String getSensor() {
        return sensor;
    }
}
