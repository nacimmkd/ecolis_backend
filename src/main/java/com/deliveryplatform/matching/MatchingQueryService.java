package com.deliveryplatform.matching;

import com.deliveryplatform.parcels.Parcel;
import com.deliveryplatform.parcels.ParcelRepository;
import com.deliveryplatform.parcels.exceptions.ParcelErrorCode;
import com.deliveryplatform.parcels.exceptions.ParcelException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MatchingQueryService {

    private final MatchingFinderService matchingFinderService;
    private final ParcelRepository parcelRepository;
    private final MatchingMapper mapper;

    public Page<MatchResultDto> findMatchingTrips(UUID parcelId, LocalDate date, UUID actualUserId, Pageable pageable) {

        var parcel = getParcelByIdOrThrow(parcelId);
        assertOwnership(parcel, actualUserId);

        var results = matchingFinderService.findMatchingTrips(parcel, date, pageable.getSort());

        var content = results.stream()
                .skip(pageable.getOffset())
                .limit(pageable.getPageSize())
                .map(mapper::toDto)
                .toList();

        return new PageImpl<>(content, pageable, results.size());
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