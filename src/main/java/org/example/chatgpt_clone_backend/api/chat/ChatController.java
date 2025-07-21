package org.example.chatgpt_clone_backend.api.chat;

import org.example.chatgpt_clone_backend.domain.chat.dto.ChatRequestDTO;
import org.example.chatgpt_clone_backend.domain.chat.dto.ChatResponseDTO;
import org.example.chatgpt_clone_backend.domain.chat.dto.PageResponseDTO;
import org.example.chatgpt_clone_backend.domain.chat.service.ChatService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    // 유저별 페이지 목록
    @GetMapping(value = "/chat", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PageResponseDTO> readAllPageApi(

    ) {
        return chatService.readAllPages();
    }

    // 신규 채팅 시작
    @PostMapping(value = "/chat", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Long>> createChatApi(
            @RequestBody ChatRequestDTO dto
    ) {
        Long pageId = chatService.createPage(dto.getText());
        Map<String, Long> responseBody = Collections.singletonMap("pageId", pageId);
        return ResponseEntity.status(201).body(responseBody);
    }

    // 채팅 메시지 응답
    @PostMapping(value = "/chat/{pageId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Flux<String> streamChatApi(
            @PathVariable("pageId") String pageId,
            @RequestBody ChatRequestDTO dto
    ) {
        return chatService.generateTextStream(dto.getText(), pageId);
    }

    // 채팅 페이지 삭제
    @DeleteMapping(value = "/chat/{pageId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deletePageApi(
            @PathVariable("pageId") String pageId
    ) {
        Boolean isDelete = chatService.deletePageChat(Long.valueOf(pageId));

        if (isDelete) {
            return ResponseEntity.status(200).build();
        } else {
            return ResponseEntity.status(400).build();
        }
    }

    // 채팅 페이지 대화 목록 가져오기
    @GetMapping(value = "/chat/{pageId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<ChatResponseDTO> pageHistoryApi(
            @PathVariable("pageId") Long pageId
    ) {
        return chatService.readAllChatsPageId(pageId);
    }

}
