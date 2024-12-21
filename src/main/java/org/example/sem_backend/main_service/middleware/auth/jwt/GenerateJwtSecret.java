package org.example.sem_backend.main_service.middleware.auth.jwt;

import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;

public class GenerateJwtSecret {
    public static void main(String[] args) {
        // Tạo một SecretKey 512-bit cho thuật toán HS512
        SecretKey key = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS512);

        // Mã hóa SecretKey bằng Base64
        String base64Secret = Encoders.BASE64.encode(key.getEncoded());

        System.out.println("Generated JWT Secret Key (Base64): " + base64Secret);
    }
}

