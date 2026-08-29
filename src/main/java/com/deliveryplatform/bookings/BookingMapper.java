package com.deliveryplatform.bookings;

import com.deliveryplatform.bookings.dto.BookingDto;
import com.deliveryplatform.parcels.ParcelMapper;
import com.deliveryplatform.trips.TripMapper;
import com.deliveryplatform.profiles.ProfileBriefMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = {TripMapper.class, ParcelMapper.class, ProfileBriefMapper.class},
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
}