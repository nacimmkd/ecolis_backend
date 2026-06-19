package com.deliveryplatform.images;

import com.deliveryplatform.storage.MediaType;
import com.deliveryplatform.users.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "images")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    private String key;

    @Column(name = "content_type")
    @Enumerated(EnumType.STRING)
    private MediaType mediaType;

    @Column(name = "uploaded_by")
    private UUID uploadedBy;

    @Builder.Default
    private boolean confirmed = false;

    @Column(name = "created_at")
    @Builder.Default
    private OffsetDateTime createdAt = OffsetDateTime.now();

    public boolean isOwnedBy(User user) {
        if (user == null) return false;
        return this.uploadedBy.equals(user.getId());
    }
}
