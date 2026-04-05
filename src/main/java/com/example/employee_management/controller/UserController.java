package com.example.employee_management.controller;

import com.example.employee_management.entity.UserFile;
import com.example.employee_management.service.UserFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserFileService userFileService;

    // Upload file for a specific user
    @PostMapping("/{id}/upload")
    public ResponseEntity<Map<String, Object>> uploadUserFile(
            @PathVariable("id") Long id,
            @RequestParam("file") MultipartFile file) throws IOException {

        UserFile userFile = userFileService.uploadFileForUser(id, file);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "File uploaded successfully for user id: " + id);
        response.put("fileId", userFile.getId());
        response.put("fileName", userFile.getFileName());

        return ResponseEntity.ok(response);
    }

    // Get list of files for a specific user
    @GetMapping("/{id}/files")
    public ResponseEntity<List<Map<String, Object>>> getUserFiles(@PathVariable("id") Long id) {
        List<UserFile> files = userFileService.getUserFiles(id);
        
        List<Map<String, Object>> response = files.stream().map(file -> {
            Map<String, Object> map = new HashMap<>();
            map.put("fileId", file.getId());
            map.put("fileName", file.getFileName());
            map.put("fileType", file.getFileType());
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    // Download/View file content
    @GetMapping("/files/{fileId}")
    public ResponseEntity<byte[]> getFile(@PathVariable("fileId") Long fileId) {
        UserFile userFile = userFileService.getFileById(fileId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + userFile.getFileName() + "\"")
                .contentType(MediaType.parseMediaType(userFile.getFileType()))
                .body(userFile.getData());
    }
}
