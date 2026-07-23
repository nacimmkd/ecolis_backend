package com.deliveryplatform.requests;

import com.deliveryplatform.matching.DetourCalculatorService;
import com.deliveryplatform.parcels.ParcelRepository;
import com.deliveryplatform.parcels.exceptions.ParcelErrorCode;
import com.deliveryplatform.parcels.exceptions.ParcelException;
import com.deliveryplatform.requests.dto.CreateRequest;
import com.deliveryplatform.requests.dto.RequestDto;
import com.deliveryplatform.requests.events.RequestAcceptedEvent;
import com.deliveryplatform.requests.events.RequestCreatedEvent;
import com.deliveryplatform.requests.exceptions.RequestErrorCode;
import com.deliveryplatform.requests.exceptions.RequestException;
import com.deliveryplatform.trips.TripRepository;
import com.deliveryplatform.trips.exceptions.TripErrorCode;
import com.deliveryplatform.trips.exceptions.TripException;
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
        request.assertInvolves(currentUserId);
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
                .orElseThrow(() -> new ParcelException(ParcelErrorCode.PARCEL_NOT_FOUND, "Parcel not found"));

        parcel.assertOwnedBy(senderId);
        assertRequestUniqueness(dto.parcelId(), dto.tripId());

        var trip = tripRepository.findTripById(dto.tripId())
                .orElseThrow(() -> new TripException(TripErrorCode.TRIP_NOT_OWNED, "Trip not found"));

        var detour = detourCalculator.calculate(trip, parcel);

        var request = Request.create(trip, parcel, detour);
        requestRepository.save(request);

        if (trip.isInstantBooking()) {
            request.accept();
            eventPublisher.publishEvent(new RequestAcceptedEvent(request.getId()));
        }

        eventPublisher.publishEvent(new RequestCreatedEvent(
                request.getId(),
                request.getCarrier(),
                trip.getDepartureAddress().getCity(),
                trip.getArrivalAddress().getCity()
        ));

        return requestMapper.toRequestDto(request);
    }

    private void assertRequestUniqueness(UUID parcelId, UUID tripId) {
        if (requestRepository.existsByParcelIdAndTripId(parcelId, tripId))
            throw new RequestException(RequestErrorCode.REQUEST_ALREADY_EXISTS, "Request for this trip and parcel already exists");
    }

    @Override
    @Transactional
    public void acceptRequest(UUID requestId, UUID currentUserId) {
        var request = getRequestByIdOrThrow(requestId);

        request.assertIsCarrier(currentUserId);
        request.assertIsPending();

        request.accept();
        requestRepository.save(request);
        eventPublisher.publishEvent(new RequestAcceptedEvent(request.getId()));
    }

    @Override
    @Transactional
    public void rejectRequest(UUID requestId, UUID carrierId, String reason) {
        var request = getRequestByIdOrThrow(requestId);
        request.assertIsCarrier(carrierId);
        request.assertIsPending();
        request.reject(reason);
        requestRepository.save(request);
    }

    @Override
    @Transactional
    public void deleteRequest(UUID requestId, UUID currentUserId) {
        var request = getRequestByIdOrThrow(requestId);
        request.assertIsCarrier(currentUserId);
        request.assertIsPending();
        request.delete();
        requestRepository.save(request);
    }

    // PRIVATE ─────────────────────────────────────────────────────────────────

    private Request getRequestByIdOrThrow(UUID id) {
        return requestRepository.findRequestById(id)
                .orElseThrow(() -> new RequestException(RequestErrorCode.REQUEST_NOT_FOUND, "Booking request not found"));
    }
}