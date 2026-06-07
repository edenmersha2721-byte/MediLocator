package com.medicinelocator.prescription.presentation.controller;

import com.medicinelocator.prescription.application.command.UploadPrescriptionCommand;
import com.medicinelocator.prescription.application.command.handler.UploadPrescriptionHandler;
import com.medicinelocator.prescription.application.dto.response.CustomerPrescriptionsResponse;
import com.medicinelocator.prescription.application.dto.response.ExtractedMedicinesResponse;
import com.medicinelocator.prescription.application.dto.response.PrescriptionResponse;
import com.medicinelocator.prescription.application.query.GetCustomerPrescriptionsQuery;
import com.medicinelocator.prescription.application.query.GetPrescriptionQuery;
import com.medicinelocator.prescription.application.query.handler.GetCustomerPrescriptionsHandler;
import com.medicinelocator.prescription.application.query.handler.GetPrescriptionHandler;
import com.medicinelocator.prescription.domain.exception.InvalidPrescriptionException;
import com.medicinelocator.prescription.infrastructure.security.CurrentUser;
import com.medicinelocator.prescription.infrastructure.security.CurrentUserProvider;
import com.medicinelocator.prescription.infrastructure.security.SecurityUtils;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

/**
 * Prescription REST controller.
 *
 * Endpoints:
 *   POST /api/prescriptions/upload                 → upload + OCR + search forward
 *   GET  /api/prescriptions/{id}                   → get single prescription
 *   GET  /api/prescriptions/customer/{customerId}  → list customer's prescriptions
 */
@RestController
@RequestMapping("/api/prescriptions")
@Validated
public class PrescriptionController {

    private static final Logger log = LoggerFactory.getLogger(PrescriptionController.class);

    private static final List<String> ALLOWED_CONTENT_TYPES = List.of(
            "image/jpeg", "image/png", "image/jpg", "application/pdf"
    );

    private final UploadPrescriptionHandler uploadPrescriptionHandler;
    private final GetPrescriptionHandler getPrescriptionHandler;
    private final GetCustomerPrescriptionsHandler getCustomerPrescriptionsHandler;
    private final CurrentUserProvider currentUserProvider;
    private final SecurityUtils securityUtils;

    public PrescriptionController(UploadPrescriptionHandler uploadPrescriptionHandler,
                                  GetPrescriptionHandler getPrescriptionHandler,
                                  GetCustomerPrescriptionsHandler getCustomerPrescriptionsHandler,
                                  CurrentUserProvider currentUserProvider,
                                  SecurityUtils securityUtils) {
        this.uploadPrescriptionHandler = uploadPrescriptionHandler;
        this.getPrescriptionHandler = getPrescriptionHandler;
        this.getCustomerPrescriptionsHandler = getCustomerPrescriptionsHandler;
        this.currentUserProvider = currentUserProvider;
        this.securityUtils = securityUtils;
    }

    /**
     * POST /api/prescriptions/upload
     *
     * Upload a prescription image.
     * Flow: store → OCR → extract medicines → forward to Search Service → return results.
     *
     * @param file       JPG, PNG or PDF prescription image
     * @param latitude   user latitude (optional, forwarded to Search Service)
     * @param longitude  user longitude (optional, forwarded to Search Service)
     * @param radiusKm   search radius (optional, forwarded to Search Service)
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ExtractedMedicinesResponse> uploadPrescription(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "latitude",  required = false)
            @DecimalMin("-90.0")  @DecimalMax("90.0")  Double latitude,
            @RequestParam(value = "longitude", required = false)
            @DecimalMin("-180.0") @DecimalMax("180.0") Double longitude,
            @RequestParam(value = "radiusKm",  required = false)
            @DecimalMin("0.1")    @DecimalMax("200.0") Double radiusKm) {

        CurrentUser currentUser = currentUserProvider.getCurrentUser();
        securityUtils.requireCustomerOrAdmin(currentUser);

        validateFileContentType(file);

        MDC.put("customerId", currentUser.getUserId().toString());
        log.info("Prescription upload initiated: customerId={} fileName={} size={}",
                currentUser.getUserId(),
                file.getOriginalFilename(),
                file.getSize());

        UploadPrescriptionCommand command = new UploadPrescriptionCommand(
                currentUser.getUserId(),
                file,
                latitude,
                longitude,
                radiusKm
        );

        ExtractedMedicinesResponse response = uploadPrescriptionHandler.handle(command);

        MDC.remove("customerId");

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/prescriptions/{id}
     * Returns a prescription by ID. Only the owning customer or admin may access it.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PrescriptionResponse> getPrescriptionById(@PathVariable UUID id) {
        CurrentUser currentUser = currentUserProvider.getCurrentUser();

        GetPrescriptionQuery query = new GetPrescriptionQuery(id, currentUser.getUserId());
        PrescriptionResponse response = getPrescriptionHandler.handle(query);

        // Enforce ownership after fetch
        securityUtils.requireCustomerOwnershipOrAdmin(currentUser, response.getCustomerId());

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/prescriptions/customer/{customerId}
     * Returns all prescriptions for a customer.
     * Customers can only access their own; admins can access any.
     */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<CustomerPrescriptionsResponse> getCustomerPrescriptions(
            @PathVariable UUID customerId) {
        CurrentUser currentUser = currentUserProvider.getCurrentUser();

        GetCustomerPrescriptionsQuery query = new GetCustomerPrescriptionsQuery(
                customerId,
                currentUser.getUserId(),
                currentUser.getRole()
        );

        return ResponseEntity.ok(getCustomerPrescriptionsHandler.handle(query));
    }

    // ─── Private helpers ──────────────────────────────────────────────────────

    private void validateFileContentType(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidPrescriptionException("File must not be empty");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new InvalidPrescriptionException(
                    "Invalid file type: '" + contentType
                            + "'. Accepted types: image/jpeg, image/png, application/pdf");
        }
    }
}