package com.deliveryplatform.requests;

import com.deliveryplatform.bookings.BookingService;
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

    private final RequestRepository        requestRepository;
    private final BookingService           bookingService;
    private final ParcelRepository         parcelRepository;
    private final TripRepository           tripRepository;
    private final RequestMapper            requestMapper;

    // ─────────────────────────────────────────────────────────────────────────

    @Override
    public RequestDto getRequest(UUID requestId, UUID currentUserId) {
        var request = getRequestByIdOrThrow(requestId);
        assertInvolves(request.involves(currentUserId));
        return requestMapper.toRequestDto(request);
    }

    @Override
    public List<RequestDto> getMySentRequests(UUID senderId) {
        var requests = requestRepository.findSentRequestsByUserId(senderId);
        return requestMapper.toRequestDto(requests);
    }

    @Override
    public List<RequestDto> getMyReceivedRequests(UUID carrierId) {
        var requests = requestRepository.findReceivedRequestsByUserId(carrierId);
        return requestMapper.toRequestDto(requests);
    }

    @Override
    public List<RequestDto> getMyTripRequests(UUID tripId, UUID currentUserId) {
        var trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found"));

        assertOwnership(trip.getOwner().getId(), currentUserId);
        var requests = requestRepository.findByTripId(tripId);
        return requestMapper.toRequestDto(requests);
    }

    @Override
    public List<RequestDto> getMyParcelRequests(UUID parcelId, UUID currentUserId) {
        var parcel = parcelRepository.findById(parcelId)
                        .orElseThrow(() -> new ResourceNotFoundException("Parcel not found"));

        assertOwnership(parcel.getOwner().getId(), currentUserId);
        var requests = requestRepository.findByParcelId(parcelId);
        return requestMapper.toRequestDto(requests);
    }

    @Override
    public List<RequestDto> getUserInvolvedRequests(UUID currentUserId) {
        return requestMapper.toRequestDto(requestRepository.findAllByInvolvedUser(currentUserId));
    }


    @Override
    @Transactional
    public RequestDto createRequest(CreateRequest dto, UUID senderId) {
        var parcel = parcelRepository.findById(dto.parcelId())
                .orElseThrow(() -> new ResourceNotFoundException("Parcel not found"));

        assertOwnership(parcel.getOwner().getId(), senderId);
        assertParcelAvailable(parcel.getStatus());

        var trip = tripRepository.findById(dto.tripId())
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found"));

        assertNoDuplicateRequest(dto.parcelId(), dto.tripId());

        var bookingRequest = Request.create(trip, parcel);

        if (trip.isInstantBooking()) {
            bookingRequest.accept();
            parcel.setStatus(ParcelStatus.BOOKED);
            bookingService.create(bookingRequest); // ccreate and save booking
            requestRepository.save(bookingRequest);
        } else {
            requestRepository.save(bookingRequest);
        }

        return requestMapper.toRequestDto(bookingRequest);
    }

    @Override
    @Transactional
    public void cancelRequest(UUID requestId, UUID currentUserId) {
        var request = getRequestByIdOrThrow(requestId);
        assertIsSender(request, currentUserId);
        assertRequestIsPending(request);
        request.cancel();
        requestRepository.save(request);
    }

    @Override
    @Transactional
    public void acceptRequest(UUID requestId, UUID carrierId) {
        var request = getRequestByIdOrThrow(requestId);
        assertIsCarrier(request, carrierId);
        assertRequestIsPending(request);

        request.accept();
        request.getParcel().setStatus(ParcelStatus.BOOKED);
        bookingService.create(request);
        requestRepository.save(request);
    }

    @Override
    @Transactional
    public void rejectRequest(UUID requestId, UUID carrierId, String reason) {
        var request = getRequestByIdOrThrow(requestId);
        assertIsCarrier(request, carrierId);
        assertRequestIsPending(request);
        request.reject(reason);
        requestRepository.save(request);
    }

    // PRIVATE ─────────────────────────────────────────────────────────────────

    private Request getRequestByIdOrThrow(UUID id) {
        return requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking request not found"));
    }

    private void assertNoDuplicateRequest(UUID parcelId, UUID tripId) {
        if (requestRepository.existsByParcelIdAndTripId(parcelId, tripId))
            throw new ConflictException("A booking request already exists for this parcel and trip");
    }

    private void assertOwnership(UUID ownerId, UUID requesterId) {
        if (!ownerId.equals(requesterId))
            throw new UnauthorizedActionException("You are not authorized to perform this action");
    }

    private void assertParcelAvailable(ParcelStatus status) {
        if (!ParcelStatus.PUBLISHED.equals(status))
            throw new InvalidDomainStateException("Parcel is not available for booking");
    }

    private void assertIsCarrier(Request request, UUID currentUserId) {
        if (!currentUserId.equals(request.getCarrierId()))
            throw new UnauthorizedActionException("you can no perform this action");
    }

    private void assertIsSender(Request request,  UUID currentUserId) {
        if(!currentUserId.equals(request.getSenderId()))
            throw new UnauthorizedActionException("You can perform this action");
    }

    private void assertInvolves(boolean involves) {
        if (!involves)
            throw new UnauthorizedActionException("You are not involved in this booking");
    }

    private void assertRequestIsPending(Request request) {
        if (!request.isPending())
            throw new InvalidDomainStateException(
                    "Booking request is not pending, current status: " + request.getStatus()
            );
    }

}