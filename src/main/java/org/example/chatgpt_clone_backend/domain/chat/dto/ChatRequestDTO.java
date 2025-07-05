package org.example.chatgpt_clone_backend.domain.chat.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatRequestDTO {

    private String text;
    private String pageId;
}
