package ru.example.monitoring.handler.button;

import ru.example.monitoring.navigation.NavigationManager;

public class HomeButtonHandler {

    private final NavigationManager navigation;

    public HomeButtonHandler(NavigationManager navigation) {
        this.navigation = navigation;
    }

    public void handle() {
        navigation.navigateTo("home");
    }
}
