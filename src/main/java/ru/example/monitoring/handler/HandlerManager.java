package ru.example.monitoring.handler;

import ru.example.monitoring.handler.button.*;
import ru.example.monitoring.navigation.NavigationManager;

public class HandlerManager {
    private CameraButtonHandler cameraButtonHandler;
    private HomeButtonHandler homeButtonHandler;
    private SettingButtonHandler settingButtonHandler;
    private ForwardButtonHandler forwardButtonHandler;
    private BackButtonHandler backButtonHandler;
    private CaptureButtonHandler captureButtonHandler;
    private LockButtonHandler lockButtonHandler;

    public HandlerManager() {
    }

    public CameraButtonHandler getCameraButtonHandler() {
        return cameraButtonHandler;
    }

    public HomeButtonHandler getHomeButtonHandler() {
        return homeButtonHandler;
    }

    public SettingButtonHandler getSettingButtonHandler() {
        return settingButtonHandler;
    }

    public void loadButtonHandlers(NavigationManager navigation) {
        cameraButtonHandler = new CameraButtonHandler(navigation);
        homeButtonHandler = new HomeButtonHandler(navigation);
        settingButtonHandler = new SettingButtonHandler(navigation);
        forwardButtonHandler = new ForwardButtonHandler(navigation);
        backButtonHandler = new BackButtonHandler(navigation);
        captureButtonHandler = new CaptureButtonHandler();
        lockButtonHandler = new LockButtonHandler();
    }

    public ForwardButtonHandler getForwardButtonHandler() {
        return forwardButtonHandler;
    }

    public BackButtonHandler getBackButtonHandler() {
        return backButtonHandler;
    }

    public CaptureButtonHandler getCaptureButtonHandler() {
        return captureButtonHandler;
    }

    public LockButtonHandler getLockButtonHandler() {
        return lockButtonHandler;
    }
}
