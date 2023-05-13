package ru.example.monitoring.data;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

public class SettingTableRow {

    private final SimpleStringProperty description;
    private final SimpleStringProperty  sensor;
    private final SimpleStringProperty  minSetPoint;
    private final SimpleStringProperty  maxSetPoint;
    private final SimpleBooleanProperty isEnabled;

    public SettingTableRow(String description, String sensor, String minSetPoint, String maxSetPoint, Boolean isEnabled) {
        this.description = new SimpleStringProperty(description);
        this.sensor = new SimpleStringProperty(sensor);
        this.minSetPoint = new SimpleStringProperty(minSetPoint);
        this.maxSetPoint = new SimpleStringProperty(maxSetPoint);
        this.isEnabled = new SimpleBooleanProperty(isEnabled);
    }

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description.setValue(description);
    }

    public String getSensor() {
        return sensor.get();
    }

    public void setSensor(String sensor) {
        this.sensor.setValue(sensor);
    }

    public String getMinSetPoint() {
        return minSetPoint.get();
    }

    public void setMinSetPoint(String minSetPoint) {
        this.minSetPoint.setValue(minSetPoint);
    }

    public String getMaxSetPoint() {
        return maxSetPoint.get();
    }

    public void setMaxSetPoint(String maxSetPoint) {
        this.maxSetPoint.setValue(maxSetPoint);
    }

    public Boolean getEnabled() {
        return isEnabled.get();
    }

    public void setEnabled(Boolean enabled) {
        isEnabled.setValue(enabled);
    }
}
