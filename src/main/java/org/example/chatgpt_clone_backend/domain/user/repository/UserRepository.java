package org.example.chatgpt_clone_backend.domain.user.repository;

import org.example.chatgpt_clone_backend.domain.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Boolean existsByUsername(String username);

    Optional<UserEntity> findByUsername(String username);

    void deleteByUsername(String username);
}
