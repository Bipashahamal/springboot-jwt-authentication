package com.example.employee_management.controller;

import com.example.employee_management.entity.UserProfile;
import com.example.employee_management.service.UserProfileService;
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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private com.example.employee_management.service.AuthService authService;

    // Upload file for a specific user
    @Operation(summary = "Upload Profile Image for User", description = "Upload a profile image for a specific user by user ID. The file will be stored in the database and the user's profileImageId will be updated.", requestBody = @RequestBody(required = true, content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE, schema = @Schema(type = "object"), encoding = @Encoding(name = "file", contentType = "application/octet-stream"))))
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
            UserProfile userProfile = userProfileService.uploadFileForUser(id, file);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Profile image uploaded successfully");
            response.put("userId", id);
            response.put("fileId", userProfile.getId());
            response.put("fileName", userProfile.getFileName());
            response.put("fileType", userProfile.getFileType());
            response.put("filePath", userProfile.getFilePath()); // Added link as requested
            response.put("status", "User profileImageId updated");

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(404).body(errorResponse);
        }
    }

    // Get list of profiles for a specific user
    @GetMapping("/{id}/files")
    public ResponseEntity<List<Map<String, Object>>> getUserProfiles(@PathVariable("id") Long id) {
        List<UserProfile> files = userProfileService.getUserProfiles(id);

        List<Map<String, Object>> response = files.stream().map(file -> {
            Map<String, Object> map = new HashMap<>();
            map.put("fileId", file.getId());
            map.put("fileName", file.getFileName());
            map.put("fileType", file.getFileType());
            map.put("filePath", file.getFilePath()); // Added link as requested
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    // Download/View file content
    @GetMapping("/files/{fileId}")
    public ResponseEntity<byte[]> getFile(@PathVariable("fileId") Long fileId) throws IOException {
        UserProfile userProfile = userProfileService.getFileById(fileId);

        byte[] fileContent = Files.readAllBytes(Paths.get(userProfile.getFilePath()));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + userProfile.getFileName() + "\"")
                .contentType(MediaType.parseMediaType(userProfile.getFileType()))
                .body(fileContent);
    }

    // ✅ DELETE USER (ADMIN ONLY)
    @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        authService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }
}
