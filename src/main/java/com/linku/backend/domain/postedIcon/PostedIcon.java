package com.linku.backend.domain.postedIcon;

import com.linku.backend.domain.common.BaseEntity;
import com.linku.backend.domain.icon.Icon;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Table(name = "posted_icons")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class PostedIcon extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postedIconId;

    private String name;

    private String imageUrl;

    // 동일한 Icon에 대해 중복적으로 PostedIcon을 생성하지 않기 위함
    private Long originalIconId;

    private LocalDateTime deletedAt;

    public static PostedIcon from(Icon icon) {
        return PostedIcon.builder()
                .name(icon.getName())
                .imageUrl(icon.getImageUrl())
                .originalIconId(icon.getIconId())
                .build();
    }
}