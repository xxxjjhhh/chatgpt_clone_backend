package org.example.chatgpt_clone_backend.domain.user.service;

import org.example.chatgpt_clone_backend.domain.user.dto.UserRequestDTO;
import org.example.chatgpt_clone_backend.domain.user.dto.UserResponseDTO;
import org.example.chatgpt_clone_backend.domain.user.entity.UserEntity;
import org.example.chatgpt_clone_backend.domain.user.entity.UserRoleType;
import org.example.chatgpt_clone_backend.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.nio.file.AccessDeniedException;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    UserRepository userRepository;

    @Test
    @DisplayName("user 도메인 : UserService : existUser : 테스트 1")
    void existUserTest1() {

        // 테스트 : User 있는 경우 true

        // given
        UserRequestDTO dto = new UserRequestDTO();
        dto.setUsername("xxxjjhhh");

        given(userRepository.existsByUsername("xxxjjhhh")).willReturn(true);

        // when
        Boolean isExist = userService.existUser(dto);

        // then
        assertTrue(isExist);

    }

    @Test
    @DisplayName("user 도메인 : UserService : existUser : 테스트 2")
    void existUserTest2() {

        // 테스트 : User 없는 경우 false

        // given
        UserRequestDTO dto = new UserRequestDTO();
        dto.setUsername("xxxjjhhh");

        given(userRepository.existsByUsername("xxxjjhhh")).willReturn(false);

        // when
        Boolean isExist = userService.existUser(dto);

        // then
        assertFalse(isExist);

    }

    @Test
    @DisplayName("user 도메인 : UserService : addUser : 테스트 1")
    void addUserTest1() {

        // 테스트 : 회원 가입 성공 id 반환

        // given
        UserRequestDTO dto = new UserRequestDTO();
        dto.setUsername("xxxjjhhh");
        dto.setPassword("password");
        dto.setNickname("devyummi");
        dto.setEmail("xxxjjhhh@naver.com");

        UserEntity entity = UserEntity.builder()
                .id(1L)
                .username(dto.getUsername())
                .password("암호화된비번")
                .email(dto.getEmail())
                .nickname(dto.getNickname())
                .isLock(false)
                .social(false)
                .roleType(UserRoleType.USER)
                .build();

        given(userRepository.save(any(UserEntity.class))).willReturn(entity);
        given(passwordEncoder.encode("password")).willReturn("암호화된비번");

        // when
        Long id = userService.addUser(dto);

        // then
        assertEquals(1L, id);

    }

    @Test
    @DisplayName("user 도메인 : UserService : addUser : 테스트 2")
    void addUserTest2() {

        // 테스트 : 회원 가입 중복 여부 확인

        // given
        UserRequestDTO dto = new UserRequestDTO();
        dto.setUsername("xxxjjhhh");
        dto.setPassword("password");
        dto.setNickname("devyummi");
        dto.setEmail("xxxjjhhh@naver.com");

        given(userRepository.existsByUsername(dto.getUsername())).willReturn(true);

        // when & then
        assertThrows(IllegalArgumentException.class, () -> userService.addUser(dto));

    }


    @Test
    @DisplayName("user 도메인 : UserService : loadUserByUsername : 테스트 1")
    void loadUserByUsernameTest1() {

        // 테스트 : UsernameNotFoundException 체크 (리포지토리에서 empty 던질시)

        // given
        given(userRepository.findByUsernameAndIsLockAndSocial("kimjihun", false, false))
                .willReturn(Optional.empty());

        // when & then
        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("kimjihun"));

    }

    @Test
    @DisplayName("user 도메인 : UserService : updateUser : 테스트 1")
    void updateUserTest1() {

        // 테스트 : 인증 정보 불일치 실패

        // given
        UserRequestDTO dto = new UserRequestDTO();
        dto.setUsername("123123");
        dto.setNickname("xxxjjhhh");
        dto.setEmail("xxxjjhh@naver.com");

        Authentication auth = Mockito.mock(Authentication.class);
        Mockito.when(auth.getName()).thenReturn("xxxjjhhh");
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

        // when & then
        assertThrows(AccessDeniedException.class, () -> userService.updateUser(dto));

    }

    @Test
    @DisplayName("user 도메인 : UserService : deleteUser : 테스트 1")
    void deleteUserTest1() {

        // 테스트 : 본인 계정 아닌 경우

        // given
        Authentication auth = Mockito.mock(Authentication.class);
        Mockito.when(auth.getName()).thenReturn("xxxjjhhh");
        Mockito.when(auth.getAuthorities()).thenAnswer(invocation -> Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

        UserRequestDTO dto = new UserRequestDTO();
        dto.setUsername("admin");

        // when & then
        assertThrows(AccessDeniedException.class, () -> userService.deleteUser(dto));

    }

    @Test
    @DisplayName("user 도메인 : UserService : loadUser : 테스트 1")
    void loadUserTest1() {

        // 테스트 : 네이버 신규 로그인 가정

        // given
        ClientRegistration registration = ClientRegistration.withRegistrationId("naver")
                .clientId("clientId")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("http://localhost:8080/login/oauth2/code/naver")
                .tokenUri("https://nid.naver.com/oauth2.0/token")
                .authorizationUri("https://nid.naver.com/oauth2.0/authorize")
                .userInfoUri("https://openapi.naver.com/v1/nid/me")
                .userNameAttributeName("response")
                .clientName("naver")
                .build();
        OAuth2UserRequest userRequest = new OAuth2UserRequest(registration, new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER, "token", Instant.now(), Instant.now().plusSeconds(60)));

        Map<String, Object> naverResponse = new HashMap<>();
        naverResponse.put("id", "12345");
        naverResponse.put("email", "naver@test.com");
        naverResponse.put("nickname", "김지훈");

        Map<String, Object> naverAttributes = new HashMap<>();
        naverAttributes.put("response", naverResponse);

        OAuth2User mockOAuth2User = Mockito.mock(OAuth2User.class);
        Mockito.when(mockOAuth2User.getAttributes()).thenReturn(naverAttributes);

        Mockito.when(userRepository.findByUsernameAndSocial("NAVER_12345", true))
                .thenReturn(Optional.empty());

        ArgumentCaptor<UserEntity> userCaptor = ArgumentCaptor.forClass(UserEntity.class);
        Mockito.when(userRepository.save(userCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));


        // when


        // then

    }

    @Test
    @DisplayName("user 도메인 : UserService : readUser : 테스트 1")
    void readUserTest1() {

        // 테스트 : 유저 없는 경우 UsernameNotFoundException 예외 체크

        // given
        Authentication auth = Mockito.mock(Authentication.class);
        Mockito.when(auth.getName()).thenReturn("xxxjjhhh");
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

        given(userRepository.findByUsernameAndIsLock("xxxjjhhh", false))
                .willReturn(Optional.empty());

        // when & then
        assertThrows(UsernameNotFoundException.class, () -> userService.readUser());

    }

    @Test
    @DisplayName("user 도메인 : UserService : readUser : 테스트 2")
    void readUserTest2() {

        // 테스트 : 존재하는 경우 정보 확인

        // given
        Authentication auth = Mockito.mock(Authentication.class);
        Mockito.when(auth.getName()).thenReturn("xxxjjhhh");
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

        UserEntity mockUser = UserEntity.builder()
                .id(1L)
                .username("xxxjjhhh")
                .nickname("xxxjjhhh")
                .isLock(false)
                .social(false)
                .roleType(UserRoleType.USER)
                .email("xxxjjhhh@naver.com")
                .build();

        given(userRepository.findByUsernameAndIsLock("xxxjjhhh", false))
                .willReturn(Optional.ofNullable(mockUser));

        // when
        UserResponseDTO response = userService.readUser();

        // then
        assertNotNull(response);
        assertEquals("xxxjjhhh", response.username());
        assertEquals("xxxjjhhh", response.nickname());
        assertEquals("xxxjjhhh@naver.com", response.email());

    }

}