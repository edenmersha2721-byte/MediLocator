package com.medicinelocator.prescription.application.service;

import org.springframework.web.multipart.MultipartFile;

public interface ImageStoragePort {


    String store(MultipartFile file, String subDir);

    void delete(String fileUrl);
}