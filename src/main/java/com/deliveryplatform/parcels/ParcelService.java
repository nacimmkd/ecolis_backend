package com.deliveryplatform.parcels;

import com.deliveryplatform.parcels.dto.ParcelCreateRequest;
import com.deliveryplatform.parcels.dto.ParcelDetailedResponse;
import com.deliveryplatform.parcels.dto.ParcelSummaryResponse;
import com.deliveryplatform.parcels.dto.ParcelUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface ParcelService {

    ParcelDetailedResponse getParcel(UUID id);

    List<ParcelSummaryResponse> getUserParcels(UUID userId);

    List<ParcelSummaryResponse> getParcels();

    ParcelDetailedResponse createParcel(UUID userId, ParcelCreateRequest request);

    ParcelDetailedResponse updateParcel(UUID parcelId, UUID userId, ParcelUpdateRequest request);

    void deleteParcel(UUID parcelId, UUID userId);
}

