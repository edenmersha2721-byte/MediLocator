package com.medicinelocator.search.infrastructure.persistence.repository;

import com.medicinelocator.search.infrastructure.persistence.entity.MedicineSearchIndexEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SearchJpaRepository extends JpaRepository<MedicineSearchIndexEntity, UUID> {

    Optional<MedicineSearchIndexEntity> findByMedicineId(UUID medicineId);

    boolean existsByMedicineId(UUID medicineId);

    @Modifying
    @Query("DELETE FROM MedicineSearchIndexEntity m WHERE m.medicineId = :medicineId")
    void deleteByMedicineId(@Param("medicineId") UUID medicineId);


    @Modifying
    @Query(value = """
            INSERT INTO medicine_search_index (
                id, medicine_id, medicine_name, generic_name, brand_name,
                category, description, requires_prescription, price,
                stock_quantity, available, active,
                pharmacy_id, pharmacy_name, address, city,
                latitude, longitude, location,
                last_synced_at, created_at, updated_at
            ) VALUES (
                gen_random_uuid(),
                :medicineId, :medicineName, :genericName, :brandName,
                :category, :description, :requiresPrescription, :price,
                :stockQuantity, :available, :active,
                :pharmacyId, :pharmacyName, :address, :city,
                :latitude, :longitude,
                ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)::geography,
                NOW(), NOW(), NOW()
            )
            ON CONFLICT (medicine_id) DO UPDATE SET
                medicine_name         = EXCLUDED.medicine_name,
                generic_name          = EXCLUDED.generic_name,
                brand_name            = EXCLUDED.brand_name,
                category              = EXCLUDED.category,
                description           = EXCLUDED.description,
                requires_prescription = EXCLUDED.requires_prescription,
                price                 = EXCLUDED.price,
                stock_quantity        = EXCLUDED.stock_quantity,
                available             = EXCLUDED.available,
                active                = EXCLUDED.active,
                pharmacy_id           = EXCLUDED.pharmacy_id,
                pharmacy_name         = EXCLUDED.pharmacy_name,
                address               = EXCLUDED.address,
                city                  = EXCLUDED.city,
                latitude              = EXCLUDED.latitude,
                longitude             = EXCLUDED.longitude,
                location              = ST_SetSRID(ST_MakePoint(EXCLUDED.longitude, EXCLUDED.latitude), 4326)::geography,
                last_synced_at        = NOW(),
                updated_at            = NOW()
            """,
            nativeQuery = true)
    void upsertMedicineIndex(
            @Param("medicineId")           UUID medicineId,
            @Param("medicineName")         String medicineName,
            @Param("genericName")          String genericName,
            @Param("brandName")            String brandName,
            @Param("category")             String category,
            @Param("description")          String description,
            @Param("requiresPrescription") boolean requiresPrescription,
            @Param("price")                BigDecimal price,
            @Param("stockQuantity")        int stockQuantity,
            @Param("available")            boolean available,
            @Param("active")               boolean active,
            @Param("pharmacyId")           UUID pharmacyId,
            @Param("pharmacyName")         String pharmacyName,
            @Param("address")              String address,
            @Param("city")                 String city,
            @Param("latitude")             double latitude,
            @Param("longitude")            double longitude
    );

    // ─── SEARCH QUERIES (unchanged from before) ───────────────────────────────

    @Query(value = """
            SELECT
                m.medicine_id, m.medicine_name, m.generic_name, m.brand_name,
                m.category, m.requires_prescription, m.price, m.stock_quantity,
                m.available, m.pharmacy_id, m.pharmacy_name, m.address, m.city,
                m.latitude, m.longitude, 0.0 AS distance_meters,
                GREATEST(
                    similarity(m.medicine_name, :searchTerm),
                    similarity(coalesce(m.generic_name, ''), :searchTerm),
                    similarity(coalesce(m.brand_name, ''), :searchTerm)
                ) AS relevance_score
            FROM medicine_search_index m
            WHERE m.active = true
              AND m.available = true
              AND m.stock_quantity > 0
              AND (
                  m.medicine_name % :searchTerm
                  OR m.generic_name % :searchTerm
                  OR m.brand_name  % :searchTerm
                  OR m.search_vector @@ plainto_tsquery('english', :searchTerm)
              )
              AND (:requiresPrescription IS NULL OR m.requires_prescription = :requiresPrescription)
              AND (:category IS NULL OR LOWER(m.category) LIKE LOWER(CONCAT('%', :category, '%')))
            ORDER BY relevance_score DESC
            LIMIT :limit OFFSET :offset
            """,
            nativeQuery = true)
    List<Object[]> searchMedicinesByNameNoLocation(
            @Param("searchTerm") String searchTerm,
            @Param("requiresPrescription") Boolean requiresPrescription,
            @Param("category") String category,
            @Param("limit") int limit,
            @Param("offset") int offset
    );

    @Query(value = """
            SELECT COUNT(*) FROM medicine_search_index m
            WHERE m.active = true AND m.available = true AND m.stock_quantity > 0
              AND (
                  m.medicine_name % :searchTerm
                  OR m.generic_name % :searchTerm
                  OR m.brand_name  % :searchTerm
                  OR m.search_vector @@ plainto_tsquery('english', :searchTerm)
              )
              AND (:requiresPrescription IS NULL OR m.requires_prescription = :requiresPrescription)
              AND (:category IS NULL OR LOWER(m.category) LIKE LOWER(CONCAT('%', :category, '%')))
            """,
            nativeQuery = true)
    long countSearchMedicinesByNameNoLocation(
            @Param("searchTerm") String searchTerm,
            @Param("requiresPrescription") Boolean requiresPrescription,
            @Param("category") String category
    );

    @Query(value = """
            SELECT
                m.medicine_id, m.medicine_name, m.generic_name, m.brand_name,
                m.category, m.requires_prescription, m.price, m.stock_quantity,
                m.available, m.pharmacy_id, m.pharmacy_name, m.address, m.city,
                m.latitude, m.longitude,
                ST_DistanceSphere(
                    m.location::geometry,
                    ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)
                ) AS distance_meters
            FROM medicine_search_index m
            WHERE m.active = true AND m.available = true AND m.stock_quantity > 0
              AND (
                  m.medicine_name % :searchTerm
                  OR m.generic_name % :searchTerm
                  OR m.brand_name  % :searchTerm
                  OR m.search_vector @@ plainto_tsquery('english', :searchTerm)
              )
              AND (:requiresPrescription IS NULL OR m.requires_prescription = :requiresPrescription)
              AND (:category IS NULL OR LOWER(m.category) LIKE LOWER(CONCAT('%', :category, '%')))
              AND (:radiusMetres IS NULL OR
                  ST_DWithin(
                      m.location,
                      ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)::geography,
                      :radiusMetres
                  )
              )
            ORDER BY distance_meters ASC
            LIMIT :limit OFFSET :offset
            """,
            nativeQuery = true)
    List<Object[]> searchMedicinesByNameWithLocation(
            @Param("searchTerm") String searchTerm,
            @Param("lat") double lat,
            @Param("lng") double lng,
            @Param("radiusMetres") Double radiusMetres,
            @Param("requiresPrescription") Boolean requiresPrescription,
            @Param("category") String category,
            @Param("limit") int limit,
            @Param("offset") int offset
    );

    @Query(value = """
            SELECT COUNT(*) FROM medicine_search_index m
            WHERE m.active = true AND m.available = true AND m.stock_quantity > 0
              AND (
                  m.medicine_name % :searchTerm
                  OR m.generic_name % :searchTerm
                  OR m.brand_name  % :searchTerm
                  OR m.search_vector @@ plainto_tsquery('english', :searchTerm)
              )
              AND (:requiresPrescription IS NULL OR m.requires_prescription = :requiresPrescription)
              AND (:category IS NULL OR LOWER(m.category) LIKE LOWER(CONCAT('%', :category, '%')))
              AND (:radiusMetres IS NULL OR
                  ST_DWithin(
                      m.location,
                      ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)::geography,
                      :radiusMetres
                  )
              )
            """,
            nativeQuery = true)
    long countSearchMedicinesByNameWithLocation(
            @Param("searchTerm") String searchTerm,
            @Param("lat") double lat,
            @Param("lng") double lng,
            @Param("radiusMetres") Double radiusMetres,
            @Param("requiresPrescription") Boolean requiresPrescription,
            @Param("category") String category
    );

    @Query(value = """
            SELECT
                m.pharmacy_id, m.pharmacy_name, m.address, m.city, m.latitude, m.longitude,
                ST_DistanceSphere(
                    m.location::geometry,
                    ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)
                ) AS distance_meters,
                COUNT(*) FILTER (WHERE m.available = true AND m.active = true) AS available_count
            FROM medicine_search_index m
            WHERE m.active = true AND m.available = true
              AND ST_DWithin(
                  m.location,
                  ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)::geography,
                  :radiusMetres
              )
            GROUP BY m.pharmacy_id, m.pharmacy_name, m.address,
                     m.city, m.latitude, m.longitude, m.location
            ORDER BY distance_meters ASC
            LIMIT :limit OFFSET :offset
            """,
            nativeQuery = true)
    List<Object[]> findNearbyPharmacies(
            @Param("lat") double lat,
            @Param("lng") double lng,
            @Param("radiusMetres") double radiusMetres,
            @Param("limit") int limit,
            @Param("offset") int offset
    );

    @Query(value = """
            SELECT COUNT(DISTINCT m.pharmacy_id) FROM medicine_search_index m
            WHERE m.active = true AND m.available = true
              AND ST_DWithin(
                  m.location,
                  ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)::geography,
                  :radiusMetres
              )
            """,
            nativeQuery = true)
    long countNearbyPharmacies(
            @Param("lat") double lat,
            @Param("lng") double lng,
            @Param("radiusMetres") double radiusMetres
    );

    @Query(value = """
            SELECT
                m.medicine_id, m.medicine_name, m.generic_name, m.brand_name,
                m.category, m.requires_prescription, m.price, m.stock_quantity,
                m.available, m.pharmacy_id, m.pharmacy_name, m.address, m.city,
                m.latitude, m.longitude,
                CASE
                    WHEN :lat IS NOT NULL AND :lng IS NOT NULL THEN
                        ST_DistanceSphere(
                            m.location::geometry,
                            ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)
                        )
                    ELSE 0.0
                END AS distance_meters
            FROM medicine_search_index m
            WHERE m.active = true AND m.available = true AND m.stock_quantity > 0
              AND EXISTS (
                  SELECT 1 FROM unnest(CAST(:medicineNames AS TEXT[])) AS search_name
                  WHERE m.medicine_name % search_name
                     OR m.generic_name  % search_name
                     OR m.brand_name    % search_name
                     OR m.search_vector @@ plainto_tsquery('english', search_name)
              )
              AND (
                  :lat IS NULL OR :lng IS NULL OR :radiusMetres IS NULL OR
                  ST_DWithin(
                      m.location,
                      ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)::geography,
                      :radiusMetres
                  )
              )
            ORDER BY distance_meters ASC, m.medicine_name ASC
            LIMIT :limit OFFSET :offset
            """,
            nativeQuery = true)
    List<Object[]> searchByPrescriptionMedicines(
            @Param("medicineNames") String[] medicineNames,
            @Param("lat") Double lat,
            @Param("lng") Double lng,
            @Param("radiusMetres") Double radiusMetres,
            @Param("limit") int limit,
            @Param("offset") int offset
    );

    @Query(value = """
            SELECT COUNT(*) FROM medicine_search_index m
            WHERE m.active = true AND m.available = true AND m.stock_quantity > 0
              AND EXISTS (
                  SELECT 1 FROM unnest(CAST(:medicineNames AS TEXT[])) AS search_name
                  WHERE m.medicine_name % search_name
                     OR m.generic_name  % search_name
                     OR m.brand_name    % search_name
                     OR m.search_vector @@ plainto_tsquery('english', search_name)
              )
              AND (
                  :lat IS NULL OR :lng IS NULL OR :radiusMetres IS NULL OR
                  ST_DWithin(
                      m.location,
                      ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)::geography,
                      :radiusMetres
                  )
              )
            """,
            nativeQuery = true)
    long countByPrescriptionMedicines(
            @Param("medicineNames") String[] medicineNames,
            @Param("lat") Double lat,
            @Param("lng") Double lng,
            @Param("radiusMetres") Double radiusMetres
    );
}