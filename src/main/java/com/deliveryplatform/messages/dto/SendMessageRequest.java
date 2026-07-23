package com.deliveryplatform.messages.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record SendMessageRequest(
        @NotNull
        UUID conversationId,

        @NotBlank
        String content,

        List<UUID> imageIds
) {
}
