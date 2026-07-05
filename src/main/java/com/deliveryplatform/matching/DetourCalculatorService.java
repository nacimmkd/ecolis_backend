package com.deliveryplatform.matching;

import com.deliveryplatform.parcels.Parcel;
import com.deliveryplatform.trips.Trip;
import org.springframework.stereotype.Service;

@Service
public class DetourCalculatorService {

    public Detour calculate(Trip trip, Parcel parcel) {
        Route route = Route.of(trip);

        double pickupDetour = route.distanceTo(
                parcel.getPickupAddress().getLatitude(),
                parcel.getPickupAddress().getLongitude()
        );
        double dropoffDetour = route.distanceTo(
                parcel.getDropoffAddress().getLatitude(),
                parcel.getDropoffAddress().getLongitude()
        );

        return new Detour(pickupDetour, dropoffDetour, route.length());
    }
}