CREATE TABLE bookings (
                          id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          trip_id         UUID NOT NULL,
                          parcel_id       UUID NOT NULL,
                          status             VARCHAR(20)    NOT NULL DEFAULT 'CONFIRMED'
                              CHECK (status IN ('CONFIRMED', 'PAID', 'COMPLETED', 'CANCELLED')),
                          price           NUMERIC(10, 2),

                          pickup_code      VARCHAR(20),
                          dropoff_code     VARCHAR(20),

                          confirmed_at     TIMESTAMPTZ DEFAULT NOW(),
                          paid_at          TIMESTAMPTZ,
                          completed_at    TIMESTAMPTZ,
                          cancelled_at    TIMESTAMPTZ,

                          UNIQUE (trip_id, parcel_id),

                          CONSTRAINT fk_booking_trip FOREIGN KEY (trip_id) REFERENCES trips(id) ON DELETE RESTRICT,
                          CONSTRAINT fk_booking_parcel FOREIGN KEY (parcel_id) REFERENCES parcels(id) ON DELETE RESTRICT
);


CREATE TABLE booking_requests (
                                  id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                  trip_id          UUID NOT NULL,
                                  parcel_id        UUID NOT NULL,
                                  status           VARCHAR(20)    NOT NULL DEFAULT 'PENDING'
                                      CHECK (status IN ('PENDING', 'ACCEPTED', 'REJECTED', 'CANCELLED')),
                                  rejection_reason TEXT,
                                  responded_at     TIMESTAMPTZ,
                                  requested_at       TIMESTAMPTZ NOT NULL DEFAULT now(),

                                  CONSTRAINT uq_booking_request_trip_parcel UNIQUE (trip_id, parcel_id),
                                  CONSTRAINT fk_booking_request_trip   FOREIGN KEY (trip_id)   REFERENCES trips(id)   ON DELETE RESTRICT,
                                  CONSTRAINT fk_booking_request_parcel FOREIGN KEY (parcel_id) REFERENCES parcels(id) ON DELETE RESTRICT
);