package ru.example.monitoring.visualprocessing;

import ru.example.monitoring.data.AreaData;

import java.util.Optional;

public class ConditionChecker {

    public Optional<String> check(AreaData areaData) {
        double actualValue = Double.parseDouble(areaData.getActualValue());
        double minValue = areaData.getParameter().getMinValue();
        double maxValue = areaData.getParameter().getMaxValue();

        String warningCode = null;
        if (actualValue <= minValue) {
            warningCode = "min";
        } else if (actualValue >= maxValue) {
            warningCode = "max";
        }


        return Optional.ofNullable(generateWarningMessage(areaData, warningCode));
    }

    private String generateWarningMessage(AreaData areaData, String warningCode) {
        if (warningCode == null) {
            return null;
        }
        return String.format("""
                        {
                          "timestamp": %s,
                          "sensor": "%s",
                          "description": "%s",
                          "actualValue": "%s",
                          "minValue": "%s",
                          "maxValue": "%s",
                          "warningCode": "%s"
                        }
                        """,
                areaData.getCreateTime(),
                areaData.getParameter().getSensor(),
                areaData.getParameter().getDescription(),
                areaData.getActualValue(),
                areaData.getParameter().getMinValue(),
                areaData.getParameter().getMaxValue(),
                warningCode
        );
    }
}
