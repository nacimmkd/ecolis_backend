ALTER TABLE profiles
    ADD COLUMN avatar_image_id UUID;

ALTER TABLE profiles
    ADD CONSTRAINT fk_profiles_image
        FOREIGN KEY (avatar_image_id) REFERENCES images(id) ON DELETE SET NULL;