package com.deliveryplatform.matching;

import com.deliveryplatform.payments.Price;
import com.deliveryplatform.trips.dto.TripSummary;
import com.deliveryplatform.users.dto.UserBrief;
import lombok.Builder;

@Builder
public record MatchResultDto(
        TripSummary trip,
        UserBrief  owner,
        Price price,
        double score
) {}