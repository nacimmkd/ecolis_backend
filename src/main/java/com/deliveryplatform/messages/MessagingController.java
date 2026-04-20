package com.deliveryplatform.messages;

import com.deliveryplatform.messages.dto.ConversationDetailedResponse;
import com.deliveryplatform.messages.dto.ConversationResponse;
import com.deliveryplatform.messages.dto.SendMessageRequest;
import com.deliveryplatform.users.UserPrincipal;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/v1/conversations")
@RequiredArgsConstructor
public class MessagingController {

    private final MessagingService messagingService;


    @PostMapping("/booking/{bookingId}")
    public ResponseEntity<ConversationResponse> getOrCreateConversation(
            @PathVariable @NotNull UUID bookingId,
            @AuthenticationPrincipal UserPrincipal user) {

        return ResponseEntity.ok(
                messagingService.getOrCreateConversation(bookingId, user.getId())
        );
    }


    @GetMapping
    public ResponseEntity<List<ConversationResponse>> getMyConversations(
            @AuthenticationPrincipal UserPrincipal user) {

        return ResponseEntity.ok(
                messagingService.getUserConversations(user.getId())
        );
    }


    @GetMapping("/{conversationId}")
    public ResponseEntity<ConversationDetailedResponse> getConversationDetails(
            @PathVariable @NotNull UUID conversationId,
            @AuthenticationPrincipal UserPrincipal user) {

        return ResponseEntity.ok(
                messagingService.getConversationDetails(conversationId, user.getId())
        );
    }


    @DeleteMapping("/{conversationId}")
    public ResponseEntity<Void> deleteConversation(
            @PathVariable @NotNull UUID conversationId,
            @AuthenticationPrincipal UserPrincipal user) {

        messagingService.deleteConversation(conversationId, user.getId());
        return ResponseEntity.noContent().build();
    }


    @PatchMapping("/{conversationId}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable @NotNull UUID conversationId,
            @AuthenticationPrincipal UserPrincipal user) {

        messagingService.markConversationAsRead(conversationId, user.getId());
        return ResponseEntity.noContent().build();
    }


    @MessageMapping("/chat.send")
    public void sendMessage(
            @Payload @Valid SendMessageRequest request,
            Principal principal) {

        UserPrincipal user = (UserPrincipal) ((Authentication) principal).getPrincipal();
        messagingService.sendMessage(request, user.getId());
    }
}