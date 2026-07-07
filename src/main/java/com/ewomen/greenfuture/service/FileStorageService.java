package com.ewomen.greenfuture.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileStorageService {

    private final String uploadDir = "uploads/";

    public String saveFile(
            MultipartFile file) {

        try {

            // Create uploads directory if missing
            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {

                Files.createDirectories(
                        uploadPath);
            }

            // Generate unique filename
            String filename = System.currentTimeMillis()
                    + "_"
                    + file.getOriginalFilename();

            Path filePath = uploadPath.resolve(
                    filename);

            // Save file
            Files.copy(
                    file.getInputStream(),
                    filePath,
                    StandardCopyOption.REPLACE_EXISTING);

            // Return stored path
            return "/uploads/" + filename;

        } catch (IOException e) {

            throw new RuntimeException(
                    "Failed to save file",
                    e);
        }
    }
}