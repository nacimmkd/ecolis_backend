CREATE TABLE reviews (
                         id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                         reviewer_id     UUID NOT NULL,
                         reviewed_id     UUID NOT NULL,
                         booking_id      UUID NOT NULL,
                         rating          SMALLINT NOT NULL CHECK (rating BETWEEN 1 AND 5),
                         comment         TEXT,
                         created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),

                         CONSTRAINT fk_reviews_reviewer FOREIGN KEY (reviewer_id) REFERENCES users(id) ON DELETE RESTRICT,
                         CONSTRAINT fk_reviews_reviewee FOREIGN KEY (reviewed_id) REFERENCES users(id) ON DELETE RESTRICT,
                         CONSTRAINT fk_reviews_booking  FOREIGN KEY (booking_id)  REFERENCES bookings(id) ON DELETE RESTRICT,
                         CONSTRAINT chk_no_self_review CHECK (reviewer_id != reviewed_id),
                         CONSTRAINT uq_review_booking_reviewer UNIQUE (booking_id, reviewer_id)
    );