package ru.example.monitoring.handler.button;

import ru.example.monitoring.navigation.NavigationManager;

public class SettingButtonHandler {
    NavigationManager navigation;

    public SettingButtonHandler(NavigationManager navigation) {
        this.navigation = navigation;
    }

    public void handle() {
        navigation.navigateTo("setting");
    }
}
