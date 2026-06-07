CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TYPE prescription_status AS ENUM (
    'PENDING',
    'PROCESSING',
    'COMPLETED',
    'FAILED'
);

CREATE TABLE IF NOT EXISTS prescriptions (
                                             id           UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_id  UUID         NOT NULL,
    image_url    TEXT         NOT NULL,
    raw_text     TEXT,
    status       VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    latitude     DOUBLE PRECISION,
    longitude    DOUBLE PRECISION,
    created_at   TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP    NOT NULL DEFAULT NOW()
    );

CREATE TABLE IF NOT EXISTS prescription_items (
                                                  id               UUID    PRIMARY KEY DEFAULT gen_random_uuid(),
    prescription_id  UUID    NOT NULL REFERENCES prescriptions(id) ON DELETE CASCADE,
    medicine_name    VARCHAR(255) NOT NULL,
    created_at       TIMESTAMP NOT NULL DEFAULT NOW()
    );

CREATE INDEX IF NOT EXISTS idx_prescriptions_customer_id ON prescriptions(customer_id);
CREATE INDEX IF NOT EXISTS idx_prescriptions_status      ON prescriptions(status);
CREATE INDEX IF NOT EXISTS idx_prescription_items_pid    ON prescription_items(prescription_id);