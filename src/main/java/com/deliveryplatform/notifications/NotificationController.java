package com.deliveryplatform.notifications;

import com.deliveryplatform.notifications.events.BookingAcceptedEvent;
import com.deliveryplatform.notifications.events.ParcelDeliveredEvent;
import com.deliveryplatform.users.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationRepository notificationRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @GetMapping
    public ResponseEntity<List<Notification>> getAll(
            @AuthenticationPrincipal UserPrincipal user) {
        return ResponseEntity.ok(
                notificationRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
        );
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Long> unreadCount(
            @AuthenticationPrincipal UserPrincipal user) {

        return ResponseEntity.ok(
                notificationRepository.countByUserIdAndIsReadFalse(user.getId())
        );
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal user) {

        notificationService.markAsRead(id, user.getId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id,
            @AuthenticationPrincipal UUID userId) {

        notificationService.delete(id, userId);
        return ResponseEntity.noContent().build();
    }


    // test
    @PostMapping("/test-ws")
    public ResponseEntity<String> testWs() {

        var event = new BookingAcceptedEvent(
                UUID.fromString("d60f95ec-b632-4c84-88c3-1a6683060ca8"),
                "mounir@gmail.com",
                "nacim",
                BigDecimal.valueOf(20.0),
                UUID.randomUUID()
        );
        applicationEventPublisher.publishEvent(event);

        return ResponseEntity.ok("Check logs");
    }
}
