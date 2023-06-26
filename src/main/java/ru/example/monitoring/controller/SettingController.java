package ru.example.monitoring.controller;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import ru.example.monitoring.data.SettingTableRow;
import ru.example.monitoring.handler.HandlerManager;
import ru.example.monitoring.loader.SettingLoader;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

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
        handlerManager.getHomeButtonHandler().handle();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        bindColumns();
        createEnabledColumnCellFactory();
        loadSettings();
        editableCols();
    }

    private void editableCols() {
        settingTable.setEditable(true);
        descriptionColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        descriptionColumn.setOnEditCommit(event -> {
            SettingTableRow row = event.getRowValue();
            String oldSettingTableRow = row.toString();
            String newValue = event.getNewValue();
            if (newValue != null && !newValue.equals("") && !newValue.contains(" -> ") && newValue.length() < 100) {
                row.setDescription(newValue);
                String newSettingTableRow = row.toString();
                updateLineInFile(oldSettingTableRow, newSettingTableRow);
            } else {
                event.consume();
                event.getTableView().refresh();
            }
        });
        sensorColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        sensorColumn.setOnEditCommit(event -> {
            SettingTableRow row = event.getRowValue();
            String oldSettingTableRow = row.toString();
            String newValue = event.getNewValue();
            if (newValue != null && !newValue.equals("") && !newValue.contains(" -> ") && newValue.length() < 20) {
                row.setSensor(newValue);
                String newSettingTableRow = row.toString();
                updateLineInFile(oldSettingTableRow, newSettingTableRow);
            } else {
                event.consume();
                event.getTableView().refresh();
            }
        });
        minSetPointColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        minSetPointColumn.setOnEditCommit(event -> {
            SettingTableRow row = event.getRowValue();
            String oldSettingTableRow = row.toString();
            String newValue = event.getNewValue();
            if (newValue.matches("^\\d{1,3}([.,]\\d{1,2})?$")) {
                row.setMinSetPoint(newValue.replace(",", "."));
                String newSettingTableRow = row.toString();
                updateLineInFile(oldSettingTableRow, newSettingTableRow);
            } else {
                event.consume();
                event.getTableView().refresh();
            }
        });
        maxSetPointColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        maxSetPointColumn.setOnEditCommit(event -> {
            SettingTableRow row = event.getRowValue();
            String oldSettingTableRow = row.toString();
            String newValue = event.getNewValue();
            if (newValue.matches("^\\d{1,3}([.,]\\d{1,2})?$")) {
                row.setMaxSetPoint(newValue.replace(",", "."));
                String newSettingTableRow = row.toString();
                updateLineInFile(oldSettingTableRow, newSettingTableRow);
            } else {
                event.consume();
                event.getTableView().refresh();
            }
        });
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
                    String oldSettingTableRow = settingTableRow.toString();
                    settingTableRow.setEnabled(checkBox.isSelected());
                    String newSettingTableRow = settingTableRow.toString();
                    updateLineInFile(oldSettingTableRow, newSettingTableRow);
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
        settings = settingLoader.loadSettings();
        settingTable.getItems().addAll(settings);
    }

    private void updateLineInFile(String lineToFind, String newLine) {
        File file = new File("src/main/resources/setting.txt");
        List<String> fileContent = null;
        try {
            fileContent = new ArrayList<>(Files.readAllLines(file.toPath(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < Objects.requireNonNull(fileContent).size(); i++) {
            if (fileContent.get(i).equals(lineToFind)) {
                fileContent.set(i, newLine);
                break;
            }
        }
        try {
            Files.write(file.toPath(), fileContent, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
