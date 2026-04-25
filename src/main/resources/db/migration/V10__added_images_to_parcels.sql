ALTER TABLE parcels
    ADD COLUMN thumbnail_image_id UUID,
    ADD CONSTRAINT fk_parcels_thumbnail
        FOREIGN KEY (thumbnail_image_id) REFERENCES images(id) ON DELETE RESTRICT;


CREATE TABLE parcel_images (
                               parcel_id   UUID NOT NULL,
                               image_id    UUID NOT NULL,

                               CONSTRAINT pk_parcel_images
                                   PRIMARY KEY (parcel_id, image_id),
                               CONSTRAINT fk_parcel_images_parcel
                                   FOREIGN KEY (parcel_id) REFERENCES parcels(id) ON DELETE CASCADE,
                               CONSTRAINT fk_parcel_images_image
                                   FOREIGN KEY (image_id) REFERENCES images(id) ON DELETE CASCADE
);