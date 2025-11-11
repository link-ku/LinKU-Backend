package com.linku.backend.domain.postedtemplate;

import com.linku.backend.domain.common.BaseEntity;
import com.linku.backend.domain.common.template.TemplateItemPosition;
import com.linku.backend.domain.common.template.TemplateItemSize;
import com.linku.backend.domain.postedIcon.PostedIcon;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Table(name = "posted_template_items")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class PostedTemplateItem extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postedTemplateItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "posted_template_id", nullable = false)
    private PostedTemplate postedTemplate;

    private String name;

    private String siteUrl;

    @Embedded
    private TemplateItemPosition position;

    @Embedded
    private TemplateItemSize size;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "posted_icon_id")
    private PostedIcon postedIcon;

    private LocalDateTime deletedAt;
}