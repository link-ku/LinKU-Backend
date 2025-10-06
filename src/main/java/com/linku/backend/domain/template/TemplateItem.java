package com.linku.backend.domain.template;

import com.linku.backend.domain.common.BaseEntity;
import com.linku.backend.domain.common.template.TemplateItemPosition;
import com.linku.backend.domain.common.template.TemplateItemSize;
import com.linku.backend.domain.icon.Icon;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Table(name = "template_items")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class TemplateItem extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long templateItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", nullable = false)
    private Template template;

    private String name;

    private String siteUrl;

    @Embedded
    private TemplateItemPosition position;

    @Embedded
    private TemplateItemSize size;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "icon_icon_id")
    private Icon icon;

    private LocalDateTime deletedAt;
}