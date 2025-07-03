package org.example.chatgpt_clone_backend.api.jwt;

import jakarta.servlet.http.HttpServletRequest;
import org.example.chatgpt_clone_backend.domain.jwt.dto.JWTResponseDTO;
import org.example.chatgpt_clone_backend.domain.jwt.service.JwtService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JwtController {

    private final JwtService jwtService;

    public JwtController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    // 소셜 로그인 쿠키 방식의 Refresh 토큰 헤더 방식으로 교환
    @PostMapping("/jwt/exchange")
    public JWTResponseDTO jwtExchangeApi(HttpServletRequest request) {
        return jwtService.cookie2Header(request);
    }

}
