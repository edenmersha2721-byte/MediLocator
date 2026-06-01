package com.medicinelocator.inventory.presentation.controller;

import com.medicinelocator.inventory.application.command.*;
import com.medicinelocator.inventory.application.command.handler.*;
import com.medicinelocator.inventory.application.dto.request.*;
import com.medicinelocator.inventory.application.dto.response.*;
import com.medicinelocator.inventory.application.mapper.InventoryMapper;
import com.medicinelocator.inventory.application.mapper.MedicineMapper;
import com.medicinelocator.inventory.application.query.*;
import com.medicinelocator.inventory.application.query.handler.*;
import com.medicinelocator.inventory.domain.model.Medicine;
import com.medicinelocator.inventory.domain.model.PharmacyInventory;
import com.medicinelocator.inventory.infrastructure.security.CurrentUser;
import com.medicinelocator.inventory.infrastructure.security.CurrentUserProvider;
import com.medicinelocator.inventory.infrastructure.security.SecurityUtils;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final AddMedicineHandler addMedicineHandler;
    private final UpdateMedicineHandler updateMedicineHandler;
    private final RemoveMedicineHandler removeMedicineHandler;
    private final AddInventoryHandler addInventoryHandler;
    private final UpdateStockHandler updateStockHandler;
    private final DeductStockHandler deductStockHandler;
    private final CreateCategoryHandler createCategoryHandler;
    private final UpdateCategoryHandler updateCategoryHandler;
    private final GetMedicineByIdHandler getMedicineByIdHandler;
    private final GetPharmacyInventoryHandler getPharmacyInventoryHandler;
    private final SearchMedicinesHandler searchMedicinesHandler;
    private final GetAvailableMedicinesHandler getAvailableMedicinesHandler;
    private final GetAllCategoriesHandler getAllCategoriesHandler;
    private final MedicineMapper medicineMapper;
    private final InventoryMapper inventoryMapper;
    private final CurrentUserProvider currentUserProvider;
    private final SecurityUtils securityUtils;

    public InventoryController(AddMedicineHandler addMedicineHandler,
                               UpdateMedicineHandler updateMedicineHandler,
                               RemoveMedicineHandler removeMedicineHandler,
                               AddInventoryHandler addInventoryHandler,
                               UpdateStockHandler updateStockHandler,
                               DeductStockHandler deductStockHandler,
                               CreateCategoryHandler createCategoryHandler,
                               UpdateCategoryHandler updateCategoryHandler,
                               GetMedicineByIdHandler getMedicineByIdHandler,
                               GetPharmacyInventoryHandler getPharmacyInventoryHandler,
                               SearchMedicinesHandler searchMedicinesHandler,
                               GetAvailableMedicinesHandler getAvailableMedicinesHandler,
                               GetAllCategoriesHandler getAllCategoriesHandler,
                               MedicineMapper medicineMapper,
                               InventoryMapper inventoryMapper,
                               CurrentUserProvider currentUserProvider,
                               SecurityUtils securityUtils) {
        this.addMedicineHandler = addMedicineHandler;
        this.updateMedicineHandler = updateMedicineHandler;
        this.removeMedicineHandler = removeMedicineHandler;
        this.addInventoryHandler = addInventoryHandler;
        this.updateStockHandler = updateStockHandler;
        this.deductStockHandler = deductStockHandler;
        this.createCategoryHandler = createCategoryHandler;
        this.updateCategoryHandler = updateCategoryHandler;
        this.getMedicineByIdHandler = getMedicineByIdHandler;
        this.getPharmacyInventoryHandler = getPharmacyInventoryHandler;
        this.searchMedicinesHandler = searchMedicinesHandler;
        this.getAvailableMedicinesHandler = getAvailableMedicinesHandler;
        this.getAllCategoriesHandler = getAllCategoriesHandler;
        this.medicineMapper = medicineMapper;
        this.inventoryMapper = inventoryMapper;
        this.currentUserProvider = currentUserProvider;
        this.securityUtils = securityUtils;
    }

    // ─── MEDICINE CATALOG (ADMIN ONLY) ───────────────────────────────────────

    @PostMapping("/medicines")
    public ResponseEntity<MedicineResponse> createMedicine(
            @Valid @RequestBody AddMedicineRequest request) {
        CurrentUser currentUser = currentUserProvider.getCurrentUser();
        securityUtils.requireAdmin(currentUser);

        AddMedicineCommand command = medicineMapper.toAddMedicineCommand(request);
        Medicine medicine = addMedicineHandler.handle(command);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(medicineMapper.toMedicineResponse(medicine));
    }

    @PutMapping("/medicines/{medicineId}")
    public ResponseEntity<MedicineResponse> updateMedicine(
            @PathVariable UUID medicineId,
            @Valid @RequestBody UpdateMedicineRequest request) {
        CurrentUser currentUser = currentUserProvider.getCurrentUser();
        securityUtils.requireAdmin(currentUser);

        UpdateMedicineCommand command = medicineMapper.toUpdateMedicineCommand(medicineId, request);
        Medicine medicine = updateMedicineHandler.handle(command);
        return ResponseEntity.ok(medicineMapper.toMedicineResponse(medicine));
    }

    @DeleteMapping("/medicines/{medicineId}")
    public ResponseEntity<MessageResponse> removeMedicine(@PathVariable UUID medicineId) {
        CurrentUser currentUser = currentUserProvider.getCurrentUser();
        securityUtils.requireAdmin(currentUser);

        removeMedicineHandler.handle(new RemoveMedicineCommand(medicineId));
        return ResponseEntity.ok(new MessageResponse("Medicine deactivated successfully"));
    }

    // ─── MEDICINE QUERIES (ALL ROLES) ────────────────────────────────────────

    @GetMapping("/medicines/{medicineId}")
    public ResponseEntity<MedicineResponse> getMedicineById(@PathVariable UUID medicineId) {
        currentUserProvider.getCurrentUser(); // Validates gateway headers
        MedicineResponse response = getMedicineByIdHandler.handle(new GetMedicineByIdQuery(medicineId));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/medicines")
    public ResponseEntity<PagedResponse<MedicineResponse>> searchMedicines(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String brandName,
            @RequestParam(required = false) String genericName,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) Boolean requiresPrescription,
            @RequestParam(defaultValue = "true") boolean activeOnly,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        currentUserProvider.getCurrentUser();

        SearchMedicinesQuery query = new SearchMedicinesQuery(
                name, brandName, genericName, categoryId, requiresPrescription, activeOnly, page, size);
        return ResponseEntity.ok(searchMedicinesHandler.handle(query));
    }

    @GetMapping("/medicines/{medicineId}/availability")
    public ResponseEntity<PagedResponse<MedicineAvailabilityResponse>> getMedicineAvailability(
            @PathVariable UUID medicineId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        currentUserProvider.getCurrentUser();

        GetAvailableMedicinesQuery query = new GetAvailableMedicinesQuery(medicineId, page, size);
        return ResponseEntity.ok(getAvailableMedicinesHandler.handle(query));
    }

    // ─── CATEGORY MANAGEMENT (ADMIN ONLY) ────────────────────────────────────

    @PostMapping("/categories")
    public ResponseEntity<CategoryResponse> createCategory(
            @Valid @RequestBody CreateCategoryRequest request) {
        CurrentUser currentUser = currentUserProvider.getCurrentUser();
        securityUtils.requireAdmin(currentUser);

        MedicineCategoryDomain category = createCategoryHandler.handle(
                medicineMapper.toCreateCategoryCommand(request));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(medicineMapper.toCategoryResponse(category));
    }

    @PutMapping("/categories/{categoryId}")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable UUID categoryId,
            @Valid @RequestBody UpdateCategoryRequest request) {
        CurrentUser currentUser = currentUserProvider.getCurrentUser();
        securityUtils.requireAdmin(currentUser);

        MedicineCategoryDomain category = updateCategoryHandler.handle(
                medicineMapper.toUpdateCategoryCommand(categoryId, request));
        return ResponseEntity.ok(medicineMapper.toCategoryResponse(category));
    }

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryResponse>> getAllCategories(
            @RequestParam(defaultValue = "true") boolean activeOnly) {
        currentUserProvider.getCurrentUser();
        List<CategoryResponse> categories = getAllCategoriesHandler.handle(
                new GetAllCategoriesQuery(activeOnly));
        return ResponseEntity.ok(categories);
    }

    // ─── PHARMACY INVENTORY MANAGEMENT (PHARMACY ONLY) ───────────────────────

    @PostMapping("/pharmacy/{pharmacyId}/inventory")
    public ResponseEntity<InventoryItemResponse> addInventory(
            @PathVariable UUID pharmacyId,
            @Valid @RequestBody AddInventoryRequest request) {
        CurrentUser currentUser = currentUserProvider.getCurrentUser();
        securityUtils.requirePharmacyOwnership(currentUser, pharmacyId);

        AddInventoryCommand command = inventoryMapper.toAddInventoryCommand(pharmacyId, request);
        PharmacyInventory inventory = addInventoryHandler.handle(command);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(inventoryMapper.toInventoryItemResponse(inventory));
    }

    @PutMapping("/pharmacy/{pharmacyId}/inventory")
    public ResponseEntity<InventoryItemResponse> updateStock(
            @PathVariable UUID pharmacyId,
            @Valid @RequestBody UpdateStockRequest request) {
        CurrentUser currentUser = currentUserProvider.getCurrentUser();
        securityUtils.requirePharmacyOwnership(currentUser, pharmacyId);

        UpdateStockCommand command = inventoryMapper.toUpdateStockCommand(pharmacyId, request);
        PharmacyInventory inventory = updateStockHandler.handle(command);
        return ResponseEntity.ok(inventoryMapper.toInventoryItemResponse(inventory));
    }

    @PostMapping("/pharmacy/{pharmacyId}/inventory/deduct")
    public ResponseEntity<InventoryItemResponse> deductStock(
            @PathVariable UUID pharmacyId,
            @RequestParam UUID medicineId,
            @RequestParam @Min(1) int amount) {
        CurrentUser currentUser = currentUserProvider.getCurrentUser();
        securityUtils.requirePharmacyOwnership(currentUser, pharmacyId);

        DeductStockCommand command = new DeductStockCommand(pharmacyId, medicineId, amount);
        PharmacyInventory inventory = deductStockHandler.handle(command);
        return ResponseEntity.ok(inventoryMapper.toInventoryItemResponse(inventory));
    }

    @GetMapping("/pharmacy/{pharmacyId}/inventory")
    public ResponseEntity<PagedResponse<InventoryItemResponse>> getPharmacyInventory(
            @PathVariable UUID pharmacyId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        CurrentUser currentUser = currentUserProvider.getCurrentUser();
        securityUtils.requirePharmacyOwnership(currentUser, pharmacyId);

        GetPharmacyInventoryQuery query = new GetPharmacyInventoryQuery(pharmacyId, page, size);
        return ResponseEntity.ok(getPharmacyInventoryHandler.handle(query));
    }
}