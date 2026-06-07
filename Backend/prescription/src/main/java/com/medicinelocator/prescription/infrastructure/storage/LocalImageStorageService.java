package com.medicinelocator.prescription.infrastructure.storage;

import com.medicinelocator.prescription.application.service.ImageStoragePort;
import com.medicinelocator.prescription.domain.exception.InvalidPrescriptionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

/**
 * Stores uploaded prescription images on the local filesystem.
 * Upload directory is configurable via `prescription.storage.upload-dir`.
 *
 * In production, this can be replaced with an S3 adapter that implements
 * ImageStoragePort without changing any application layer code.
 */
@Component
public class LocalImageStorageService implements ImageStoragePort {

    private static final Logger log = LoggerFactory.getLogger(LocalImageStorageService.class);

    private static final List<String> ALLOWED_CONTENT_TYPES = List.of(
            "image/jpeg", "image/png", "image/jpg", "application/pdf"
    );

    private static final long MAX_FILE_SIZE_BYTES = 10L * 1024 * 1024; // 10 MB

    private final Path uploadRootDir;

    public LocalImageStorageService(
            @Value("${prescription.storage.upload-dir:./uploads/prescriptions}") String uploadDir) {
        this.uploadRootDir = Paths.get(uploadDir).toAbsolutePath().normalize();
        createDirectoryIfAbsent(this.uploadRootDir);
        log.info("Prescription storage directory: {}", this.uploadRootDir);
    }

    @Override
    public String store(MultipartFile file, String subDir) {
        validateFile(file);

        Path targetDir = uploadRootDir.resolve(subDir);
        createDirectoryIfAbsent(targetDir);

        String extension  = getExtension(file.getOriginalFilename());
        String fileName   = UUID.randomUUID() + "." + extension;
        Path   targetPath = targetDir.resolve(fileName);

        try {
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            log.debug("File stored: path={}", targetPath);
        } catch (IOException e) {
            throw new InvalidPrescriptionException(
                    "Failed to store prescription image: " + e.getMessage());
        }

        // Return a relative path that can be used as a URL reference
        return subDir + "/" + fileName;
    }

    @Override
    public void delete(String fileUrl) {
        if (fileUrl == null || fileUrl.isBlank()) {
            return;
        }
        try {
            Path filePath = uploadRootDir.resolve(fileUrl).normalize();
            Files.deleteIfExists(filePath);
            log.debug("File deleted: path={}", filePath);
        } catch (IOException e) {
            log.warn("Failed to delete file: url={} error={}", fileUrl, e.getMessage());
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidPrescriptionException("File is empty or null");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new InvalidPrescriptionException(
                    "Invalid file type: " + contentType
                            + ". Allowed types: " + ALLOWED_CONTENT_TYPES);
        }

        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new InvalidPrescriptionException(
                    "File size exceeds maximum allowed size of 10 MB. "
                            + "Uploaded size: " + (file.getSize() / 1024 / 1024) + " MB");
        }
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "bin";
        }
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
    }

    private void createDirectoryIfAbsent(Path dir) {
        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Cannot create upload directory: " + dir + " — " + e.getMessage(), e);
        }
    }
}