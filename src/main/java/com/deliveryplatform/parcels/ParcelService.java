package com.deliveryplatform.parcels;

import com.deliveryplatform.parcels.dto.ParcelRequest;
import com.deliveryplatform.parcels.dto.ParcelResponse;

import java.util.List;
import java.util.UUID;

public interface ParcelService {

    ParcelResponse getParcel(UUID id);

    String getConfirmationCode(UUID parcelId, UUID userId);

    List<ParcelResponse> getUserParcels(UUID userId);

    List<ParcelResponse> getParcels();

    ParcelResponse createParcel(UUID userId, ParcelRequest request);

    ParcelResponse updateParcel(UUID parcelId, UUID userId, ParcelRequest request);

    void deleteParcel(UUID parcelId, UUID userId);
}

