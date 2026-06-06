package com.medicinelocator.prescription.infrastructure.ocr;

import com.medicinelocator.prescription.application.service.MedicineExtractorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;


@Component
public class OcrResultParser implements MedicineExtractorService {

    private static final Logger log = LoggerFactory.getLogger(OcrResultParser.class);

    // Dosage units to strip (with optional numbers before them)
    private static final Pattern DOSAGE_PATTERN = Pattern.compile(
            "\\b\\d+\\.?\\d*\\s*(mg|mcg|g|ml|l|iu|mmol|tablet|tablets|tab|tabs|" +
                    "capsule|capsules|cap|caps|drops|drop|cream|ointment|syrup|injection|" +
                    "suppository|patch|inhaler|puff|puffs|unit|units|%|vial|ampoule)\\b",
            Pattern.CASE_INSENSITIVE
    );

    // Standalone numbers
    private static final Pattern NUMBER_PATTERN = Pattern.compile("\\b\\d+\\b");

    // Common instruction words to remove from medicine lines
    private static final Set<String> INSTRUCTION_WORDS = Set.of(
            "take", "daily", "twice", "three", "times", "every", "hours", "days",
            "weeks", "with", "food", "water", "morning", "evening", "night",
            "bedtime", "needed", "prescribed", "directed", "apply", "use", "once",
            "dose", "doses", "repeat", "after", "before", "meal", "meals", "as",
            "until", "for", "and", "or", "the", "a", "an", "of", "to", "in",
            "x", "rx", "no", "refills", "doctor", "dr", "patient", "date", "sig"
    );

    // Lines that are obviously not medicine names
    private static final Pattern NOISE_LINE_PATTERN = Pattern.compile(
            "(?i)^(patient|name|date|dr\\.?|doctor|clinic|hospital|address|phone|" +
                    "dob|age|sex|gender|diagnosis|prescription|refill|pharmacy|signature|" +
                    "signed|dispensed|qty|quantity|instructions?|rx#?|\\s*\\d{1,2}[/-]\\d{1,2}).*"
    );

    // Minimum length for a medicine name to be considered valid
    private static final int MIN_MEDICINE_NAME_LENGTH = 3;

    @Override
    public List<String> extract(String rawOcrText) {
        if (rawOcrText == null || rawOcrText.isBlank()) {
            log.debug("OcrResultParser: empty input");
            return List.of();
        }

        String[] lines = rawOcrText.split("[\\r\\n]+");
        Set<String> seen = new LinkedHashSet<>();
        List<String> result = new ArrayList<>();

        for (String line : lines) {
            String processed = processLine(line.trim());
            if (processed.isBlank() || processed.length() < MIN_MEDICINE_NAME_LENGTH) {
                continue;
            }
            if (!seen.contains(processed)) {
                seen.add(processed);
                result.add(processed);
            }
        }

        log.debug("OcrResultParser: extracted {} medicines from {} lines",
                result.size(), lines.length);
        return result;
    }

    private String processLine(String line) {
        // Skip noise lines immediately
        if (NOISE_LINE_PATTERN.matcher(line).matches()) {
            return "";
        }

        // Remove everything after a dash or hyphen (dosage instructions often follow)
        int dashIdx = line.indexOf(" — ");
        if (dashIdx == -1) dashIdx = line.indexOf(" - ");
        if (dashIdx > 0) {
            line = line.substring(0, dashIdx).trim();
        }

        // Remove dosage patterns
        line = DOSAGE_PATTERN.matcher(line).replaceAll("").trim();

        // Remove standalone numbers
        line = NUMBER_PATTERN.matcher(line).replaceAll("").trim();

        // Remove punctuation except letters and spaces
        line = line.replaceAll("[^a-zA-Z\\s]", " ").trim();

        // Remove instruction words word-by-word
        String[] words = line.split("\\s+");
        StringBuilder name = new StringBuilder();
        for (String word : words) {
            if (!word.isBlank() && !INSTRUCTION_WORDS.contains(word.toLowerCase())) {
                if (!name.isEmpty()) name.append(" ");
                name.append(word);
            }
        }

        return name.toString().trim().toLowerCase();
    }
}