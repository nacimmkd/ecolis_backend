package com.deliveryplatform.matching;

import com.deliveryplatform.parcels.Parcel;
import com.deliveryplatform.parcels.ParcelRepository;
import com.deliveryplatform.parcels.exceptions.ParcelErrorCode;
import com.deliveryplatform.parcels.exceptions.ParcelException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MatchingQueryService {

    private final MatchingFinderService matchingFinderService;
    private final ParcelRepository parcelRepository;
    private final MatchingMapper mapper;

    public List<MatchResultDto> findMatchingTrips(UUID parcelId, LocalDate date, UUID actualUserId) {

        var parcel = getParcelByIdOrThrow(parcelId);
        assertOwnership(parcel, actualUserId);

        return matchingFinderService.findMatchingTrips(parcel, date).stream()
                .map(mapper::toDto)
                .toList();
    }

    private Parcel getParcelByIdOrThrow(UUID parcelId) {
        return parcelRepository.findById(parcelId)
                .orElseThrow(() -> new ParcelException(ParcelErrorCode.PARCEL_NOT_FOUND,"parcel not found"));
    }

    private void assertOwnership(Parcel parcel, UUID userId) {
        var parcelOwner = parcel.getOwner();
        if (!parcelOwner.getId().equals(userId)) throw new ParcelException(ParcelErrorCode.PARCEL_NOT_OWNED,"parcel not found");
    }
}