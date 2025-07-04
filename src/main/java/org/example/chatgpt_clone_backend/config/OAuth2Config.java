package org.example.chatgpt_clone_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.client.JdbcOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

@Configuration
public class OAuth2Config {

    private final JdbcTemplate jdbcTemplate;
    private final ClientRegistrationRepository clientRegistrationRepository;

    public OAuth2Config(JdbcTemplate jdbcTemplate, ClientRegistrationRepository clientRegistrationRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    // OAuth2 인증 과정 중 발생하는 provider 코드, 토큰 저장소
    @Bean
    public OAuth2AuthorizedClientService authorizedClientService() {
        return new JdbcOAuth2AuthorizedClientService(jdbcTemplate, clientRegistrationRepository);
    }

}
