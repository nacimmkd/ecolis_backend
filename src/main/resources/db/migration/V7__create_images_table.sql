CREATE TABLE images (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    key             VARCHAR(500) UNIQUE NOT NULL,
    content_type     VARCHAR(50) NOT NULL,
    confirmed       BOOLEAN NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMPTZ DEFAULT now(),
    uploaded_by     UUID NOT NULL REFERENCES users(id) ON DELETE SET NULL
);