package ru.example.monitoring.navigation;

import javafx.scene.Parent;
import javafx.scene.Scene;
import ru.example.monitoring.loader.PageLoader;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class NavigationManager {
    private static final Logger logger = Logger.getLogger(NavigationManager.class.getName());

    private final Map<String, Parent> pages = new HashMap<>();
    private Scene scene;
    private final PageLoader pageLoader;

    public NavigationManager(PageLoader pageLoader) {
        this.pageLoader = pageLoader;
        pages.put("home", (pageLoader.loadPage("home")));
        pages.put("setting", (pageLoader.loadPage("setting")));
        pages.put("camera", (pageLoader.loadPage("camera")));

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
            logger.warning("Не удалось найти страницу \"" + name + "\".");
        }
    }

    public Scene getScene() {
        return scene;
    }
}
