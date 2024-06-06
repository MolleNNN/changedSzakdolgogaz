package com.changedprogram.component;

import java.util.Base64;

import javax.crypto.Cipher;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component  // Marks this class as a Spring component, enabling Spring to discover it for dependency injection
public class EncryptionUtil {

    private static final String AES = "AES";  // Constant for the AES encryption algorithm

    @Value("${encryption.key}")  // Injects the encryption key from the application's properties file
    private String encryptionKey;

    // Encrypt data
    public String encrypt(String data) throws Exception {
        // Create a SecretKeySpec using the encryption key decoded from Base64 and AES algorithm
        SecretKeySpec secretKeySpec = new SecretKeySpec(Base64.getDecoder().decode(encryptionKey), AES);
        // Get an AES cipher instance with ECB mode and PKCS5 padding
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        // Initialize the cipher in encryption mode with the secret key
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        // Encrypt the data and encode the result to Base64
        byte[] encryptedData = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedData);
    }

    // Decrypt data
    public String decrypt(byte[] encryptedData) throws Exception {
        // Create a SecretKeySpec using the encryption key decoded from Base64 and AES algorithm
        SecretKeySpec secretKeySpec = new SecretKeySpec(Base64.getDecoder().decode(encryptionKey), AES);
        // Get an AES cipher instance with ECB mode and PKCS5 padding
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        // Initialize the cipher in decryption mode with the secret key
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        // Decrypt the data and convert the result to a plain string
        byte[] decryptedData = cipher.doFinal(encryptedData);
        return new String(decryptedData);  // Return as plain string
    }

    // Helper to convert encrypted Base64 string to byte array
    public byte[] decodeBase64(String base64Data) throws IllegalArgumentException {
        try {
            // Remove data URL prefix if present
            if (base64Data.startsWith("data:image/png;base64,")) {
                base64Data = base64Data.substring("data:image/png;base64,".length());
            }
            // Decode the Base64 string to byte array
            return Base64.getDecoder().decode(base64Data);
        } catch (IllegalArgumentException e) {
            // Print error message for invalid Base64 input
            System.err.println("Invalid Base64 input: " + base64Data);
            throw e;  // Rethrow the exception
        }
    }
}
