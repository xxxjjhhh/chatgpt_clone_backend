package org.example.chatgpt_clone_backend.domain.chat.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "chat_page_entity")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "title", nullable = false)
    private String title;

    @LastModifiedDate
    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    // 시간 업데이트를 위한 메소드
    public void updateTime() {
        this.updatedDate = LocalDateTime.now();
    }

}
