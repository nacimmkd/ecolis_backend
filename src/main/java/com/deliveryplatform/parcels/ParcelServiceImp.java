package com.deliveryplatform.parcels;

import com.deliveryplatform.addresses.AddressService;
import com.deliveryplatform.common.exceptions.InvalidDomainStateException;
import com.deliveryplatform.common.exceptions.ResourceNotFoundException;
import com.deliveryplatform.common.exceptions.UnauthorizedActionException;
import com.deliveryplatform.images.Image;
import com.deliveryplatform.images.ImageService;
import com.deliveryplatform.parcels.dto.*;
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
    private final AddressService   addressService;
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
        parcel.setThumbnail(
                request.thumbnailId() == null ? null : imageService.getImage(request.thumbnailId(),owner)
        );

        parcel.setPickupAddress(addressService.geocode(request.pickupAddress()));
        parcel.setDropoffAddress(addressService.geocode(request.dropoffAddress()));

        return parcelMapper.toDetailedDto(parcelRepository.save(parcel));
    }

    @Override
    @Transactional
    public ParcelDetails updateParcel(UUID parcelId, UUID userId, ParcelUpdateRequest request) {
        var parcel = getParcelByIdOrThrow(parcelId);
        assertOwnership(parcel, userId);
        assertParcelIsInState(parcel, List.of(ParcelState.PUBLISHED));

        parcel.setDescription(request.description());
        parcel.setWeightKg(request.weightKg());
        parcel.setSize(request.size());
        parcel.setFragile(request.fragile());
        parcel.setPickupAddress(addressService.geocode(request.pickupAddress()));
        parcel.setDropoffAddress(addressService.geocode(request.dropoffAddress()));

        updateThumbnail(parcel, request.thumbnailId());
        updateParcelImages(parcel, request.imageIds());

        return parcelMapper.toDetailedDto(parcelRepository.save(parcel));
    }

    @Override
    @Transactional
    public void deleteParcel(UUID parcelId, UUID userId) {
        var parcel = getParcelByIdOrThrow(parcelId);
        assertOwnership(parcel, userId);
        assertParcelIsInState(parcel, List.of(ParcelState.PUBLISHED));

        imageService.remove(parcel.getImages());
        parcel.removeAllImages();

        parcel.softDelete();
        parcelRepository.save(parcel);
    }

    @Override
    public List<TrackEventDto> getTrackingEvents(UUID parcelId) {
        var parcel = getParcelByIdOrThrow(parcelId);
        return parcelMapper.toListTrackingEventDto(parcel.getTrackEvents());
    }

    // ----------------------------------------------------------------

    private Parcel getParcelByIdOrThrow(UUID id) {
        return parcelRepository.findParcelWithImagesAndOwnerAndTrackingById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Parcel not found"));
    }

    private User getUserByIdOrThrow(UUID id) {
        return userRepository.findUserWithProfileById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private void assertOwnership(Parcel parcel, UUID userId) {
        if (!parcel.isOwner(userId))
            throw new UnauthorizedActionException("User is not owner of this parcel");
    }

    private void assertParcelIsInState(Parcel parcel, List<ParcelState> state) {
        if (!state.contains(parcel.getState()))
            throw new InvalidDomainStateException("Parcel is not in a valid state for this operation");
    }

    private void updateParcelImages(Parcel parcel, List<UUID> imageIds) {
        if (imageIds == null || parcel == null) return;

        if (imageIds.isEmpty()) {
            imageService.remove(parcel.getImages());
            parcel.removeAllImages();
        } else {
            List<Image> toDelete = parcel.getImages().stream()
                    .filter(img -> !imageIds.contains(img.getId()))
                    .toList();
            imageService.remove(toDelete);
            parcel.removeImages(toDelete);
            parcel.addImages(imageService.getImages(imageIds));
        }
    }

    private void updateThumbnail(Parcel parcel, UUID thumbnailId) {
        if (thumbnailId == null) {
            parcel.setThumbnail(null);
            return;
        }
        // if thumbnailId in not null, we update thumbnail with new one
        var image = imageService.getImage(thumbnailId, parcel.getOwner());
        parcel.setThumbnail(image);
    }
}