package com.deliveryplatform.parcels;

import com.deliveryplatform.common.CodeGeneratorUtil;
import com.deliveryplatform.common.exceptions.InvalidDomainStateException;
import com.deliveryplatform.common.exceptions.ResourceNotFoundException;
import com.deliveryplatform.common.exceptions.UnauthorizedActionException;
import com.deliveryplatform.parcels.dto.ParcelRequest;
import com.deliveryplatform.parcels.dto.ParcelResponse;
import com.deliveryplatform.users.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ParcelService {

    private final ParcelRepository parcelRepository;
    private final UserService userService;
    private final ParcelMapper parcelMapper;

    public ParcelResponse getParcel(UUID id) {
        var parcel = getParcelByIdOrThrow(id);
        return parcelMapper.toDto(parcel);
    }

    public String getConfirmationCode(UUID parcelId, UUID userId) {
        var parcel = getParcelByIdOrThrow(parcelId);
        assertOwnership(parcel,userId);
        return parcel.getCodeOTP();
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



    public ParcelResponse createParcel(UUID userId, ParcelRequest request) {
        var parcel = parcelMapper.toEntity(request);
        var user = userService.getUserByIdOrThrow(userId);
        parcel.setUser(user);

        if(request.requireCode() != null && request.requireCode()){
            parcel.setCodeOTP(CodeGeneratorUtil.generateParcelCode());
        }

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

    public Parcel getParcelByIdOrThrow(UUID parcelId){
        return parcelRepository.findById(parcelId)
                .orElseThrow(() -> new ResourceNotFoundException("Parcel"));
    }


    private void assertOwnership(Parcel parcel, UUID userId) {
        if (!parcel.isOwner(userId)) {
            throw new UnauthorizedActionException("User is not owner of this parcel");
        }
    }

    private void assertParcelIsAvailable(Parcel parcel){
        if (!parcel.isAvailable()) {
            throw new InvalidDomainStateException("Parcel is not in a valid state for this operation");
        }
    }
}
