package com.deliveryplatform.requests;

import com.deliveryplatform.parcels.ParcelMapper;
import com.deliveryplatform.requests.dto.RequestDto;
import com.deliveryplatform.trips.TripMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RequestMapper {

    private final TripMapper tripMapper;
    private final ParcelMapper parcelMapper;


    public RequestDto toRequestDto(Request request) {
        return RequestDto.builder()
                .requestId(request.getId())
                .trip(tripMapper.toSummaryDto(request.getTrip()))
                .parcel(parcelMapper.toSummaryDto(request.getParcel()))
                .status(request.getStatus())
                .rejectionReason(request.getRejectionReason())
                .requestedAt(request.getRequestedAt())
                .respondedAt(request.getRespondedAt())
                .build();
    }

    public List<RequestDto> toRequestDto(List<Request> requests) {
        return requests.stream().map(this::toRequestDto).toList();
    }
}
