package org.example.chatgpt_clone_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ApiClientConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
