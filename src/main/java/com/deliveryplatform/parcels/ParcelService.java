package com.deliveryplatform.parcels;

import com.deliveryplatform.parcels.exceptions.IllegalParcelStateException;
import com.deliveryplatform.parcels.exceptions.ParcelNotFoundException;
import com.deliveryplatform.parcels.exceptions.UnauthorizedParcelActionException;
import com.deliveryplatform.users.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.deliveryplatform.parcels.ParcelDto.*;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ParcelService {

    private final ParcelRepository parcelRepository;
    private final UserService userService;
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


    public List<ParcelResponse> getAvailableParcels(){
        return parcelRepository.findByStatus(ParcelStatus.PUBLISHED).stream()
                .map(parcelMapper::toDto)
                .toList();
    }


    public ParcelResponse createParcel(UUID userId,ParcelRequest request) {
        var parcel = parcelMapper.toEntity(request);
        var user = userService.getUserByIdOrThrow(userId);
        parcel.setUser(user);
        parcelRepository.save(parcel);
        return parcelMapper.toDto(parcel);
    }

    public ParcelResponse updateParcel(UUID parcelId, UUID userId, ParcelRequest request) {
        Parcel parcel = getParcelByIdOrThrow(parcelId);
        assertOwnership(parcel, userId);
        assertParcelIsAvailable(parcel);
        parcelMapper.updateEntity(parcel, request);
        return parcelMapper.toDto(parcelRepository.save(parcel));
    }


    public void deleteParcel(UUID parcelId, UUID userId) {
        var parcel = getParcelByIdOrThrow(parcelId);
        assertOwnership(parcel, userId);
        assertParcelIsAvailable(parcel);
        parcelRepository.delete(parcel);
    }



    //----------------- private methods-----------------------------

    private Parcel getParcelByIdOrThrow(UUID parcelId){
        return parcelRepository.findById(parcelId)
                .orElseThrow(ParcelNotFoundException::new);
    }


    private void assertOwnership(Parcel parcel, UUID userId) {
        if (!parcel.isOwner(userId)) {
            throw new UnauthorizedParcelActionException();
        }
    }

    private void assertParcelIsAvailable(Parcel parcel){
        if (!parcel.isAvailable()) {
            throw new IllegalParcelStateException();
        }
    }
}
