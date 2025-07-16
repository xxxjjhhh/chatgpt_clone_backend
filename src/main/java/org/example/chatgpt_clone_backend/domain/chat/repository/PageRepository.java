package org.example.chatgpt_clone_backend.domain.chat.repository;

import org.example.chatgpt_clone_backend.domain.chat.entity.PageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PageRepository extends JpaRepository<PageEntity, Long> {

    List<PageEntity> findByUsernameOrderByUpdatedDateDesc(String username);

    @Transactional
    void deleteByUsername(String username);

}
