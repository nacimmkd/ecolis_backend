CREATE TABLE bookings (
                          id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          trip_id         UUID NOT NULL,
                          parcel_id       UUID NOT NULL,
                          state             VARCHAR(20)    NOT NULL,
                          price_amount_in_cents  BIGINT NOT NULL CHECK (price_amount_in_cents >= 0),
                          price_currency          VARCHAR(3) NOT NULL DEFAULT 'eur',

                          pickup_detour_km    NUMERIC(10,2) NOT NULL,
                          dropoff_detour_km   NUMERIC(10,2) NOT NULL,
                          rejection_reason TEXT,

                          pickup_code      VARCHAR(20),
                          dropoff_code     VARCHAR(20),

                          created_at     TIMESTAMPTZ DEFAULT NOW(),
                          responded_at     TIMESTAMPTZ,
                          completed_at    TIMESTAMPTZ,

                          cancelled_at    TIMESTAMPTZ,
                          cancelled_by    VARCHAR(10),
                          cancel_reason TEXT,

                          CONSTRAINT fk_booking_trip FOREIGN KEY (trip_id) REFERENCES trips(id) ON DELETE RESTRICT,
                          CONSTRAINT fk_booking_parcel FOREIGN KEY (parcel_id) REFERENCES parcels(id) ON DELETE RESTRICT
);
