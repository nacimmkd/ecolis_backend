CREATE TABLE parcels (
                         id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                         user_id         UUID NOT NULL,
                         description     TEXT,
                         weight_kg       NUMERIC(8,2)  NOT NULL,
                         size            VARCHAR(10) NOT NULL
                            CHECK ( size IN ('S','M','L','XL','XXL')),
                         is_fragile      BOOLEAN NOT NULL DEFAULT FALSE,

                         -- pickup address
                         pickup_street       TEXT         NOT NULL,
                         pickup_city         VARCHAR(100) NOT NULL,
                         pickup_postal_code  VARCHAR(20)  NOT NULL,
                         pickup_country      VARCHAR(60)  NOT NULL,
                         pickup_lat          NUMERIC(10,7),
                         pickup_lng          NUMERIC(10,7),

                         -- dropoff address
                         dropoff_street      TEXT         NOT NULL,
                         dropoff_city        VARCHAR(100) NOT NULL,
                         dropoff_postal_code VARCHAR(20)  NOT NULL,
                         dropoff_country     VARCHAR(60)  NOT NULL,
                         dropoff_lat         NUMERIC(10,7),
                         dropoff_lng         NUMERIC(10,7),

                         status              VARCHAR(20) NOT NULL DEFAULT 'PUBLISHED'
                             CHECK (status IN ('PUBLISHED','BOOKED','PICKED_UP','IN_TRANSIT','DELIVERED','CANCELLED')),

                         code_otp                VARCHAR(20),
                         deadline_date   DATE,
                         created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),

                         CONSTRAINT fk_parcels_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT
);