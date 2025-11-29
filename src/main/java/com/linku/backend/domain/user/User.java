package com.linku.backend.domain.user;

import com.linku.backend.domain.common.BaseEntity;
import com.linku.backend.domain.oauth.dto.GoogleUserInfo;
import com.linku.backend.global.auth.AuthRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

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

    @Column(length = 50)
    private String name;

    private String gMail;

    private String kuMail;

    @Column(nullable = false, unique = true, length = 30)
    private String providerId;

    @Column(length = 20)
    private AuthRole authRole;

    private LocalDateTime deletedAt;

    public static User guest(GoogleUserInfo userInfo) {
        return new User(
                userInfo.sub(),
                userInfo.email(),
                userInfo.name()
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