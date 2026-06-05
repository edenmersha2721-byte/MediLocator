-- ===================================================================
-- 1. EXTENSIONS INITIALIZATION
-- ===================================================================
CREATE EXTENSION IF NOT EXISTS postgis;
CREATE EXTENSION IF NOT EXISTS pg_trgm;
CREATE EXTENSION IF NOT EXISTS unaccent;
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- ===================================================================
-- 2. READ MODEL SCHEMA (Search Index Target Table)
-- ===================================================================
CREATE TABLE IF NOT EXISTS medicine_search_index (
                                                     id                    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    medicine_id           UUID NOT NULL UNIQUE,
    medicine_name         VARCHAR(255) NOT NULL,
    generic_name          VARCHAR(255),
    brand_name            VARCHAR(255),
    category              VARCHAR(100),
    description           TEXT,
    requires_prescription BOOLEAN NOT NULL DEFAULT FALSE,
    price                 NUMERIC(12,2) NOT NULL DEFAULT 0.00,
    stock_quantity        INT NOT NULL DEFAULT 0,
    available             BOOLEAN NOT NULL DEFAULT FALSE,
    active                BOOLEAN NOT NULL DEFAULT TRUE,
    pharmacy_id           UUID NOT NULL,
    pharmacy_name         VARCHAR(255) NOT NULL,
    address               TEXT NOT NULL,
    city                  VARCHAR(150) NOT NULL,
    latitude              DOUBLE PRECISION NOT NULL,
    longitude             DOUBLE PRECISION NOT NULL,
    location              GEOGRAPHY(Point, 4326) NOT NULL,
    search_vector         TSVECTOR,
    last_synced_at        TIMESTAMP NOT NULL DEFAULT NOW(),
    created_at            TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at            TIMESTAMP NOT NULL DEFAULT NOW()
    );

-- ===================================================================
-- 3. PERFORMANCE AND GEOSPATIAL INDEXES
-- ===================================================================
CREATE INDEX IF NOT EXISTS idx_msi_location ON medicine_search_index USING GIST (location);
CREATE INDEX IF NOT EXISTS idx_msi_medicine_name_trgm ON medicine_search_index USING GIN (medicine_name gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_msi_generic_name_trgm ON medicine_search_index USING GIN (generic_name gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_msi_brand_name_trgm ON medicine_search_index USING GIN (brand_name gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_msi_search_vector ON medicine_search_index USING GIN (search_vector);

-- ===================================================================
-- 4. ENVIRONMENT PROFILE OPTIMIZATION
-- ===================================================================
ALTER DATABASE search_db SET pg_trgm.similarity_threshold = 0.1;