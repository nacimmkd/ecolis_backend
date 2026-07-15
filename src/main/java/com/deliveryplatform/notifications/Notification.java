package com.deliveryplatform.notifications;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "notifications")
@SQLRestriction("deleted = false")
public class Notification {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "user_id")
    private UUID userId;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @Column(name = "reference_id")
    private UUID referenceId;

    @Column(name = "is_read", nullable = false)
    @Builder.Default
    private boolean isRead = false;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payload", columnDefinition = "jsonb", updatable = false)
    private Map<String, Object> payload;

    @Builder.Default
    private boolean deleted = false;

    @Column(name = "deleted_at")
    @Builder.Default
    private OffsetDateTime deletedAt = null;

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private OffsetDateTime createdAt = OffsetDateTime.now();


    public static Notification createFromNotificationPayload(NotificationEvent event) {

        var user = event.getUser();
        if(user == null) throw new IllegalArgumentException("could not create notification - user is null");

        return Notification.builder()
                .userId(user.getId())
                .type(event.getNotificationType())
                .referenceId(event.getReferenceId())
                .isRead(false)
                .payload(event.getPayload())
                .deleted(false)
                .deletedAt(null)
                .build();
    }


    public void delete() {
        this.deleted = true;
        this.deletedAt = OffsetDateTime.now();
    }

    public void read() {
        this.isRead = true;
    }

}
