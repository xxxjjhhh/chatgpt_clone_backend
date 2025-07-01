package org.example.chatgpt_clone_backend.domain.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequestDTO {

    private String username;
    private String password;
    private String nickname;
    private String email;
}
