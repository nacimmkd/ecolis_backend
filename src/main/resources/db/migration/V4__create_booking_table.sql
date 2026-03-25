CREATE TABLE bookings (
                          id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          trip_id         UUID NOT NULL REFERENCES trips(id) ON DELETE CASCADE,
                          parcel_id       UUID NOT NULL REFERENCES parcels(id) ON DELETE CASCADE,
                          requester_id    UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                          status          VARCHAR(30)  NOT NULL DEFAULT 'pending',
                          agreed_price    NUMERIC(8, 2),
                          message         TEXT,
                          confirmed_at    TIMESTAMPTZ,
                          picked_up_at    TIMESTAMPTZ,
                          delivered_at    TIMESTAMPTZ,
                          created_at      TIMESTAMPTZ DEFAULT NOW(),

                          UNIQUE (trip_id, parcel_id),

                          CONSTRAINT fk_booking_trip FOREIGN KEY (trip_id) REFERENCES trips(id) ON DELETE CASCADE ON UPDATE CASCADE,
                          CONSTRAINT fk_booking_parcel FOREIGN KEY (parcel_id) REFERENCES parcels(id) ON DELETE CASCADE ON UPDATE CASCADE,
                          CONSTRAINT fk_booking_user FOREIGN KEY (requester_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE
);