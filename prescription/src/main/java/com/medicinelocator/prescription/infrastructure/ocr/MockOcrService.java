package com.medicinelocator.prescription.infrastructure.ocr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * Mock OCR provider for local development.
 * Active when: prescription.ocr.provider=mock
 */
@Component
@ConditionalOnProperty(name = "prescription.ocr.provider", havingValue = "mock")
public class MockOcrService implements OcrProvider {

    private static final Logger log = LoggerFactory.getLogger(MockOcrService.class);
    private static final String PROVIDER_NAME = "mock";

    @Override
    public String getProviderName() {
        return PROVIDER_NAME;
    }

    @Override
    public String extractText(MultipartFile file) {

        log.info("MockOcrService running for file={}",
                file != null ? file.getOriginalFilename() : "null");

        return """
                Patient: John Doe
                Date: 2024-01-15

                Rx:
                Amoxicillin 500mg — take 1 capsule 3x daily for 7 days
                Paracetamol 1000mg — take 2 tablets every 6 hours as needed
                Ibuprofen 400mg — take 1 tablet with food
                Vitamin C 1000mg — take 1 tablet daily

                Dr. Smith
                """;
    }
}