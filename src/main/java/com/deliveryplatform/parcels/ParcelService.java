package com.deliveryplatform.parcels;

import com.deliveryplatform.parcels.dto.*;

import java.util.List;
import java.util.UUID;

public interface ParcelService {

    ParcelOwnerDto getParcel(UUID id);

    List<ParcelSummaryDto> getUserParcels(UUID userId);

    List<ParcelSummaryDto> getParcels();

    ParcelOwnerDto createParcel(UUID userId, ParcelCreateRequest request);

    ParcelOwnerDto updateParcel(UUID parcelId, UUID userId, ParcelUpdateRequest request);

    void deleteParcel(UUID parcelId, UUID userId);

    List<TrackEventDto> getTrackingEvents(UUID parcelId);
}

