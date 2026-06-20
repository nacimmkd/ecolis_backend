package com.deliveryplatform.parcels;

import com.deliveryplatform.parcels.dto.*;

import java.util.List;
import java.util.UUID;

public interface ParcelService {

    ParcelDetails getParcel(UUID id);

    List<ParcelSummary> getUserParcels(UUID userId);

    List<ParcelSummary> getParcels();

    ParcelDetails createParcel(UUID userId, ParcelCreateRequest request);

    ParcelDetails updateParcel(UUID parcelId, UUID userId, ParcelUpdateRequest request);

    void deleteParcel(UUID parcelId, UUID userId);

    List<TrackEventDto> getTrackingEvents(UUID parcelId);
}

