package com.linku.backend.domain.user;

import com.linku.backend.domain.common.BaseEntity;
import com.linku.backend.domain.user.dto.SignupRequest;
import com.linku.backend.global.auth.AuthRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
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

    @Column(nullable = false)
    private String name;

    @Email
    @Column(nullable = false)
    private String email;

    private String password;

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

    private User(String providerId, String email, String name) {
        this.providerId = providerId;
        this.email = email;
        this.name = name;
        this.authRole = AuthRole.ROLE_GUEST;
    }

    public void updateInfo(SignupRequest request) {
        this.name = request.name();
        this.authRole = AuthRole.ROLE_MEMBER;
    }
}