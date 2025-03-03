package org.esales.products.util;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ErrorResponseUtil {

    public static Map<String, Object> createErrorResponse(String message, int statusCode, String path) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now().toString());
        errorResponse.put("status", statusCode);
        errorResponse.put("error", getErrorTitle(statusCode));
        errorResponse.put("message", message);
        errorResponse.put("path", path);

        return errorResponse;
    }

    public static Map<String, Object> createErrorResponse(String message, int statusCode, String path, Map<String, String> validationErrors) {
        Map<String, Object> errorResponse = createErrorResponse(message, statusCode, path);
        errorResponse.put("validationErrors", validationErrors);
        return errorResponse;
    }

    private static String getErrorTitle(int statusCode) {
        switch (statusCode) {
            case 400:
                return "Bad Request";
            case 401:
                return "Unauthorized";
            case 403:
                return "Forbidden";
            case 404:
                return "Not Found";
            case 500:
                return "Internal Server Error";
            default:
                return "Unexpected Error";
        }
    }
}
