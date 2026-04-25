package com.deliveryplatform.parcels;

import com.deliveryplatform.addresses.GeocodingService;
import com.deliveryplatform.common.CodeGeneratorUtil;
import com.deliveryplatform.common.exceptions.InvalidDomainStateException;
import com.deliveryplatform.common.exceptions.ResourceNotFoundException;
import com.deliveryplatform.common.exceptions.UnauthorizedActionException;
import com.deliveryplatform.parcels.dto.ParcelCreateRequest;
import com.deliveryplatform.parcels.dto.ParcelResponse;
import com.deliveryplatform.parcels.dto.ParcelUpdateRequest;
import com.deliveryplatform.users.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ParcelServiceImp implements ParcelService {

    private final ParcelRepository parcelRepository;
    private final UserRepository userRepository;
    private final GeocodingService geocodingService;

    @Override
    public ParcelResponse getParcel(UUID id) {
        var parcel = getParcelByIdOrThrow(id);
        return ParcelResponse.of(parcel);
    }

    @Override
    public String getConfirmationCode(UUID parcelId, UUID userId) {
        var parcel = getParcelByIdOrThrow(parcelId);
        assertOwnership(parcel, userId);
        return parcel.getCodeOTP();
    }

    @Override
    public List<ParcelResponse> getUserParcels(UUID userId) {
        return parcelRepository.findByUserId(userId).stream()
                .map(ParcelResponse::of)
                .toList();
    }

    @Override
    public List<ParcelResponse> getParcels() {
        return parcelRepository.findAll().stream()
                .map(ParcelResponse::of)
                .toList();
    }


    @Override
    @Transactional
    public ParcelResponse createParcel(UUID userId, ParcelCreateRequest request) {
        var parcel = toEntity(request);
        var user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        parcel.setUser(user);
        if (request.requireCode() != null && request.requireCode()) {
            parcel.setCodeOTP(CodeGeneratorUtil.generateParcelCode());
        }

        return ParcelResponse.of(parcelRepository.save(parcel));
    }

    @Override
    @Transactional
    public ParcelResponse updateParcel(UUID parcelId, UUID userId, ParcelUpdateRequest request) {
        Parcel parcel = getParcelByIdOrThrow(parcelId);
        assertOwnership(parcel, userId);
        assertParcelIsAvailable(parcel);
        updateParcel(parcel, request);
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

    private Parcel getParcelByIdOrThrow(UUID parcelId) {
        return parcelRepository.findById(parcelId)
                .orElseThrow(() -> new ResourceNotFoundException("Parcel"));
    }


    private void assertOwnership(Parcel parcel, UUID userId) {
        if (!parcel.isOwner(userId)) {
            throw new UnauthorizedActionException("User is not owner of this parcel");
        }
    }

    private void assertParcelIsAvailable(Parcel parcel) {
        if (!parcel.isAvailable()) {
            throw new InvalidDomainStateException("Parcel is not in a valid state for this operation");
        }
    }

    private Parcel toEntity(ParcelCreateRequest request) {
        return Parcel.builder()
                .size(request.size())
                .fragile(request.fragile())
                .description(request.description())
                .weightKg(request.weightKg())
                .deadlineDate(request.deadlineDate())
                .codeOTP(Boolean.TRUE.equals(request.requireCode()) ? CodeGeneratorUtil.generateParcelCode() : null)
                .pickupAddress(
                        geocodingService.geocode(request.pickupAddress())
                )
                .dropoffAddress(
                        geocodingService.geocode(request.dropoffAddress())
                )
                .build();
    }

    private void updateParcel(Parcel parcel, ParcelUpdateRequest request) {
        if (request.description() != null) parcel.setDescription(request.description());
        if (request.weightKg() != null) parcel.setWeightKg(request.weightKg());
        if (request.size() != null) parcel.setSize(request.size());
        if (request.fragile() != null) parcel.setFragile(request.fragile());
        if (request.deadlineDate() != null) parcel.setDeadlineDate(request.deadlineDate());

        if (request.pickupAddress() != null) parcel.setPickupAddress(geocodingService.geocode(request.pickupAddress()));
        if (request.dropoffAddress() != null)
            parcel.setDropoffAddress(geocodingService.geocode(request.dropoffAddress()));

        if (request.requireCode() != null) {
            if (request.requireCode() && parcel.getCodeOTP() == null) {
                parcel.setCodeOTP(CodeGeneratorUtil.generateParcelCode());
            } else if (!request.requireCode()) {
                parcel.setCodeOTP(null);
            }
        }
    }
}
