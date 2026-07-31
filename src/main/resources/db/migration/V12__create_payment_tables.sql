CREATE TABLE connected_accounts (
                                     id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                     profile_id          UUID NOT NULL UNIQUE,
                                     stripe_account_id   VARCHAR(255) NOT NULL UNIQUE,

                                     charges_enabled     BOOLEAN NOT NULL DEFAULT FALSE,
                                     payouts_enabled     BOOLEAN NOT NULL DEFAULT FALSE,
                                     details_submitted   BOOLEAN NOT NULL DEFAULT FALSE,

                                     created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),

                                     CONSTRAINT fk_connected_accounts_profile FOREIGN KEY (profile_id) REFERENCES profiles(id) ON DELETE RESTRICT
);

CREATE TABLE payments (
                          id                          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          request_id                  UUID NOT NULL UNIQUE,
                          payer_id                    UUID NOT NULL,
                          stripe_checkout_session_id  VARCHAR(255) NOT NULL UNIQUE,
                          stripe_payment_intent_id    VARCHAR(255) UNIQUE,
                          stripe_charge_id            VARCHAR(255),
                          amount                      BIGINT NOT NULL CHECK (amount > 0),
                          application_fee_amount      BIGINT NOT NULL CHECK (application_fee_amount >= 0),
                          currency                    VARCHAR(3) NOT NULL DEFAULT 'eur',
                          status                      VARCHAR(20) NOT NULL DEFAULT 'PENDING'
                              CHECK (status IN ('PENDING', 'AUTHORIZED', 'SUCCEEDED', 'FAILED', 'CANCELED', 'REFUNDED')),
                          created_at                  TIMESTAMPTZ NOT NULL DEFAULT now(),
                          authorized_at               TIMESTAMPTZ,
                          succeeded_at                TIMESTAMPTZ,
                          canceled_at                 TIMESTAMPTZ,
                          refunded_at                 TIMESTAMPTZ,
                          version                     BIGINT NOT NULL DEFAULT 0,
                          CONSTRAINT fk_payments_request FOREIGN KEY (request_id) REFERENCES booking_requests(id)         ON DELETE RESTRICT,
                          CONSTRAINT fk_payments_payer   FOREIGN KEY (payer_id)   REFERENCES users(id)            ON DELETE RESTRICT,
                          CONSTRAINT chk_payments_fee    CHECK (application_fee_amount <= amount)
);

CREATE INDEX idx_payments_payer  ON payments (payer_id);
CREATE INDEX idx_payments_status ON payments (status, created_at);

CREATE TABLE transfers (
                            id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                            payment_id              UUID NOT NULL UNIQUE,
                            connected_account_id    UUID NOT NULL,

                            stripe_transfer_id      VARCHAR(255) NOT NULL UNIQUE,

                            amount                  BIGINT NOT NULL,
                            currency                VARCHAR(3) NOT NULL DEFAULT 'eur',

                            created_at              TIMESTAMPTZ NOT NULL DEFAULT now(),

                            CONSTRAINT fk_transfers_payment           FOREIGN KEY (payment_id)           REFERENCES payments(id)           ON DELETE RESTRICT,
                            CONSTRAINT fk_transfers_connected_account FOREIGN KEY (connected_account_id) REFERENCES connected_accounts(id) ON DELETE RESTRICT
);