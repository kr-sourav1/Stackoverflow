package com.stackoverflow.beta.controller;

import com.stackoverflow.beta.service.impl.StorageService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller for handling file storage operations.
 */
@RestController
@RequestMapping("/file")
public class StorageController {

    private static final Logger logger = LoggerFactory.getLogger(StorageController.class);
    private final StorageService storageService;

    @Autowired
    public StorageController(StorageService storageService) {
        this.storageService = storageService;
    }

    /**
     * Endpoint for uploading a file.
     *
     * @param file The file to upload.
     * @return A ResponseEntity containing a message indicating the status of the upload operation.
     */
    @PostMapping(value = "/upload",  consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        logger.info("Upload request received for file: {}", file.getOriginalFilename());

        if (file.isEmpty()) {
            logger.error("File is empty: {}", file.getOriginalFilename());
            return ResponseEntity.badRequest().body("File cannot be empty.");
        }

        try {
            String fileUrl = storageService.uploadFile(file);
            logger.info("File uploaded successfully: {}", fileUrl);
            return ResponseEntity.ok("File uploaded successfully. URL: " + fileUrl);
        } catch (Exception e) {
            logger.error("File upload failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to upload file: " + e.getMessage());
        }
    }

    /**
     * Endpoint for downloading a file.
     *
     * @param fileName The name of the file to download.
     * @return A ResponseEntity containing the downloaded file data.
     */
    @GetMapping("/download/{fileName}")
    @Operation(summary = "Download a file", description = "Downloads a file by name")
    public ResponseEntity<?> downloadFile(@PathVariable @NotBlank String fileName) {
        logger.info("Download request received for file: {}", fileName);

        try {
            byte[] data = storageService.downloadFile(fileName);
            ByteArrayResource resource = new ByteArrayResource(data);

            logger.info("File downloaded successfully: {}", fileName);
            return ResponseEntity
                    .ok()
                    .contentLength(data.length)
                    .header("Content-Type", "application/octet-stream")
                    .header("Content-Disposition", "attachment; filename=\"" + fileName + "\"")
                    .body(resource);
        } catch (Exception e) {
            logger.error("Unexpected error during file download: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to download file: " + e.getMessage());
        }
    }
}
