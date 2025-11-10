package com.linku.backend.domain.like.repository;

import com.linku.backend.domain.like.Like;
import com.linku.backend.domain.postedtemplate.PostedTemplate;
import com.linku.backend.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByUserAndPostedTemplate(User user, PostedTemplate postedTemplate);
}