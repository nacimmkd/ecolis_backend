package com.deliveryplatform.parcels;

import com.deliveryplatform.common.CodeGeneratorUtil;
import com.deliveryplatform.common.exceptions.InvalidDomainStateException;
import com.deliveryplatform.common.exceptions.ResourceNotFoundException;
import com.deliveryplatform.common.exceptions.UnauthorizedActionException;
import com.deliveryplatform.parcels.dto.ParcelRequest;
import com.deliveryplatform.parcels.dto.ParcelResponse;
import com.deliveryplatform.users.User;
import com.deliveryplatform.users.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ParcelServiceImp implements ParcelService{

    private final ParcelRepository parcelRepository;
    private final UserRepository userRepository;
    private final ParcelMapper parcelMapper;

    @Override
    public ParcelResponse getParcel(UUID id) {
        var parcel = getParcelByIdOrThrow(id);
        return ParcelResponse.of(parcel);
    }

    @Override
    public String getConfirmationCode(UUID parcelId, UUID userId) {
        var parcel = getParcelByIdOrThrow(parcelId);
        assertOwnership(parcel,userId);
        return parcel.getCodeOTP();
    }

    @Override
    public List<ParcelResponse> getUserParcels(UUID userId){
        return parcelRepository.findByUserId(userId).stream()
                .map(ParcelResponse::of)
                .toList();
    }

    @Override
    public List<ParcelResponse> getParcels(){
        return parcelRepository.findAll().stream()
                .map(ParcelResponse::of)
                .toList();
    }


    @Override
    @Transactional
    public ParcelResponse createParcel(UUID userId, ParcelRequest request) {
        var parcel = toEntity(request);
        var user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        parcel.setUser(user);
        if(request.requireCode() != null && request.requireCode()){
            parcel.setCodeOTP(CodeGeneratorUtil.generateParcelCode());
        }

        return ParcelResponse.of(parcelRepository.save(parcel));
    }

    @Override
    @Transactional
    public ParcelResponse updateParcel(UUID parcelId, UUID userId, ParcelRequest request) {
        Parcel parcel = getParcelByIdOrThrow(parcelId);
        assertOwnership(parcel, userId);
        assertParcelIsAvailable(parcel);
        parcelMapper.updateEntity(parcel, request);
        return ParcelResponse.of(parcelRepository.save(parcel));
    }


    @Override
    @Transactional
    public void deleteParcel(UUID parcelId, UUID userId) {
        var parcel = getParcelByIdOrThrow(parcelId);
        assertOwnership(parcel, userId);
        assertParcelIsAvailable(parcel);
        parcelRepository.delete(parcel);
    }



    //----------------- private methods-----------------------------

    private Parcel getParcelByIdOrThrow(UUID parcelId){
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

    private Parcel toEntity(ParcelRequest request) {
        return Parcel.builder()
                .size(request.size())
                .fragile(request.fragile())
                .description(request.description())
                .weightKg(request.weightKg())
                .deadlineDate(request.deadlineDate())
                .build();
    }

    private void update(Parcel parcel, ParcelRequest request) {
        parcel.setDescription(request.description());
        parcel.setWeightKg(request.weightKg());
        parcel.setSize(request.size());
        parcel.setFragile(request.fragile());
        parcel.setDeadlineDate(request.deadlineDate());
    }
}
