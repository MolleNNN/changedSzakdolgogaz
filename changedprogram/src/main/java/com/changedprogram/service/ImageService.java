package com.changedprogram.service;

import java.util.Base64;

import org.springframework.stereotype.Service;

@Service
public class ImageService {

    public String encodeImageToBase64(byte[] imageBytes) {
        return Base64.getEncoder().encodeToString(imageBytes);
    }
}
