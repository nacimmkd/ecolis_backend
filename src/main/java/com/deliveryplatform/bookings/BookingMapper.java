package com.deliveryplatform.bookings;

import com.deliveryplatform.bookings.dto.BookingDto;
import com.deliveryplatform.bookings.dto.ParcelBookingDto;
import com.deliveryplatform.bookings.dto.TripBookingDto;
import com.deliveryplatform.parcels.ParcelMapper;
import com.deliveryplatform.trips.TripMapper;
import com.deliveryplatform.users.UserMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = {TripMapper.class, ParcelMapper.class, UserMapper.class},
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface BookingMapper {

    @Mapping(target = "bookingId", source = "id")
    @Mapping(target = "trip", source = "trip")
    @Mapping(target = "parcel", source = "parcel")
    @Mapping(target = "carrier", source = "trip.owner")
    @Mapping(target = "sender", source = "parcel.owner")
    BookingDto toDto(Booking booking);

    List<BookingDto> toDto(List<Booking> bookings);

    @Mapping(target = "bookingId", source = "id")
    @Mapping(target = "trip", source = "trip")
    @Mapping(target = "carrier", source = "trip.owner")
    ParcelBookingDto toParcelBookingDto(Booking booking);

    @Mapping(target = "bookingId", source = "id")
    @Mapping(target = "parcel", source = "parcel")
    @Mapping(target = "sender", source = "parcel.owner")
    TripBookingDto toTripBookingDto(Booking booking);

    List<ParcelBookingDto> toParcelBookingDto(List<Booking> bookings);

    List<TripBookingDto> toTripBookingDto(List<Booking> bookings);
}