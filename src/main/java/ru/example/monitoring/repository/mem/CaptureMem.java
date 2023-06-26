package ru.example.monitoring.repository.mem;

import ru.example.monitoring.data.Capture;

import java.util.*;
import java.util.stream.Collectors;

public class CaptureMem {
    private static final Map<String, List<Capture>> captures = new HashMap<>();

    public static Capture getCapture(String captureId, String cameraPageName) {
        return captures.getOrDefault(cameraPageName, Collections.emptyList())
                .stream()
                .filter(capture -> capture.getCaptureId().equals(captureId))
                .findFirst()
                .orElse(null);
    }

    public static void putCapture(Capture capture, String cameraPageName) {
        captures.computeIfAbsent(cameraPageName, key -> new ArrayList<>()).add(capture);
    }

    public static List<Capture> getCaptureListForCameraPageName(String cameraPageName) {
        return Optional.ofNullable(captures.get(cameraPageName)).orElse(Collections.emptyList());
    }

    public static List<Capture> getCaptureListForCameraName(String cameraName) {
        return captures.entrySet().stream()
                .filter(entry -> entry.getKey().substring(entry.getKey().indexOf(" ") + 1).equals(cameraName))
                .flatMap(entry -> entry.getValue().stream())
                .collect(Collectors.toList());
    }
}
