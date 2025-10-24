package com.example.rsa.service;

import java.security.*;
import javax.crypto.Cipher;
import java.util.Base64;

import org.springframework.stereotype.Service;

@Service
public class RSAService {
    private KeyPair keyPair;
    // private final Cipher cipher;

    public RSAService() {
        try {
            generateKeys(); // <-- Automatically generate keys at startup
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Generate a new RSA key pair
    public void generateKeys() throws NoSuchAlgorithmException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        keyPair = generator.generateKeyPair();
        System.out.println("🔑 RSA key pair generated successfully."); // <-- Added debug print
    }

    // Return the public and private keys as Base64 strings
    public String[] getKeys() {
        if (keyPair == null) return new String[]{"No keys generated", "No keys generated"};
        String pub = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
        String priv = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
        return new String[]{pub, priv};
    }

    public String getPublicKey() {
        return Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
    }

    public String getPrivateKey() {
        return Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
    }

    public String encrypt(String plainText) throws Exception {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());
            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return "Encryption failed: " + e.getMessage();
        }
    }

    public String decrypt(String cipherText) throws Exception {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(cipherText));
            return new String(decryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return "Decryption failed: " + e.getMessage();
        }
    }
}
