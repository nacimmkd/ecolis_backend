package com.deliveryplatform.common;

import com.deliveryplatform.auth.AuthenticationSessionException;
import com.deliveryplatform.bookings.exceptions.BookingAlreadyExistsException;
import com.deliveryplatform.bookings.exceptions.BookingNotFoundException;
import com.deliveryplatform.bookings.exceptions.UnauthorizedBookingActionException;
import com.deliveryplatform.parcels.exceptions.IllegalParcelStateException;
import com.deliveryplatform.parcels.exceptions.ParcelNotFoundException;
import com.deliveryplatform.parcels.exceptions.UnauthorizedParcelActionException;
import com.deliveryplatform.trips.exceptions.*;
import com.deliveryplatform.users.exceptions.EmailAlreadyExistsException;
import com.deliveryplatform.users.exceptions.PasswordNotValidException;
import com.deliveryplatform.users.exceptions.UserNotFoundException;
import com.deliveryplatform.common.validations.ValidationError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    // USER Exceptions
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiError> handleUserNotFoundException(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiError(ex.getMessage()));
    }

    @ExceptionHandler(PasswordNotValidException.class)
    public ResponseEntity<ApiError> handlePasswordNotValidException(PasswordNotValidException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiError(ex.getMessage()));
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleEmailAlreadyExistsException(EmailAlreadyExistsException    ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiError(ex.getMessage()));
    }


    // PARCEL Exceptions
    @ExceptionHandler(ParcelNotFoundException.class)
    public ResponseEntity<ApiError> handleParcelNotFoundException(ParcelNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiError(ex.getMessage()));
    }

    @ExceptionHandler(UnauthorizedParcelActionException.class)
    public ResponseEntity<ApiError> handleUnauthorizedActionException(UnauthorizedParcelActionException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiError(ex.getMessage()));
    }

    @ExceptionHandler(IllegalParcelStateException.class)
    public ResponseEntity<ApiError> handleIllegalParcelStateException(IllegalParcelStateException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiError(ex.getMessage()));
    }

    // TRIPS
    @ExceptionHandler(TripNotFoundException.class)
    public ResponseEntity<ApiError> handleTripNotFoundException(TripNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiError(ex.getMessage()));
    }

    @ExceptionHandler(UnauthorizedTripActionException.class)
    public ResponseEntity<ApiError> handleUnauthorizedTripActionException(UnauthorizedTripActionException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiError(ex.getMessage()));
    }

    @ExceptionHandler(IllegalTripStateException.class)
    public ResponseEntity<ApiError> handleIllegalTripStateException(IllegalTripStateException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiError(ex.getMessage()));
    }

    @ExceptionHandler(TripStopNotFoundException.class)
    public ResponseEntity<ApiError> handleTripStopNotFoundException(TripStopNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiError(ex.getMessage()));
    }

    @ExceptionHandler(InvalidStopOrderException.class)
    public ResponseEntity<ApiError> handleInvalidStopOrderException(InvalidStopOrderException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiError(ex.getMessage()));
    }

    // BOOKING

    @ExceptionHandler(BookingNotFoundException.class)
    public ResponseEntity<ApiError> handleBookingNotFoundException(BookingNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiError(ex.getMessage()));
    }

    @ExceptionHandler(BookingAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleBookingAlreadyExistsException(BookingAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiError(ex.getMessage()));
    }

    @ExceptionHandler(UnauthorizedBookingActionException.class)
    public ResponseEntity<ApiError> handleUnauthorizedBookingActionException(UnauthorizedBookingActionException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiError(ex.getMessage()));
    }

    // AUTH
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleBadCredentialsException(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiError("Email or password is invalid"));
    }

    @ExceptionHandler(MissingRequestCookieException.class)
    public ResponseEntity<ApiError> handleMissingRequestCookieException() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                new ApiError("Authentication token is missing"));
    }

    @ExceptionHandler(AuthenticationSessionException.class)
    public ResponseEntity<ApiError> handleAuthenticationSessionException(AuthenticationSessionException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                new ApiError(ex.getMessage()));
    }

    // VALIDATIONS
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,Object>> handleValidationErrors(
            MethodArgumentNotValidException ex
    ){
        List<ValidationError> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new ValidationError(
                        error.getField(),
                        error.getDefaultMessage()
                ))
                .toList();

        Map<String, Object> response = Map.of(
                "status", 400,
                "errors", errors
        );

        return ResponseEntity.badRequest().body(response);
    }
}
