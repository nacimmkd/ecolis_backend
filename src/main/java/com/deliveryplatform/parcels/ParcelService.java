package com.deliveryplatform.parcels;

import com.deliveryplatform.parcels.dto.ParcelBookingDto;
import com.deliveryplatform.parcels.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ParcelService {

    ParcelDetails getParcel(UUID id);

    Page<ParcelSummary> getUserParcels(UUID currentUserId, ParcelState state, Pageable pageable);

    Page<ParcelBookingDto> getParcelBookings(UUID parcelId, UUID currentUserId, Pageable pageable);

    Page<ParcelSummary> getParcels(Pageable pageable);

    ParcelDetails createParcel(UUID userId, ParcelCreateRequest request);

    ParcelDetails updateParcel(UUID parcelId, UUID userId, ParcelUpdateRequest request);

    void deleteParcel(UUID parcelId, UUID userId);

    List<TrackEventDto> getTrackingEvents(UUID parcelId);

    ParcelImageDto addParcelImage(UUID parcelId, UUID userId, ParcelImageRequest request);

    void removeParcelImage(UUID parcelId, UUID imageId, UUID userId);
}

