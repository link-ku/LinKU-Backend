package com.linku.backend.domain.like.service;

import com.linku.backend.domain.like.Like;
import com.linku.backend.domain.like.dto.response.LikeResponse;
import com.linku.backend.domain.like.repository.LikeRepository;
import com.linku.backend.domain.postedtemplate.PostedTemplate;
import com.linku.backend.domain.postedtemplate.repository.PostedTemplateRepository;
import com.linku.backend.domain.user.User;
import com.linku.backend.domain.user.repository.UserRepository;
import com.linku.backend.global.exception.LinkuException;
import com.linku.backend.global.response.ResponseCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.linku.backend.global.response.ResponseCode.LIKE_COUNT_MINUS_ERR;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final PostedTemplateRepository postedTemplateRepository;

    @Transactional
    public LikeResponse createLike(Long postedTemplateId) {
        Long userId = getCurrentUserId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> LinkuException.of(ResponseCode.USER_NOT_FOUND));

        PostedTemplate postedTemplate = postedTemplateRepository.findById(postedTemplateId)
                .orElseThrow(() -> LinkuException.of(ResponseCode.POSTED_TEMPLATE_NOT_FOUND));

        likeRepository.findByUserAndPostedTemplate(user, postedTemplate).ifPresent(like -> {
            throw LinkuException.of(ResponseCode.LIKE_ALREADY_EXISTS);
        });

        Like newLike = Like.builder()
                .user(user)
                .postedTemplate(postedTemplate)
                .build();
        likeRepository.save(newLike);
        increaseLikesCount(postedTemplate);
        return LikeResponse.of(true, postedTemplate.getLikesCount());
    }

    @Transactional
    public LikeResponse deleteLike(Long postedTemplateId) {
        Long userId = getCurrentUserId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> LinkuException.of(ResponseCode.USER_NOT_FOUND));

        PostedTemplate postedTemplate = postedTemplateRepository.findById(postedTemplateId)
                .orElseThrow(() -> LinkuException.of(ResponseCode.POSTED_TEMPLATE_NOT_FOUND));

        Like like = likeRepository.findByUserAndPostedTemplate(user, postedTemplate)
                .orElseThrow(() -> LinkuException.of(ResponseCode.LIKE_NOT_FOUND));

        likeRepository.delete(like);
        decreaseLikesCount(postedTemplate);
        return LikeResponse.of(false, postedTemplate.getLikesCount());
    }

    public void increaseLikesCount(PostedTemplate postedTemplate) {
        Integer currentLikes = postedTemplate.getLikesCount();
        postedTemplate.setLikesCount(currentLikes+1);
    }

    public void decreaseLikesCount(PostedTemplate postedTemplate) {
        Integer currentLikes = postedTemplate.getLikesCount();
        if (currentLikes > 0) postedTemplate.setLikesCount(currentLikes-1);
        else throw LinkuException.of(LIKE_COUNT_MINUS_ERR);
    }

    private Long getCurrentUserId() {
        // TODO 인증&인가 측 구현 완료 후 SecurityContext 기반 사용자 ID 반환 로직 추가
        return 1L;
    }
}