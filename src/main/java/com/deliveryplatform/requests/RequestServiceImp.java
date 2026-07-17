package com.deliveryplatform.requests;

import com.deliveryplatform.common.exceptions.ConflictException;
import com.deliveryplatform.common.exceptions.InvalidDomainStateException;
import com.deliveryplatform.common.exceptions.ResourceNotFoundException;
import com.deliveryplatform.common.exceptions.UnauthorizedActionException;
import com.deliveryplatform.matching.DetourCalculatorService;
import com.deliveryplatform.parcels.Parcel;
import com.deliveryplatform.parcels.ParcelRepository;
import com.deliveryplatform.requests.dto.CreateRequest;
import com.deliveryplatform.requests.dto.RequestDto;
import com.deliveryplatform.requests.events.RequestAcceptedEvent;
import com.deliveryplatform.requests.events.RequestCreatedEvent;
import com.deliveryplatform.trips.TripRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RequestServiceImp implements RequestService {

    private final RequestRepository        requestRepository;
    private final ParcelRepository         parcelRepository;
    private final TripRepository           tripRepository;
    private final DetourCalculatorService  detourCalculator;
    private final ApplicationEventPublisher eventPublisher;
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
    @Transactional
    public RequestDto createRequest(CreateRequest dto, UUID senderId) {
        var parcel = parcelRepository.findParcelSummaryById(dto.parcelId())
                .orElseThrow(() -> new ResourceNotFoundException("Parcel not found"));

        assertIsParcelOwner(parcel, senderId);
        assertRequestUniqueness(dto.parcelId(), dto.tripId());

        var trip = tripRepository.findTripById(dto.tripId())
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found"));

        var detour = detourCalculator.calculate(trip,parcel);

        var request = Request.create(trip, parcel, detour);
        requestRepository.save(request);
        if (trip.isInstantBooking()) {
            request.accept();
            eventPublisher.publishEvent(new RequestAcceptedEvent(request.getId()));
        }

        eventPublisher.publishEvent(new RequestCreatedEvent(request.getId(), request.getCarrier()));
        return requestMapper.toRequestDto(request);
    }

    private void assertRequestUniqueness(UUID parcelId, UUID tripId) {
        if (requestRepository.existsByParcelIdAndTripId(parcelId,tripId))
            throw new ConflictException("request for this trip and parcel already exists");
    }

    @Override
    @Transactional
    public void acceptRequest(UUID requestId, UUID carrierId) {
        var request = getRequestByIdOrThrow(requestId);

        assertIsCarrier(request, carrierId);
        assertRequestIsPending(request);

        request.accept();
        requestRepository.save(request);
        eventPublisher.publishEvent(new RequestAcceptedEvent(request.getId()));
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

    @Override
    @Transactional
    public void deleteRequest(UUID requestId, UUID currentUserId) {
        var request = getRequestByIdOrThrow(requestId);
        assertIsCarrier(request, currentUserId);
        assertRequestIsPending(request);
        request.delete();
        requestRepository.save(request);
    }

    // PRIVATE ─────────────────────────────────────────────────────────────────

    private Request getRequestByIdOrThrow(UUID id) {
        return requestRepository.findRequestById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking request not found"));
    }

    private void assertIsParcelOwner(Parcel parcel, UUID currentUserId) {
        if (!currentUserId.equals(parcel.getOwnerId()))
            throw new UnauthorizedActionException("You are not authorized to perform this action");
    }

    private void assertIsCarrier(Request request, UUID currentUserId) {
        if (!currentUserId.equals(request.getCarrierId()))
            throw new UnauthorizedActionException("you can no perform this action");
    }

    private void assertInvolves(boolean involves) {
        if (!involves)
            throw new UnauthorizedActionException("You are not involved in this booking");
    }

    private void assertRequestIsPending(Request request) {
        if (!request.isPending())
            throw new InvalidDomainStateException(
                    "Booking request is not pending, current state: " + request.getState()
            );
    }

}