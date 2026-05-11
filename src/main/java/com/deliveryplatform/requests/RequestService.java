package com.deliveryplatform.requests;

import com.deliveryplatform.requests.dto.CreateRequest;
import com.deliveryplatform.requests.dto.RequestDto;

import java.util.List;
import java.util.UUID;

public interface RequestService {

    RequestDto getBookingRequest(UUID requestId, UUID currentUserId);

    List<RequestDto> getBookingRequests(UUID currentUserId);

    RequestDto createBookingRequest(CreateRequest dto, UUID senderId);

    void cancelRequest(UUID requestId, UUID userId);

    void acceptRequest(UUID requestId, UUID carrierId);

    void rejectRequest(UUID requestId, UUID carrierId, String reason);
}