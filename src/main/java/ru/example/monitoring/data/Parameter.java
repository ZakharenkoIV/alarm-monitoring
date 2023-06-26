package ru.example.monitoring.data;

import java.util.Locale;

public class Parameter {

    private String description;
    private String sensor;
    private double minValue;
    private double maxValue;
    private final String regex;
    private final boolean isActive;

    public Parameter(String description, String sensor, String minValue, String maxValue, boolean isActive) {
        this.description = description;
        this.sensor = sensor.toUpperCase(Locale.ROOT);
        this.regex = defineRegex(this.sensor);
        this.minValue = Double.parseDouble(minValue);
        this.maxValue = Double.parseDouble(maxValue);
        this.isActive = isActive;
    }

    private String defineRegex(String sensor) {
        if (sensor.matches("((^PT)|(^РТ)|(^РT)|(^PТ)).*")) {
            return "^(((\\d)|([1-9][0-9]))\\.\\d{2})$";
        } else if (sensor.matches("(ИМ$)|(^Б(Ч|О)М$)")) {
            return "(^[1-9]\\d\\.\\d$)|(^\\d\\.\\d$)|(^100\\.0$)";
        } else if (sensor.matches("((^TE)|(^ТЕ)|(^TЕ)|(^ТE)|(^K)|(^К)|(^FE)|(^FЕ)|(^Т/Ч)).*")) {
            return "^([1-9][0-9]?[0-9]|[0])?\\.\\d$";
        }
        return "-";
    }

    public String getRegex() {
        return regex;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSensor() {
        return sensor;
    }

    public void setSensor(String sensor) {
        this.sensor = sensor;
    }

    public double getMinValue() {
        return minValue;
    }

    public void setMinValue(double minValue) {
        this.minValue = minValue;
    }

    public double getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(double maxValue) {
        this.maxValue = maxValue;
    }
}
