package com.deliveryplatform.bookings;

import com.deliveryplatform.bookings.dto.BookingDto;
import com.deliveryplatform.bookings.dto.BookingRequestDto;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
@DecoratedWith(BookingMapperDecorator.class)
public interface BookingMapper {

    @Mapping(target = "bookingId", source = "id")
    @Mapping(target = "trip", ignore = true)
    @Mapping(target = "parcel", ignore = true)
    @Mapping(target = "confirmedAt", source = "confirmedAt")
    @Mapping(target = "paidAt", source = "paidAt")
    @Mapping(target = "completedAt", source = "completedAt")
    @Mapping(target = "cancelledAt", source = "cancelledAt")
    BookingDto toDto(Booking booking);

    @Mapping(target = "requestId", source = "id")
    @Mapping(target = "trip", ignore = true)
    @Mapping(target = "parcel", ignore = true)
    @Mapping(target = "status", source = "status")
    @Mapping(target = "rejectionReason", source = "rejectionReason")
    @Mapping(target = "requestedAt", source = "requestedAt")
    @Mapping(target = "respondedAt", source = "respondedAt")
    BookingRequestDto toRequestDto(BookingRequest bookingRequest);
}