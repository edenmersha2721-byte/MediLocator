-- Enable required PostgreSQL extensions
CREATE EXTENSION IF NOT EXISTS postgis;
CREATE EXTENSION IF NOT EXISTS pg_trgm;
CREATE EXTENSION IF NOT EXISTS unaccent;

-- ─── Search Read Model ────────────────────────────────────────────────────────
--
-- This table is the search service's OWN read model.
-- It is populated/synchronized from the inventory service via REST sync calls
-- or a lightweight background job. It is NOT a replica of the pharmacy_medicines
-- table — it is purpose-built for full-text + geo search.
--
CREATE TABLE IF NOT EXISTS medicine_search_index (
                                                     id                    UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    medicine_id           UUID          NOT NULL,
    medicine_name         VARCHAR(255)  NOT NULL,
    generic_name          VARCHAR(255),
    brand_name            VARCHAR(255),
    category              VARCHAR(100),
    description           TEXT,
    requires_prescription BOOLEAN       NOT NULL DEFAULT FALSE,
    price                 NUMERIC(12,2) NOT NULL DEFAULT 0.00,
    stock_quantity        INT           NOT NULL DEFAULT 0,
    available             BOOLEAN       NOT NULL DEFAULT FALSE,
    active                BOOLEAN       NOT NULL DEFAULT TRUE,
    pharmacy_id           UUID          NOT NULL,
    pharmacy_name         VARCHAR(255)  NOT NULL,
    address               TEXT          NOT NULL,
    city                  VARCHAR(150)  NOT NULL,
    latitude              DOUBLE PRECISION NOT NULL,
    longitude             DOUBLE PRECISION NOT NULL,
    -- PostGIS Geography Point — longitude first, then latitude
    location              GEOGRAPHY(Point, 4326) NOT NULL,
    -- Full-text search vector (automatically maintained by trigger)
    search_vector         TSVECTOR,
    last_synced_at        TIMESTAMP     NOT NULL DEFAULT NOW(),
    created_at            TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at            TIMESTAMP     NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_medicine_search_medicine_id UNIQUE (medicine_id)
    );

-- ─── GiST index for PostGIS spatial queries (ST_DWithin, ST_DistanceSphere)
CREATE INDEX IF NOT EXISTS idx_msi_location
    ON medicine_search_index USING GIST (location);

-- ─── GIN index for pg_trgm fuzzy/trigram search
CREATE INDEX IF NOT EXISTS idx_msi_medicine_name_trgm
    ON medicine_search_index USING GIN (medicine_name gin_trgm_ops);

CREATE INDEX IF NOT EXISTS idx_msi_generic_name_trgm
    ON medicine_search_index USING GIN (generic_name gin_trgm_ops);

CREATE INDEX IF NOT EXISTS idx_msi_brand_name_trgm
    ON medicine_search_index USING GIN (brand_name gin_trgm_ops);

-- ─── GIN index for full-text search vector
CREATE INDEX IF NOT EXISTS idx_msi_search_vector
    ON medicine_search_index USING GIN (search_vector);

-- ─── Standard B-Tree indexes
CREATE INDEX IF NOT EXISTS idx_msi_pharmacy_id    ON medicine_search_index(pharmacy_id);
CREATE INDEX IF NOT EXISTS idx_msi_available      ON medicine_search_index(available);
CREATE INDEX IF NOT EXISTS idx_msi_active         ON medicine_search_index(active);
CREATE INDEX IF NOT EXISTS idx_msi_category       ON medicine_search_index(category);
CREATE INDEX IF NOT EXISTS idx_msi_requires_rx    ON medicine_search_index(requires_prescription);

-- ─── Function to update tsvector automatically
CREATE OR REPLACE FUNCTION update_medicine_search_vector()
RETURNS TRIGGER AS $$
BEGIN
    NEW.search_vector :=
        setweight(to_tsvector('english', coalesce(NEW.medicine_name, '')), 'A') ||
        setweight(to_tsvector('english', coalesce(NEW.generic_name, '')),  'B') ||
        setweight(to_tsvector('english', coalesce(NEW.brand_name, '')),    'B') ||
        setweight(to_tsvector('english', coalesce(NEW.category, '')),      'C') ||
        setweight(to_tsvector('english', coalesce(NEW.description, '')),   'D');
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- ─── Trigger to maintain search_vector on insert/update
CREATE TRIGGER trg_medicine_search_vector
    BEFORE INSERT OR UPDATE ON medicine_search_index
                         FOR EACH ROW EXECUTE FUNCTION update_medicine_search_vector();

-- ─── Similarity threshold for pg_trgm (0.2 = tolerant fuzzy, 0.5 = stricter)
-- This can be changed per session with: SET pg_trgm.similarity_threshold = 0.3;
ALTER DATABASE medicine_locator_db SET pg_trgm.similarity_threshold = 0.2;