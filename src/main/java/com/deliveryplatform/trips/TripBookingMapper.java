package com.deliveryplatform.trips;

import com.deliveryplatform.bookings.Booking;
import com.deliveryplatform.parcels.ParcelMapper;
import com.deliveryplatform.trips.dto.TripBookingDto;
import com.deliveryplatform.profiles.ProfileBriefMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = {ParcelMapper.class, ProfileBriefMapper.class},
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface TripBookingMapper {

    @Mapping(target = "bookingId", source = "id")
    @Mapping(target = "parcel", source = "parcel")
    @Mapping(target = "sender", source = "parcel.owner")
    TripBookingDto toTripBookingDto(Booking booking);

    List<TripBookingDto> toTripBookingDto(List<Booking> bookings);
}
