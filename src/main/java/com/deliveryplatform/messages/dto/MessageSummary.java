package com.deliveryplatform.messages.dto;


import com.deliveryplatform.images.dto.ImageDto;
import com.deliveryplatform.users.dto.UserBrief;
import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record MessageSummary(
        UUID           messageId,
        UserBrief sender,
        String         content,
        List<ImageDto>   images,
        OffsetDateTime sentAt
) {}