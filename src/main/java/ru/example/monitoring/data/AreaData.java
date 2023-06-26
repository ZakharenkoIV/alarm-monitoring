package ru.example.monitoring.data;

import org.opencv.core.Mat;
import ru.example.monitoring.repository.mem.ParameterMem;

import java.util.Locale;

public class AreaData {

    private final Mat area;
    private final Parameter parameter;
    private Mat preparedImage;
    private final String areaName;
    private final String sensor;

    public AreaData(Mat area, String areaName, String sensor) {
        String adaptiveSensor = sensor.toUpperCase(Locale.ROOT).replace(",", ".");
        this.parameter = ParameterMem.getParameter(adaptiveSensor);
        this.area = area;
        this.areaName = areaName;
        this.sensor = adaptiveSensor;
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
