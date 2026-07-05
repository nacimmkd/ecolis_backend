package com.deliveryplatform.matching;

import com.deliveryplatform.trips.Trip;
import org.springframework.stereotype.Service;

@Service
public class TripViabilityService {

    public boolean isViable(Trip trip, Detour detour) {
        double maxDetour = trip.getMaxDetourKm().doubleValue();
        return detour.pickupDetourKm() <= maxDetour && detour.dropoffDetourKm() <= maxDetour;
    }

}
