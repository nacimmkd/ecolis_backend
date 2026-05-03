package com.deliveryplatform.bookings;


import com.deliveryplatform.parcels.Parcel;
import com.deliveryplatform.trips.Trip;

import java.math.BigDecimal;

public final class BookingPriceCalculator {

    private BookingPriceCalculator() {}

    public static BigDecimal calculate(Parcel parcel, Trip trip) {
        return parcel.getWeightKg().multiply(trip.getPricePerKg());
    }
}
