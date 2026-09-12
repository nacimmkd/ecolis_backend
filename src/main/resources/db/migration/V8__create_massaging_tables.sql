CREATE TABLE conversations (
                               id                  UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
                               booking_id          UUID        NOT NULL,
                               sender_id           UUID        NOT NULL,
                               carrier_id          UUID        NOT NULL,
                               last_message_id     UUID,
                               created_at          TIMESTAMPTZ NOT NULL    DEFAULT now(),

                               CONSTRAINT conversation_booking FOREIGN KEY (booking_id) REFERENCES bookings(id),
                               CONSTRAINT conversation_sender  FOREIGN KEY (sender_id)  REFERENCES users(id) ON DELETE CASCADE,
                               CONSTRAINT conversation_carrier FOREIGN KEY (carrier_id) REFERENCES users(id) ON DELETE CASCADE,
                               CONSTRAINT conversation_distinct_users CHECK (sender_id <> carrier_id)
);


CREATE TABLE messages (
                          id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
                          conversation_id UUID        NOT NULL,
                          sender_id       UUID        NOT NULL,
                          content         TEXT,
                          read            BOOLEAN DEFAULT FALSE,
                          read_at         TIMESTAMPTZ,
                          notified        BOOLEAN DEFAULT FALSE,
                          sent_at         TIMESTAMPTZ NOT NULL    DEFAULT now(),

                          CONSTRAINT fk_messages_conversation FOREIGN KEY (conversation_id) REFERENCES conversations(id) ON DELETE CASCADE,
                          CONSTRAINT fk_messages_sender       FOREIGN KEY (sender_id)       REFERENCES users(id)         ON DELETE CASCADE
);


CREATE TABLE message_images (
                                id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                key          VARCHAR(500) UNIQUE NOT NULL,
                                content_type VARCHAR(50) NOT NULL,
                                message_id   UUID NOT NULL,
                                created_at   TIMESTAMPTZ DEFAULT now(),

                                CONSTRAINT fk_message_images_message FOREIGN KEY (message_id) REFERENCES messages(id) ON DELETE CASCADE
);
