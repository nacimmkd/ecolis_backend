package com.deliveryplatform.requests;

import com.deliveryplatform.bookings.Booking;
import com.deliveryplatform.bookings.BookingPriceCalculator;
import com.deliveryplatform.bookings.BookingRepository;
import com.deliveryplatform.common.CodeGeneratorUtil;
import com.deliveryplatform.common.exceptions.ConflictException;
import com.deliveryplatform.common.exceptions.InvalidDomainStateException;
import com.deliveryplatform.common.exceptions.ResourceNotFoundException;
import com.deliveryplatform.common.exceptions.UnauthorizedActionException;
import com.deliveryplatform.parcels.ParcelRepository;
import com.deliveryplatform.parcels.ParcelStatus;
import com.deliveryplatform.requests.dto.CreateRequest;
import com.deliveryplatform.requests.dto.RequestDto;
import com.deliveryplatform.trips.TripRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RequestServiceImp implements RequestService {

    private final BookingRequestRepository bookingRequestRepository;
    private final BookingRepository        bookingRepository;
    private final ParcelRepository         parcelRepository;
    private final TripRepository           tripRepository;
    private final RequestMapper            requestMapper;

    // ─────────────────────────────────────────────────────────────────────────

    @Override
    public RequestDto getBookingRequest(UUID requestId, UUID currentUserId) {
        var request = getRequestByIdOrThrow(requestId);
        assertInvolves(request.involves(currentUserId));
        return requestMapper.toRequestDto(request);
    }

    @Override
    public List<RequestDto> getBookingRequests(UUID currentUserId) {
        return requestMapper.toRequestDto(bookingRequestRepository.findAllByInvolvedUser(currentUserId));
    }

    @Override
    @Transactional
    public RequestDto createBookingRequest(CreateRequest dto, UUID senderId) {
        var parcel = parcelRepository.findById(dto.parcelId())
                .orElseThrow(() -> new ResourceNotFoundException("Parcel not found"));

        assertParcelOwner(parcel.getOwner().getId(), senderId);
        assertParcelAvailable(parcel.getStatus());

        var trip = tripRepository.findById(dto.tripId())
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found"));

        assertNoDuplicateRequest(dto.parcelId(), dto.tripId());

        var bookingRequest = BookingRequest.create(trip, parcel);

        if (trip.isInstantBooking()) {
            bookingRequest.accept();
            parcel.setStatus(ParcelStatus.BOOKED);
            bookingRequestRepository.save(bookingRequest);
            bookingRepository.save(buildBookingFromRequest(bookingRequest));
        } else {
            bookingRequestRepository.save(bookingRequest);
        }

        return requestMapper.toRequestDto(bookingRequest);
    }

    @Override
    @Transactional
    public void cancelRequest(UUID requestId, UUID userId) {
        var request = getRequestByIdOrThrow(requestId);
        assertInvolves(request.involves(userId));
        assertRequestIsPending(request);
        request.cancel();
        bookingRequestRepository.save(request);
    }

    @Override
    @Transactional
    public void acceptRequest(UUID requestId, UUID carrierId) {
        var request = getRequestByIdOrThrow(requestId);
        assertIsCarrier(request.getCarrierId(), carrierId);
        assertRequestIsPending(request);

        request.accept();
        request.getParcel().setStatus(ParcelStatus.BOOKED);
        bookingRequestRepository.save(request);
        bookingRepository.save(Booking.createFromRequest(request));
    }

    @Override
    @Transactional
    public void rejectRequest(UUID requestId, UUID carrierId, String reason) {
        var request = getRequestByIdOrThrow(requestId);
        assertIsCarrier(request.getCarrierId(), carrierId);
        assertRequestIsPending(request);
        request.reject(reason);
        bookingRequestRepository.save(request);
    }

    // PRIVATE ─────────────────────────────────────────────────────────────────

    private BookingRequest getRequestByIdOrThrow(UUID id) {
        return bookingRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking request not found"));
    }

    private void assertNoDuplicateRequest(UUID parcelId, UUID tripId) {
        if (bookingRequestRepository.existsByParcelIdAndTripId(parcelId, tripId))
            throw new ConflictException("A booking request already exists for this parcel and trip");
    }

    private void assertParcelOwner(UUID ownerId, UUID requesterId) {
        if (!ownerId.equals(requesterId))
            throw new UnauthorizedActionException("You are not the owner of this parcel");
    }

    private void assertParcelAvailable(ParcelStatus status) {
        if (!ParcelStatus.PUBLISHED.equals(status))
            throw new InvalidDomainStateException("Parcel is not available for booking");
    }

    private void assertIsCarrier(UUID carrierId, UUID currentUserId) {
        if (!carrierId.equals(currentUserId))
            throw new UnauthorizedActionException("You are not the carrier of this trip");
    }

    private void assertInvolves(boolean involves) {
        if (!involves)
            throw new UnauthorizedActionException("You are not involved in this booking");
    }

    private void assertRequestIsPending(BookingRequest request) {
        if (!request.isPending())
            throw new InvalidDomainStateException(
                    "Booking request is not pending, current status: " + request.getStatus()
            );
    }

    private Booking buildBookingFromRequest(BookingRequest request) {
        var booking = Booking.createFromRequest(request);
        booking.setPickupCode(CodeGeneratorUtil.generateParcelCode());
        booking.setDropOffCode(CodeGeneratorUtil.generateParcelCode());
        booking.setPrice(BookingPriceCalculator.calculate(request.getParcel(), request.getTrip()));
        return booking;
    }
}