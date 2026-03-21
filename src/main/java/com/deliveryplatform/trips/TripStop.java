package com.deliveryplatform.trips;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "trip_stops")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripStop {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "trip_id")
    @JsonIgnore
    private Trip trip;

    @Column(name = "stop_order")
    private int stopOrder;

    private String street;

    private String city;

    @Column(name = "postal_code")
    private String postalCode;

    private String country;

    private BigDecimal latitude;

    private BigDecimal longitude;

}