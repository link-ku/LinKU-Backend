package com.linku.backend.domain.postedIcon;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostedIconRepository extends JpaRepository<PostedIcon, Long> {
    Optional<PostedIcon> findByOriginalIconId(Long originalIconId);
}