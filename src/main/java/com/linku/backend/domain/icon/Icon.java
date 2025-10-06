package com.linku.backend.domain.icon;

import com.linku.backend.domain.common.BaseEntity;
import com.linku.backend.domain.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import lombok.Builder;

import java.time.LocalDateTime;

@Entity
@Table(name = "icons")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class Icon extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long iconId;

    private String name;

    private String imageUrl; // S3 링크

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User owner; // null이면 default 아이콘

    private LocalDateTime deletedAt;

    @Builder.Default
    private Boolean isDefault = false;

    @Builder.Default
    private Boolean cloned = false;
}