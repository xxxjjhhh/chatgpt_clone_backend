package org.example.chatgpt_clone_backend.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequestDTO {

    @NotBlank @Size(min = 4)
    private String username;
    @NotBlank @Size(min = 4)
    private String password;
    @NotBlank
    private String nickname;
    @Email
    private String email;
}
