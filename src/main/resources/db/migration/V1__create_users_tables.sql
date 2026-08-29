CREATE TABLE users (
                       id                  UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
                       email               VARCHAR(255)    NOT NULL UNIQUE,
                       password            VARCHAR(255),
                       role                VARCHAR(20)     NOT NULL CHECK (role IN ('USER','ADMIN')),
                       is_verified         BOOLEAN         NOT NULL DEFAULT FALSE,
                       provider            VARCHAR(255)    NOT NULL,
                       registered_at       TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
                       deleted             BOOLEAN         NOT NULL DEFAULT FALSE,
                       deleted_At          TIMESTAMPTZ
);


CREATE TABLE profiles (
                                 id                  UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
                                 first_name          VARCHAR(100)    NOT NULL,
                                 last_name           VARCHAR(100)    NOT NULL,
                                 phone               VARCHAR(20),
                                 country             VARCHAR(20),
                                 avg_rating          NUMERIC(2,1),
                                 review_count        INT             NOT NULL DEFAULT 0,
                                 completed_trips     INT             NOT NULL DEFAULT 0,
                                 sent_parcels        INT             NOT NULL DEFAULT 0,

                                 CONSTRAINT fk_profiles_user FOREIGN KEY (id) REFERENCES users(id) ON DELETE CASCADE
);

