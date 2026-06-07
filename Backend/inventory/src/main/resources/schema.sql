CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Each row is a medicine owned entirely by one pharmacy.
-- There is NO global medicine catalog.
CREATE TABLE IF NOT EXISTS pharmacy_medicines (
                                                  id                    UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    pharmacy_id           UUID        NOT NULL,
    medicine_name         VARCHAR(255) NOT NULL,
    generic_name          VARCHAR(255),
    brand_name            VARCHAR(255),
    category              VARCHAR(100),
    description           TEXT,
    price                 NUMERIC(12, 2) NOT NULL DEFAULT 0.00,
    stock_quantity        INT         NOT NULL DEFAULT 0,
    available             BOOLEAN     NOT NULL DEFAULT FALSE,
    requires_prescription BOOLEAN     NOT NULL DEFAULT FALSE,
    expiry_date           DATE,
    active                BOOLEAN     NOT NULL DEFAULT TRUE,
    created_at            TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at            TIMESTAMP   NOT NULL DEFAULT NOW()
    );

-- Indexes for cross-pharmacy search (LIKE queries on indexed columns)
CREATE INDEX IF NOT EXISTS idx_pm_pharmacy_id      ON pharmacy_medicines(pharmacy_id);
CREATE INDEX IF NOT EXISTS idx_pm_medicine_name    ON pharmacy_medicines(medicine_name);
CREATE INDEX IF NOT EXISTS idx_pm_generic_name     ON pharmacy_medicines(generic_name);
CREATE INDEX IF NOT EXISTS idx_pm_brand_name       ON pharmacy_medicines(brand_name);
CREATE INDEX IF NOT EXISTS idx_pm_category         ON pharmacy_medicines(category);
CREATE INDEX IF NOT EXISTS idx_pm_available        ON pharmacy_medicines(available);
CREATE INDEX IF NOT EXISTS idx_pm_active           ON pharmacy_medicines(active);
CREATE INDEX IF NOT EXISTS idx_pm_expiry_date      ON pharmacy_medicines(expiry_date);
CREATE INDEX IF NOT EXISTS idx_pm_requires_rx      ON pharmacy_medicines(requires_prescription);

-- Prevent a pharmacy adding the exact same medicine name twice (case-insensitive prevented at app layer)
CREATE UNIQUE INDEX IF NOT EXISTS uq_pharmacy_medicine_name
    ON pharmacy_medicines(pharmacy_id, LOWER(medicine_name));