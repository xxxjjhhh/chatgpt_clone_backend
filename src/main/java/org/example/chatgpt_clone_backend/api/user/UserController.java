package org.example.chatgpt_clone_backend.api.user;

import org.example.chatgpt_clone_backend.domain.user.dto.UserRequestDTO;
import org.example.chatgpt_clone_backend.domain.user.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(value = "/user/join", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Long> joinApi(@Validated(UserRequestDTO.addGroup.class) @RequestBody UserRequestDTO dto) {
        Long id = userService.addUser(dto);
        return ResponseEntity.status(201).body(id);
    }

}
