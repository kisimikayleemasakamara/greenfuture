package com.ewomen.greenfuture.controller;

import com.ewomen.greenfuture.service.FileUploadService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
public class FileUploadController {

    private final FileUploadService fileUploadService;

    public FileUploadController(
            FileUploadService fileUploadService) {
        this.fileUploadService = fileUploadService;
    }

    @PreAuthorize("hasAuthority('can_upload_files')")
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(
            @RequestParam("file") MultipartFile file) {

        String filename = fileUploadService.uploadFile(file);

        return ResponseEntity.ok(filename);
    }
}