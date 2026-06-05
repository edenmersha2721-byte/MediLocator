-- ===================================================================
-- DATA INITIALIZATION FOR CQRS SEARCH INDEX
-- ===================================================================
INSERT INTO medicine_search_index (
    id, medicine_id, medicine_name, generic_name, brand_name, category, description,
    requires_prescription, price, stock_quantity, available, active,
    pharmacy_id, pharmacy_name, address, city, latitude, longitude, location,
    search_vector, last_synced_at, created_at, updated_at
) VALUES (
             gen_random_uuid(),
             'e8fcbb95-d43e-4568-afaa-0e77b412ef0b', -- mock medicine_id
             'paracetamol',
             'acetaminophen',
             'panadol',
             'ANALGESIC',
             'Fever and pain reducer',
             false,
             5.50,
             150,
             true, -- available
             true, -- active
             '400f3e66-6d3f-4fc9-8d6e-735405cb4f9d', -- mock pharmacy_id
             'Bole Pharmacy',
             'Bole Road',
             'Addis Ababa',
             8.9912,
             38.7634,
             ST_SetSRID(ST_MakePoint(38.7634, 8.9912), 4326)::geography, -- Longitude first
             setweight(to_tsvector('english', 'paracetamol'), 'A') ||
             setweight(to_tsvector('english', 'acetaminophen'), 'B') ||
             setweight(to_tsvector('english', 'panadol'), 'B') ||
             setweight(to_tsvector('english', 'ANALGESIC'), 'C') ||
             setweight(to_tsvector('english', 'Fever and pain reducer'), 'D'),
             NOW(), NOW(), NOW()
         ) ON CONFLICT (medicine_id) DO NOTHING;