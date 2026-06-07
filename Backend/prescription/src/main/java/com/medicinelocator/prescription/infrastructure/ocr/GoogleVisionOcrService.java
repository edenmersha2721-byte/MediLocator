package com.medicinelocator.prescription.infrastructure.ocr;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.medicinelocator.prescription.domain.exception.OcrProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.List;
import java.util.Map;

/**
 * Google Cloud Vision API OCR adapter.
 * Active when: prescription.ocr.provider=google_vision
 */
@Component
@ConditionalOnProperty(
        name = "prescription.ocr.provider",
        havingValue = "google_vision"
)public class GoogleVisionOcrService implements OcrProvider {

    private static final Logger log = LoggerFactory.getLogger(GoogleVisionOcrService.class);
    private static final String PROVIDER_NAME = "google_vision";

    private final String apiKey;
    private final String endpoint;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public GoogleVisionOcrService(
            @Value("${ocr.google.api-key}") String apiKey,
            @Value("${ocr.google.endpoint}") String endpoint,
            RestTemplate restTemplate,
            ObjectMapper objectMapper
    ) {
        this.apiKey = apiKey;
        this.endpoint = endpoint;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public String getProviderName() {
        return PROVIDER_NAME;
    }

    @Override
    public String extractText(MultipartFile file) {

        log.debug("GoogleVisionOcrService processing file={}", file.getOriginalFilename());

        byte[] imageBytes;
        try {
            imageBytes = file.getBytes();
        } catch (Exception e) {
            throw new OcrProcessingException("Failed to read uploaded file bytes", e);
        }

        String base64Image = Base64.getEncoder().encodeToString(imageBytes);
        String requestBody = buildRequestBody(base64Image);
        String url = endpoint + "?key=" + apiKey;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response =
                    restTemplate.postForEntity(url, entity, String.class);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new OcrProcessingException(
                        "Google Vision API returned error: " + response.getStatusCode()
                );
            }

            return parseResponse(response.getBody());

        } catch (RestClientException e) {
            throw new OcrProcessingException("Google Vision API call failed", e);
        }
    }

    private String buildRequestBody(String base64Image) {
        try {
            Map<String, Object> requestBody = Map.of(
                    "requests", List.of(
                            Map.of(
                                    "image", Map.of("content", base64Image),
                                    "features", List.of(
                                            Map.of("type", "DOCUMENT_TEXT_DETECTION")
                                    )
                            )
                    )
            );

            return objectMapper.writeValueAsString(requestBody);
        } catch (Exception e) {
            throw new OcrProcessingException("Failed to build request body", e);
        }
    }

    private String parseResponse(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode responses = root.path("responses");

            if (!responses.isArray() || responses.isEmpty()) {
                log.warn("Google Vision API returned empty response");
                return "";
            }

            JsonNode firstResponse = responses.get(0);

            JsonNode error = firstResponse.path("error");
            if (!error.isMissingNode()) {
                throw new OcrProcessingException(
                        "Google Vision API error: " + error.path("message").asText()
                );
            }

            JsonNode fullText = firstResponse.path("fullTextAnnotation").path("text");
            if (!fullText.asText("").isBlank()) {
                return fullText.asText();
            }

            JsonNode annotations = firstResponse.path("textAnnotations");
            if (annotations.isArray() && annotations.size() > 0) {
                return annotations.get(0).path("description").asText("");
            }

            return "";

        } catch (Exception e) {
            throw new OcrProcessingException("Failed to parse OCR response", e);
        }
    }
}