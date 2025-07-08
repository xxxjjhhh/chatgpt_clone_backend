package org.example.chatgpt_clone_backend.api.chat;

import org.example.chatgpt_clone_backend.domain.chat.dto.PageResponseDTO;
import org.example.chatgpt_clone_backend.domain.chat.service.ChatService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PageController {

    private final ChatService chatService;

    public PageController(ChatService chatService) {
        this.chatService = chatService;
    }

    // 유저별 페이지 목록
    @GetMapping("/page")
    public List<PageResponseDTO> readAllPageApi() {
        return chatService.readAllPages();
    }

}
