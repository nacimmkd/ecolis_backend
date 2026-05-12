package com.deliveryplatform.requests;

import com.deliveryplatform.requests.dto.CreateRequest;
import com.deliveryplatform.requests.dto.RequestDto;

import java.util.List;
import java.util.UUID;

public interface RequestService {

    RequestDto getRequest(UUID requestId, UUID currentUserId);

    List<RequestDto> getMySentRequests(UUID senderId);

    List<RequestDto> getMyReceivedRequests(UUID carrierId);

    List<RequestDto> getMyTripRequests(UUID tripId, UUID currentUserId);

    List<RequestDto> getMyParcelRequests(UUID parcelId, UUID currentUserId);

    List<RequestDto> getUserInvolvedRequests(UUID currentUserId);

    RequestDto createRequest(CreateRequest dto, UUID senderId);

    void cancelRequest(UUID requestId, UUID userId);

    void acceptRequest(UUID requestId, UUID carrierId);

    void rejectRequest(UUID requestId, UUID carrierId, String reason);
}