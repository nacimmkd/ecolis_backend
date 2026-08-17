CREATE TABLE parcel_images (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    key          VARCHAR(500) UNIQUE NOT NULL,
    content_type VARCHAR(50) NOT NULL,
    parcel_id    UUID NOT NULL,
    created_at   TIMESTAMPTZ DEFAULT now(),

    CONSTRAINT fk_parcel_images_parcel
        FOREIGN KEY (parcel_id) REFERENCES parcels(id) ON DELETE CASCADE
);