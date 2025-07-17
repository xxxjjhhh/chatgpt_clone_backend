package org.example.chatgpt_clone_backend.domain.chat.repository;

import org.example.chatgpt_clone_backend.domain.chat.entity.PageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface PageRepository extends JpaRepository<PageEntity, Long> {

    List<PageEntity> findByUsernameOrderByUpdatedDateDesc(String username);

    @Transactional
    void deleteByUsername(String username);

    // 페이지 최종 메시지 시간 업데이트 메소드
    @Transactional
    @Modifying
    @Query("UPDATE PageEntity p SET p.updatedDate = :updatedDate WHERE p.id = :id")
    void updateUpdateDateById(@Param("id") Long id, @Param("updatedDate") LocalDateTime updatedDate);

}
