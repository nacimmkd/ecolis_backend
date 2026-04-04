CREATE TABLE notifications (
                               id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                               user_id      UUID NOT NULL,
                               type         VARCHAR(50) NOT NULL,
                               reference_id UUID,
                               is_read      BOOLEAN NOT NULL DEFAULT FALSE,
                               created_at   TIMESTAMPTZ NOT NULL DEFAULT NOW(),

                               CONSTRAINT fk_notifications_user
                                   FOREIGN KEY (user_id)
                                       REFERENCES users(id)
                                       ON DELETE CASCADE
);
