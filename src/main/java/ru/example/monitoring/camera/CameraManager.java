package ru.example.monitoring.camera;

import com.github.sarxos.webcam.Webcam;
import javafx.scene.control.ComboBox;
import ru.example.monitoring.controller.SettingController;
import ru.example.monitoring.navigation.NavigationManager;
import ru.example.monitoring.visualprocessing.CameraImageHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CameraManager {

    private final Map<String, CameraStreamer> cameraStreamers = new HashMap<>();
    private List<Webcam> camerasWebcams;
    private final Map<Integer, ComboBox<String>> cameraComboBoxes = new HashMap<>();
    private NavigationManager navigation;
    private SettingController settingController;
    private int cameraPageCount = 1;
    private CameraImageHandler cameraImageHandler;

    public CameraManager(CameraImageHandler cameraImageHandler) {
        this.cameraImageHandler = cameraImageHandler;
    }

    public List<String> getCamerasNames() {
        readCameras();
        return camerasWebcams.stream().map(Webcam::getName).collect(Collectors.toList());
    }

    /**
     * Добавляет страницу с видео-трансляцией камеры.
     * Если трансляция уже открыта, то страница отображает открытую трансляцию, не открывая новую.
     */
    public void addCameraPage(String cameraName) {
        if (!cameraStreamers.containsKey(cameraName)) {
            cameraStreamers.put(cameraName, new CameraStreamer(
                    cameraName,
                    cameraImageHandler,
                    camerasWebcams.stream()
                            .map(Webcam::getName)
                            .collect(Collectors.toList())
                            .indexOf(cameraName)));
        }
        CameraPane cameraPane = navigation.loadCameraPage(cameraName);
        cameraPageCount++;
        cameraStreamers.get(cameraName).attach(cameraPane);
    }

    public void setNavigation(NavigationManager setNavigation) {
        this.navigation = setNavigation;
    }

    public void addCameraComboBox(ComboBox<String> comboBox) {
        cameraComboBoxes.put(cameraComboBoxes.keySet().size() + 1, comboBox);
    }

    private void readCameras() {
        camerasWebcams = Webcam.getWebcams();
    }

    public int getCameraPageCount() {
        return cameraPageCount;
    }

    public void setSettingController(SettingController settingController) {
        this.settingController = settingController;
    }
}
