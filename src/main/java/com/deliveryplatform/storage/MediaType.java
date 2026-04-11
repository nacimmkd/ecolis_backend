package com.deliveryplatform.storage;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@Getter
public enum MediaType {

    IMAGE_JPEG     ("image/jpeg",      ".jpg"),
    IMAGE_PNG      ("image/png",       ".png"),
    IMAGE_WEBP     ("image/webp",      ".webp");

    private final String value;
    private final String extension;

    MediaType(String value, String extension) {
        this.value     = value;
        this.extension = extension;
    }

    public static Optional<MediaType> of(String value) {
        return Arrays.stream(MediaType.values())
                .filter(ft -> ft.value.equalsIgnoreCase(value))
                .findFirst();
    }
}