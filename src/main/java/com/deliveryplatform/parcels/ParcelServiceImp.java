package com.deliveryplatform.parcels;

import com.deliveryplatform.addresses.GeocodingService;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParcelServiceImp implements ParcelService {

    private final ParcelRepository parcelRepository;
    private final UserRepository   userRepository;
    private final GeocodingService geocodingService;
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
        updateParcelThumbnail(parcel, request.thumbnailImageId());
        updateParcelImages(parcel, request.imageIds());

        return parcelMapper.toDetailedDto(parcelRepository.save(parcel));
    }

    @Override
    @Transactional
    public ParcelDetails updateParcel(UUID parcelId, UUID userId, ParcelUpdateRequest request) {
        var parcel = getParcelByIdOrThrow(parcelId);
        assertOwnership(parcel, userId);
        assertParcelIsInStatusAvailable(parcel);

        applyUpdates(parcel, request);
        updateParcelThumbnail(parcel, request.thumbnailImageId());
        updateParcelImages(parcel, request.imageIds());

        return parcelMapper.toDetailedDto(parcelRepository.save(parcel));
    }

    @Override
    @Transactional
    public void deleteParcel(UUID parcelId, UUID userId) {
        var parcel = getParcelByIdOrThrow(parcelId);
        assertOwnership(parcel, userId);
        assertParcelIsInStatusAvailable(parcel);

        var imageIds = parcel.getImages().stream()
                .map(Image::getId)
                .collect(Collectors.toList());

        if (parcel.getThumbnailImage() != null)
            imageIds.add(parcel.getThumbnailImage().getId());

        parcel.softDelete();
        imageService.removeAll(imageIds);
        parcelRepository.save(parcel);
    }

    // ----------------------------------------------------------------


    private void applyUpdates(Parcel parcel, ParcelUpdateRequest request) {
        if (request.description()    != null) parcel.setDescription(request.description());
        if (request.weightKg()       != null) parcel.setWeightKg(request.weightKg());
        if (request.size()           != null) parcel.setSize(request.size());
        if (request.fragile()        != null) parcel.setFragile(request.fragile());
        if (request.pickupAddress()  != null) parcel.setPickupAddress(geocodingService.geocode(request.pickupAddress()));
        if (request.dropoffAddress() != null) parcel.setDropoffAddress(geocodingService.geocode(request.dropoffAddress()));
    }

    private void updateParcelThumbnail(Parcel parcel, UUID thumbnailImageId) {
        if (thumbnailImageId == null) return;
        var image = imageService.getImageEntity(thumbnailImageId);
        if (image.isConfirmed()) parcel.setThumbnailImage(image);
    }

    private void updateParcelImages(Parcel parcel, List<UUID> imageIds) {
        if (imageIds == null) return;
        parcel.clearImages();
        imageService.getImageEntities(imageIds).forEach(image -> {
            if (image.isConfirmed()) parcel.addImage(image);
        });
    }

    private Parcel getParcelByIdOrThrow(UUID id) {
        return parcelRepository.findParcelWithImagesAndOwnerById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Parcel not found"));
    }

    private User getUserByIdOrThrow(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private void assertOwnership(Parcel parcel, UUID userId) {
        if (!parcel.isOwner(userId))
            throw new UnauthorizedActionException("User is not owner of this parcel");
    }

    private void assertParcelIsInStatusAvailable(Parcel parcel) {
        if (!parcel.isAvailable())
            throw new InvalidDomainStateException("Parcel is not in a valid state for this operation");
    }
}