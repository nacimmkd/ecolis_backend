package com.deliveryplatform.parcels;

import com.deliveryplatform.storage.MediaType;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "parcel_images")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder(access = AccessLevel.PRIVATE)
public class ParcelImage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String key;

    @Column(name = "content_type")
    @Enumerated(EnumType.STRING)
    private MediaType mediaType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parcel_id")
    @Setter(AccessLevel.PACKAGE)
    private Parcel parcel;

    @Column(name = "created_at")
    @Builder.Default
    private OffsetDateTime createdAt = OffsetDateTime.now();

    public static ParcelImage create(MediaType mediaType, String key) {
        return ParcelImage.builder()
                .key(key)
                .mediaType(mediaType)
                .build();
    }
}