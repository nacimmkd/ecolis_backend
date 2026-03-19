package com.deliveryplatform.parcels;

import com.deliveryplatform.parcels.exceptions.ParcelNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.deliveryplatform.parcels.ParcelDto.*;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ParcelService {

    private final ParcelRepository parcelRepository;
    private final ParcelMapper parcelMapper;

    public ParcelResponse getParcel(UUID id) {
        var parcel = parcelRepository.findById(id)
                .orElseThrow(ParcelNotFoundException::new);
        return parcelMapper.toDto(parcel);
    }

    public List<ParcelResponse> getUserParcels(UUID userId){
        return parcelRepository.findByUserId(userId).stream()
                .map(parcelMapper::toDto)
                .toList();
    }

    public List<ParcelResponse> getParcels(){
        return parcelRepository.findAll().stream()
                .map(parcelMapper::toDto)
                .toList();
    }


    public ParcelResponse createParcel(UUID userId,ParcelRequest request) {
        var parcel = parcelMapper.toEntity(request);
        parcel.setUserId(userId);
        parcelRepository.save(parcel);
        return parcelMapper.toDto(parcel);
    }
}
