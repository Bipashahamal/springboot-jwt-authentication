package com.example.employee_management.controller;

import com.example.employee_management.entity.UserFile;
import com.example.employee_management.service.UserFileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
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
    @Operation(
        summary = "Upload Profile Image for User", 
        description = "Upload a profile image for a specific user by user ID. The file will be stored in the database and the user's profileImageId will be updated.",
        requestBody = @RequestBody(
            required = true,
            content = @Content(
                mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                schema = @Schema(type = "object"),
                encoding = @Encoding(
                    name = "file",
                    contentType = "application/octet-stream"
                )
            )
        )
    )
    @PostMapping(value = "/{id}/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> uploadUserFile(
            @PathVariable("id") Long id,
            @RequestParam("file") MultipartFile file) throws IOException {

        if (file.isEmpty()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "File is empty");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        try {
            UserFile userFile = userFileService.uploadFileForUser(id, file);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Profile image uploaded successfully");
            response.put("userId", id);
            response.put("fileId", userFile.getId());
            response.put("fileName", userFile.getFileName());
            response.put("fileType", userFile.getFileType());
            response.put("status", "User profileImageId updated");

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.notFound().build();
        }
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
