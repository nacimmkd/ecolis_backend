package com.deliveryplatform.parcels;

import com.deliveryplatform.addresses.AddressService;
import com.deliveryplatform.common.exceptions.InvalidDomainStateException;
import com.deliveryplatform.common.exceptions.ResourceNotFoundException;
import com.deliveryplatform.common.exceptions.UnauthorizedActionException;
import com.deliveryplatform.images.Image;
import com.deliveryplatform.images.ImageService;
import com.deliveryplatform.parcels.dto.ParcelCreateRequest;
import com.deliveryplatform.parcels.dto.ParcelDetails;
import com.deliveryplatform.parcels.dto.ParcelSummary;
import com.deliveryplatform.parcels.dto.ParcelUpdateRequest;
import com.deliveryplatform.users.User;
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
    private final UserRepository   userRepository;
    private final AddressService addressService;
    private final ImageService     imageService;
    private final ParcelMapper     parcelMapper;

    @Override
    public ParcelDetails getParcel(UUID id) {
        return parcelMapper.toDetailedDto(getParcelByIdOrThrow(id));
    }

    @Override
    public List<ParcelSummary> getUserParcels(UUID userId) {
        return parcelRepository.findWithOwnerByUserId(userId).stream()
                .map(parcelMapper::toSummaryDto)
                .toList();
    }

    @Override
    public List<ParcelSummary> getParcels() {
        return parcelRepository.findAll().stream()
                .map(parcelMapper::toSummaryDto)
                .toList();
    }

    @Override
    @Transactional
    public ParcelDetails createParcel(UUID userId, ParcelCreateRequest request) {

        var owner  = getUserByIdOrThrow(userId);
        var parcel = parcelMapper.toEntity(request);

        parcel.setOwner(owner);
        parcel.addImages(imageService.getImages(request.imageIds()));

        parcel.setPickupAddress(addressService.geocode(request.pickupAddress()));
        parcel.setDropoffAddress(addressService.geocode(request.dropoffAddress()));

        return parcelMapper.toDetailedDto(parcelRepository.save(parcel));
    }

    @Override
    @Transactional
    public ParcelDetails updateParcel(UUID parcelId, UUID userId, ParcelUpdateRequest request) {
        var parcel = getParcelByIdOrThrow(parcelId);
        assertOwnership(parcel, userId);
        assertParcelIsInState(parcel, List.of(ParcelStatus.PUBLISHED));
        applyUpdates(parcel, request);
        return parcelMapper.toDetailedDto(parcelRepository.save(parcel));
    }

    @Override
    @Transactional
    public void deleteParcel(UUID parcelId, UUID userId) {
        var parcel = getParcelByIdOrThrow(parcelId);
        assertOwnership(parcel, userId);
        assertParcelIsInState(parcel, List.of(ParcelStatus.PUBLISHED));

        imageService.remove(parcel.getImages());
        parcel.removeAllImages();

        parcel.softDelete();
        parcelRepository.save(parcel);
    }

    // ----------------------------------------------------------------


    private void applyUpdates(Parcel parcel, ParcelUpdateRequest request) {
        if (request.description()    != null) parcel.setDescription(request.description());
        if (request.weightKg()       != null) parcel.setWeightKg(request.weightKg());
        if (request.size()           != null) parcel.setSize(request.size());
        if (request.fragile()        != null) parcel.setFragile(request.fragile());
        if (request.pickupAddress()  != null) parcel.setPickupAddress(addressService.geocode(request.pickupAddress()));
        if (request.dropoffAddress() != null) parcel.setDropoffAddress(addressService.geocode(request.dropoffAddress()));
        updateParcelImages(parcel, request.imageIds());
    }

    private void updateParcelImages(Parcel parcel, List<UUID> imageIds) {
        if (imageIds == null) return;

        if (!imageIds.isEmpty()) {
            List<Image> toDelete = parcel.getImages().stream()
                    .filter(img -> !imageIds.contains(img.getId()))
                    .toList();
            imageService.remove(toDelete);
            parcel.removeImages(toDelete);
        } else {
            imageService.remove(parcel.getImages());
            parcel.removeAllImages();
        }

        parcel.removeAllImages();
        if (!imageIds.isEmpty()) {
            parcel.addImages(imageService.getImages(imageIds));
        }
    }

    private Parcel getParcelByIdOrThrow(UUID id) {
        return parcelRepository.findParcelWithImagesAndOwnerById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Parcel not found"));
    }

    private User getUserByIdOrThrow(UUID id) {
        return userRepository.findWithProfileById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private void assertOwnership(Parcel parcel, UUID userId) {
        if (!parcel.isOwner(userId))
            throw new UnauthorizedActionException("User is not owner of this parcel");
    }

    private void assertParcelIsInState(Parcel parcel, List<ParcelStatus> state) {
        if (!state.contains(parcel.getStatus()))
            throw new InvalidDomainStateException("Parcel is not in a valid state for this operation");
    }
}