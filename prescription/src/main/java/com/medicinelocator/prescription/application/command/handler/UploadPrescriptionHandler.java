package com.medicinelocator.prescription.application.command.handler;

import com.medicinelocator.prescription.application.command.UploadPrescriptionCommand;
import com.medicinelocator.prescription.application.dto.response.ExtractedMedicinesResponse;
import com.medicinelocator.prescription.application.service.ImageStoragePort;
import com.medicinelocator.prescription.application.service.MedicineExtractorService;
import com.medicinelocator.prescription.application.service.OcrService;
import com.medicinelocator.prescription.application.service.PrescriptionService;
import com.medicinelocator.prescription.application.service.SearchServiceClient;
import com.medicinelocator.prescription.domain.enums.PrescriptionStatus;
import com.medicinelocator.prescription.domain.exception.InvalidPrescriptionException;
import com.medicinelocator.prescription.domain.model.ExtractedMedicine;
import com.medicinelocator.prescription.domain.model.Prescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
public class UploadPrescriptionHandler {

    private static final Logger log = LoggerFactory.getLogger(UploadPrescriptionHandler.class);

    private final PrescriptionService prescriptionService;
    private final OcrService ocrService;
    private final MedicineExtractorService medicineExtractorService;
    private final ImageStoragePort imageStoragePort;
    private final SearchServiceClient searchServiceClient;

    public UploadPrescriptionHandler(PrescriptionService prescriptionService,
                                     OcrService ocrService,
                                     MedicineExtractorService medicineExtractorService,
                                     ImageStoragePort imageStoragePort,
                                     SearchServiceClient searchServiceClient) {
        this.prescriptionService = prescriptionService;
        this.ocrService = ocrService;
        this.medicineExtractorService = medicineExtractorService;
        this.imageStoragePort = imageStoragePort;
        this.searchServiceClient = searchServiceClient;
    }

    @Transactional
    public ExtractedMedicinesResponse handle(UploadPrescriptionCommand command) {
        validateFile(command);

        UUID prescriptionId = UUID.randomUUID();
        MDC.put("prescriptionId", prescriptionId.toString());
        MDC.put("customerId", command.getCustomerId().toString());

        log.info("Starting prescription upload: prescriptionId={} customerId={}",
                prescriptionId, command.getCustomerId());

        // STEP 1 — Store file
        String imageUrl = imageStoragePort.store(
                command.getFile(),
                command.getCustomerId().toString()
        );
        log.debug("File stored: imageUrl={}", imageUrl);

        // STEP 2 — Persist prescription record in PENDING state
        Prescription prescription = new Prescription(
                prescriptionId,
                command.getCustomerId(),
                imageUrl,
                null,
                PrescriptionStatus.PENDING,
                command.getLatitude(),
                command.getLongitude(),
                List.of(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        prescription = prescriptionService.save(prescription);

        // STEP 3 — OCR
        String rawText;
        try {
            prescription.markProcessing();
            prescriptionService.update(prescription);
            rawText = ocrService.extractText(command.getFile());
            log.debug("OCR completed: prescriptionId={} textLength={}",
                    prescriptionId, rawText != null ? rawText.length() : 0);
        } catch (Exception e) {
            log.error("OCR failed: prescriptionId={} error={}", prescriptionId, e.getMessage(), e);
            prescription.markFailed();
            prescriptionService.update(prescription);
            throw e;
        }

        // STEP 4 — Extract medicine names
        List<String> normalizedNames = medicineExtractorService.extract(rawText);
        log.info("Medicines extracted: prescriptionId={} count={} medicines={}",
                prescriptionId, normalizedNames.size(), normalizedNames);

        List<ExtractedMedicine> medicines = normalizedNames.stream()
                .map(name -> ExtractedMedicine.of(prescriptionId, name))
                .toList();

        prescription.completeProcessing(rawText, medicines);
        prescriptionService.update(prescription);

        // STEP 5 — Forward to Search Service
        Object pharmacyResults = null;
        if (!normalizedNames.isEmpty()) {
            try {
                pharmacyResults = searchServiceClient.searchMedicines(
                        normalizedNames,
                        command.getLatitude(),
                        command.getLongitude(),
                        command.getRadiusKm()
                );
                log.info("Search service call successful: prescriptionId={}", prescriptionId);
            } catch (Exception e) {
                log.warn("Search service call failed (non-fatal): prescriptionId={} error={}",
                        prescriptionId, e.getMessage());
                // Non-fatal: we still return the extracted medicines
            }
        }

        // STEP 6 — Build response
        ExtractedMedicinesResponse response = new ExtractedMedicinesResponse();
        response.setPrescriptionId(prescriptionId);
        response.setExtractedMedicines(normalizedNames);
        response.setPharmacyResults(pharmacyResults);
        response.setMessage(
                normalizedNames.isEmpty()
                        ? "No medicine names could be extracted from the prescription image"
                        : "Prescription processed successfully"
        );

        MDC.remove("prescriptionId");
        MDC.remove("customerId");

        return response;
    }

    private void validateFile(UploadPrescriptionCommand command) {
        if (command.getFile() == null || command.getFile().isEmpty()) {
            throw new InvalidPrescriptionException("Prescription file is required and must not be empty");
        }
    }
}