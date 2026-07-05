package com.deliveryplatform.matching;

import com.deliveryplatform.common.exceptions.ResourceNotFoundException;
import com.deliveryplatform.common.exceptions.UnauthorizedActionException;
import com.deliveryplatform.parcels.Parcel;
import com.deliveryplatform.parcels.ParcelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MatchingQueryService {

    private final MatchingFinderService matchingFinderService;
    private final ParcelRepository parcelRepository;
    private final MatchingMapper mapper;

    public List<MatchResultDto> findMatchingTrips(UUID parcelId, UUID actualUserId) {
        var parcel = getParcelByIdOrThrow(parcelId);
        assertOwnership(parcel, actualUserId);
        return matchingFinderService.findMatchingTrips(parcel).stream()
                .map(mapper::toDto)
                .toList();
    }

    private Parcel getParcelByIdOrThrow(UUID parcelId) {
        return parcelRepository.findById(parcelId)
                .orElseThrow(() -> new ResourceNotFoundException("parcel not found"));
    }

    private void assertOwnership(Parcel parcel, UUID userId) {
        var parcelOwner = parcel.getOwner();
        if (!parcelOwner.getId().equals(userId)) throw new UnauthorizedActionException("Unauthorized action");
    }
}