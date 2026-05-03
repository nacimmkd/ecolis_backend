package com.deliveryplatform.bookings;

import com.deliveryplatform.bookings.dto.BookingRequestResponse;
import com.deliveryplatform.bookings.dto.BookingResponse;
import com.deliveryplatform.parcels.ParcelResolver;
import com.deliveryplatform.trips.TripResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookingResolver {

    private final BookingMapper  bookingMapper;
    private final TripResolver tripResolver;
    private final ParcelResolver parcelResolver;

    public BookingResponse resolve(Booking booking) {
        return bookingMapper.toResponse(booking)
                .withTrip(tripResolver.resolveSummary(booking.getTrip()))
                .withParcels(parcelResolver.resolveSummary(booking.getParcel()));
    }

    public BookingRequestResponse resolve(BookingRequest request) {
        return bookingMapper.toRequestResponse(request)
                .withTrip(tripResolver.resolveSummary(request.getTrip()))
                .withParcel(parcelResolver.resolveSummary(request.getParcel()));
    }
}
