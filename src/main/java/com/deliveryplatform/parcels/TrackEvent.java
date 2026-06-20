package com.deliveryplatform.parcels;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

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

    private ParcelStatus status;

    @Builder.Default
    private String note = "";

    @Column(name = "occurred_at")
    @Builder.Default
    private OffsetDateTime occurredAt = OffsetDateTime.now();

    @ManyToOne
    @JoinColumn(name = "parcel_id")
    private Parcel parcel;


}
