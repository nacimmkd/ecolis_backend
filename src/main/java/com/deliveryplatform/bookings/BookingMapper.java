package com.deliveryplatform.bookings;

import com.deliveryplatform.bookings.dto.BookingDto;
import com.deliveryplatform.bookings.dto.BookingRequestDto;
import com.deliveryplatform.parcels.ParcelMapper;
import com.deliveryplatform.trips.TripMapper;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        uses = {ParcelMapper.class , TripMapper.class}
)
public interface BookingMapper {

    @Mapping(target = "bookingId", source = "id")
    BookingDto toDto(Booking booking);

    List<BookingDto> toDto(List<Booking> bookings);

    @Mapping(target = "requestId", source = "id")
    BookingRequestDto toRequestDto(BookingRequest bookingRequest);

    List<BookingRequestDto> toRequestDto(List<BookingRequest> bookingRequests);
}