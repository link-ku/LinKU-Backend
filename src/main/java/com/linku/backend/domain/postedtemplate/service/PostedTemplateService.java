package com.linku.backend.domain.postedtemplate.service;

import com.linku.backend.domain.common.enums.Status;
import com.linku.backend.domain.icon.repository.IconRepository;
import com.linku.backend.domain.postedtemplate.PostedTemplate;
import com.linku.backend.domain.postedtemplate.PostedTemplateItem;
import com.linku.backend.domain.postedtemplate.dto.PostedTemplateMapper;
import com.linku.backend.domain.postedtemplate.dto.response.PostedTemplateListResponse;
import com.linku.backend.domain.postedtemplate.dto.response.PostedTemplateResponse;
import com.linku.backend.domain.postedtemplate.repository.PostedTemplateItemRepository;
import com.linku.backend.domain.postedtemplate.repository.PostedTemplateRepository;
import com.linku.backend.domain.template.Template;
import com.linku.backend.domain.template.TemplateItem;
import com.linku.backend.domain.template.dto.TemplateMapper;
import com.linku.backend.domain.template.dto.response.TemplateResponse;
import com.linku.backend.domain.template.repository.TemplateRepository;
import com.linku.backend.domain.user.User;
import com.linku.backend.domain.user.repository.UserRepository;
import com.linku.backend.global.exception.LinkuException;
import com.linku.backend.global.response.ResponseCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostedTemplateService {

    private static final String DEFAULT_SORT_TYPE = "newest";

    private final UserRepository userRepository;
    private final IconRepository iconRepository;
    private final TemplateRepository templateRepository;
    private final PostedTemplateRepository postedTemplateRepository;
    private final PostedTemplateItemRepository postedTemplateItemRepository;


    @Transactional(readOnly = true)
    public List<PostedTemplateListResponse> getMyPostedTemplates(String sort, String query) {
        Long userId = getCurrentUserId();
        List<PostedTemplate> postedTemplates = findPostedTemplatesByOwner(userId, sort, query);
        return convertToPostedTemplateListResponse(postedTemplates);
    }

    @Transactional(readOnly = true)
    public List<PostedTemplateListResponse> getPublicPostedTemplates(String sort, String query) {
        List<PostedTemplate> postedTemplates = findPublicPostedTemplates(sort, query);
        return convertToPostedTemplateListResponse(postedTemplates);
    }

    @Transactional(readOnly = true)
    public PostedTemplateResponse getPostedTemplateDetail(Long postedTemplateId) {
        PostedTemplate postedTemplate = validateAndGetPostedTemplate(postedTemplateId);
        return PostedTemplateMapper.toPostedTemplateResponse(postedTemplate);
    }

    @Transactional
    public void deletePostedTemplate(Long postedTemplateId) {
        Long userId = getCurrentUserId();
        PostedTemplate postedTemplate = validateAndGetOwnerPostedTemplate(postedTemplateId, userId);
        softDeletePostedTemplate(postedTemplate);
    }

    @Transactional
    public TemplateResponse clonePostedTemplate(Long postedTemplateId) {
        Long userId = getCurrentUserId();
        User user = validateAndGetUser(userId);
        PostedTemplate postedTemplate = validateAndGetPostedTemplate(postedTemplateId);

        incrementUsageCount(postedTemplate);

        Template newTemplate = createClonedTemplate(postedTemplate, user);
        List<TemplateItem> newTemplateItems = cloneTemplateItems(postedTemplate, newTemplate);
        newTemplate.setItems(newTemplateItems);

        Template savedTemplate = templateRepository.save(newTemplate);

        return TemplateMapper.toTemplateResponse(savedTemplate);
    }

    private List<PostedTemplate> findPostedTemplatesByOwner(Long userId, String sort, String query) {
        String sortOrder = (sort != null) ? sort : DEFAULT_SORT_TYPE;
        if (StringUtils.hasText(query)) {
            return postedTemplateRepository.findByOwner_UserIdAndStatusAndNameContainingOrOwnerNameContainingOrderBySort(
                    userId, Status.ACTIVE, query, query, sortOrder);
        }
        return postedTemplateRepository.findByOwner_UserIdAndStatusOrderBySort(userId, Status.ACTIVE, sortOrder);
    }

    private List<PostedTemplate> findPublicPostedTemplates(String sort, String query) {
        String sortOrder = (sort != null) ? sort : DEFAULT_SORT_TYPE;
        if (StringUtils.hasText(query)) {
            return postedTemplateRepository.findByStatusAndNameContainingOrOwnerNameContainingOrderBySort(
                    Status.ACTIVE, query, query, sortOrder);
        }
        return postedTemplateRepository.findByStatusOrderBySort(Status.ACTIVE, sortOrder);
    }

    private List<PostedTemplateListResponse> convertToPostedTemplateListResponse(List<PostedTemplate> postedTemplates) {
        return postedTemplates.stream()
                .map(PostedTemplateMapper::toPostedTemplateListResponse)
                .collect(Collectors.toList());
    }

    private PostedTemplate validateAndGetPostedTemplate(Long postedTemplateId) {
        return postedTemplateRepository.findByPostedTemplateIdAndStatus(postedTemplateId, Status.ACTIVE)
                .orElseThrow(() -> LinkuException.of(ResponseCode.POSTED_TEMPLATE_NOT_FOUND));
    }

    private PostedTemplate validateAndGetOwnerPostedTemplate(Long postedTemplateId, Long userId) {
        return postedTemplateRepository.findByPostedTemplateIdAndOwner_UserIdAndStatus(
                        postedTemplateId, userId, Status.ACTIVE)
                .orElseThrow(() -> LinkuException.of(ResponseCode.POSTED_TEMPLATE_NOT_FOUND));
    }

    private User validateAndGetUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> LinkuException.of(ResponseCode.USER_NOT_FOUND));
    }

    private void softDeletePostedTemplate(PostedTemplate postedTemplate) {
        postedTemplate.setStatus(Status.DELETED);
        postedTemplate.setDeletedAt(LocalDateTime.now());

        List<PostedTemplateItem> items = postedTemplateItemRepository.findAllByPostedTemplate_PostedTemplateIdAndStatus(
                postedTemplate.getPostedTemplateId(), Status.ACTIVE);
        items.forEach(item -> {
            item.setStatus(Status.DELETED);
            item.setDeletedAt(LocalDateTime.now());
        });
    }

    private void incrementUsageCount(PostedTemplate postedTemplate) {
        postedTemplate.setUsageCount(postedTemplate.getUsageCount() + 1);
    }

    private Template createClonedTemplate(PostedTemplate postedTemplate, User user) {
        return Template.builder()
                .name(postedTemplate.getName())
                .height(postedTemplate.getHeight())
                .owner(user)
                .cloned(true)
                .status(Status.ACTIVE)
                .build();
    }

    private List<TemplateItem> cloneTemplateItems(PostedTemplate postedTemplate, Template newTemplate) {
        List<PostedTemplateItem> postedTemplateItems = postedTemplateItemRepository
                .findAllByPostedTemplate_PostedTemplateIdAndStatus(postedTemplate.getPostedTemplateId(), Status.ACTIVE);

        return postedTemplateItems.stream()
                .map(postedItem -> TemplateItem.builder()
                        .template(newTemplate)
                        .name(postedItem.getName())
                        .siteUrl(postedItem.getSiteUrl())
                        .position(postedItem.getPosition())
                        .size(postedItem.getSize())
                        .icon(postedItem.getIcon())
                        .status(Status.ACTIVE)
                        .build())
                .collect(Collectors.toList());
    }

    private Long getCurrentUserId() {
        // TODO 인증&인가 측 구현 완료 후 SecurityContext 기반 사용자 ID 반환 로직 추가
        return 1L;
    }
}
