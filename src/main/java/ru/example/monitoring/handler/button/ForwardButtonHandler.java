package ru.example.monitoring.handler.button;

import ru.example.monitoring.navigation.NavigationManager;

public class ForwardButtonHandler {
    private final NavigationManager navigation;

    public ForwardButtonHandler(NavigationManager navigation) {
        this.navigation = navigation;
    }

    public void handle(String cameraPageName) {
        String nextCameraPageName = navigation.getNextCameraPageName(cameraPageName);
        navigation.navigateTo(nextCameraPageName);
    }
}
