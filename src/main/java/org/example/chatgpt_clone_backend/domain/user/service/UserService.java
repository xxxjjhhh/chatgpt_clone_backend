package org.example.chatgpt_clone_backend.domain.user.service;

import org.example.chatgpt_clone_backend.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


}
