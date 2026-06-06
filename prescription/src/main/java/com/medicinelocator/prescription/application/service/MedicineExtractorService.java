package com.medicinelocator.prescription.application.service;

import java.util.List;


public interface MedicineExtractorService {

    List<String> extract(String rawOcrText);
}