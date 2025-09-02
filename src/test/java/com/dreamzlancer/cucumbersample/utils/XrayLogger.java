package com.dreamzlancer.cucumbersample.utils;

import io.cucumber.java.Scenario;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class XrayLogger {

    // ThreadLocal ensures each thread has its own log buffer for parallel execution
    private static final ThreadLocal<List<String>> logBuffer = ThreadLocal.withInitial(ArrayList::new);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    public static void log(String message) {
        String timestamp = LocalDateTime.now().format(formatter);
        String formattedMessage = String.format("[%s] %s", timestamp, message);

        // Add to the thread-local buffer
        logBuffer.get().add(formattedMessage);

        // Also print to console for local execution
        System.out.println(formattedMessage);
    }

    public static void flushToXray(Scenario scenario) {
        List<String> logs = logBuffer.get();
        if (!logs.isEmpty()) {
            // Join all log messages with a newline
            String consolidatedLog = String.join("\n", logs);
            // Attach as a text/plain item
            scenario.attach(consolidatedLog, "text/plain", "Execution Log");
            // Clear the buffer for the next scenario
            clear();
        }
    }

    public static void clear() {
        logBuffer.get().clear();
    }
}