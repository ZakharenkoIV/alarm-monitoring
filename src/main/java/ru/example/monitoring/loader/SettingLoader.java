package ru.example.monitoring.loader;

import ru.example.monitoring.data.SettingTableRow;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;

public class SettingLoader {

    private final String fileName;

    public SettingLoader(String fileName) {
        this.fileName = fileName;
    }

    public Collection<SettingTableRow> loadSettings() throws IOException {
        Collection<SettingTableRow> settings = new LinkedList<>();
        File file = new File(fileName);
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                settings.add(createSettingTableRow(line));
            }
        }
        return settings;
    }

    private SettingTableRow createSettingTableRow(String line) {
        String[] data = line.split(" -> ");
        return new SettingTableRow(data[0], data[1], data[2], data[3], Boolean.parseBoolean(data[4]));
    }
}