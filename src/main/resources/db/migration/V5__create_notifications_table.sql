CREATE TABLE notifications (
                               id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                               user_id      UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                               type         VARCHAR(50) NOT NULL,
                               reference_id UUID,
                               payload      JSONB,
                               is_read      BOOLEAN NOT NULL DEFAULT FALSE,
                               created_at   TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
