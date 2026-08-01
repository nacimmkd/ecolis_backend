package com.deliveryplatform.matching;

import com.deliveryplatform.addresses.Address;
import com.deliveryplatform.parcels.Parcel;
import com.deliveryplatform.payments.PriceCalculator;
import com.deliveryplatform.trips.Trip;
import com.deliveryplatform.trips.TripRepository;
import com.deliveryplatform.trips.TripState;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MatchingFinderService {

    private static final double SEARCH_RADIUS_KM = 50.0;

    private final TripRepository tripRepository;
    private final DetourCalculatorService detourCalculatorService;
    private final MatchingScoreService scoreCalculator;
    private final TripViabilityService viabilityCalculator;
    private final PriceCalculator priceCalculator;

    @Transactional(readOnly = true)
    public List<MatchResult> findMatchingTrips(Parcel parcel, LocalDate date) {
        Address pickup = parcel.getPickupAddress();
        var box = GeoUtils.boundingBox(pickup.getLatitude(), pickup.getLongitude(), SEARCH_RADIUS_KM);

        List<Trip> preMatchingTrips = tripRepository.findCandidateTrips(
                TripState.PUBLISHED,
                date,
                parcel.getWeightKg(),
                box.minLat(), box.maxLat(),
                box.minLng(), box.maxLng()
        );

        return preMatchingTrips.stream()
                .map(trip -> computeResult(trip, parcel))
                .filter(MatchResult::viable)
                .sorted(Comparator.comparingDouble(MatchResult::score))
                .toList();
    }


    private MatchResult computeResult(Trip trip, Parcel parcel) {

        var detour = detourCalculatorService.calculate(trip,parcel);

        var viable = viabilityCalculator.isViable(trip, detour);
        var price = priceCalculator.calculateBookingPrice(trip, parcel);
        var score = scoreCalculator.calculate(detour);

        return new MatchResult(trip, price, score, viable);
    }
}