package com.deliveryplatform.matching;

import com.deliveryplatform.addresses.Address;
import com.deliveryplatform.parcels.Parcel;
import com.deliveryplatform.payments.PriceCalculator;
import com.deliveryplatform.trips.Trip;
import com.deliveryplatform.trips.TripRepository;
import com.deliveryplatform.trips.TripState;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
    public List<MatchResult> findMatchingTrips(Parcel parcel, LocalDate date, Sort sort) {
        Address pickup = parcel.getPickupAddress();
        var box = GeoUtils.boundingBox(pickup.getLatitude(), pickup.getLongitude(), SEARCH_RADIUS_KM);

        List<Trip> preMatchingTrips = tripRepository.findCandidateTrips(
                List.of(TripState.PUBLISHED, TripState.ACTIVE),
                date,
                parcel.getWeightKg(),
                box.minLat(), box.maxLat(),
                box.minLng(), box.maxLng()
        );

        return preMatchingTrips.stream()
                .map(trip -> computeResult(trip, parcel))
                .filter(MatchResult::viable)
                .sorted(sort(sort))
                .toList();
    }


    private MatchResult computeResult(Trip trip, Parcel parcel) {

        var detour = detourCalculatorService.calculate(trip,parcel);

        var viable = viabilityCalculator.isViable(trip, detour);
        var price = priceCalculator.calculateBookingPrice(trip, parcel);
        var score = scoreCalculator.calculate(detour);

        return new MatchResult(trip, price, score, viable);
    }

    private Comparator<MatchResult> sort(Sort sort) {
        var order = sort.stream()
                .findFirst()
                .orElse(Sort.Order.asc(MatchSortBy.SCORE));

        Comparator<MatchResult> comparator = switch (order.getProperty()) {
            case MatchSortBy.PRICE -> Comparator.comparingLong(result -> result.price().getAmountInCents());
            case MatchSortBy.DRIVER_RATING -> Comparator.comparing(this::driverRating, Comparator.nullsLast(Comparator.naturalOrder()));
            default -> Comparator.comparingDouble(MatchResult::score);
        };

        return order.isDescending() ? comparator.reversed() : comparator;
    }

    private BigDecimal driverRating(MatchResult result) {
        return result.trip().getOwner().getProfile().getAvgRating();
    }
}