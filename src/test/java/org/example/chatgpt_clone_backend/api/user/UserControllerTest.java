package org.example.chatgpt_clone_backend.api.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.chatgpt_clone_backend.domain.chat.entity.ChatEntity;
import org.example.chatgpt_clone_backend.domain.chat.entity.PageEntity;
import org.example.chatgpt_clone_backend.domain.chat.repository.ChatRepository;
import org.example.chatgpt_clone_backend.domain.chat.repository.PageRepository;
import org.example.chatgpt_clone_backend.domain.user.dto.UserRequestDTO;
import org.example.chatgpt_clone_backend.domain.user.entity.UserEntity;
import org.example.chatgpt_clone_backend.domain.user.entity.UserRoleType;
import org.example.chatgpt_clone_backend.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PageRepository pageRepository;

    @Autowired
    ChatRepository chatRepository;

    @Test
    @DisplayName("user API : UserController : existUserApi : 통합 테스트 1")
    void existUserApiTest1() throws Exception {

        // 테스트 : 유저 존재 확인 (존재 케이스)

        // given
        UserEntity userEntity = UserEntity.builder()
                .username("xxxjjhhh")
                .password("")
                .roleType(UserRoleType.ADMIN)
                .isLock(false)
                .social(false)
                .nickname("xxxjjhhh")
                .build();

        userRepository.save(userEntity);

        UserRequestDTO dto = new UserRequestDTO();
        dto.setUsername("xxxjjhhh");

        // when & then
        mockMvc.perform(post("/user/exist")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

    }

    @Test
    @DisplayName("user API : UserController : existUserApi : 통합 테스트 2")
    void existUserApiTest2() throws Exception {

        // 테스트 : 유저 존재 확인 (미존재 케이스)

        // given
        UserRequestDTO dto = new UserRequestDTO();
        dto.setUsername("xxxjjhhh");

        // when & then
        mockMvc.perform(post("/user/exist")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));

    }

    @Test
    @DisplayName("user API : UserController : joinApi : 통합 테스트 1")
    void joinApiTest1() throws Exception {

        // 테스트 : 회원 가입 확인

        // given
        UserRequestDTO dto = new UserRequestDTO();
        dto.setUsername("xxxjjhhh");
        dto.setPassword("1234");
        dto.setEmail("xxxjjhhh@gmail.com");
        dto.setNickname("김지훈");

        // when & then
        mockMvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isCreated());

        assertTrue(userRepository.existsByUsername("xxxjjhhh"));

    }

    @Test
    @DisplayName("user API : UserController : joinApi : 통합 테스트 2")
    void joinApiTest2() throws Exception {

        // 테스트 : 회원 가입 누락 데이터 실패 확인

        // given
        UserRequestDTO dto = new UserRequestDTO();
        dto.setUsername("xxxjjhhh");
        dto.setEmail("xxxjjhhh@gmail.com");
        dto.setNickname("김지훈");

        // when & then
        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isBadRequest());

    }

    @Test
    @DisplayName("user API : UserController : userMeApi : 통합 테스트 1")
    void userMeApiTest1() throws Exception {

        // 테스트 : 권한 없는 (미로그인) 상태 조회

        // given

        // when & then
        mockMvc.perform(get("/user")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

    }

    @Test
    @DisplayName("user API : UserController : userMeApi : 통합 테스트 2")
    @WithMockUser(username = "xxxjjhhh", roles = {"ADMIN"})
    void userMeApiTest2() throws Exception {

        // 테스트 : 로그인 상태 본인 조회

        // given
        UserEntity userEntity = UserEntity.builder()
                .username("xxxjjhhh")
                .password("")
                .roleType(UserRoleType.ADMIN)
                .isLock(false)
                .social(false)
                .email("xxxjjhhh@gmail.com")
                .nickname("김지훈")
                .build();

        userRepository.save(userEntity);

        // when & then
        mockMvc.perform(get("/user")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("xxxjjhhh"))
                .andExpect(jsonPath("$.social").value(false))
                .andExpect(jsonPath("$.nickname").value("김지훈"))
                .andExpect(jsonPath("$.email").value("xxxjjhhh@gmail.com"));

    }

    @Test
    @DisplayName("user API : UserController : updateUserApi : 통합 테스트 1")
    void updateUserApiTest1() throws Exception {

        // 테스트 : 권한 없는 (미로그인) 상태로 타인 계정 수정 방지

        // given
        UserEntity userEntity = UserEntity.builder()
                .username("xxxjjhhh")
                .password("")
                .roleType(UserRoleType.ADMIN)
                .isLock(false)
                .social(false)
                .email("xxxjjhhh@gmail.com")
                .nickname("김지훈")
                .build();

        userRepository.save(userEntity);

        UserRequestDTO dto = new UserRequestDTO();
        dto.setUsername("xxxjjhhh");
        dto.setEmail("xxxjjhhh@gmail.com");
        dto.setNickname("수정값");

        // when & then
        mockMvc.perform(put("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());

    }

    @Test
    @DisplayName("user API : UserController : updateUserApi : 통합 테스트 2")
    @WithMockUser(username = "user1", roles = {"USER"})
    void updateUserApiTest2() throws Exception {

        // 테스트 : 로그인 상태로 타인 계정 수정 방지

        // given
        UserEntity userEntity = UserEntity.builder()
                .username("xxxjjhhh")
                .password("")
                .roleType(UserRoleType.ADMIN)
                .isLock(false)
                .social(false)
                .email("xxxjjhhh@gmail.com")
                .nickname("김지훈")
                .build();

        userRepository.save(userEntity);

        UserRequestDTO dto = new UserRequestDTO();
        dto.setUsername("xxxjjhhh");
        dto.setEmail("xxxjjhhh@gmail.com");
        dto.setNickname("수정값");

        // when & then
        mockMvc.perform(put("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isForbidden());

    }

    @Test
    @DisplayName("user API : UserController : updateUserApi : 통합 테스트 3")
    @WithMockUser(username = "xxxjjhhh", roles = {"USER"})
    void updateUserApiTest3() throws Exception {

        // 테스트 : 로그인 상태로 타인 계정 수정 방지

        // given
        UserEntity userEntity = UserEntity.builder()
                .username("xxxjjhhh")
                .password("")
                .roleType(UserRoleType.ADMIN)
                .isLock(false)
                .social(false)
                .email("xxxjjhhh@gmail.com")
                .nickname("김지훈")
                .build();

        userRepository.save(userEntity);

        UserRequestDTO dto = new UserRequestDTO();
        dto.setUsername("xxxjjhhh");
        dto.setEmail("xxxjjhhh@gmail.com");
        dto.setNickname("수정값");

        // when & then
        mockMvc.perform(put("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isOk());

        UserEntity findEntity = userRepository.findByUsernameAndIsLock("xxxjjhhh", false)
                .orElseThrow();

        assertEquals("수정값", findEntity.getNickname());

    }

    @Test
    @DisplayName("user API : UserController : deleteUserApi : 통합 테스트 1")
    void deleteUserApiTest1() throws Exception {

        // 테스트 : 권한 없는 (미로그인) 상태로 타인 계정 제거 방지

        // given
        UserEntity userEntity = UserEntity.builder()
                .username("xxxjjhhh")
                .password("")
                .roleType(UserRoleType.ADMIN)
                .isLock(false)
                .social(false)
                .email("xxxjjhhh@gmail.com")
                .nickname("김지훈")
                .build();

        userRepository.save(userEntity);

        UserRequestDTO dto = new UserRequestDTO();
        dto.setUsername("xxxjjhhh");

        // when & then
        mockMvc.perform(delete("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());

    }

    @Test
    @DisplayName("user API : UserController : deleteUserApi : 통합 테스트 2")
    @WithMockUser(username = "user1", roles = {"USER"})
    void deleteUserApiTest2() throws Exception {

        // 테스트 : 로그인 상태로 타인 계정 제거 방지

        // given
        UserEntity userEntity = UserEntity.builder()
                .username("xxxjjhhh")
                .password("")
                .roleType(UserRoleType.ADMIN)
                .isLock(false)
                .social(false)
                .email("xxxjjhhh@gmail.com")
                .nickname("김지훈")
                .build();

        userRepository.save(userEntity);

        UserRequestDTO dto = new UserRequestDTO();
        dto.setUsername("xxxjjhhh");

        // when & then
        mockMvc.perform(delete("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isForbidden());

        assertTrue(userRepository.existsByUsername("xxxjjhhh"));

    }

    @Test
    @DisplayName("user API : UserController : deleteUserApi : 통합 테스트 3")
    @WithMockUser(username = "xxxjjhhh", roles = {"ADMIN"})
    void deleteUserApiTest3() throws Exception {

        // 테스트 : 로그인 (ADMIN) 상태로 타인 계정 제거

        // given
        UserEntity userEntity = UserEntity.builder()
                .username("user1")
                .password("")
                .roleType(UserRoleType.ADMIN)
                .isLock(false)
                .social(false)
                .email("xxxjjhhh@gmail.com")
                .nickname("유저1")
                .build();

        userRepository.save(userEntity);

        UserRequestDTO dto = new UserRequestDTO();
        dto.setUsername("user1");

        // when & then
        mockMvc.perform(delete("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isOk());

        assertFalse(userRepository.existsByUsername("user1"));

    }

    @Test
    @DisplayName("user API : UserController : deleteUserApi : 통합 테스트 4")
    @WithMockUser(username = "xxxjjhhh", roles = {"ADMIN"})
    void deleteUserApiTest4() throws Exception {

        // 테스트 : 로그인 (ADMIN) 상태로 타인 계정 제거 및 Chat 페이지 제거 확인

        // given
        UserEntity userEntity = UserEntity.builder()
                .username("user1")
                .password("")
                .roleType(UserRoleType.ADMIN)
                .isLock(false)
                .social(false)
                .email("xxxjjhhh@gmail.com")
                .nickname("유저1")
                .build();

        userRepository.save(userEntity);

        PageEntity pageEntity = PageEntity.builder()
                .username("user1")
                .title("the weeknd 노래")
                .build();

        Long pageId = pageRepository.save(pageEntity).getId();

        ChatEntity chatEntity = ChatEntity.builder()
                .content("the weeknd의 save your tears 노래가 좋아. 더 추천해줘")
                .pageId(pageId)
                .messageType(MessageType.USER)
                .build();

        Long chatId = chatRepository.save(chatEntity).getId();

        UserRequestDTO dto = new UserRequestDTO();
        dto.setUsername("user1");

        // when & then
        mockMvc.perform(delete("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isOk());

        assertFalse(userRepository.existsByUsername("user1"));
        assertFalse(pageRepository.existsById(pageId));
        assertFalse(chatRepository.existsById(chatId));

    }

}