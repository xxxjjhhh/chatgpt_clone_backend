package org.example.chatgpt_clone_backend.util;

import io.jsonwebtoken.Jwts;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

public class JWTUtil {

    private final SecretKey secretKey;

    public JWTUtil() {
        String secretKeyString = "himynameiskimjihunmyyoutubechann";
        this.secretKey = new SecretKeySpec(secretKeyString.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }



}
