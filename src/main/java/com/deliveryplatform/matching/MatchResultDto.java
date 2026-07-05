package com.deliveryplatform.matching;

import com.deliveryplatform.trips.dto.TripSummary;
import com.deliveryplatform.users.dto.UserBrief;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record MatchResultDto(
        TripSummary trip,
        UserBrief  owner,
        BigDecimal price,
        double score
) {}