package com.example.rsa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RSAWebappApplication {
    public static void main(String[] args) {
        SpringApplication.run(RSAWebappApplication.class, args);
        System.out.println("✅ RSA Web App backend running on http://localhost:8080");
    }
}
