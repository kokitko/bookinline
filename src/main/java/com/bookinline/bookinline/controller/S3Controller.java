package com.bookinline.bookinline.controller;

import com.bookinline.bookinline.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/s3")
public class S3Controller {
    @Autowired
    private S3Service s3Service;

    @GetMapping("/presigned-url")
    public Map<String, String> getPresignedUrl(@RequestParam String key, @RequestParam(defaultValue= "120") int expirationInMinutes) {
        String url = s3Service.generatePresignedUrl(key, expirationInMinutes);
        return Map.of("url", url);
    }
}
