package com.deliveryplatform.parcels;

import com.deliveryplatform.addresses.GeocodingService;
import com.deliveryplatform.common.CodeGeneratorUtil;
import com.deliveryplatform.common.exceptions.InvalidDomainStateException;
import com.deliveryplatform.common.exceptions.ResourceNotFoundException;
import com.deliveryplatform.common.exceptions.UnauthorizedActionException;
import com.deliveryplatform.images.Image;
import com.deliveryplatform.images.ImageService;
import com.deliveryplatform.parcels.dto.ParcelCreateRequest;
import com.deliveryplatform.parcels.dto.ParcelDetailedResponse;
import com.deliveryplatform.parcels.dto.ParcelResponse;
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
    private final GeocodingService geocodingService;
    private final ImageService     imageService;

    @Override
    public ParcelDetailedResponse getParcel(UUID id) {
        var parcel = getParcelByIdOrThrow(id);
        return toDetailedResponse(parcel);
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
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<ParcelResponse> getParcels() {
        return parcelRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public ParcelDetailedResponse createParcel(UUID userId, ParcelCreateRequest request) {
        var user   = getUserByIdOrThrow(userId);
        var parcel = toEntity(request);
        parcel.setUser(user);

        updateParcelThumbnail(parcel, request.thumbnailImageId());
        updateParcelImages(parcel, request.imageIds());

        return toDetailedResponse(parcelRepository.save(parcel));
    }

    @Override
    @Transactional
    public ParcelDetailedResponse updateParcel(UUID parcelId, UUID userId, ParcelUpdateRequest request) {
        var parcel = getParcelByIdOrThrow(parcelId);
        assertOwnership(parcel, userId);
        assertParcelIsAvailable(parcel);

        applyUpdates(parcel, request);
        updateParcelThumbnail(parcel, request.thumbnailImageId());
        updateParcelImages(parcel, request.imageIds());

        return toDetailedResponse(parcelRepository.save(parcel));
    }

    @Override
    @Transactional
    public void deleteParcel(UUID parcelId, UUID userId) {
        var parcel = getParcelByIdOrThrow(parcelId);
        assertOwnership(parcel, userId);
        assertParcelIsAvailable(parcel);
        parcelRepository.delete(parcel);
    }

    // ----------------------------------------------------------------

    private ParcelResponse toResponse(Parcel parcel) {
        var thumbnailUrl = resolveThumbnailUrl(parcel);
        return ParcelResponse.of(parcel, thumbnailUrl);
    }

    private ParcelDetailedResponse toDetailedResponse(Parcel parcel) {
        var thumbnailUrl = resolveThumbnailUrl(parcel);
        var imageUrls    = resolveImageUrls(parcel);
        return ParcelDetailedResponse.of(parcel, thumbnailUrl, imageUrls);
    }

    private String resolveThumbnailUrl(Parcel parcel) {
        var thumbnail = parcel.getThumbnailImage();
        if (thumbnail == null || !thumbnail.isConfirmed()) return null;
        return imageService.getReadUrl(thumbnail.getId());
    }

    private List<String> resolveImageUrls(Parcel parcel) {
        if (parcel.getImages() == null) return List.of();
        return parcel.getImages().stream()
                .filter(Image::isConfirmed)
                .map(image -> imageService.getReadUrl(image.getId()))
                .toList();
    }

    private Parcel toEntity(ParcelCreateRequest request) {
        return Parcel.builder()
                .description(request.description())
                .weightKg(request.weightKg())
                .size(request.size())
                .fragile(request.fragile())
                .deadlineDate(request.deadlineDate())
                .codeOTP(Boolean.TRUE.equals(request.requireCode())
                        ? CodeGeneratorUtil.generateParcelCode()
                        : null)
                .pickupAddress(geocodingService.geocode(request.pickupAddress()))
                .dropoffAddress(geocodingService.geocode(request.dropoffAddress()))
                .build();
    }

    private void applyUpdates(Parcel parcel, ParcelUpdateRequest request) {
        if (request.description()   != null) parcel.setDescription(request.description());
        if (request.weightKg()      != null) parcel.setWeightKg(request.weightKg());
        if (request.size()          != null) parcel.setSize(request.size());
        if (request.fragile()       != null) parcel.setFragile(request.fragile());
        if (request.deadlineDate()  != null) parcel.setDeadlineDate(request.deadlineDate());
        if (request.pickupAddress() != null) parcel.setPickupAddress(geocodingService.geocode(request.pickupAddress()));
        if (request.dropoffAddress()!= null) parcel.setDropoffAddress(geocodingService.geocode(request.dropoffAddress()));

        if (request.requireCode() != null) {
            if (Boolean.TRUE.equals(request.requireCode()) && parcel.getCodeOTP() == null) {
                parcel.setCodeOTP(CodeGeneratorUtil.generateParcelCode());
            } else if (Boolean.FALSE.equals(request.requireCode())) {
                parcel.setCodeOTP(null);
            }
        }
    }

    private void updateParcelThumbnail(Parcel parcel, UUID thumbnailImageId) {
        if (thumbnailImageId == null) return;
        var image = imageService.getImageEntity(thumbnailImageId);
        if (image.isConfirmed()) parcel.setThumbnailImage(image);
    }

    private void updateParcelImages(Parcel parcel, List<UUID> imageIds) {
        if (imageIds == null) return;
        if (imageIds.isEmpty()) {
            parcel.clearImages();
            return;
        }
        var confirmedImages = imageService.getImageEntities(imageIds).stream()
                .filter(Image::isConfirmed)
                .toList();
        parcel.setImages(confirmedImages);
    }

    private Parcel getParcelByIdOrThrow(UUID id) {
        return parcelRepository.findParcelWithImagesById(id)
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

    private void assertParcelIsAvailable(Parcel parcel) {
        if (!parcel.isAvailable())
            throw new InvalidDomainStateException("Parcel is not in a valid state for this operation");
    }
}