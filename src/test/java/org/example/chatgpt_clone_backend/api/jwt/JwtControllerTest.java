package org.example.chatgpt_clone_backend.api.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.example.chatgpt_clone_backend.domain.jwt.dto.RefreshRequestDTO;
import org.example.chatgpt_clone_backend.util.JWTUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class JwtControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("jwt API : JwtController : jwtExchangeApi : 통합 테스트 1")
    void jwtExchangeApiTest1() throws Exception {

        // 테스트 : 쿠키 방식의 Refresh 토큰 전송시 Access/Refresh 응답 여부

        // given
        String beforeRefresh = JWTUtil.createJWT("xxxjjhhh", "ROLE_ADMIN", false);

        Cookie refreshTokenCookie = new Cookie("refreshToken", beforeRefresh);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/");

        // when & then
        mockMvc.perform(post("/jwt/exchange")
                        .cookie(refreshTokenCookie)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists());

    }

    @Test
    @DisplayName("jwt API : JwtController : jwtExchangeApi : 통합 테스트 2")
    void jwtExchangeApiTest2() throws Exception {

        // 테스트 : 쿠키 Refresh 토큰 미 첨부시 예외 확인

        // given

        // when & then
        mockMvc.perform(post("/jwt/exchange")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

    }

    @Test
    @DisplayName("jwt API : JwtController : jwtRefreshApi : 통합 테스트 1")
    void jwtRefreshApiTest1() throws Exception {

        // 테스트 : Refresh 토큰 전송 후 Refresh/Access 재발급 확인

        // given
        String beforeRefresh = JWTUtil.createJWT("xxxjjhhh", "ROLE_ADMIN", false);

        RefreshRequestDTO dto = new RefreshRequestDTO();
        dto.setRefreshToken(beforeRefresh);

        // when & then
        mockMvc.perform(post("/jwt/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists());

    }

    @Test
    @DisplayName("jwt API : JwtController : jwtRefreshApi : 통합 테스트 2")
    void jwtRefreshApiTest2() throws Exception {

        // 테스트 : Refresh 토큰 미 첨부시 예외 확인

        // given
        RefreshRequestDTO dto = new RefreshRequestDTO();

        // when & then
        mockMvc.perform(post("/jwt/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isBadRequest());

    }

}