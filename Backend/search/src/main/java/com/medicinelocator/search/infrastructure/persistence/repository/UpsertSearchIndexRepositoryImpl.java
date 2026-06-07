package com.medicinelocator.search.infrastructure.persistence.repository;

import com.medicinelocator.search.application.command.IndexMedicineCommand;
import com.medicinelocator.search.application.service.UpsertSearchIndexService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component
public class UpsertSearchIndexRepositoryImpl implements UpsertSearchIndexService {

    private static final Logger log = LoggerFactory.getLogger(UpsertSearchIndexRepositoryImpl.class);

    private final SearchJpaRepository jpaRepository;

    public UpsertSearchIndexRepositoryImpl(SearchJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void upsert(IndexMedicineCommand command) {
        log.debug("Upserting search index: medicineId={} pharmacyId={}",
                command.getMedicineId(), command.getPharmacyId());

        jpaRepository.upsertMedicineIndex(
                command.getMedicineId(),
                command.getMedicineName(),
                command.getGenericName(),
                command.getBrandName(),
                command.getCategory(),
                command.getDescription(),
                command.isRequiresPrescription(),
                command.getPrice(),
                command.getStockQuantity(),
                command.isAvailable(),
                command.isActive(),
                command.getPharmacyId(),
                command.getPharmacyName(),
                command.getAddress(),
                command.getCity(),
                command.getLatitude(),
                command.getLongitude()
        );

        log.debug("Search index upsert complete: medicineId={}", command.getMedicineId());
    }
}