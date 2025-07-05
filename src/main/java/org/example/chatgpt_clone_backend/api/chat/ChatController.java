package org.example.chatgpt_clone_backend.api.chat;

import org.example.chatgpt_clone_backend.domain.chat.service.ChatService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }


}
