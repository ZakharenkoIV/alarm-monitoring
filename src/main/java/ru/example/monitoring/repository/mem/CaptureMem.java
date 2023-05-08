package ru.example.monitoring.repository.mem;

import ru.example.monitoring.data.Capture;

import java.util.HashMap;
import java.util.Map;

public class CaptureMem {
    private static final Map<String, Capture> captures = new HashMap<>();

    public static Capture getCapture(String captureId) {
        return captures.get(captureId);
    }

    public static void putCapture(Capture capture) {
        captures.put(capture.getCaptureId(), capture);
    }
}
