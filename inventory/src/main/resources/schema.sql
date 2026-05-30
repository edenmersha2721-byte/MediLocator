CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE IF NOT EXISTS medicine_categories (
                                                   id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name         VARCHAR(150) NOT NULL UNIQUE,
    description  TEXT,
    active       BOOLEAN NOT NULL DEFAULT TRUE,
    created_at   TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP NOT NULL DEFAULT NOW()
    );

CREATE TABLE IF NOT EXISTS medicines (
                                         id                    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name                  VARCHAR(255) NOT NULL,
    generic_name          VARCHAR(255),
    brand_name            VARCHAR(255),
    description           TEXT,
    category_id           UUID NOT NULL REFERENCES medicine_categories(id),
    requires_prescription BOOLEAN NOT NULL DEFAULT FALSE,
    active                BOOLEAN NOT NULL DEFAULT TRUE,
    created_at            TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at            TIMESTAMP NOT NULL DEFAULT NOW()
    );

CREATE TABLE IF NOT EXISTS pharmacy_inventories (
                                                    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    pharmacy_id  UUID NOT NULL,
    medicine_id  UUID NOT NULL REFERENCES medicines(id),
    quantity     INT NOT NULL DEFAULT 0,
    unit_price   NUMERIC(10,2) NOT NULL DEFAULT 0.00,
    available    BOOLEAN NOT NULL DEFAULT FALSE,
    created_at   TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_pharmacy_medicine UNIQUE (pharmacy_id, medicine_id)
    );

CREATE INDEX IF NOT EXISTS idx_medicines_name           ON medicines(name);
CREATE INDEX IF NOT EXISTS idx_medicines_generic_name   ON medicines(generic_name);
CREATE INDEX IF NOT EXISTS idx_medicines_brand_name     ON medicines(brand_name);
CREATE INDEX IF NOT EXISTS idx_medicines_category_id    ON medicines(category_id);
CREATE INDEX IF NOT EXISTS idx_medicines_active         ON medicines(active);
CREATE INDEX IF NOT EXISTS idx_categories_name          ON medicine_categories(name);
CREATE INDEX IF NOT EXISTS idx_inventory_pharmacy       ON pharmacy_inventories(pharmacy_id);
CREATE INDEX IF NOT EXISTS idx_inventory_medicine       ON pharmacy_inventories(medicine_id);
CREATE INDEX IF NOT EXISTS idx_inventory_available      ON pharmacy_inventories(available);