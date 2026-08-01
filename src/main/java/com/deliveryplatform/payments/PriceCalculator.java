package com.deliveryplatform.payments;

import com.deliveryplatform.bookings.Booking;
import com.deliveryplatform.parcels.Parcel;
import com.deliveryplatform.trips.Trip;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PriceCalculator {

    private final PlatformFees platformFees;

    public record Quote(Price total, long applicationFeeInCents) {}

    public Price calculateBookingPrice(Trip trip, Parcel parcel) {
        var basePrice = trip.getPricePerKg().multiply(parcel.getWeightKg());
        var applicationFeeInCents = calculateApplicationFee(basePrice.getAmountInCents());
        return basePrice.add(Price.of(applicationFeeInCents, basePrice.getCurrency()));
    }

    public Quote calculate(Booking booking) {
        var total = booking.getPrice();
        var applicationFeeInCents = calculateApplicationFee(total.getAmountInCents());
        return new Quote(total, applicationFeeInCents);
    }

    private long calculateApplicationFee(long amountInCents) {
        return ((amountInCents * platformFees.getFeeRate()) / 100) + platformFees.getConstFee();
    }

}
