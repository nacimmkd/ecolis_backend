package com.deliveryplatform.requests;

import com.deliveryplatform.requests.dto.CreateRequest;
import com.deliveryplatform.requests.dto.RequestDto;
import com.deliveryplatform.users.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/booking-requests")
@RequiredArgsConstructor
public class RequestController {

    private final RequestService requestService;

    @GetMapping("/{requestId}")
    public ResponseEntity<RequestDto> getRequestById(
            @PathVariable UUID requestId,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        return ResponseEntity.ok(requestService.getRequest(requestId, user.getId()));
    }

    @GetMapping
    public ResponseEntity<List<RequestDto>> getRequests(
            @RequestParam(name = "tripId", required = false)  UUID tripId,
            @RequestParam(name = "parcelId", required = false)  UUID parcelId,
            @AuthenticationPrincipal UserPrincipal user
    ) throws BadRequestException
    {
        if (tripId != null && parcelId != null)
            throw new BadRequestException("tripId and parcelId cannot be presented at same time");

        List<RequestDto> requests = null;

        if (tripId != null) { requests = requestService.getMyTripRequests(tripId, user.getId());}
        if (parcelId != null) { requests = requestService.getMyParcelRequests(parcelId, user.getId());}

        return ResponseEntity.ok(requests);
    }

    @GetMapping("/received")
    public ResponseEntity<List<RequestDto>> getReceivedRequests(
            @AuthenticationPrincipal UserPrincipal user
    ) {
        return ResponseEntity.ok(requestService.getMyReceivedRequests(user.getId()));
    }

    @GetMapping("/sent")
    public ResponseEntity<List<RequestDto>> getSentRequests(
            @AuthenticationPrincipal UserPrincipal user
    ) {
        return ResponseEntity.ok(requestService.getMySentRequests(user.getId()));
    }

    @PostMapping
    public ResponseEntity<RequestDto> createRequest(
            @RequestBody @Valid CreateRequest dto,
            @AuthenticationPrincipal UserPrincipal user,
            UriComponentsBuilder uriBuilder
    ) {
        var request = requestService.createRequest(dto, user.getId());
        var uri = uriBuilder
                .path("/api/v1/booking-requests/{id}")
                .build(request.requestId());
        return ResponseEntity.created(uri).body(request);
    }

    @PatchMapping("/{requestId}/accept")
    public ResponseEntity<Void> acceptRequest(
            @PathVariable UUID requestId,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        requestService.acceptRequest(requestId, user.getId());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{requestId}/reject")
    public ResponseEntity<Void> rejectRequest(
            @PathVariable UUID requestId,
            @RequestParam String reason,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        requestService.rejectRequest(requestId, user.getId(), reason);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{requestId}/delete")
    public ResponseEntity<Void> cancelRequest(
            @PathVariable UUID requestId,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        requestService.deleteRequest(requestId, user.getId());
        return ResponseEntity.noContent().build();
    }
}