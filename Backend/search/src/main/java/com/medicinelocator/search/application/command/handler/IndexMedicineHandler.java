package com.medicinelocator.search.application.command.handler;

import com.medicinelocator.search.application.command.IndexMedicineCommand;
import com.medicinelocator.search.application.service.UpsertSearchIndexService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
public class IndexMedicineHandler {

    private static final Logger log = LoggerFactory.getLogger(IndexMedicineHandler.class);

    private final UpsertSearchIndexService upsertSearchIndexService;

    public IndexMedicineHandler(UpsertSearchIndexService upsertSearchIndexService) {
        this.upsertSearchIndexService = upsertSearchIndexService;
    }

    @Transactional
    public void handle(IndexMedicineCommand command) {
        log.debug("IndexMedicineHandler: upserting medicineId={} pharmacyId={} available={}",
                command.getMedicineId(), command.getPharmacyId(), command.isAvailable());

        upsertSearchIndexService.upsert(command);

        log.info("Search index upserted: medicineId={} medicineName='{}' pharmacyId={}",
                command.getMedicineId(), command.getMedicineName(), command.getPharmacyId());
    }
}