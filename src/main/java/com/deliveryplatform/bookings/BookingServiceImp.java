package com.deliveryplatform.bookings;

import com.deliveryplatform.bookings.dto.BookingCreateRequest;
import com.deliveryplatform.bookings.dto.BookingRequestDto;
import com.deliveryplatform.bookings.dto.BookingDto;
import com.deliveryplatform.common.CodeGeneratorUtil;
import com.deliveryplatform.common.exceptions.ConflictException;
import com.deliveryplatform.common.exceptions.InvalidDomainStateException;
import com.deliveryplatform.common.exceptions.ResourceNotFoundException;
import com.deliveryplatform.common.exceptions.UnauthorizedActionException;
import com.deliveryplatform.parcels.ParcelRepository;
import com.deliveryplatform.parcels.ParcelStatus;
import com.deliveryplatform.trips.TripRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingServiceImp implements BookingService {

    private final BookingRepository        bookingRepository;
    private final BookingRequestRepository bookingRequestRepository;
    private final ParcelRepository         parcelRepository;
    private final TripRepository           tripRepository;
    private final BookingMapper            bookingMapper;

    // ─────────────────────────────────────────────────────────────────────────

    @Override
    public BookingRequestDto getBookingRequest(UUID requestId, UUID currentUserId) {
        var request = getRequestByIdOrThrow(requestId);
        assertInvolves(request.involves(currentUserId));
        return bookingMapper.toRequestDto(request);
    }

    @Override
    public List<BookingRequestDto> getBookingRequests(UUID currentUserId) {
        return bookingMapper.toRequestDto(bookingRequestRepository.findAllByInvolvedUser(currentUserId));
    }


    @Override
    @Transactional
    public BookingRequestDto createBookingRequest(BookingCreateRequest dto, UUID senderId) {
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

        return bookingMapper.toRequestDto(bookingRequest);
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

    @Override
    public BookingDto getBooking(UUID bookingId, UUID currentUserId) {
        var booking = getBookingByIdOrThrow(bookingId);
        assertInvolves(booking.involves(currentUserId));
        return bookingMapper.toDto(booking);
    }

    @Override
    public List<BookingDto> getMyBookings(UUID currentUserId) {
        return bookingMapper.toDto(bookingRepository.findAllByInvolvedUser(currentUserId));
    }

    @Override
    public List<BookingDto> getTripBookings(UUID tripId, UUID currentUserId) {
        var trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found"));

        assertIsCarrier(trip.getOwner().getId(), currentUserId);
        return bookingMapper.toDto(bookingRepository.findByTripId(tripId));
    }

    @Override
    public BookingDto getParcelBooking(UUID parcelId, UUID currentUserId) {
        var parcel = parcelRepository.findById(parcelId)
                .orElseThrow(() -> new ResourceNotFoundException("Parcel not found"));

        assertIsSender(parcel.getOwner().getId(), currentUserId);
        return bookingMapper.toDto(bookingRepository.findByParcelId(parcelId));
    }

    @Override
    @Transactional
    public void cancelBooking(UUID bookingId, UUID userId) {
        var booking = getBookingByIdOrThrow(bookingId);
        assertInvolves(booking.involves(userId));
        assertBookingInStatus(booking, BookingStatus.PENDING, "Only PENDING bookings can be cancelled");
        booking.cancel();
        booking.getParcel().setStatus(ParcelStatus.PUBLISHED);
        bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public void pay(UUID bookingId, UUID senderId) {
        var booking = getBookingByIdOrThrow(bookingId);
        assertIsSender(booking.getParcel().getOwner().getId(), senderId);
        assertBookingInStatus(booking, BookingStatus.PENDING, "Only PENDING bookings can be paid");
        booking.pay();
        bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public void complete(UUID bookingId, UUID carrierId) {
        var booking = getBookingByIdOrThrow(bookingId);
        assertIsCarrier(booking.getTrip().getOwner().getId(), carrierId);
        assertBookingInStatus(booking, BookingStatus.PAID, "Only PAID bookings can be completed");
        booking.complete();
        booking.getParcel().setStatus(ParcelStatus.DELIVERED);
        bookingRepository.save(booking);
    }

    // PRIVATE ─────────────────────────────────────────────────────────────────

    private Booking getBookingByIdOrThrow(UUID id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
    }

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

    private void assertIsSender(UUID senderId, UUID currentUserId) {
        if (!senderId.equals(currentUserId))
            throw new UnauthorizedActionException("You are not the sender of this booking");
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

    private void assertBookingInStatus(Booking booking, BookingStatus expected, String message) {
        if (!booking.getStatus().equals(expected))
            throw new InvalidDomainStateException(message);
    }

    private Booking buildBookingFromRequest(BookingRequest request) {
        var booking = Booking.createFromRequest(request);
        booking.setPickupCode(CodeGeneratorUtil.generateParcelCode());
        booking.setDropOffCode(CodeGeneratorUtil.generateParcelCode());
        booking.setPrice(BookingPriceCalculator.calculate(request.getParcel(), request.getTrip()));
        return booking;
    }
}