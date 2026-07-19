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
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
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
    private int order;

    @Embedded
    private Address address;

    private boolean deleted;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;

    public static TripStop create(Address address, int order) {
        if (order < 1)
            throw new InvalidDomainStateException("stop order must be >= 1");

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

    public void delete() {
        if (this.deleted)
            throw new InvalidDomainStateException("stop already deleted");
        this.deleted = true;
        this.deletedAt = OffsetDateTime.now();
    }
}