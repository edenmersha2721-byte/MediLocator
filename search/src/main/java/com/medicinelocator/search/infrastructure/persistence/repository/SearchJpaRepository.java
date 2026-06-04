package com.medicinelocator.search.infrastructure.persistence.repository;

import com.medicinelocator.search.infrastructure.persistence.entity.MedicineSearchIndexEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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

    /**
     * Fuzzy + full-text search WITHOUT location — ordered by pg_trgm similarity.
     * Searches medicine_name, generic_name, and brand_name using trigram matching.
     * Falls back to full-text search for lower similarity scores.
     * Only returns active, available medicines with stock > 0.
     */
    @Query(value = """
            SELECT
                m.medicine_id,
                m.medicine_name,
                m.generic_name,
                m.brand_name,
                m.category,
                m.requires_prescription,
                m.price,
                m.stock_quantity,
                m.available,
                m.pharmacy_id,
                m.pharmacy_name,
                m.address,
                m.city,
                m.latitude,
                m.longitude,
                0.0 AS distance_meters,
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
            """,
            nativeQuery = true)
    long countSearchMedicinesByNameNoLocation(
            @Param("searchTerm") String searchTerm,
            @Param("requiresPrescription") Boolean requiresPrescription,
            @Param("category") String category
    );

    /**
     * Fuzzy + full-text search WITH PostGIS proximity ordering.
     * Uses ST_DWithin for radius filtering and ST_DistanceSphere for ordering.
     * Results ordered by nearest pharmacy first, then by relevance.
     */
    @Query(value = """
            SELECT
                m.medicine_id,
                m.medicine_name,
                m.generic_name,
                m.brand_name,
                m.category,
                m.requires_prescription,
                m.price,
                m.stock_quantity,
                m.available,
                m.pharmacy_id,
                m.pharmacy_name,
                m.address,
                m.city,
                m.latitude,
                m.longitude,
                ST_DistanceSphere(
                    m.location::geometry,
                    ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)
                ) AS distance_meters
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

    /**
     * Find nearby pharmacies that have at least one available medicine.
     * Results ordered by distance (nearest first).
     */
    @Query(value = """
            SELECT
                m.pharmacy_id,
                m.pharmacy_name,
                m.address,
                m.city,
                m.latitude,
                m.longitude,
                ST_DistanceSphere(
                    m.location::geometry,
                    ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)
                ) AS distance_meters,
                COUNT(*) FILTER (WHERE m.available = true AND m.active = true) AS available_count
            FROM medicine_search_index m
            WHERE m.active = true
              AND m.available = true
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
            WHERE m.active = true
              AND m.available = true
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

    /**
     * Batch search for multiple medicine names (prescription use case).
     * Returns medicines matching ANY of the provided names with proximity ordering.
     */
    @Query(value = """
            SELECT
                m.medicine_id,
                m.medicine_name,
                m.generic_name,
                m.brand_name,
                m.category,
                m.requires_prescription,
                m.price,
                m.stock_quantity,
                m.available,
                m.pharmacy_id,
                m.pharmacy_name,
                m.address,
                m.city,
                m.latitude,
                m.longitude,
                CASE
                    WHEN :lat IS NOT NULL AND :lng IS NOT NULL THEN
                        ST_DistanceSphere(
                            m.location::geometry,
                            ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)
                        )
                    ELSE 0.0
                END AS distance_meters
            FROM medicine_search_index m
            WHERE m.active = true
              AND m.available = true
              AND m.stock_quantity > 0
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
            WHERE m.active = true
              AND m.available = true
              AND m.stock_quantity > 0
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