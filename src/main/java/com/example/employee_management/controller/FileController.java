package com.example.employee_management.controller;

import com.example.employee_management.service.FileService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
public class FileController {

    @Autowired
    private FileService fileService;

    // Upload file
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> uploadFile(
            @RequestParam("file") MultipartFile file) {

        String fileName = fileService.uploadFile(file);

        Map<String, String> response = new HashMap<>();
        response.put("message", "File uploaded successfully");
        response.put("fileName", fileName);

        return ResponseEntity.ok(response);
    }
}