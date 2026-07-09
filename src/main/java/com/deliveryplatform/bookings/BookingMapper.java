package com.deliveryplatform.bookings;

import com.deliveryplatform.bookings.dto.BookingDto;
import com.deliveryplatform.bookings.dto.ParcelBookingDto;
import com.deliveryplatform.bookings.dto.TripBookingDto;
import com.deliveryplatform.parcels.ParcelMapper;
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
                .trip(tripMapper.toPublicDto(booking.getTrip()))
                .parcel(parcelMapper.toPublicDto(booking.getParcel()))
                .price(booking.getPrice())
                .status(booking.getStatus())
                .createdAt(booking.getCreatedAt())
                .paidAt(booking.getPaidAt())
                .completedAt(booking.getCompletedAt())
                .cancelledAt(booking.getCancelledAt())
                .build();
    }

    public ParcelBookingDto toParcelBookingDto(Booking booking) {
        return ParcelBookingDto.builder()
                .bookingId(booking.getId())
                .trip(tripMapper.toPublicDto(booking.getTrip()))
                .price(booking.getPrice())
                .status(booking.getStatus())
                .createdAt(booking.getCreatedAt())
                .paidAt(booking.getPaidAt())
                .completedAt(booking.getCompletedAt())
                .cancelledAt(booking.getCancelledAt())
                .build();
    }

    public TripBookingDto toTripBookingDto(Booking booking) {
        return TripBookingDto.builder()
                .bookingId(booking.getId())
                .parcel(parcelMapper.toPublicDto(booking.getParcel()))
                .price(booking.getPrice())
                .status(booking.getStatus())
                .createdAt(booking.getCreatedAt())
                .paidAt(booking.getPaidAt())
                .completedAt(booking.getCompletedAt())
                .cancelledAt(booking.getCancelledAt())
                .build();
    }

    public List<TripBookingDto> toTripBookingDto(List<Booking> bookings) {
        return bookings.stream()
                .map(this::toTripBookingDto)
                .toList();
    }

    public List<ParcelBookingDto> toParcelBookingDto(List<Booking> bookings) {
        return bookings.stream()
                .map(this::toParcelBookingDto)
                .toList();
    }

    public List<BookingDto> toDto(List<Booking> bookings) {
        return bookings.stream().map(this::toDto).toList();
    }

}