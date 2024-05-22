package com.changedprogram;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EncryptionUtil {

    private static final String AES = "AES";

    @Value("${encryption.key}")
    private String encryptionKey;

    // Encrypt data
    public String encrypt(String data) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(Base64.getDecoder().decode(encryptionKey), AES);
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        byte[] encryptedData = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedData);
    }

    // Decrypt data
    public String decrypt(byte[] encryptedData) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(Base64.getDecoder().decode(encryptionKey), AES);
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        byte[] decryptedData = cipher.doFinal(encryptedData);
        return new String(decryptedData); // Return as plain string
    }

    // Helper to convert encrypted base64 string to byte array
    public byte[] decodeBase64(String base64Data) throws IllegalArgumentException {
        try {
            // Remove data URL prefix if present
            if (base64Data.startsWith("data:image/png;base64,")) {
                base64Data = base64Data.substring("data:image/png;base64,".length());
            }
            return Base64.getDecoder().decode(base64Data);
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid Base64 input: " + base64Data);
            throw e;
        }
    }
    


}