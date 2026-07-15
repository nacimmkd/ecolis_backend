package com.deliveryplatform.notifications;

import com.deliveryplatform.users.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<NotificationDto>> getUserNotifications(
            @AuthenticationPrincipal UserPrincipal user) {
        return ResponseEntity.ok(
                notificationService.getUserNotifications(user.getId())
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
            @AuthenticationPrincipal UserPrincipal user) {

        notificationService.delete(id, user.getId());
        return ResponseEntity.noContent().build();
    }
}
