package com.linku.backend.global.config;

import com.linku.backend.domain.common.enums.Status;
import com.linku.backend.domain.icon.Icon;
import com.linku.backend.domain.icon.repository.IconRepository;
import com.linku.backend.domain.user.User;
import com.linku.backend.domain.user.repository.UserRepository;
import com.linku.backend.global.auth.AuthRole;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final IconRepository iconRepository;

    @Value("${data-initialization.user.master.email}")
    private String masterEmail;

    @Value("${data-initialization.user.master.password}")
    private String masterPassword;

    @Value("${cloud.aws.s3.bucket-url}")
    private String bucketUrl;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            User masterUser = User.builder()
                    .name("master")
                    .gMail(masterEmail)
                    .providerId("MASTER_ADMIN")
                    .authRole(AuthRole.ROLE_MEMBER)
                    .status(Status.ACTIVE)
                    .build();
            userRepository.save(masterUser);
        }

        if (iconRepository.count() == 0) {
            List<String> iconFileNames = List.of(
                    // iconic
                    "contact", "users-round", "message-circle-more", "map-pinned", "speech",
                    // building
                    "university", "school", "house", "building", "landmark",
                    // study
                    "book-marked", "scroll-text", "library-big", "graduation-cap", "backpack", "trophy",
                    // services
                    "bot", "mail", "github", "figma", "youtube", "instagram", "slack", "linkedin",
                    // utils
                    "clock", "alarm-clock", "calendar-days", "utensils", "lightbulb", "bell-ring", "bed", "paperclip"
            );

            List<Icon> defaultIcons = iconFileNames.stream()
                    .map(this::createDefaultIcon)
                    .toList();
            iconRepository.saveAll(defaultIcons);
        }
    }

    private Icon createDefaultIcon(String name) {
        return Icon.builder()
                .name(name)
                .imageUrl(bucketUrl + "/" + name + ".svg")
                .owner(null)
                .isDefault(true)
                .status(Status.ACTIVE)
                .build();
    }
}