package ru.example.monitoring.navigation;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import ru.example.monitoring.camera.CameraPane;
import ru.example.monitoring.loader.PageLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class NavigationManager {
    private static final Logger logger = Logger.getLogger(NavigationManager.class.getName());

    private final Map<String, Parent> pages = new HashMap<>();
    private final ArrayList<String> cameraPages = new ArrayList<>();
    private Scene scene;
    private final PageLoader pageLoader;

    public NavigationManager(PageLoader pageLoader) {
        this.pageLoader = pageLoader;
        pages.put("home", pageLoader.loadPage("home"));
        pages.put("setting", pageLoader.loadPage("setting"));
    }

    public CameraPane loadCameraPage(String cameraName) {
        Parent cameraPage = pageLoader.loadPage("camera");
        String cameraPageName = "№" + (cameraPages.size() + 1) + " " + cameraName;
        pages.put(cameraPageName, cameraPage);
        Label cameraNameLabel = (Label) cameraPage.lookup("#cameraPageName");
        cameraPages.add(cameraPageName);
        cameraNameLabel.setText(cameraPageName);
        return new CameraPane((Pane) cameraPage.lookup("#camera"));
    }

    public String getFirstCameraName() {
        return cameraPages.isEmpty() ? "Empty cameraPages" : cameraPages.get(0);
    }

    public void loadStartScene(String pageName) {
        Parent root = pageLoader.loadPage(pageName);
        if (root != null) {
            scene = new Scene(root, 800, 600);
            pages.put(pageName, root);
        } else {
            logger.warning("Не удалось загрузить стартовую страницу \"" + pageName + "\".");
        }
    }

    public void navigateTo(String name) {
        Parent root = pages.get(name);
        if (root != null) {
            scene.setRoot(root);
        } else {
            if  (!"Empty cameraPages".equals(name))
            logger.warning("Не удалось найти страницу \"" + name + "\".");
        }
    }

    public Scene getScene() {
        return scene;
    }

    public String getNextCameraPageName(String cameraPageName) {
        int cameraPageNameIndex = cameraPages.indexOf(cameraPageName);
        return cameraPages.size() == cameraPageNameIndex + 1
                ? cameraPages.get(0)
                : cameraPages.get(cameraPageNameIndex + 1);
    }

    public String getPreviousCameraPageName(String cameraPageName) {
        int cameraPageNameIndex = cameraPages.indexOf(cameraPageName);
        return 0 == cameraPageNameIndex
                ? cameraPages.get(cameraPages.size() - 1)
                : cameraPages.get(cameraPageNameIndex - 1);
    }
}
