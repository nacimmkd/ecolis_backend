package com.deliveryplatform.bookings;

import com.deliveryplatform.bookings.dto.BookingDto;
import com.deliveryplatform.bookings.dto.ParcelBookingDto;
import com.deliveryplatform.bookings.dto.TripBookingDto;
import com.deliveryplatform.parcels.ParcelMapper;
import com.deliveryplatform.trips.TripMapper;
import com.deliveryplatform.users.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BookingMapper {

    private final TripMapper   tripMapper;
    private final ParcelMapper parcelMapper;
    private final UserMapper userMapper;

    public BookingDto toDto(Booking booking) {
        return BookingDto.builder()
                .bookingId(booking.getId())
                .trip(tripMapper.toTripSummaryDto(booking.getTrip()))
                .parcel(parcelMapper.toSummaryDto(booking.getParcel()))
                .carrier(userMapper.toRefDto(booking.getTrip().getOwner()))
                .sender(userMapper.toRefDto(booking.getParcel().getOwner()))
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
                .trip(tripMapper.toTripSummaryDto(booking.getTrip()))
                .carrier(userMapper.toRefDto(booking.getTrip().getOwner()))
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
                .parcel(parcelMapper.toSummaryDto(booking.getParcel()))
                .sender(userMapper.toRefDto(booking.getParcel().getOwner()))
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