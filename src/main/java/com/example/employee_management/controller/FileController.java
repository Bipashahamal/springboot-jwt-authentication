package com.example.employee_management.controller;

import com.example.employee_management.entity.UserProfile;
import com.example.employee_management.service.UserProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/files")
@Tag(name = "Files", description = "File retrieval endpoints")
public class FileController {

    @Autowired
    private UserProfileService userProfileService;

    // Get all files metadata (without downloading data)
    @Operation(summary = "Get All Files Metadata", description = "Retrieve metadata for all uploaded files without file content")
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllFiles() {
        List<UserProfile> files = userProfileService.getAllFiles();
        
        List<Map<String, Object>> response = files.stream().map(file -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", file.getId());
            map.put("fileName", file.getFileName());
            map.put("fileType", file.getFileType());
            map.put("filePath", file.getFilePath());
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    // Download/View specific file
    @Operation(summary = "Get Specific File", description = "Download or view a specific file by ID")
    @GetMapping("/{fileId}")
    public ResponseEntity<byte[]> getFile(@PathVariable("fileId") Long fileId) throws IOException {
        UserProfile userProfile = userProfileService.getFileById(fileId);

        byte[] fileContent = Files.readAllBytes(Paths.get(userProfile.getFilePath()));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, 
                        "attachment; filename=\"" + userProfile.getFileName() + "\"")
                .contentType(MediaType.parseMediaType(userProfile.getFileType()))
                .body(fileContent);
    }
}

