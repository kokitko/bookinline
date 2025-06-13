package com.bookinline.bookinline.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface S3Service {
    public String uploadFile(MultipartFile file) throws IOException;
    public String generatePresignedUrl(String key, int expirationInMinutes);
}
