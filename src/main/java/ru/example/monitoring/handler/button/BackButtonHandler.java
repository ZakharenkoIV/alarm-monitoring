package ru.example.monitoring.handler.button;

import ru.example.monitoring.navigation.NavigationManager;

public class BackButtonHandler {
    private final NavigationManager navigation;

    public BackButtonHandler(NavigationManager navigation) {
        this.navigation = navigation;
    }

    public void handle(String cameraPageName) {
        String previousCameraPageName = navigation.getPreviousCameraPageName(cameraPageName);
        navigation.navigateTo(previousCameraPageName);
    }
}
