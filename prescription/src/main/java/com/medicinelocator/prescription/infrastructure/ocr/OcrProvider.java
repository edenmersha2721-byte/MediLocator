package com.medicinelocator.prescription.infrastructure.ocr;

import com.medicinelocator.prescription.application.service.OcrService;


public interface OcrProvider extends OcrService {

    String getProviderName();
}