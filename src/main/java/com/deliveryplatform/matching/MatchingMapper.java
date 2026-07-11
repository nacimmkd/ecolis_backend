package com.deliveryplatform.matching;

import com.deliveryplatform.trips.TripMapper;
import com.deliveryplatform.users.UserMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = {TripMapper.class, UserMapper.class},
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface MatchingMapper {

    @Mapping(target = "trip", source = "trip")
    @Mapping(target = "owner", source = "trip.owner")
    MatchResultDto toDto(MatchResult result);

    List<MatchResultDto> toDto(List<MatchResult> results);
}