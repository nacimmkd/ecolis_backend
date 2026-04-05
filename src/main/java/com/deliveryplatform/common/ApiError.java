package com.deliveryplatform.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public record ApiError(
        int status,
        String message,
        String path,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime timestamp
) {
    public static ApiError of(int status, String message, String path) {
        return new ApiError(status, message, path, LocalDateTime.now());
    }
}