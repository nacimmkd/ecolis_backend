package com.deliveryplatform.trips;

import com.deliveryplatform.profiles.ProfileMapper;
import com.deliveryplatform.trips.dto.TripDetails;
import com.deliveryplatform.trips.dto.TripStopSummary;
import com.deliveryplatform.trips.dto.TripSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public abstract class TripMapperDecorator implements TripMapper {

    @Autowired
    private TripMapper delegate;

    @Autowired
    private ProfileMapper profileMapper;

    @Override
    public TripSummary toSummaryDto(Trip trip) {

        TripSummary dto = delegate.toSummaryDto(trip);

        return dto.toBuilder()
                .owner(profileMapper.toSummaryDto(
                        trip.getOwner().getProfile()
                ))
                .departureAddress(trip.getDepartureAddress())
                .arrivalAddress(trip.getArrivalAddress())
                .stops(resolveStops(trip))
                .build();
    }

    @Override
    public TripDetails toDetailsDto(Trip trip) {

        TripDetails dto = delegate.toDetailsDto(trip);

        return dto.toBuilder()
                .owner(profileMapper.toSummaryDto(
                        trip.getOwner().getProfile()
                ))
                .departureAddress(trip.getDepartureAddress())
                .arrivalAddress(trip.getArrivalAddress())
                .stops(resolveStops(trip))
                .build();
    }

    private List<TripStopSummary> resolveStops(Trip trip) {

        if (trip.getStops() == null || trip.getStops().isEmpty()) {
            return List.of();
        }

        return trip.getStops()
                .stream()
                .map(delegate::toStopDto)
                .toList();
    }
}