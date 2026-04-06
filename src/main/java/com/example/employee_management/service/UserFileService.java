package com.example.employee_management.service;

import com.example.employee_management.entity.User;
import com.example.employee_management.entity.UserFile;
import com.example.employee_management.repository.UserFileRepository;
import com.example.employee_management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class UserFileService {

    @Autowired
    private UserFileRepository userFileRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public UserFile uploadFileForUser(Long userId, MultipartFile file) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        UserFile userFile = UserFile.builder()
                .fileName(file.getOriginalFilename())
                .fileType(file.getContentType())
                .data(file.getBytes())
                .user(user)
                .build();

        UserFile savedFile = userFileRepository.save(userFile);
        
        // Update user's profileImageId to reference this file
        user.setProfileImageId(savedFile.getId());
        userRepository.save(user);
        
        return savedFile;
    }

    @Transactional
    public UserFile uploadFileByUserEmail(String email, MultipartFile file) throws IOException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        UserFile userFile = UserFile.builder()
                .fileName(file.getOriginalFilename())
                .fileType(file.getContentType())
                .data(file.getBytes())
                .user(user)
                .build();

        return userFileRepository.save(userFile);
    }

    public List<UserFile> getUserFiles(Long userId) {
        return userFileRepository.findByUserId(userId);
    }

    public UserFile getFileById(Long fileId) {
        return userFileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found with id: " + fileId));
    }

    public List<UserFile> getAllFiles() {
        return userFileRepository.findAll();
    }
}
