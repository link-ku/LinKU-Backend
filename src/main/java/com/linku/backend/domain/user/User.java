package com.linku.backend.domain.user;

import com.linku.backend.domain.common.BaseEntity;
import com.linku.backend.global.auth.AuthRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String name;

    private String gMail;

    private String kuMail;

    @Column(nullable = false, unique = true)
    private String providerId;

    private Boolean verified;

    private LocalDateTime deletedAt;

    private AuthRole authRole;

    public static User guest(OAuth2User oAuth2User) {
        return new User(
                oAuth2User.getAttribute("sub"),
                oAuth2User.getAttribute("email"),
                oAuth2User.getAttribute("name")
        );
    }

    private User(String providerId, String gMail, String name) {
        this.providerId = providerId;
        this.gMail = gMail;
        this.name = name;
        this.authRole = AuthRole.ROLE_GUEST;
    }

    public void updateInfo(String kuMail) {
        this.kuMail = kuMail;
        this.authRole = AuthRole.ROLE_MEMBER;
    }
}