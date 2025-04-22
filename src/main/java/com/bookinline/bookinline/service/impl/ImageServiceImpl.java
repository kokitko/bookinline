package com.bookinline.bookinline.service.impl;

import com.bookinline.bookinline.service.ImageService;
import lombok.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class ImageServiceImpl implements ImageService {

    @Override
    public String uploadImage(MultipartFile file) {
        String uploadDir = System.getProperty("user.dir") + "/images/";
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            try {
                Files.createDirectories(uploadPath);
            } catch (IOException e) {
                throw new RuntimeException("Failed to create upload directory", e);
            }
        }
        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filepath = Paths.get(uploadDir, filename);
        try {
            Files.createDirectories(filepath.getParent());
            file.transferTo(filepath.toFile());
        } catch (Exception e) {
            throw new RuntimeException("Failed to store file " + filename, e);
        }
        return "/images/" + filename;
    }
}
