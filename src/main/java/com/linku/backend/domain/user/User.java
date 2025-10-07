package com.linku.backend.domain.user;

import com.linku.backend.domain.common.BaseEntity;
import com.linku.backend.global.auth.AuthRole;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    private String password;

    private Boolean verified;

    private LocalDateTime deletedAt;

    private AuthRole authRole;
}