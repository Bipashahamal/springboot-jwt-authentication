package com.example.employee_management.controller;

import com.example.employee_management.entity.UserFile;
import com.example.employee_management.service.UserFileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/files")
@Tag(name = "Files", description = "File retrieval endpoints")
public class FileController {

    @Autowired
    private UserFileService userFileService;

    // Get all files metadata (without downloading data)
    @Operation(summary = "Get All Files Metadata", description = "Retrieve metadata for all uploaded files without file content")
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllFiles() {
        List<UserFile> files = userFileService.getAllFiles();
        
        List<Map<String, Object>> response = files.stream().map(file -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", file.getId());
            map.put("fileName", file.getFileName());
            map.put("fileType", file.getFileType());
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    // Download/View specific file
    @Operation(summary = "Get Specific File", description = "Download or view a specific file by ID")
    @GetMapping("/{fileId}")
    public ResponseEntity<byte[]> getFile(@PathVariable("fileId") Long fileId) {
        UserFile userFile = userFileService.getFileById(fileId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, 
                        "attachment; filename=\"" + userFile.getFileName() + "\"")
                .contentType(MediaType.parseMediaType(userFile.getFileType()))
                .body(userFile.getData());
    }
}
