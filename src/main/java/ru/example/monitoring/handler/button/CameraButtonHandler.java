package ru.example.monitoring.handler.button;

import ru.example.monitoring.navigation.NavigationManager;

public class CameraButtonHandler {
    private final NavigationManager navigation;

    public CameraButtonHandler(NavigationManager navigation) {
        this.navigation = navigation;
    }

    public void handle() {
        navigation.navigateTo("camera");
    }
}
