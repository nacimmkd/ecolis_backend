CREATE TABLE "trips" (
                         id                    UUID          NOT NULL DEFAULT gen_random_uuid(),
                         user_id               UUID          NOT NULL,

    -- departure address
                         departure_street      TEXT          NOT NULL,
                         departure_city        VARCHAR(100)  NOT NULL,
                         departure_postal_code VARCHAR(20)   NOT NULL,
                         departure_country     VARCHAR(60)   NOT NULL,
                         departure_lat         DOUBLE PRECISION,
                         departure_lng         DOUBLE PRECISION,

    -- arrival address
                         arrival_street         TEXT          NOT NULL,
                         arrival_city           VARCHAR(100)  NOT NULL,
                         arrival_postal_code    VARCHAR(20)   NOT NULL,
                         arrival_country        VARCHAR(60)   NOT NULL,
                         arrival_lat            DOUBLE PRECISION,
                         arrival_lng            DOUBLE PRECISION,

                         departure_date        DATE,
                         arrival_date          DATE,
                         available_volume_cm3  NUMERIC(8,2)  NOT NULL,
                         available_weight_kg   NUMERIC(8,2)  NOT NULL,
                         price_per_kg          NUMERIC(8,2)  NOT NULL DEFAULT 0.00,
                         max_detour_km         NUMERIC(6,2)  NOT NULL DEFAULT 1,
                         status                VARCHAR(20)   NOT NULL DEFAULT 'PUBLISHED'
                             CHECK ("status" IN ('PUBLISHED', 'FULL', 'IN_TRANSIT', 'COMPLETED', 'CANCELLED')),
                         instant_booking       BOOLEAN NOT NULL DEFAULT FALSE,
                         notes                 TEXT,
                         created_at            TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
                         deleted               BOOLEAN       DEFAULT FALSE,
                         deleted_at            TIMESTAMPTZ,

                         PRIMARY KEY (id),
                         FOREIGN KEY (user_id) REFERENCES "users"(id) ON DELETE RESTRICT
);


CREATE TABLE "trip_stops" (
                              id           UUID          NOT NULL DEFAULT gen_random_uuid(),
                              trip_id      UUID          NOT NULL,
                              stop_order   INTEGER       NOT NULL,

    -- stop address
                              street       TEXT          NOT NULL,
                              city         VARCHAR(100)  NOT NULL,
                              postal_code  VARCHAR(20)   NOT NULL,
                              country      VARCHAR(60)   NOT NULL,
                              latitude     DOUBLE PRECISION,
                              longitude    DOUBLE PRECISION,
                              deleted               BOOLEAN       DEFAULT FALSE,
                              deleted_at            TIMESTAMPTZ,

                              PRIMARY KEY (id),
                              FOREIGN KEY (trip_id) REFERENCES trips(id) ON DELETE CASCADE ON UPDATE CASCADE
);