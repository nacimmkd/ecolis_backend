CREATE TABLE bookings (
                          id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          trip_id         UUID NOT NULL,
                          parcel_id       UUID NOT NULL,
                          sender_id    UUID NOT NULL,
                          carrier_id    UUID NOT NULL,
                          "status"                VARCHAR(20)   NOT NULL DEFAULT 'PENDING'
                              CHECK ("status" IN ('PENDING', 'ACCEPTED', 'REJECTED', 'PAID','COMPLETED' ,'CANCELLED', 'DISPUTED')),
                          price           NUMERIC(8, 2),

                          accepted_at     TIMESTAMPTZ,
                          completed_at    TIMESTAMPTZ,
                          created_at      TIMESTAMPTZ DEFAULT NOW(),

                          UNIQUE (trip_id, parcel_id),

                          CONSTRAINT fk_booking_trip FOREIGN KEY (trip_id) REFERENCES trips(id),
                          CONSTRAINT fk_booking_parcel FOREIGN KEY (parcel_id) REFERENCES parcels(id),
                          CONSTRAINT fk_booking_sender FOREIGN KEY (sender_id) REFERENCES users(id),
                          CONSTRAINT fk_booking_carrier FOREIGN KEY (carrier_id) REFERENCES users(id)
);