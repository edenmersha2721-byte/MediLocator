package com.medicinelocator.search.presentation.controller;

import com.medicinelocator.search.application.command.IndexMedicineCommand;
import com.medicinelocator.search.application.command.handler.IndexMedicineHandler;
import com.medicinelocator.search.application.dto.request.IndexMedicineRequest;
import com.medicinelocator.search.application.mapper.IndexMapper;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/index")
public class IndexController {

    private static final Logger log = LoggerFactory.getLogger(IndexController.class);

    private final IndexMedicineHandler indexMedicineHandler;
    private final IndexMapper indexMapper;

    public IndexController(IndexMedicineHandler indexMedicineHandler,
                           IndexMapper indexMapper) {
        this.indexMedicineHandler = indexMedicineHandler;
        this.indexMapper = indexMapper;
    }


    @PostMapping("/medicine")
    public ResponseEntity<Void> indexMedicine(@Valid @RequestBody IndexMedicineRequest request) {
        log.debug("IndexController: received index request for medicineId={} pharmacyId={}",
                request.getMedicineId(), request.getPharmacyId());

        IndexMedicineCommand command = indexMapper.toCommand(request);
        indexMedicineHandler.handle(command);

        return ResponseEntity.noContent().build();
    }
}