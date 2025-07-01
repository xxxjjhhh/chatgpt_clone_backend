package org.example.chatgpt_clone_backend.domain.user.service;

import org.example.chatgpt_clone_backend.domain.user.dto.UserRequestDTO;
import org.example.chatgpt_clone_backend.domain.user.entity.UserEntity;
import org.example.chatgpt_clone_backend.domain.user.entity.UserRoleType;
import org.example.chatgpt_clone_backend.domain.user.repository.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 자체 로그인 회원가입 (자체 인증 가입 후 로그인 시킬 예정 따라서 Long 응답)
    @Transactional
    public Long addUser(UserRequestDTO dto) {

        UserEntity entity = UserEntity.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .isLock(false)
                .social(false)
                .roleType(UserRoleType.USER) // 모두 일반 유저로 가입
                .nickname(dto.getNickname())
                .email(dto.getEmail())
                .build();

        return userRepository.save(entity).getId();
    }

    // 자체 로그인 Read
    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserEntity entity = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        return User.builder()
                .username(entity.getUsername())
                .password(entity.getPassword())
                .roles(entity.getRoleType().name())
                .accountLocked(entity.getIsLock())
                .build();
    }


}
