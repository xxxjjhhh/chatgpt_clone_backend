package org.example.chatgpt_clone_backend.domain.chat.dto;

import org.springframework.ai.chat.messages.MessageType;

public record ChatResponseDTO(
        String content,
        MessageType messageType
) {
}
