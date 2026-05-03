package com.deliveryplatform.bookings;

import com.deliveryplatform.bookings.dto.BookingRequestResponse;
import com.deliveryplatform.bookings.dto.BookingResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    @Mapping(target = "bookingId",    source = "id")
    @Mapping(target = "trip",         ignore = true)
    @Mapping(target = "parcel",      ignore = true)
    @Mapping(target = "confirmedAt",  source = "confirmedAt")
    @Mapping(target = "paidAt",       source = "paidAt")
    @Mapping(target = "completedAt",  source = "completedAt")
    @Mapping(target = "cancelledAt",  source = "cancelledAt")
    BookingResponse toResponse(Booking booking);

    @Mapping(target = "requestId",       source = "id")
    @Mapping(target = "trip",            ignore = true)
    @Mapping(target = "parcel",         ignore = true)
    @Mapping(target = "status",          source = "status")
    @Mapping(target = "rejectionReason", source = "rejectionReason")
    @Mapping(target = "requestedAt",     source = "requestedAt")
    @Mapping(target = "respondedAt",     source = "respondedAt")
    BookingRequestResponse toRequestResponse(BookingRequest bookingRequest);
}