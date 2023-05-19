package ru.example.monitoring.repository.mem;

import ru.example.monitoring.data.Capture;

import java.util.*;

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

    public static List<Capture> getCaptureList(String cameraPageName) {
        return captures.get(cameraPageName);
    }
}
