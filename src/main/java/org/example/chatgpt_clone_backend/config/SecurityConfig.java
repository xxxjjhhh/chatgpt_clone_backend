package org.example.chatgpt_clone_backend.config;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletResponse;
import org.example.chatgpt_clone_backend.domain.jwt.service.JwtService;
import org.example.chatgpt_clone_backend.domain.user.entity.UserRoleType;
import org.example.chatgpt_clone_backend.filter.JWTFilter;
import org.example.chatgpt_clone_backend.filter.LoginFilter;
import org.example.chatgpt_clone_backend.handler.RefreshTokenLogoutHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final AuthenticationSuccessHandler loginSuccessHandler;
    private final AuthenticationSuccessHandler socialSuccessHandler;
    private final OAuth2AuthorizedClientService oAuth2AuthorizedClientService;
    private final JwtService jwtService;

    public SecurityConfig(
            AuthenticationConfiguration authenticationConfiguration,
            @Qualifier("LoginSuccessHandler") AuthenticationSuccessHandler loginSuccessHandler,
            @Qualifier("SocialSuccessHandler") AuthenticationSuccessHandler socialSuccessHandler,
            OAuth2AuthorizedClientService oAuth2AuthorizedClientService,
            JwtService jwtService
    ) {
        this.authenticationConfiguration = authenticationConfiguration;
        this.loginSuccessHandler = loginSuccessHandler;
        this.socialSuccessHandler = socialSuccessHandler;
        this.oAuth2AuthorizedClientService = oAuth2AuthorizedClientService;
        this.jwtService = jwtService;
    }

    // 커스텀 자체 로그인 필터를 위한 AuthenticationManager Bean 수동 등록
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    // 비밀번호 단방향(BCrypt) 암호화용 Bean
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 권한 계층
    @Bean
    public RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.withRolePrefix("ROLE_")
                .role(UserRoleType.ADMIN.name()).implies(UserRoleType.PAID.name())
                .role(UserRoleType.PAID.name()).implies(UserRoleType.USER.name())
                .build();
    }

    // CORS 설정
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(List.of("Authorization", "Set-Cookie"));
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    // SecurityFilterChain
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        // CSRF 보안 필터 disable
        http
                .csrf(AbstractHttpConfigurer::disable);

        // CORS 설정
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()));

        // 기본 로그아웃 필터 + 커스텀 Refresh 토큰 삭제 핸들러 추가
        http
                .logout(logout -> logout
                        .addLogoutHandler(new RefreshTokenLogoutHandler(jwtService)));

        // 기본 Form 기반 인증 필터들 disable
        http
                .formLogin(AbstractHttpConfigurer::disable);

        // 기본 Basic 인증 필터 disable
        http
                .httpBasic(AbstractHttpConfigurer::disable);

        // OAuth2(OIDC) 인증용 설정
        http
                .oauth2Login(oauth2 -> oauth2
                        .authorizedClientService(oAuth2AuthorizedClientService)
                        .successHandler(socialSuccessHandler));

        // 인가
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/chat", "/chat/**").hasRole(UserRoleType.USER.name())
                        .requestMatchers("/jwt/exchange", "/jwt/refresh").permitAll()
                        .requestMatchers(HttpMethod.POST, "/user/exist", "/user").permitAll()
                        .requestMatchers(HttpMethod.GET, "/user").hasRole(UserRoleType.USER.name())
                        .requestMatchers(HttpMethod.PUT, "/user").hasRole(UserRoleType.USER.name())
                        .requestMatchers(HttpMethod.DELETE, "/user").hasRole(UserRoleType.USER.name())
                        .anyRequest().authenticated()
                );

        // 예외 처리
        http
                .exceptionHandling(e -> e
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED); // 401 응답
                        })
                        .accessDeniedHandler((request, response, authException) -> {
                            response.sendError(HttpServletResponse.SC_FORBIDDEN); // 403 응답
                        })
                );

        // 커스텀 필터 추가
        http
                .addFilterBefore(new JWTFilter(), LogoutFilter.class);
        http
                .addFilterBefore(new LoginFilter(authenticationManager(authenticationConfiguration), loginSuccessHandler), UsernamePasswordAuthenticationFilter.class);

        // 세션 필터 설정 (STATELESS)
        http
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    // SecurityFilterChain 제외 필터
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web
                .ignoring()
                .dispatcherTypeMatchers(DispatcherType.ASYNC, DispatcherType.ERROR);
    }

}
