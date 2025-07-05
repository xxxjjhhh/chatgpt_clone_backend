package org.example.chatgpt_clone_backend.domain.chat.service;

import org.example.chatgpt_clone_backend.domain.chat.dto.ChatResponseDTO;
import org.example.chatgpt_clone_backend.domain.chat.entity.ChatEntity;
import org.example.chatgpt_clone_backend.domain.chat.repository.ChatRepository;
import org.example.chatgpt_clone_backend.domain.chat.repository.PageRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.messages.MessageType;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @InjectMocks
    ChatService chatService;

    @Mock
    ChatRepository chatRepository;

    @Mock
    PageRepository pageRepository;

    @Test
    @DisplayName("chat 도메인 : ChatService : readAllChatsPageId : 테스트 1")
    void readAllChatsPageIdTest1() {

        // 테스트 : pageId 부여시 조회 테스트

        // given
        Long pageId = 1L;

        ChatEntity chat1 = ChatEntity.builder()
                .id(1L)
                .pageId(pageId)
                .content("안녕하세요")
                .messageType(MessageType.USER)
                .createdDate(LocalDateTime.of(2023, 1, 1, 12, 0))
                .build();

        ChatEntity chat2 = ChatEntity.builder()
                .id(2L)
                .pageId(pageId)
                .content("무엇을 도와드릴까요?")
                .messageType(MessageType.ASSISTANT)
                .createdDate(LocalDateTime.of(2023, 1, 1, 12, 1))
                .build();

        List<ChatEntity> mockResult = List.of(chat1, chat2);
        given(chatRepository.findByPageIdOrderByCreatedDateAsc(pageId)).willReturn(mockResult);

        // when
        List<ChatResponseDTO> result = chatService.readAllChatsPageId(pageId);

        // then
        assertThat(result.get(0).content()).isEqualTo("안녕하세요");
        assertThat(result.get(0).messageType()).isEqualTo(MessageType.USER);
        assertThat(result.get(1).content()).isEqualTo("무엇을 도와드릴까요?");
        assertThat(result.get(1).messageType()).isEqualTo(MessageType.ASSISTANT);

    }

}