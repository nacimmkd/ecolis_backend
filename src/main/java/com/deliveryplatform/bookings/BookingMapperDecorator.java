package com.deliveryplatform.bookings;

import com.deliveryplatform.bookings.dto.BookingDto;
import com.deliveryplatform.bookings.dto.BookingRequestDto;
import com.deliveryplatform.parcels.ParcelMapper;
import com.deliveryplatform.trips.TripMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public abstract class BookingMapperDecorator implements BookingMapper {

    @Autowired
    private BookingMapper delegate;

    @Autowired
    private ParcelMapper parcelMapper;

    @Autowired
    private TripMapper tripMapper;

    @Override
    public BookingDto toDto(Booking booking) {

        BookingDto dto = delegate.toDto(booking);

        return dto.toBuilder()
                .parcel(
                        parcelMapper.toSummaryDto(
                                booking.getParcel()
                        )
                )
                .trip(
                        tripMapper.toSummaryDto(
                                booking.getTrip()
                        )
                )
                .build();
    }

    @Override
    public BookingRequestDto toRequestDto(BookingRequest request) {

        BookingRequestDto dto = delegate.toRequestDto(request);

        return dto.toBuilder()
                .parcel(
                        parcelMapper.toSummaryDto(
                                request.getParcel()
                        )
                )
                .trip(
                        tripMapper.toSummaryDto(
                                request.getTrip()
                        )
                )
                .build();
    }
}