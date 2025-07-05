package org.example.chatgpt_clone_backend.domain.chat.service;

import org.example.chatgpt_clone_backend.domain.chat.dto.ChatResponseDTO;
import org.example.chatgpt_clone_backend.domain.chat.entity.ChatEntity;
import org.example.chatgpt_clone_backend.domain.chat.entity.PageEntity;
import org.example.chatgpt_clone_backend.domain.chat.repository.ChatRepository;
import org.example.chatgpt_clone_backend.domain.chat.repository.PageRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class ChatService {

    private final ChatRepository chatRepository;
    private final PageRepository pageRepository;
    private final ChatMemoryRepository chatMemoryRepository;
    private final ChatClient openAiChatClient;

    public ChatService(ChatRepository chatRepository, PageRepository pageRepository, ChatMemoryRepository chatMemoryRepository, @Qualifier("openAiChatClient") ChatClient openAiChatClient) {
        this.chatRepository = chatRepository;
        this.pageRepository = pageRepository;
        this.chatMemoryRepository = chatMemoryRepository;
        this.openAiChatClient = openAiChatClient;
    }

    // 신규 채팅 시작 (첫 텍스트 받은 후 제목도 생성)
    @Transactional
    public Long createPage(String text) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        Prompt prompt = new Prompt("다음 내용을 약 12자 이내로 요약해서 제목을 지어줘:\n" + text);
        String titleSummation = openAiChatClient.prompt(prompt)
                .call()
                .content();

        // 채팅 페이지 생성
        PageEntity entity = PageEntity.builder()
                .username(username)
                .title(titleSummation)
                .build();

        return pageRepository.save(entity).getId();
    }

    // 기존 채팅 대화 내역 불러오기
    @Transactional(readOnly = true)
    public List<ChatResponseDTO> readAllChatsPageId(Long pageId) {

        // pageId 값의 PageEntity username이 세션 username 이랑 같은지
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<PageEntity> pageEntity = pageRepository.findById(pageId);
        if (pageEntity.isEmpty()) {
            return Collections.emptyList();
        }

        if (pageEntity.get().getUsername().equals(username)) {
            return Collections.emptyList();
        }

        // 조회 후 응답
        return chatRepository.findByPageIdOrderByCreatedDateAsc(pageId).stream()
                .map(entity -> new ChatResponseDTO(
                        entity.getContent(),
                        entity.getMessageType()
                ))
                .toList();
    }

    // LLM API 호출
    @Transactional
    public Flux<String> generateTextStream(String text, String pageId) {

        // 전체 대화 저장용
        ChatEntity chatUserEntity = ChatEntity.builder()
                .pageId(Long.valueOf(pageId))
                .content(text)
                .messageType(MessageType.USER)
                .build();

        // 멀티턴용 Prompt 메시지
        ChatMemory chatMemory = MessageWindowChatMemory.builder()
                .maxMessages(20)
                .chatMemoryRepository(chatMemoryRepository)
                .build();
        chatMemory.add(pageId, new UserMessage(text));// 신규 메시지 추가

        // 옵션
        OpenAiChatOptions options = OpenAiChatOptions.builder()
                .model(OpenAiApi.ChatModel.GPT_4_1_NANO)
                .temperature(0.7)
                .build();

        // 프롬프트
        Prompt prompt = new Prompt(chatMemory.get(pageId), options);

        // 스트림 응답 메시지를 저장할 임시 버퍼 (히스토리 관리를 위한)
        StringBuilder responseBuffer = new StringBuilder();

        // 요청 및 응답
        return openAiChatClient.prompt(prompt)
                .stream()
                .content()
                .map(token -> {
                    responseBuffer.append(token);
                    return token;
                })
                .doOnComplete(() -> {
                    // 멀티턴용 ChatMemory 저장
                    chatMemory.add(pageId, new AssistantMessage(responseBuffer.toString()));
                    chatMemoryRepository.saveAll(pageId, chatMemory.get(pageId));

                    // 전체 대화 저장용
                    ChatEntity chatAssistantEntity = ChatEntity.builder()
                            .pageId(Long.valueOf(pageId))
                            .content(responseBuffer.toString())
                            .messageType(MessageType.ASSISTANT)
                            .build();

                    chatRepository.saveAll(List.of(chatUserEntity, chatAssistantEntity));
                });

    }

    // 채팅 내역 삭제

}
