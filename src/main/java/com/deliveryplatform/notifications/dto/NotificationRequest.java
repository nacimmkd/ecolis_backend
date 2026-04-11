package com.deliveryplatform.notifications.dto;


import com.deliveryplatform.notifications.NotificationType;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.UUID;

@Builder
public record NotificationRequest(
        @NotNull UUID userId,
        @NotNull String emailTo,
        @NotNull NotificationType type,
        @NotNull UUID referenceId
) {}