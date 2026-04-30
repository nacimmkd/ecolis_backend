package com.deliveryplatform.trips;

import com.deliveryplatform.addresses.GeocodedAddress;
import com.deliveryplatform.trips.dto.TripStopRequest;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "trip_stops")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SQLRestriction("deleted = false")
public class TripStop {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "trip_id")
    @JsonIgnore
    private Trip trip;

    @Column(name = "stop_order")
    private int stopOrder;

    @Embedded
    private GeocodedAddress address;

    @Builder.Default
    private boolean deleted = false;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;


    public static TripStop of(TripStopRequest request, GeocodedAddress address) {
        return TripStop.builder()
                .id(null)
                .stopOrder(request.stopOrder())
                .address(address)
                .build();
    }

}