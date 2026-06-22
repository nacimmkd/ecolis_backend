package com.deliveryplatform.parcels;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "parcel_tracking_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrackEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private ParcelState state;

    @Builder.Default
    @Column(name = "note")
    private String message = "";

    @Column(name = "occurred_at")
    @Builder.Default
    private OffsetDateTime occurredAt = OffsetDateTime.now();

    @ManyToOne
    @JoinColumn(name = "parcel_id")
    private Parcel parcel;


    public static TrackEvent of(ParcelState state, String message) {
        return TrackEvent.builder()
                .state(state)
                .message(message)
                .build();
    }


}
