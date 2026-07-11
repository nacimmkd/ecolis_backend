package com.deliveryplatform.parcels;

import com.deliveryplatform.addresses.AddressService;
import com.deliveryplatform.bookings.Booking;
import com.deliveryplatform.bookings.BookingMapper;
import com.deliveryplatform.bookings.BookingRepository;
import com.deliveryplatform.bookings.dto.ParcelBookingDto;
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
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final ParcelMapper     parcelMapper;

    @Override
    public ParcelDetails getParcel(UUID id) {
        return parcelMapper.toDetailedDto(getParcelByIdOrThrow(id));
    }

    @Override
    public List<ParcelSummary> getUserParcels(UUID userId) {
        return parcelMapper.toSummaryDto(
                parcelRepository.findByOwnerId(userId)
        );
    }

    @Override
    public List<ParcelBookingDto> getParcelBookings(UUID parcelId, UUID currentUserId) {
        Parcel parcel = getParcelByIdOrThrow(parcelId);

        assertOwnership(parcel, currentUserId);

        List<Booking> bookings = bookingRepository.findByParcelId(parcelId);
        return bookingMapper.toParcelBookingDto(bookings);
    }

    @Override
    public List<ParcelSummary> getParcels() {
        return parcelMapper.toSummaryDto(
                parcelRepository.findAll()
        );
    }

    @Override
    @Transactional
    public ParcelDetails createParcel(UUID userId, ParcelCreateRequest request) {
        var owner  = getUserByIdOrThrow(userId);
        var parcel = Parcel.createFromRequest(
                request,
                owner,
                addressService.geocode(request.pickupAddress()),
                addressService.geocode(request.dropoffAddress()),
                imageService.getImage(request.thumbnailId(),owner),
                imageService.getImages(request.imageIds())
        );
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
        var parcel = parcelRepository.findParcelWithTrackingById(parcelId)
                .orElseThrow(() -> new ResourceNotFoundException("parcel not found"));
        return parcelMapper.toListTrackingEventDto(parcel.getTrackEvents());
    }

    // ----------------------------------------------------------------

    private Parcel getParcelByIdOrThrow(UUID id) {
        return parcelRepository.findParcelDetailsById(id)
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