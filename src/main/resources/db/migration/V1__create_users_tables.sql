CREATE TABLE users (
                       id                  UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
                       email               VARCHAR(255)    NOT NULL UNIQUE,
                       password            VARCHAR(255)    NOT NULL,
                       role                VARCHAR(20)     NOT NULL CHECK (role IN ('USER','ADMIN')),
                       is_verified         BOOLEAN         NOT NULL DEFAULT FALSE,
                       registered_at       TIMESTAMPTZ       NOT NULL DEFAULT NOW(),
                       is_active           BOOLEAN         NOT NULL DEFAULT TRUE
);


CREATE TABLE profiles (
                                 id                  UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
                                 first_name          VARCHAR(100)    NOT NULL,
                                 last_name           VARCHAR(100)    NOT NULL,
                                 phone               VARCHAR(20),
                                 avg_rating          NUMERIC(2,1),
                                 total_deliveries    INT             NOT NULL DEFAULT 0,
                                 total_orders        INT             NOT NULL DEFAULT 0,

                                 CONSTRAINT fk_profiles_user FOREIGN KEY (id) REFERENCES users(id) ON DELETE CASCADE
);

