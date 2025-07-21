package org.example.chatgpt_clone_backend.api.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.chatgpt_clone_backend.domain.chat.dto.ChatRequestDTO;
import org.example.chatgpt_clone_backend.domain.chat.entity.ChatEntity;
import org.example.chatgpt_clone_backend.domain.chat.entity.PageEntity;
import org.example.chatgpt_clone_backend.domain.chat.repository.ChatRepository;
import org.example.chatgpt_clone_backend.domain.chat.repository.PageRepository;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ChatControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    PageRepository pageRepository;

    @Autowired
    ChatRepository chatRepository;

    @Test
    @DisplayName("chat API : ChatController : readAllPageApi : 통합 테스트 1")
    void readAllPageApiTest1() throws Exception {

        // 테스트 : 권한 없는 (미로그인) 경우

        // given

        // when & then
        mockMvc.perform(get("/chat"))
                .andExpect(status().isUnauthorized());

    }

    @Test
    @DisplayName("chat API : ChatController : readAllPageApi : 통합 테스트 2")
    @WithMockUser(roles = {"GUEST"})
    void readAllPageApiTest2() throws Exception {

        // 테스트 : 권한 없는 (로그인) 경우

        // given

        // when & then
        mockMvc.perform(get("/chat"))
                .andExpect(status().isForbidden());

    }

    @Test
    @DisplayName("chat API : ChatController : readAllPageApi : 통합 테스트 3")
    @WithMockUser(username = "xxxjjhhh", roles = {"ADMIN"})
    void readAllPageApiTest3() throws Exception {

        // 테스트 : 권한 있는 경우 (데이터 있을때)

        // given
        PageEntity pageEntity1 = PageEntity.builder()
                .title("개발자 유미란?")
                .username("xxxjjhhh")
                .build();

        pageRepository.save(pageEntity1);

        // when & then
        mockMvc.perform(get("/chat"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1));

    }

    @Test
    @DisplayName("chat API : ChatController : readAllPageApi : 통합 테스트 4")
    @WithMockUser(username = "xxxjjhhh", roles = {"ADMIN"})
    void readAllPageApiTest4() throws Exception {

        // 테스트 : 권한 있는 경우 (데이터 없을을때)

        // given

        // when & then
        mockMvc.perform(get("/chat"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));

    }

    @Test
    @DisplayName("chat API : ChatController : createChatApi : 통합 테스트 1")
    void createChatApiTest1() throws Exception {

        // 테스트 : 권한 없는 (미로그인) 경우

        // given
        ChatRequestDTO dto = new ChatRequestDTO();
        dto.setText("에이브릴 라빈 노래 추천해줘");

        // when & then
        mockMvc.perform(post("/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());

    }

    @Test
    @DisplayName("chat API : ChatController : createChatApi : 통합 테스트 2")
    @WithMockUser(username = "xxxjjhhh", roles = {"ADMIN"})
    void createChatApiTest2() throws Exception {

        // 테스트 : 로그인 완료 페이지 생성

        // given
        ChatRequestDTO dto = new ChatRequestDTO();
        dto.setText("에이브릴 라빈 노래 추천해줘");

        // when & then
        mockMvc.perform(post("/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.pageId").exists());

    }

    @Test
    @DisplayName("chat API : ChatController : streamChatApi : 통합 테스트 1")
    @WithMockUser(username = "xxxjjhhh", roles = {"ADMIN"})
    void streamChatApiTest1() throws Exception {

        // 테스트 : 로그인 후 채팅 진행

        // given
        PageEntity pageEntity = PageEntity.builder()
                .title("에이브릴 라빈 노래")
                .username("xxxjjhhh")
                .build();

        Long pageId = pageRepository.save(pageEntity).getId();

        ChatEntity chatEntity1 = ChatEntity.builder()
                .pageId(pageId)
                .content("에이브릴 라빈 노래 추천해줘")
                .messageType(MessageType.USER)
                .build();

        chatRepository.save(chatEntity1);

        ChatEntity chatEntity2 = ChatEntity.builder()
                .pageId(pageId)
                .content("complicated, what the hell 추천합니다.")
                .messageType(MessageType.ASSISTANT)
                .build();

        chatRepository.save(chatEntity2);

        ChatRequestDTO dto = new ChatRequestDTO();
        dto.setText("에이브릴 라빈 노래 추천해줘");

        // when & then
        mockMvc.perform(post("/chat/" + pageId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isOk());

    }

    @Test
    @DisplayName("chat API : ChatController : deletePageApi : 통합 테스트 1")
    @WithMockUser(username = "xxxjjhhh", roles = {"ADMIN"})
    void deletePageApiTest1() throws Exception {

        // 테스트 : 로그인 후 페이지 삭제

        // given
        PageEntity pageEntity = PageEntity.builder()
                .title("에이브릴 라빈 노래")
                .username("xxxjjhhh")
                .build();

        Long pageId = pageRepository.save(pageEntity).getId();

        ChatEntity chatEntity1 = ChatEntity.builder()
                .pageId(pageId)
                .content("에이브릴 라빈 노래 추천해줘")
                .messageType(MessageType.USER)
                .build();

        Long chatId1 = chatRepository.save(chatEntity1).getId();

        ChatEntity chatEntity2 = ChatEntity.builder()
                .pageId(pageId)
                .content("complicated, what the hell 추천합니다.")
                .messageType(MessageType.ASSISTANT)
                .build();

        Long chatId2 = chatRepository.save(chatEntity2).getId();

        // when & then
        mockMvc.perform(delete("/chat/" + pageId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Optional<PageEntity> deletedPage = pageRepository.findById(pageId);
        Optional<ChatEntity> deletedChat1 = chatRepository.findById(chatId1);
        Optional<ChatEntity> deletedChat2 = chatRepository.findById(chatId2);

        assertThat(deletedPage).isEmpty();
        assertThat(deletedChat1).isEmpty();
        assertThat(deletedChat2).isEmpty();

    }

    @Test
    @DisplayName("chat API : ChatController : deletePageApi : 통합 테스트 2")
    @WithMockUser(username = "xxxjjhhh", roles = {"ADMIN"})
    void deletePageApiTest2() throws Exception {

        // 테스트 : 로그인 후 페이지 삭제 (페이지 없는 경우)

        // given

        // when & then
        mockMvc.perform(delete("/chat/3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

    }

    @Test
    @DisplayName("chat API : ChatController : pageHistoryApi : 통합 테스트 1")
    @WithMockUser(username = "xxxjjhhh", roles = {"ADMIN"})
    void pageHistoryApiTest1() throws Exception {

        // 테스트 : 로그인 후 특정 페이지 대화 내용 조회

        // given
        PageEntity pageEntity = PageEntity.builder()
                .title("에이브릴 라빈 노래")
                .username("xxxjjhhh")
                .build();

        Long pageId = pageRepository.save(pageEntity).getId();

        ChatEntity chatEntity1 = ChatEntity.builder()
                .pageId(pageId)
                .content("에이브릴 라빈 노래 추천해줘")
                .messageType(MessageType.USER)
                .build();

        chatRepository.save(chatEntity1);

        ChatEntity chatEntity2 = ChatEntity.builder()
                .pageId(pageId)
                .content("complicated, what the hell 추천합니다.")
                .messageType(MessageType.ASSISTANT)
                .build();

        chatRepository.save(chatEntity2);

        // when & then
        mockMvc.perform(get("/chat/" + pageId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

    }
  
}