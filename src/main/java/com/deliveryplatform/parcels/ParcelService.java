package com.deliveryplatform.parcels;

import com.deliveryplatform.parcels.dto.ParcelCreateRequest;
import com.deliveryplatform.parcels.dto.ParcelResponse;
import com.deliveryplatform.parcels.dto.ParcelUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface ParcelService {

    ParcelResponse getParcel(UUID id);

    String getConfirmationCode(UUID parcelId, UUID userId);

    List<ParcelResponse> getUserParcels(UUID userId);

    List<ParcelResponse> getParcels();

    ParcelResponse createParcel(UUID userId, ParcelCreateRequest request);

    ParcelResponse updateParcel(UUID parcelId, UUID userId, ParcelUpdateRequest request);

    void deleteParcel(UUID parcelId, UUID userId);
}

