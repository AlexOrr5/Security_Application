package com.example.rsa.service;

import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Map;

@Service
public class RSAService {

    private KeyPair keyPair;
    private static final int AES_KEY_SIZE = 128;
    private static final int GCM_TAG_LENGTH = 128;
    private static final int GCM_IV_LENGTH = 12;

    public RSAService() {
        generateKeys();
    }

    public synchronized void generateKeys() {
        try {
            KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
            gen.initialize(2048, new SecureRandom());
            this.keyPair = gen.generateKeyPair();
        } catch (Exception e) {
            throw new RuntimeException("Unable to generate RSA keys", e);
        }
    }

    public String getPublicKeyBase64() {
        return Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
    }

    public String getPrivateKeyBase64() {
        return Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
    }

    // ==========================
    // 🔒 AES + RSA HYBRID LOGIC
    // ==========================
    public Map<String, String> hybridEncrypt(String plainText) throws Exception {
        // Generate AES key
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(AES_KEY_SIZE);
        SecretKey aesKey = keyGen.generateKey();

        // Encrypt plaintext with AES-GCM
        byte[] iv = new byte[GCM_IV_LENGTH];
        new SecureRandom().nextBytes(iv);
        Cipher aesCipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        aesCipher.init(Cipher.ENCRYPT_MODE, aesKey, spec);
        byte[] cipherBytes = aesCipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

        // Encrypt AES key with RSA
        Cipher rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        rsaCipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());
        byte[] encKey = rsaCipher.doFinal(aesKey.getEncoded());

        return Map.of(
                "encryptedKey", Base64.getEncoder().encodeToString(encKey),
                "iv", Base64.getEncoder().encodeToString(iv),
                "cipherText", Base64.getEncoder().encodeToString(cipherBytes)
        );
    }

    public String hybridDecrypt(String encKeyB64, String ivB64, String cipherB64) throws Exception {
        // Decrypt AES key
        Cipher rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        rsaCipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
        byte[] aesKeyBytes = rsaCipher.doFinal(Base64.getDecoder().decode(encKeyB64));
        SecretKey aesKey = new SecretKeySpec(aesKeyBytes, "AES");

        // Decrypt message
        byte[] iv = Base64.getDecoder().decode(ivB64);
        Cipher aesCipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        aesCipher.init(Cipher.DECRYPT_MODE, aesKey, spec);
        byte[] plain = aesCipher.doFinal(Base64.getDecoder().decode(cipherB64));

        return new String(plain, StandardCharsets.UTF_8);
    }

    // ==========================
    // 🧱 (Still supports direct RSA)
    // ==========================
    public String encrypt(String plainText) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());
        byte[] out = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(out);
    }

    public String decrypt(String cipherText) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
        byte[] decoded = Base64.getDecoder().decode(cipherText);
        byte[] out = cipher.doFinal(decoded);
        return new String(out, StandardCharsets.UTF_8);
    }
}
