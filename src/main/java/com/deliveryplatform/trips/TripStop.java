package com.deliveryplatform.trips;

import com.deliveryplatform.addresses.Address;
import com.deliveryplatform.common.exceptions.InvalidDomainStateException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "trip_stops")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SQLRestriction("deleted = false")
public class TripStop {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Setter(AccessLevel.PRIVATE)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "trip_id")
    @JsonIgnore
    @Setter(AccessLevel.PROTECTED)
    private Trip trip;

    @Column(name = "stop_order")
    @Setter(AccessLevel.PRIVATE)
    private int order;

    @Embedded
    @Setter(AccessLevel.PRIVATE)
    private Address address;

    @Setter(AccessLevel.PRIVATE)
    private boolean deleted;

    @Column(name = "deleted_at")
    @Setter(AccessLevel.PRIVATE)
    private OffsetDateTime deletedAt;

    public static TripStop create(Address address, int order) {
        var stop = new TripStop();
        stop.setOrder(order);
        stop.setAddress(address);
        stop.setDeleted(false);
        stop.setDeletedAt(null);
        return stop;
    }

    public static void reorderStops(List<TripStop> stops) {
        for (int i = 0; i < stops.size(); i++) {
            stops.get(i).setOrder(i + 1);
        }
    }

}