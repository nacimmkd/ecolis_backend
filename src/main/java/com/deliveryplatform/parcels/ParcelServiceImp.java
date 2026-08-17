package com.deliveryplatform.parcels;

import com.deliveryplatform.addresses.AddressService;
import com.deliveryplatform.bookings.Booking;
import com.deliveryplatform.bookings.BookingRepository;
import com.deliveryplatform.parcels.dto.ParcelBookingDto;
import com.deliveryplatform.parcels.dto.*;
import com.deliveryplatform.parcels.exceptions.ParcelErrorCode;
import com.deliveryplatform.parcels.exceptions.ParcelException;
import com.deliveryplatform.storage.MediaType;
import com.deliveryplatform.storage.StorageService;
import com.deliveryplatform.storage.exceptions.StorageErrorCode;
import com.deliveryplatform.storage.exceptions.StorageException;
import com.deliveryplatform.users.User;
import com.deliveryplatform.users.UserRepository;
import com.deliveryplatform.users.exceptions.UserErrorCode;
import com.deliveryplatform.users.exceptions.UserException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ParcelServiceImp implements ParcelService {

    private final ParcelRepository      parcelRepository;
    private final UserRepository        userRepository;
    private final AddressService        addressService;
    private final StorageService        storageService;
    private final BookingRepository     bookingRepository;
    private final ParcelBookingMapper   parcelBookingsMapper;
    private final ParcelMapper          parcelMapper;
    private final ParcelImageMapper     parcelImageMapper;

    @Override
    public ParcelDetails getParcel(UUID parcelId) {
        var parcel = getParcelByIdOrThrow(parcelId);
        return parcelMapper.toDetailedDto(parcel, getParcelBookings(parcelId));
    }

    @Override
    public Page<ParcelSummary> getUserParcels(UUID userId, ParcelState state, Pageable pageable) {
        Page<Parcel> parcels = (state == null)
                ? parcelRepository.findByOwnerId(userId, pageable)
                : parcelRepository.findByOwnerIdAndState(userId, state, pageable);

        return parcels.map(parcelMapper::toSummaryDto);
    }

    @Override
    public List<ParcelBookingDto> getParcelBookings(UUID parcelId, UUID currentUserId) {
        Parcel parcel = getParcelByIdOrThrow(parcelId);
        var bookings = bookingRepository.findByParcelIdOrderByCreatedAtDesc(parcelId);
        parcel.assertOwnedBy(currentUserId);
        return parcelBookingsMapper.toParcelBookingDto(bookings);
    }

    @Override
    public Page<ParcelSummary> getParcels(Pageable pageable) {
        return parcelRepository.findAll(pageable)
                .map(parcelMapper::toSummaryDto);
    }

    @Override
    @Transactional
    public ParcelDetails createParcel(UUID userId, ParcelCreateRequest request) {
        var owner  = getUserByIdOrThrow(userId);
        var parcel = Parcel.createFromRequest(
                request,
                owner,
                addressService.geocode(request.pickupAddress()),
                addressService.geocode(request.dropoffAddress())
        );
        parcelRepository.save(parcel);
        return parcelMapper.toDetailedDto(parcel, List.of());
    }

    @Override
    @Transactional
    public ParcelDetails updateParcel(UUID parcelId, UUID userId, ParcelUpdateRequest request) {
        var parcel = getParcelByIdOrThrow(parcelId);

        parcel.assertOwnedBy(userId);
        parcel.assertIsInState(List.of(ParcelState.PUBLISHED));

        parcel.setTitle(request.title());
        parcel.setWeightKg(request.weightKg());
        parcel.setSize(request.size());
        parcel.setFragile(request.fragile());
        parcel.setPickupAddress(addressService.geocode(request.pickupAddress()));
        parcel.setDropoffAddress(addressService.geocode(request.dropoffAddress()));

        parcelRepository.save(parcel);
        return parcelMapper.toDetailedDto(parcel, getParcelBookings(parcelId));
    }

    @Override
    @Transactional
    public void deleteParcel(UUID parcelId, UUID userId) {
        var parcel = getParcelByIdOrThrow(parcelId);
        parcel.assertOwnedBy(userId);
        parcel.assertIsInState(List.of(ParcelState.PUBLISHED));

        parcel.getImages().forEach(image -> storageService.delete(image.getKey()));
        parcel.removeAllImages();

        parcel.softDelete();
        parcelRepository.save(parcel);
    }

    @Override
    public List<TrackEventDto> getTrackingEvents(UUID parcelId) {
        var parcel = parcelRepository.findParcelWithTrackingById(parcelId)
                .orElseThrow(() -> new ParcelException(ParcelErrorCode.PARCEL_NOT_FOUND, "Parcel not found"));
        return parcelMapper.toListTrackingEventDto(parcel.getTrackEvents());
    }

    @Override
    @Transactional
    public ParcelImageDto addParcelImage(UUID parcelId, UUID userId, ParcelImageRequest request) {
        var parcel = getParcelByIdOrThrow(parcelId);
        parcel.assertOwnedBy(userId);

        var mediaType = resolveMediaType(request.contentType());
        assertExistsInStorage(request.key());

        var image = ParcelImage.create(mediaType, request.key());
        parcel.addImage(image);
        parcelRepository.save(parcel);

        return parcelImageMapper.toDto(image);
    }

    @Override
    @Transactional
    public void removeParcelImage(UUID parcelId, UUID imageId, UUID userId) {
        var parcel = getParcelByIdOrThrow(parcelId);
        parcel.assertOwnedBy(userId);

        var image = parcel.getImages().stream()
                .filter(img -> img.getId().equals(imageId))
                .findFirst()
                .orElseThrow(() -> new ParcelException(ParcelErrorCode.PARCEL_IMAGE_NOT_FOUND, "Parcel image not found"));

        storageService.delete(image.getKey());
        parcel.removeImage(image);
        parcelRepository.save(parcel);
    }

    // ----------------------------------------------------------------

    private Parcel getParcelByIdOrThrow(UUID id) {
        return parcelRepository.findParcelDetailsById(id)
                .orElseThrow(() -> new ParcelException(ParcelErrorCode.PARCEL_NOT_FOUND, "Parcel not found"));
    }

    private User getUserByIdOrThrow(UUID id) {
        return userRepository.findUserById(id)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND, "User not found"));
    }

    private List<Booking> getParcelBookings(UUID parcelId) {
        return bookingRepository.findByParcelIdOrderByCreatedAtDesc(parcelId);
    }

    private MediaType resolveMediaType(String content) {
        return MediaType.of(content)
                .orElseThrow(() -> new StorageException(StorageErrorCode.INVALID_MEDIA_TYPE, "Content type not supported"));
    }

    private void assertExistsInStorage(String key) {
        if (!storageService.exists(key)) {
            throw new StorageException(StorageErrorCode.FILE_NOT_FOUND, "Image not found : " + key);
        }
    }
}