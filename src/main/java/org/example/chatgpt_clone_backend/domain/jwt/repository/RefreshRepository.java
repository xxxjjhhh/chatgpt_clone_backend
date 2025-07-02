package org.example.chatgpt_clone_backend.domain.jwt.repository;

import org.example.chatgpt_clone_backend.domain.jwt.entity.RefreshEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshRepository extends JpaRepository<RefreshEntity, Long> {
}
