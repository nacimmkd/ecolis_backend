package com.deliveryplatform.parcels;

import com.deliveryplatform.parcels.dto.ParcelCreateRequest;
import com.deliveryplatform.parcels.dto.ParcelDetails;
import com.deliveryplatform.parcels.dto.ParcelSummary;
import com.deliveryplatform.parcels.dto.ParcelUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface ParcelService {

    ParcelDetails getParcel(UUID id);

    List<ParcelSummary> getUserParcels(UUID userId);

    List<ParcelSummary> getParcels();

    ParcelDetails createParcel(UUID userId, ParcelCreateRequest request);

    ParcelDetails updateParcel(UUID parcelId, UUID userId, ParcelUpdateRequest request);

    void deleteParcel(UUID parcelId, UUID userId);
}

