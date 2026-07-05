package com.deliveryplatform.matching;

import com.deliveryplatform.trips.TripMapper;
import com.deliveryplatform.users.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MatchingMapper {

    private final TripMapper tripMapper;
    private final UserMapper userMapper;

    public MatchResultDto toDto(MatchResult result) {
        return MatchResultDto.builder()
                .trip(tripMapper.toTripSummaryDto(result.trip()))
                .owner(userMapper.toRefDto(result.trip().getOwner()))
                .price(result.price())
                .score(result.score())
                .build();
    }

    public List<MatchResultDto> toDto(List<MatchResult> results) {
        return results.stream()
                .map(this::toDto)
                .toList();
    }
}