package com.deliveryplatform.messages.dto;


import com.deliveryplatform.profiles.dto.ProfileSummary;
import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Builder(toBuilder = true)
public record MessageSummary(
        UUID           messageId,
        ProfileSummary sender,
        String         content,
        List<String>   imagesUrls,
        OffsetDateTime sentAt
) {}