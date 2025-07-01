package org.example.chatgpt_clone_backend.domain.user.service;

import org.example.chatgpt_clone_backend.domain.user.dto.UserRequestDTO;
import org.example.chatgpt_clone_backend.domain.user.entity.UserEntity;
import org.example.chatgpt_clone_backend.domain.user.entity.UserRoleType;
import org.example.chatgpt_clone_backend.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.nio.file.AccessDeniedException;
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
    @DisplayName("user 도메인 : UserService : loadUserByUsername : 테스트 1")
    void loadUserByUsernameTest1() {

        // 테스트 : UsernameNotFoundException 체크 (리포지토리에서 empty 던질시)

        // given
        given(userRepository.findByUsername("kimjihun"))
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

        // 테스트 :

        // given


        // when & then


    }

}