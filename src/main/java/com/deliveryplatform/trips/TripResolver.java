package com.deliveryplatform.trips;

import com.deliveryplatform.profiles.ProfileResolver;
import com.deliveryplatform.trips.dto.TripDetailedResponse;
import com.deliveryplatform.trips.dto.TripStopResponse;
import com.deliveryplatform.trips.dto.TripSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TripResolver {

    private final TripMapper tripMapper;
    private final ProfileResolver profileResolver;

    public TripDetailedResponse resolveDetailed(Trip trip) {
        return tripMapper.toDetailedResponse(trip)
                .withOwner(profileResolver.resolveSummary(trip.getOwner().getProfile()))
                .withDepartureAddress(trip.getDepartureAddress())
                .withArrivalAddress(trip.getArrivalAddress())
                .withStops(resolveStops(trip));
    }

    public TripSummaryResponse resolveSummary(Trip trip) {
        return tripMapper.toSummaryResponse(trip)
                .withOwner(profileResolver.resolveSummary(trip.getOwner().getProfile()))
                .withDepartureAddress(trip.getDepartureAddress())
                .withArrivalAddress(trip.getArrivalAddress());
    }

    // ----------------------------------------------------------------

    private List<TripStopResponse> resolveStops(Trip trip) {
        if (trip.getStops() == null) return List.of();
        return trip.getStops().stream()
                .map(stop -> tripMapper.toStopResponse(stop)
                        .withAddress(stop.getAddress()))
                .toList();
    }
}