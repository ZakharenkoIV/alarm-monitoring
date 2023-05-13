package ru.example.monitoring.controller;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import ru.example.monitoring.data.SettingTableRow;
import ru.example.monitoring.handler.HandlerManager;
import ru.example.monitoring.loader.SettingLoader;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.ResourceBundle;

public class SettingController implements Initializable {

    @FXML
    private TableView<SettingTableRow> settingTable;
    @FXML
    private TableColumn<SettingTableRow, String> descriptionColumn;
    @FXML
    private TableColumn<SettingTableRow, String> sensorColumn;
    @FXML
    private TableColumn<SettingTableRow, String> minSetPointColumn;
    @FXML
    private TableColumn<SettingTableRow, String> maxSetPointColumn;
    @FXML
    private TableColumn<SettingTableRow, Boolean> isEnabledColumn;
    private final HandlerManager handlerManager;

    public SettingController(HandlerManager handlerManager) {
        this.handlerManager = handlerManager;
    }

    @FXML
    public void handleHomeButtonAction() {
        handlerManager.getHomeButton().handle();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        bindColumns();
        createEnabledColumnCellFactory();
        loadSettings();
    }

    private void bindColumns() {
        descriptionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));
        sensorColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSensor()));
        minSetPointColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMinSetPoint()));
        maxSetPointColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMaxSetPoint()));
        isEnabledColumn.setCellValueFactory(cellData -> new SimpleBooleanProperty(cellData.getValue().getEnabled()));
    }

    private void createEnabledColumnCellFactory() {
        isEnabledColumn.setCellFactory(column -> {
            CheckBox checkBox = new CheckBox();
            checkBox.setOnAction(event -> {
                SettingTableRow settingTableRow = (SettingTableRow) checkBox.getUserData();
                if (settingTableRow != null) {
                    settingTableRow.setEnabled(checkBox.isSelected());
                }
            });
            return new TableCell<>() {
                @Override
                protected void updateItem(Boolean item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!empty) {
                        checkBox.setSelected(item);
                        checkBox.setUserData(getTableRow().getItem());
                        setGraphic(checkBox);
                    } else {
                        setGraphic(null);
                    }
                }
            };
        });
    }

    private void loadSettings() {
        SettingLoader settingLoader = new SettingLoader("src/main/resources/setting.txt");
        Collection<SettingTableRow> settings;
        try {
            settings = settingLoader.loadSettings();
        } catch (IOException e) {
            e.printStackTrace();
            settings = Collections.emptyList();
        }
        settingTable.getItems().addAll(settings);
    }
}
