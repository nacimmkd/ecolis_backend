CREATE TABLE users (
                       id                  UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
                       first_name          VARCHAR(100)    NOT NULL,
                       last_name           VARCHAR(100)    NOT NULL,
                       email               VARCHAR(255)    NOT NULL UNIQUE,
                       password            VARCHAR(255)    NOT NULL,
                       phone               VARCHAR(20),
                       avatar_url          VARCHAR(500),
                       role                VARCHAR(20)     NOT NULL CHECK (role IN ('CUSTOMER','DRIVER','ADMIN')),
                       is_verified         BOOLEAN         NOT NULL DEFAULT FALSE,
                       registered_at       TIMESTAMP       NOT NULL DEFAULT NOW(),
                       is_active           BOOLEAN         NOT NULL DEFAULT TRUE
);


CREATE TABLE driver_profiles (
                                 id                  UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
                                 user_id             UUID            NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
                                 avg_rating          NUMERIC(2,1)    DEFAULT 0,
                                 total_deliveries    INT             NOT NULL DEFAULT 0,
                                 vehicle_type        VARCHAR(50)     CHECK (vehicle_type IN ('BICYCLE','SCOOTER','CAR','VAN','TRUCK','ON_FOOT')),
                                 max_weight_kg       NUMERIC(8,2),
                                 max_volume_m3       NUMERIC(8,3),
                                 iban                VARCHAR(34),
                                 is_available        BOOLEAN         NOT NULL DEFAULT TRUE
);


CREATE TABLE customer_profiles (
                                   id                  UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
                                   user_id             UUID            NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
                                   avg_rating          NUMERIC(2,1)    DEFAULT 0,
                                   total_orders        INT             NOT NULL DEFAULT 0,
                                   default_address     TEXT
);