package com.linku.backend.domain.like;

import com.linku.backend.domain.common.BaseEntity;
import com.linku.backend.domain.postedtemplate.PostedTemplate;
import com.linku.backend.domain.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "likes", uniqueConstraints = {
    @UniqueConstraint(
        name = "like_uk",
        columnNames = {"user_id", "posted_template_id"}
    )
})
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class Like extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "posted_template_id", nullable = false)
    private PostedTemplate postedTemplate;
}
