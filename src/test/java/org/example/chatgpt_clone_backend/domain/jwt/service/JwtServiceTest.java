package org.example.chatgpt_clone_backend.domain.jwt.service;

import org.example.chatgpt_clone_backend.domain.jwt.repository.RefreshRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @InjectMocks
    JwtService jwtService;

    @Mock
    RefreshRepository refreshRepository;

    @Test
    @DisplayName("jwt 도메인 : JwtService : existsRefresh : 테스트 1")
    void existsRefreshTest1() {

        // 테스트 : 존재하는 경우 가정 true

        // given
        String refreshToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsInJvbGUiOiJST0xFX1VTRVIiLCJ0eXBlIjoicmVmcmVzaCIsImlhdCI6MTc1MTQ2MjQ4MCwiZXhwIjoxNzUyMDY3MjgwfQ.Jjrjgdsp5dClwPU-1-w2mm92cmW7TqNNpbQapDJQxJ0";

        given(refreshRepository.existsByRefresh(refreshToken)).willReturn(true);

        // when
        Boolean isExist = jwtService.existsRefresh(refreshToken);

        // then
        assertTrue(isExist);

    }

    @Test
    @DisplayName("jwt 도메인 : JwtService : existsRefresh : 테스트 2")
    void existsRefreshTest2() {

        // 테스트 : 없는 경우 가정 false

        // given
        String refreshToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsInJvbGUiOiJST0xFX1VTRVIiLCJ0eXBlIjoicmVmcmVzaCIsImlhdCI6MTc1MTQ2MjQ4MCwiZXhwIjoxNzUyMDY3MjgwfQ.Jjrjgdsp5dClwPU-1-w2mm92cmW7TqNNpbQapDJQxJ0";

        given(refreshRepository.existsByRefresh(refreshToken)).willReturn(false);

        // when
        Boolean isExist = jwtService.existsRefresh(refreshToken);

        // then
        assertFalse(isExist);

    }

}