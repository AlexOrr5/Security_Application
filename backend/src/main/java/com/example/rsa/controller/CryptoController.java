package com.example.rsa.controller;

import org.springframework.web.bind.annotation.*;
import com.example.rsa.service.RSAService;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Map;

@CrossOrigin(origins = "*") // ✅ Added to allow frontend requests from localhost:5500 or other ports
@RestController
@RequestMapping("/api/rsa") // ✅ Matches your frontend’s API constant
public class CryptoController {

    @Autowired
    private RSAService rsaService;

    // ✅ Generate and return new keys
    @GetMapping("/keys")
    public String[] getKeys() {
        return rsaService.getKeys();
    }

    // ✅ Encrypt endpoint
    @PostMapping("/encrypt")
    public String encrypt(@RequestBody String plainText) throws Exception { // ✅ Accept raw JSON string (like "hello")
        plainText = plainText.replace("\"", ""); // ✅ Strip quotes if frontend sends raw string
        return rsaService.encrypt(plainText);
    }

    // Decrypt endpoint
    @PostMapping("/decrypt")
    public String decrypt(@RequestBody String cipherText) throws Exception { // ✅ Accept raw JSON string (like "ZyA...")
        cipherText = cipherText.replace("\"", ""); // ✅ Strip quotes
        return rsaService.decrypt(cipherText);
    }

    // Endpoint to regenerate keys manually
    @PostMapping("/generate")
    public String regenerateKeys() {
        try {
            rsaService.generateKeys();
            return "New RSA key pair generated successfully.";
        } catch (Exception e) {
            return "Failed to generate keys: " + e.getMessage();
        }
    }
}
