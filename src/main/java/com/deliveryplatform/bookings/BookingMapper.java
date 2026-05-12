package com.deliveryplatform.bookings;

import com.deliveryplatform.bookings.dto.BookingDto;
import com.deliveryplatform.requests.dto.RequestDto;
import com.deliveryplatform.parcels.ParcelMapper;
import com.deliveryplatform.requests.Request;
import com.deliveryplatform.trips.TripMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BookingMapper {

    private final TripMapper   tripMapper;
    private final ParcelMapper parcelMapper;

    public BookingDto toDto(Booking booking) {
        return BookingDto.builder()
                .bookingId(booking.getId())
                .trip(tripMapper.toSummaryDto(booking.getTrip()))
                .parcel(parcelMapper.toSummaryDto(booking.getParcel()))
                .price(booking.getPrice())
                .status(booking.getStatus())
                .createdAt(booking.getCreatedAt())
                .paidAt(booking.getPaidAt())
                .completedAt(booking.getCompletedAt())
                .cancelledAt(booking.getCancelledAt())
                .build();
    }

    public List<BookingDto> toDto(List<Booking> bookings) {
        return bookings.stream().map(this::toDto).toList();
    }

}