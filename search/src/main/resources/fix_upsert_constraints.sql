-- Ensure the unique constraint on medicine_id exists for ON CONFLICT to work.
-- The V1 migration already creates this, but this is a safety guard.
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conname = 'uq_medicine_search_medicine_id'
          AND conrelid = 'medicine_search_index'::regclass
    ) THEN
ALTER TABLE medicine_search_index
    ADD CONSTRAINT uq_medicine_search_medicine_id UNIQUE (medicine_id);
END IF;
END $$;

-- Ensure pg_trgm similarity threshold is set
DO $$
BEGIN
    PERFORM set_config('pg_trgm.similarity_threshold', '0.2', false);
EXCEPTION WHEN others THEN
    NULL;
END $$;