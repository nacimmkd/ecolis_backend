package com.deliveryplatform.matching;

import org.springframework.stereotype.Service;

@Service
public class MatchingScoreService {

    private static final double MIN_ROUTE_LENGTH_KM = 1.0;

    public double calculate(Detour detour) {
        double totalDetour = detour.pickupDetourKm() + detour.dropoffDetourKm();
        return totalDetour / Math.max(detour.routeLength(), MIN_ROUTE_LENGTH_KM);
    }
}