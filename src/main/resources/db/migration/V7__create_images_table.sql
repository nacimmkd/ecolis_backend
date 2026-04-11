CREATE TABLE images (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    key             VARCHAR(200) UNIQUE NOT NULL,
    content_type     VARCHAR(30) NOT NULL,
    confirmed       BOOLEAN NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMPTZ DEFAULT now()
);