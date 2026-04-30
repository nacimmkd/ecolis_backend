package com.deliveryplatform.trips;

import com.deliveryplatform.addresses.GeocodedAddress;
import com.deliveryplatform.addresses.GeocodingService;
import com.deliveryplatform.common.exceptions.InvalidDomainStateException;
import com.deliveryplatform.common.exceptions.ResourceNotFoundException;
import com.deliveryplatform.common.exceptions.UnauthorizedActionException;
import com.deliveryplatform.images.ImageService;
import com.deliveryplatform.profiles.dto.ProfileSummaryResponse;
import com.deliveryplatform.trips.dto.TripStopRequest;
import com.deliveryplatform.trips.dto.TripStopResponse;
import com.deliveryplatform.trips.dto.TripCreateRequest;
import com.deliveryplatform.trips.dto.TripDetailedResponse;
import com.deliveryplatform.users.UserServiceImp;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TripServiceImp implements TripService {

    private final TripRepository tripRepository;
    private final TripStopRepository stopRepository;
    private final GeocodingService geocodingService;
    private final UserServiceImp userService;
    private final ImageService imageService;



    @Override
    public TripDetailedResponse getTrip(UUID tripId) {
        return buildTripResponse(getTripByIdOrThrow(tripId));
    }

    @Override
    public List<TripDetailedResponse> getUserTrips(UUID currentUserId) {
        return tripRepository.findByOwnerId(currentUserId).stream()
                .map(this::buildTripResponse)
                .toList();
    }

    @Override
    public List<TripDetailedResponse> getAllTrips() {
        return tripRepository.findAll().stream()
                .map(this::buildTripResponse)
                .toList();
    }

    @Override
    @Transactional
    public TripDetailedResponse createTrip(UUID currentUserId, TripCreateRequest request) {
        var owner = userService.getUserByIdOrThrow(currentUserId);

        var departure = geocodingService.geocode(request.departureAddress());
        var arrival   = geocodingService.geocode(request.arrivalAddress());
        var stops     = buildTripStopEntities(request.stops());

        var trip = buildTripEntity(request, departure, arrival, stops);
        trip.setOwner(owner);

        return buildTripResponse(tripRepository.save(trip));
    }

    @Override
    @Transactional
    public TripDetailedResponse updateTrip(UUID tripId, UUID currentUserId, TripCreateRequest request) {
        var trip = getTripByIdOrThrow(tripId);
        assertOwnership(trip, currentUserId);
        assertTripInStatusPublished(trip);
        validateStopsSequence(request.stops());

        var departure = geocodingService.geocode(request.departureAddress());
        var arrival   = geocodingService.geocode(request.arrivalAddress());
        var stops     = buildTripStopEntities(request.stops());

        trip.setDepartureAddress(departure);
        trip.setArrivalAddress(arrival);
        trip.setDepartureDate(request.departureDate());
        trip.setArrivalDate(request.arrivalDate());
        trip.setAvailableVolumeCm3(request.availableVolumeCm3());
        trip.setInstantBooking(request.instantBooking());
        trip.setMaxDetourKm(request.maxDetourKm());
        trip.setNotes(request.notes());
        trip.setAvailableWeightKg(request.availableWeightKg());
        trip.setPricePerKg(request.pricePerKg());
        trip.updateStops(stops);

        return buildTripResponse(tripRepository.save(trip));
    }

    @Override
    @Transactional
    public void deleteTrip(UUID tripId, UUID currentUserId) {
        var trip = getTripByIdOrThrow(tripId);
        assertOwnership(trip, currentUserId);
        assertTripInStatusPublished(trip);
        trip.softDelete();
        tripRepository.save(trip);
    }

    @Override
    @Transactional
    public TripStopResponse addStop(UUID tripId, UUID currentUserId, TripStopRequest request) {
        var trip = getTripByIdOrThrow(tripId);
        assertOwnership(trip, currentUserId);

        var geoAddress = geocodingService.geocode(request.address());
        var stop = TripStop.of(request, geoAddress);
        stop.setStopOrder(trip.getStops().size() + 1);

        trip.addStop(stop);
        return TripStopResponse.of(stopRepository.save(stop));
    }

    @Override
    @Transactional
    public TripStopResponse updateStop(UUID stopId, UUID tripId, UUID currentUserId, TripStopRequest request) {
        var trip = getTripByIdOrThrow(tripId);
        assertOwnership(trip, currentUserId);

        var stop       = findStopInTrip(trip, stopId);
        var geoAddress = geocodingService.geocode(request.address());
        stop.setAddress(geoAddress);

        return TripStopResponse.of(stopRepository.save(stop));
    }

    @Override
    @Transactional
    public void deleteStop(UUID stopId, UUID tripId, UUID currentUserId) {
        var trip = getTripByIdOrThrow(tripId);
        assertOwnership(trip, currentUserId);
        removeAndReorder(trip, stopId);
        tripRepository.save(trip);
    }


    // PRIVATE ————————————————————————————————————————————————————————————

    private TripDetailedResponse buildTripResponse(Trip trip) {
        var profile        = trip.getOwner().getProfile();
        var avatarUrl = profile.getAvatar() != null ? imageService.getReadUrl(profile.getAvatar().getId()) : null;
        var profileSummary = ProfileSummaryResponse.of(
                profile,
                avatarUrl
        );
        var stopsResponse = trip.getStops().stream()
                .map(TripStopResponse::of)
                .toList();

        return TripDetailedResponse.of(trip, profileSummary, stopsResponse);
    }

    private Trip buildTripEntity(TripCreateRequest request,
                                 GeocodedAddress departure,
                                 GeocodedAddress arrival,
                                 List<TripStop> stops) {
        return Trip.builder()
                .departureAddress(departure)
                .arrivalAddress(arrival)
                .departureDate(request.departureDate())
                .arrivalDate(request.arrivalDate())
                .availableVolumeCm3(request.availableVolumeCm3())
                .instantBooking(request.instantBooking())
                .maxDetourKm(request.maxDetourKm())
                .notes(request.notes())
                .availableWeightKg(request.availableWeightKg())
                .pricePerKg(request.pricePerKg())
                .status(TripStatus.PUBLISHED)
                .stops(stops)
                .build();
    }

    private List<TripStop> buildTripStopEntities(List<TripStopRequest> stops) {
        return stops.stream()
                .map(req -> TripStop.of(req, geocodingService.geocode(req.address())))
                .toList();
    }


    private Trip getTripByIdOrThrow(UUID id) {
        return tripRepository.findByIdWithStopsAndOwner(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found"));
    }

    private void assertOwnership(Trip trip, UUID userId) {
        if (!trip.getOwner().getId().equals(userId)) {
            throw new UnauthorizedActionException("User is not owner of this trip");
        }
    }

    private void assertTripInStatusPublished(Trip trip) {
        if (!trip.getStatus().equals(TripStatus.PUBLISHED)) {
            throw new InvalidDomainStateException("Trip is not published");
        }
    }

    private TripStop findStopInTrip(Trip trip, UUID stopId) {
        return trip.getStops().stream()
                .filter(s -> s.getId().equals(stopId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Trip stop"));
    }

    private void removeAndReorder(Trip trip, UUID stopId) {
        trip.removeStop(findStopInTrip(trip, stopId));
        trip.reorderStops();
    }

    private void validateStopsSequence(List<TripStopRequest> stops) {
        for (int i = 0; i < stops.size(); i++) {
            if (stops.get(i).stopOrder() != i + 1) {
                throw new InvalidDomainStateException("Trip stops must be in sequence order");
            }
        }
    }
}