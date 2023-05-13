package ru.example.monitoring.handler;

import ru.example.monitoring.handler.button.CameraButtonHandler;
import ru.example.monitoring.handler.button.HomeButtonHandler;
import ru.example.monitoring.handler.button.SettingButtonHandler;
import ru.example.monitoring.navigation.NavigationManager;

public class HandlerManager {
    private CameraButtonHandler cameraButton;
    private HomeButtonHandler homeButton;
    private SettingButtonHandler settingButton;

    public HandlerManager() {
    }

    public CameraButtonHandler getCameraButton() {
        return cameraButton;
    }

    public HomeButtonHandler getHomeButton() {
        return homeButton;
    }

    public SettingButtonHandler getSettingButton() {
        return settingButton;
    }

    public void loadButtonHandlers(NavigationManager navigation) {
        cameraButton = new CameraButtonHandler(navigation);
        homeButton = new HomeButtonHandler(navigation);
        settingButton = new SettingButtonHandler(navigation);
    }
}
