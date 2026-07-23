package com.deliveryplatform.trips;

import com.deliveryplatform.addresses.AddressRequest;
import com.deliveryplatform.addresses.AddressService;
import com.deliveryplatform.bookings.Booking;
import com.deliveryplatform.bookings.BookingMapper;
import com.deliveryplatform.bookings.BookingRepository;
import com.deliveryplatform.bookings.dto.TripBookingDto;
import com.deliveryplatform.requests.RequestMapper;
import com.deliveryplatform.requests.RequestRepository;
import com.deliveryplatform.requests.dto.TripRequestDto;
import com.deliveryplatform.trips.dto.*;
import com.deliveryplatform.trips.exceptions.TripErrorCode;
import com.deliveryplatform.trips.exceptions.TripException;
import com.deliveryplatform.users.UserRepository;
import com.deliveryplatform.users.exceptions.UserErrorCode;
import com.deliveryplatform.users.exceptions.UserException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TripServiceImp implements TripService {

    private final TripRepository     tripRepository;
    private final UserRepository     userRepository;
    private final AddressService     addressService;
    private final BookingRepository  bookingRepository;
    private final RequestRepository  requestRepository;
    private final RequestMapper      requestMapper;
    private final TripMapper         tripMapper;
    private final BookingMapper      bookingMapper;

    @Override
    public TripDetails getTrip(UUID tripId) {
        var trip = getTripByIdOrThrow(tripId);
        var requestCount = requestRepository.countByTripId(tripId);
        var bookingsCount = requestRepository.countByTripId(tripId);
        return tripMapper.toTripDetailsDto(trip, requestCount, bookingsCount);
    }

    @Override
    public List<TripSummary> getAllTrips() {
        return tripMapper.toTripSummaryDto(tripRepository.findAll());
    }

    @Override
    public List<TripSummary> getMyTrips(UUID currentUserId) {
        return tripMapper.toTripSummaryDto(tripRepository.findByOwnerId(currentUserId));
    }

    @Override
    public List<TripBookingDto> getTripBookings(UUID tripId, UUID userId) {
        var trip = getTripByIdOrThrow(tripId);
        trip.assertOwnedBy(userId);

        List<Booking> bookings = bookingRepository.findByTripId(tripId);
        return bookingMapper.toTripBookingDto(bookings);
    }

    @Override
    public List<TripRequestDto> getTripRequests(UUID tripId, UUID currentUserId) {
        var trip = getTripByIdOrThrow(tripId);
        trip.assertOwnedBy(currentUserId);

        var requests = requestRepository.findByTripId(tripId);
        return requestMapper.toTripRequestDto(requests);
    }

    @Override
    @Transactional
    public TripDetails createTrip(UUID currentUserId, TripCreateRequest request) {
        var owner = userRepository.findUserWithProfileById(currentUserId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND, "trip owner not found"));

        var trip = Trip.createFromRequest(
                request,
                addressService.geocode(request.departureAddress()),
                addressService.geocode(request.arrivalAddress()),
                owner
        );
        return tripMapper.toTripDetailsDto(tripRepository.save(trip), 0, 0);
    }

    @Override
    @Transactional
    public TripDetails updateTrip(UUID tripId, UUID currentUserId, TripUpdateRequest request) {
        var trip = getTripByIdOrThrow(tripId);

        trip.update(
                currentUserId,
                addressService.geocode(request.departureAddress()),
                addressService.geocode(request.arrivalAddress()),
                request.departureDate(),
                request.arrivalDate(),
                request.availableWeightKg(),
                request.pricePerKg(),
                request.maxDetourKm(),
                request.notes(),
                request.instantBooking()
        );

        var requestCount = requestRepository.countByTripId(tripId);
        var bookingsCount = bookingRepository.countByTripId(tripId);

        tripRepository.save(trip);
        return tripMapper.toTripDetailsDto(trip, requestCount, bookingsCount);
    }

    @Override
    @Transactional
    public void deleteTrip(UUID tripId, UUID currentUserId) {
        var trip = getTripByIdOrThrow(tripId);
        trip.delete(currentUserId);
        tripRepository.save(trip);
    }

    @Override
    @Transactional
    public void addStop(UUID tripId, UUID currentUserId, AddressRequest address) {
        var trip = getTripByIdOrThrow(tripId);
        trip.addStop(currentUserId, addressService.geocode(address));
        tripRepository.save(trip);
    }

    @Override
    @Transactional
    public List<TripStopDto> updateStops(UUID tripId, UUID currentUserId, List<TripStopRequest> newStopsRequest) {
        var trip = getTripByIdOrThrow(tripId);

        var stops = newStopsRequest.stream()
                .map(request -> TripStop.create(addressService.geocode(request.address()), request.order()))
                .toList();

        var updatedStops = trip.updateStops(currentUserId, stops);
        tripRepository.save(trip);
        return tripMapper.toTripStopDto(updatedStops);
    }

    @Override
    @Transactional
    public void deleteStop(UUID stopId, UUID tripId, UUID currentUserId) {
        var trip = getTripByIdOrThrow(tripId);
        trip.removeStop(currentUserId, stopId);
        tripRepository.save(trip);
    }

    // ----------------------------------------------------------------

    private Trip getTripByIdOrThrow(UUID id) {
        return tripRepository.findTripById(id)
                .orElseThrow(() -> new TripException(TripErrorCode.TRIP_NOT_FOUND, "Trip Not found"));
    }
}