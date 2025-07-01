package org.example.chatgpt_clone_backend.domain.user.repository;

import org.example.chatgpt_clone_backend.domain.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
}
