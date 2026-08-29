package com.deliveryplatform.matching;

import com.deliveryplatform.payments.Price;
import com.deliveryplatform.trips.dto.TripSummary;
import com.deliveryplatform.profiles.dto.ProfileBrief;
import lombok.Builder;

@Builder
public record MatchResultDto(
        TripSummary trip,
        ProfileBrief owner,
        Price price,
        double score
) {}