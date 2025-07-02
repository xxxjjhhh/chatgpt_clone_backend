package org.example.chatgpt_clone_backend.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class JWTUtilTest {

    @Test
    @DisplayName("util 클래스 : JWTUtilTest : isValid : 테스트 1")
    void isValidTest1() {

        // 테스트 : JWT 유효하지 않은 JWT false 테스트

        // given
        String forgeryToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyMTIzIiwicm9sZSI6IlVTRVIiLCJ0eXBlIjoiYWNjZXNzIn0.invalidsignature";

        // when
        Boolean isValid = JWTUtil.isValid(forgeryToken, true);

        // then
        assertFalse(isValid);

    }

    @Test
    @DisplayName("util 클래스 : JWTUtilTest : createJWT : 테스트 1")
    void createJWTTest1() {

        // 테스트 : JWT(Access) 생성 검증

        // given
        String username = "xxxjjhhh";
        String role = "ROLE_ADMIN";
        Boolean isAccess = true;

        // when
        String accessToken = JWTUtil.createJWT(username, role, isAccess);

        // then
        String[] parts = accessToken.split("\\.");
        assertEquals(3, parts.length);

        String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]));
        System.out.println("Payload JSON: " + payloadJson);

        assertTrue(payloadJson.contains("\"sub\":\"" + username + "\""));
        assertTrue(payloadJson.contains("\"role\":\"" + role + "\""));
        assertTrue(payloadJson.contains("\"type\":\"access\""));

    }

}