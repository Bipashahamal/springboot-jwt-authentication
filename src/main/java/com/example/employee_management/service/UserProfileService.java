package com.example.employee_management.service;

import com.example.employee_management.entity.User;
import com.example.employee_management.entity.UserProfile;
import com.example.employee_management.repository.UserProfileRepository;
import com.example.employee_management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class UserProfileService {

    private static final String UPLOAD_DIR = "/Users/bipaashahamal/Documents/Sample folder";

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public UserProfile uploadFileByUserEmail(String email, MultipartFile file) throws IOException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        return processAndSaveFile(user, file, false);
    }

    private UserProfile processAndSaveFile(User user, MultipartFile file, boolean isPrimaryProfileImage)
            throws IOException {
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);

        // Save physically to disk
        Files.write(filePath, file.getBytes());

        UserProfile userProfile = UserProfile.builder()
                .fileName(file.getOriginalFilename())
                .fileType(file.getContentType())
                .filePath(filePath.toAbsolutePath().toString())
                .user(user)
                .build();

        UserProfile savedFile = userProfileRepository.save(userProfile);

        if (isPrimaryProfileImage) {
            // Update user's profileImageId to reference this file
            user.setProfileImageId(savedFile.getId());
            userRepository.save(user);
        }

        return savedFile;
    }

    public List<UserProfile> getUserProfiles(Long userId) {
        return userProfileRepository.findByUserId(userId);
    }

    public UserProfile getFileById(Long fileId) {
        return userProfileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found with id: " + fileId));
    }

    public List<UserProfile> getAllFiles() {
        return userProfileRepository.findAll();
    }

    @Transactional
    public UserProfile uploadFileForUser(Long id, MultipartFile file) throws IOException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        return processAndSaveFile(user, file, true);
    }
}
