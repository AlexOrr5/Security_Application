package com.example.rsa.controller;

import com.example.rsa.service.RSAService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = "*") // FIX: Allow requests from the static frontend
@RestController
@RequestMapping("/api/rsa") // FIX: Matches the frontend API base
public class CryptoController {

    @Autowired
    private RSAService rsaService;

    // DTOs (records) for clean JSON requests
    public record PlainRequest(String message) {}
    public record CipherRequest(String cipher) {}

    // FIX: Return clearly labeled keys so the frontend doesn't rely on array order
    @GetMapping("/keys")
    public Map<String, String> keys() {
        return Map.of(
                "publicKey", rsaService.getPublicKeyBase64(),
                "privateKey", rsaService.getPrivateKeyBase64()
        );
    }

    // FIX: Accept JSON { "message": "..." } OR raw JSON string e.g. "hello"
    @PostMapping(value = "/encrypt", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE})
    public String encrypt(@RequestBody(required = false) Object body) throws Exception {
        String plain;
        if (body instanceof Map<?, ?> map && map.get("message") != null) {
            plain = String.valueOf(map.get("message"));
        } else {
            // If body is just a JSON string like "hello", strip quotes
            plain = String.valueOf(body);
            if (plain.startsWith("\"") && plain.endsWith("\"")) {
                plain = plain.substring(1, plain.length() - 1);
            }
        }
        return rsaService.encrypt(plain);
    }

    // FIX: Accept JSON { "cipher": "..." } OR raw JSON string
    @PostMapping(value = "/decrypt", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE})
    public String decrypt(@RequestBody(required = false) Object body) throws Exception {
        String cipherText;
        if (body instanceof Map<?, ?> map && map.get("cipher") != null) {
            cipherText = String.valueOf(map.get("cipher"));
        } else {
            cipherText = String.valueOf(body);
            if (cipherText.startsWith("\"") && cipherText.endsWith("\"")) {
                cipherText = cipherText.substring(1, cipherText.length() - 1);
            }
        }
        return rsaService.decrypt(cipherText);
    }

    // FIX: Button to regenerate keys on demand
    @PostMapping("/generate")
    public String regenerateKeys() {
        rsaService.generateKeys();
        return "New RSA key pair generated successfully.";
    }

    @PostMapping("/hybrid/encrypt")
    public Map<String, String> hybridEncrypt(@RequestBody Map<String, String> body) throws Exception {
        String message = body.get("message");
        return rsaService.hybridEncrypt(message);
    }

    @PostMapping("/hybrid/decrypt")
    public String hybridDecrypt(@RequestBody Map<String, String> body) throws Exception {
        return rsaService.hybridDecrypt(
            body.get("encryptedKey"),
            body.get("iv"),
            body.get("cipherText")
        );
    }

}
