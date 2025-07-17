package org.example.chatgpt_clone_backend.domain.user.dto;

public record UserResponseDTO(String username, Boolean social, String nickname, String email) {
}
