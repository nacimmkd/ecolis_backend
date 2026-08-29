package com.deliveryplatform.parcels;

import com.deliveryplatform.bookings.Booking;
import com.deliveryplatform.parcels.dto.ParcelBookingDto;
import com.deliveryplatform.trips.TripMapper;
import com.deliveryplatform.profiles.ProfileBriefMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = {TripMapper.class, ProfileBriefMapper.class},
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface ParcelBookingMapper {

    @Mapping(target = "bookingId", source = "id")
    @Mapping(target = "trip", source = "trip")
    @Mapping(target = "carrier", source = "trip.owner")
    ParcelBookingDto toParcelBookingDto(Booking booking);

    List<ParcelBookingDto> toParcelBookingDto(List<Booking> bookings);
}