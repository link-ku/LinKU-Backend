package com.linku.backend.domain.user;

import com.linku.backend.domain.common.BaseEntity;
import com.linku.backend.domain.oauth.dto.GoogleUserInfo;
import com.linku.backend.global.auth.AuthRole;
import com.linku.backend.global.exception.LinkuException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

import static com.linku.backend.global.response.ResponseCode.GOOGLE_MAIL_INVALID;

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

    @Column(length = 1024)
    private String picture;

    @Column(length = 20)
    @Enumerated(EnumType.STRING)
    private AuthRole authRole;

    private LocalDateTime deletedAt;

    public static User guest(GoogleUserInfo userInfo) {
        return new User(
                userInfo.sub(),
                userInfo.email(),
                userInfo.name(),
                userInfo.picture()
        );
    }

    private User(String providerId, String gMail, String name, String picture) {
        this.providerId = providerId;
        this.gMail = gMail;
        this.name = name;
        this.picture = picture;
        this.authRole = AuthRole.ROLE_GUEST;
    }

    public void updateInfo(String kuMail) {
        this.kuMail = kuMail;
        this.authRole = AuthRole.ROLE_MEMBER;
    }

    public String getGoogleId() {
        if (gMail == null || gMail.isBlank()) {
            return null;
        }

        int atIndex = gMail.indexOf('@');
        if (atIndex <= 0) {
            throw LinkuException.of(GOOGLE_MAIL_INVALID);
        }

        return gMail.substring(0, atIndex);
    }
}