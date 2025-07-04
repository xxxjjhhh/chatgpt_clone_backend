package org.example.chatgpt_clone_backend.domain.chat.repository;

import org.example.chatgpt_clone_backend.domain.chat.entity.PageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PageRepository extends JpaRepository<PageEntity, Long> {
}
