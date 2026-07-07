package com.ewomen.greenfuture.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileUploadService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    public String uploadFile(MultipartFile file) {

        try {

            String filename = System.currentTimeMillis()
                    + "_" + file.getOriginalFilename();

            Path uploadPath = Paths.get(uploadDir);

            // Create uploads folder if not exists
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(filename);

            Files.copy(file.getInputStream(), filePath);

            return filename;

        } catch (IOException e) {
            throw new RuntimeException("File upload failed");
        }
    }
}