package com.deliveryplatform.messages.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record SendMessageRequest(
        @NotNull UUID        conversationId,
        String     content,
        List<UUID>  imageIds
) {}
