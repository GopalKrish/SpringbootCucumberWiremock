package com.dreamzlancer.cucumbersample.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;

@UtilityClass
@Log4j2
public class LoggingUtils {

    public static void logRequest(String method, String url, String body) {
        log.debug("=== HTTP REQUEST ===");
        log.debug("Method: {}", method);
        log.debug("URL: {}", url);
        if (body != null && !body.trim().isEmpty()) {
            log.debug("Body: {}", body);
        }
        log.debug("===================");
    }

    public static void logResponse(ResponseEntity<String> response) {
        if (response != null) {
            log.debug("=== HTTP RESPONSE ===");
            log.debug("Status: {}", response.getStatusCode());
            log.debug("Body: {}", response.getBody());
            log.debug("====================");
        }
    }

    public static void logError(String message, Throwable throwable) {
        log.error("=== ERROR ===");
        log.error("Message: {}", message);
        if (throwable != null) {
            log.error("Exception: ", throwable);
        }
        log.error("=============");
    }

    public static void logInfo(String message, Object... args) {
        log.info(message, args);
    }

    public static void logDebug(String message, Object... args) {
        log.debug(message, args);
    }

    public static void logWarn(String message, Object... args) {
        log.warn(message, args);
    }
}