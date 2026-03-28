package com.deliveryplatform.bookings;

import com.deliveryplatform.bookings.dto.BookingRequest;
import com.deliveryplatform.bookings.dto.BookingResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    @Mapping(source = "trip.id",target = "tripId")
    @Mapping(source = "parcel.id",target = "parcelId")
    @Mapping(source = "requester.id",target = "requesterId")
    BookingResponse toDto(Booking booking);

    Booking toEntity(BookingRequest bookingRequest);
}