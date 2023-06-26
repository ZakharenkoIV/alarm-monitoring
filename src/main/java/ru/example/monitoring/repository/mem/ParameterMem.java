package ru.example.monitoring.repository.mem;

import ru.example.monitoring.data.Parameter;
import ru.example.monitoring.loader.SettingLoader;

import java.util.List;
import java.util.stream.Collectors;

public class ParameterMem {

    private static final List<Parameter> parameters;

    static {
        SettingLoader settingLoader = new SettingLoader("src/main/resources/setting.txt");
        parameters = settingLoader.loadSettings().stream()
                .map(settingTableRow -> new Parameter(
                        settingTableRow.getDescription(),
                        settingTableRow.getSensor(),
                        settingTableRow.getMinSetPoint(),
                        settingTableRow.getMaxSetPoint(),
                        settingTableRow.getEnabled()
                ))
                .collect(Collectors.toList());
    }

    public static Parameter getParameter(String sensor) {
        for (Parameter parameter : parameters) {
            if (parameter.getSensor().equals(sensor)) {
                return parameter;
            }
        }
        return new Parameter("", "", "", "", false);
    }
}
