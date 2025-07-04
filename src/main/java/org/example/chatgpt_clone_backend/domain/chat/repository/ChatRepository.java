package org.example.chatgpt_clone_backend.domain.chat.repository;

import org.example.chatgpt_clone_backend.domain.chat.entity.ChatEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRepository extends JpaRepository<ChatEntity, Long> {

    List<ChatEntity> findByPageIdOrderByCreatedDateAsc(Long pageId);
}
