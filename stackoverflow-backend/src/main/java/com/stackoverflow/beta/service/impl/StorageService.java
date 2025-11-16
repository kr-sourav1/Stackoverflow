package com.stackoverflow.beta.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

@Slf4j
@Service
public class StorageService {
    @Value("${application.bucket.name}")
    private String bucketName;

    @Autowired
    private AmazonS3 s3Client;

    public String uploadFile(MultipartFile file) {
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        File fileObj = null;

        try {
            // Convert MultipartFile to File
            fileObj = convertMultiPartFileToFile(file);

            // Upload file to S3 bucket
            log.info("Uploading file: {}", fileName);
            s3Client.putObject(new PutObjectRequest(bucketName, fileName, fileObj));
            log.info("File uploaded successfully: {}", fileName);

            return fileName;
        } catch (AmazonS3Exception e) {
            log.error("Error occurred while uploading file to S3: {}", e.getMessage());
            throw new RuntimeException("Failed to upload file: " + fileName, e);
        } catch (IOException e) {
            log.error("I/O error occurred while processing file: {}", fileName, e);
            throw new RuntimeException("Error while processing file: " + fileName, e);
        } finally {
            if (fileObj != null && fileObj.exists()) {
                boolean deleted = fileObj.delete();
                if (deleted) {
                    log.info("Temporary file deleted successfully: {}", fileObj.getName());
                } else {
                    log.warn("Failed to delete temporary file: {}", fileObj.getName());
                }
            }
        }
    }

    public byte[] downloadFile(String fileName) {
        log.info("Attempting to download file: {}", fileName);
        try {
            S3Object s3Object = s3Client.getObject(bucketName, fileName);
            try (S3ObjectInputStream inputStream = s3Object.getObjectContent()) {
                log.info("File downloaded successfully: {}", fileName);
                return IOUtils.toByteArray(inputStream);
            }
        } catch (AmazonS3Exception e) {
            if ("NoSuchKey".equals(e.getErrorCode())) {
                log.error("File not found in bucket: {}. Key: {}", bucketName, fileName);
                throw new RuntimeException("File not found: " + fileName, e);
            }
            log.error("S3 error occurred while downloading file: {}", e.getMessage());
            throw new RuntimeException("Error downloading file: " + fileName, e);
        } catch (IOException e) {
            log.error("I/O error occurred while reading file: {}", fileName, e);
            throw new RuntimeException("Error reading file: " + fileName, e);
        }
    }

    private File convertMultiPartFileToFile(MultipartFile file) throws IOException {
        File convertedFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
            fos.write(file.getBytes());
        }
        return convertedFile;
    }
}
