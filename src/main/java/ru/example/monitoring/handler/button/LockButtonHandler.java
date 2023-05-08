package ru.example.monitoring.handler.button;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

public class LockButtonHandler {

    public void handle(Button lockButton, BorderPane borderPaneControl) {
        ObservableList<String> styleClasses = lockButton.getStyleClass();
        if (styleClasses.contains("lock-button-img")) {
            styleClasses.remove("lock-button-img");
            styleClasses.add("unlock-button-img");
            disableButtonsAndTextFieldsExceptLockButton(borderPaneControl, lockButton, false);
        } else {
            styleClasses.remove("unlock-button-img");
            styleClasses.add("lock-button-img");
            disableButtonsAndTextFieldsExceptLockButton(borderPaneControl, lockButton, true);
        }
    }

    private void disableButtonsAndTextFieldsExceptLockButton(Parent parent, Button lockButton, Boolean disableButtonsAndTextFields) {
        for (Node node : parent.getChildrenUnmodifiable()) {
            if (node instanceof Button || node instanceof TextField) {
                if (!(node.getId() != null && node.getId().equals(lockButton.getId()))) {
                    node.setDisable(disableButtonsAndTextFields);
                }
            }
            if (node instanceof TextField) {
                node.setDisable(disableButtonsAndTextFields);
            } else if (node instanceof Parent) {
                disableButtonsAndTextFieldsExceptLockButton((Parent) node, lockButton, disableButtonsAndTextFields);
            }
        }
    }
}
