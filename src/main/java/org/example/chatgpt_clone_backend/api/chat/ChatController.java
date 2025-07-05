package org.example.chatgpt_clone_backend.api.chat;

import org.example.chatgpt_clone_backend.domain.chat.dto.ChatRequestDTO;
import org.example.chatgpt_clone_backend.domain.chat.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.Collections;
import java.util.Map;

@RestController
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    // 신규 채팅 시작
    @PostMapping("/chat")
    public ResponseEntity<Map<String, Long>> createChatApi(@RequestBody ChatRequestDTO dto) {
        Long pageId = chatService.createPage(dto.getText());
        Map<String, Long> responseBody = Collections.singletonMap("pageId", pageId);
        return ResponseEntity.status(201).body(responseBody);
    }

    // 채팅 메시지 응답
    @PostMapping("/chat/{pageId}")
    public Flux<String> streamChatApi(@PathVariable("pageId") String pageId, @RequestBody ChatRequestDTO dto) {
        return chatService.generateTextStream(dto.getText(), pageId);
    }

    // 채팅 페이지 삭제
    @DeleteMapping("/chat/{pageId}")
    public ResponseEntity<?> deletePageApi(@PathVariable("pageId") String pageId) {
        Boolean isDelete = chatService.deletePageChat(Long.valueOf(pageId));

        if (isDelete) {
            return ResponseEntity.status(200).build();
        } else {
            return ResponseEntity.status(400).build();
        }
    }

}
