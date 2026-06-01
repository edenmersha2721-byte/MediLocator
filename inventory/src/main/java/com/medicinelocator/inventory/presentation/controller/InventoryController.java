package com.medicinelocator.inventory.presentation.controller;

import com.medicinelocator.inventory.application.command.*;
import com.medicinelocator.inventory.application.command.handler.*;
import com.medicinelocator.inventory.application.dto.request.AddMedicineRequest;
import com.medicinelocator.inventory.application.dto.request.UpdateStockRequest;
import com.medicinelocator.inventory.application.dto.response.*;
import com.medicinelocator.inventory.application.mapper.MedicineMapper;
import com.medicinelocator.inventory.application.query.*;
import com.medicinelocator.inventory.application.query.handler.*;
import com.medicinelocator.inventory.domain.model.Medicine;
import com.medicinelocator.inventory.infrastructure.security.CurrentUser;
import com.medicinelocator.inventory.infrastructure.security.CurrentUserProvider;
import com.medicinelocator.inventory.infrastructure.security.SecurityUtils;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
@RestController
@Validated
public class InventoryController {

    private final AddMedicineHandler addMedicineHandler;
    private final UpdateStockHandler updateStockHandler;
    private final DeductStockHandler deductStockHandler;
    private final RemoveMedicineHandler removeMedicineHandler;
    private final GetPharmacyInventoryHandler getPharmacyInventoryHandler;
    private final GetMedicineByIdHandler getMedicineByIdHandler;
    private final SearchMedicinesHandler searchMedicinesHandler;
    private final MedicineMapper medicineMapper;
    private final CurrentUserProvider currentUserProvider;
    private final SecurityUtils securityUtils;

    public InventoryController(AddMedicineHandler addMedicineHandler,
                               UpdateStockHandler updateStockHandler,
                               DeductStockHandler deductStockHandler,
                               RemoveMedicineHandler removeMedicineHandler,
                               GetPharmacyInventoryHandler getPharmacyInventoryHandler,
                               GetMedicineByIdHandler getMedicineByIdHandler,
                               SearchMedicinesHandler searchMedicinesHandler,
                               MedicineMapper medicineMapper,
                               CurrentUserProvider currentUserProvider,
                               SecurityUtils securityUtils) {
        this.addMedicineHandler = addMedicineHandler;
        this.updateStockHandler = updateStockHandler;
        this.deductStockHandler = deductStockHandler;
        this.removeMedicineHandler = removeMedicineHandler;
        this.getPharmacyInventoryHandler = getPharmacyInventoryHandler;
        this.getMedicineByIdHandler = getMedicineByIdHandler;
        this.searchMedicinesHandler = searchMedicinesHandler;
        this.medicineMapper = medicineMapper;
        this.currentUserProvider = currentUserProvider;
        this.securityUtils = securityUtils;
    }


    @PostMapping("/pharmacies/{pharmacyId}/medicines")
    public ResponseEntity<MedicineResponse> addMedicine(
            @PathVariable UUID pharmacyId,
            @Valid @RequestBody AddMedicineRequest request) {

        CurrentUser currentUser = currentUserProvider.getCurrentUser();
        securityUtils.requirePharmacyOwnership(currentUser, pharmacyId);

        AddMedicineCommand command = medicineMapper.toAddMedicineCommand(pharmacyId, request);
        Medicine medicine = addMedicineHandler.handle(command);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(medicineMapper.toMedicineResponse(medicine));
    }

    @PutMapping("/pharmacies/{pharmacyId}/medicines/{medicineId}")
    public ResponseEntity<MedicineResponse> updateMedicine(
            @PathVariable UUID pharmacyId,
            @PathVariable UUID medicineId,
            @Valid @RequestBody UpdateStockRequest request) {

        CurrentUser currentUser = currentUserProvider.getCurrentUser();
        securityUtils.requirePharmacyOwnership(currentUser, pharmacyId);

        UpdateStockCommand command = medicineMapper.toUpdateStockCommand(pharmacyId, medicineId, request);
        Medicine medicine = updateStockHandler.handle(command);
        return ResponseEntity.ok(medicineMapper.toMedicineResponse(medicine));
    }


    @DeleteMapping("/pharmacies/{pharmacyId}/medicines/{medicineId}")
    public ResponseEntity<MessageResponse> removeMedicine(
            @PathVariable UUID pharmacyId,
            @PathVariable UUID medicineId) {

        CurrentUser currentUser = currentUserProvider.getCurrentUser();
        securityUtils.requirePharmacyOwnership(currentUser, pharmacyId);

        removeMedicineHandler.handle(new RemoveMedicineCommand(pharmacyId, medicineId));
        return ResponseEntity.ok(new MessageResponse("Medicine removed successfully"));
    }


    @PostMapping("/pharmacies/{pharmacyId}/medicines/{medicineId}/deduct")
    public ResponseEntity<MedicineResponse> deductStock(
            @PathVariable UUID pharmacyId,
            @PathVariable UUID medicineId,
            @RequestParam @Positive(message = "Amount must be positive") int amount) {

        CurrentUser currentUser = currentUserProvider.getCurrentUser();
        securityUtils.requirePharmacyOwnership(currentUser, pharmacyId);

        DeductStockCommand command = new DeductStockCommand(pharmacyId, medicineId, amount);
        Medicine medicine = deductStockHandler.handle(command);
        return ResponseEntity.ok(medicineMapper.toMedicineResponse(medicine));
    }


    @GetMapping("/pharmacies/{pharmacyId}/medicines")
    public ResponseEntity<PharmacyInventoryResponse> getPharmacyInventory(
            @PathVariable UUID pharmacyId,
            @RequestParam(defaultValue = "false") boolean availableOnly,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {

        CurrentUser currentUser = currentUserProvider.getCurrentUser();
        securityUtils.requirePharmacyOwnership(currentUser, pharmacyId);

        GetPharmacyInventoryQuery query = new GetPharmacyInventoryQuery(
                pharmacyId, availableOnly, page, size);
        return ResponseEntity.ok(getPharmacyInventoryHandler.handle(query));
    }


    @GetMapping("/pharmacies/{pharmacyId}/medicines/{medicineId}")
    public ResponseEntity<MedicineResponse> getMedicineById(
            @PathVariable UUID pharmacyId,
            @PathVariable UUID medicineId) {

        CurrentUser currentUser = currentUserProvider.getCurrentUser();
        securityUtils.requirePharmacyOwnership(currentUser, pharmacyId);

        GetMedicineByIdQuery query = new GetMedicineByIdQuery(pharmacyId, medicineId);
        return ResponseEntity.ok(getMedicineByIdHandler.handle(query));
    }



    @GetMapping("/medicines/search")
    public ResponseEntity<PagedResponse<MedicineSearchResponse>> searchMedicines(
            @RequestParam(required = false) String medicineName,
            @RequestParam(required = false) String brandName,
            @RequestParam(required = false) String genericName,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Boolean availableOnly,
            @RequestParam(required = false) Boolean requiresPrescription,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {

        CurrentUser currentUser = currentUserProvider.getCurrentUser();
        securityUtils.requireAuthenticated(currentUser);

        SearchMedicinesQuery query = new SearchMedicinesQuery(
                medicineName, brandName, genericName, category,
                availableOnly, requiresPrescription, page, size);

        return ResponseEntity.ok(searchMedicinesHandler.handle(query));
    }
}