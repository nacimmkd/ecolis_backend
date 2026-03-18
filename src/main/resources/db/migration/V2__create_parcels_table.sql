CREATE TABLE parcels (
                           "id"              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                           "user_id"     UUID NOT NULL,
                           "description"     VARCHAR(255),
                           "weight_kg"       NUMERIC(8,2) NOT NULL,
                           "length_cm"       NUMERIC(6,1),
                           "width_cm"        NUMERIC(6,1),
                           "height_cm"       NUMERIC(6,1),
                           "declared_value"  NUMERIC(10,2),
                           "is_fragile"      BOOLEAN NOT NULL DEFAULT FALSE,
                           "content_type"    VARCHAR(50),
                           "created_at"      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                            CONSTRAINT fk_parcels_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT
);