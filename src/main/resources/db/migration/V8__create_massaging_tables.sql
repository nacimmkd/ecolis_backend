CREATE TABLE conversations (
                               id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
                               booking_id  UUID        NOT NULL,
                               created_at  TIMESTAMPTZ NOT NULL    DEFAULT now(),

                               CONSTRAINT uq_conversations_booking  UNIQUE      (booking_id),
                               CONSTRAINT fk_conversations_booking  FOREIGN KEY (booking_id)  REFERENCES bookings(id) ON DELETE CASCADE
);


CREATE TABLE messages (
                          id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
                          conversation_id UUID        NOT NULL,
                          sender_id       UUID        NOT NULL,
                          content         TEXT,
                          read            BOOLEAN     DEFAULT FALSE,
                          sent_at         TIMESTAMPTZ NOT NULL    DEFAULT now(),

                          CONSTRAINT fk_messages_conversation FOREIGN KEY (conversation_id) REFERENCES conversations(id) ON DELETE CASCADE,
                          CONSTRAINT fk_messages_sender       FOREIGN KEY (sender_id)       REFERENCES users(id)         ON DELETE SET NULL,
                          CONSTRAINT chk_message_not_empty    CHECK (content IS NOT NULL)
);


CREATE TABLE message_images (
                                message_id  UUID NOT NULL,
                                image_id    UUID NOT NULL,

                                CONSTRAINT pk_message_images        PRIMARY KEY (message_id, image_id),
                                CONSTRAINT fk_message_images_msg    FOREIGN KEY (message_id) REFERENCES messages(id) ON DELETE CASCADE,
                                CONSTRAINT fk_message_images_img    FOREIGN KEY (image_id)   REFERENCES images(id)   ON DELETE CASCADE
);
