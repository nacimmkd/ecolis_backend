package com.deliveryplatform.images;

import com.deliveryplatform.images.exceptions.ImageErrorCode;
import com.deliveryplatform.images.exceptions.ImageException;
import com.deliveryplatform.storage.MediaType;
import com.deliveryplatform.storage.PresignedUrl;
import com.deliveryplatform.users.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "images")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder(access = AccessLevel.PRIVATE)
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


    public static Image create(MediaType mediaType, PresignedUrl presignedUrl, UUID uploadedBy) {
        return Image.builder()
                .key(presignedUrl.key())
                .mediaType(mediaType)
                .uploadedBy(uploadedBy)
                .build();
    }

    public void confirm() {
        this.confirmed = true;
    }

    public boolean isOwnedBy(User requestedBy) {
        return this.uploadedBy.equals(requestedBy.getId());
    }

    public void assertOwnedBy(UUID requestedBy) {
        if (!this.uploadedBy.equals(requestedBy)) {
            throw new ImageException(ImageErrorCode.IMAGE_NOT_OWNED, "Not allowed to perform this action");
        }
    }
}
