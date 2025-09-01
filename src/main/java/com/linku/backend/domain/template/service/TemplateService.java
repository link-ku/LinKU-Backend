package com.linku.backend.domain.template.service;

import com.linku.backend.domain.common.enums.Status;
import com.linku.backend.domain.postedtemplate.PostedTemplate;
import com.linku.backend.domain.postedtemplate.PostedTemplateItem;
import com.linku.backend.domain.postedtemplate.dto.PostedTemplateItemMapper;
import com.linku.backend.domain.postedtemplate.dto.response.PostedTemplateResponse;
import com.linku.backend.domain.postedtemplate.repository.PostedTemplateRepository;
import com.linku.backend.domain.template.Template;
import com.linku.backend.domain.template.TemplateItem;
import com.linku.backend.domain.template.dto.TemplateItemIconRequestMapper;
import com.linku.backend.domain.template.dto.TemplateItemMapper;
import com.linku.backend.domain.template.dto.TemplateItemPositionRequestMapper;
import com.linku.backend.domain.template.dto.TemplateItemSizeRequestMapper;
import com.linku.backend.domain.template.dto.request.TemplateCreateRequest;
import com.linku.backend.domain.template.dto.request.TemplateItemUpdateRequest;
import com.linku.backend.domain.template.dto.request.TemplateUpdateRequest;
import com.linku.backend.domain.template.dto.response.TemplateListResponse;
import com.linku.backend.domain.template.dto.response.TemplateResponse;
import com.linku.backend.domain.template.repository.TemplateItemRepository;
import com.linku.backend.domain.template.repository.TemplateRepository;
import com.linku.backend.domain.template.util.TemplateValidator;
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
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TemplateService {

    private static final String DEFAULT_SORT_TYPE = "newest";

    private final UserRepository userRepository;
    private final TemplateRepository templateRepository;
    private final TemplateItemRepository templateItemRepository;
    private final PostedTemplateRepository postedTemplateRepository;

    private final TemplateItemMapper templateItemMapper;
    private final PostedTemplateItemMapper postedTemplateItemMapper;
    private final TemplateItemPositionRequestMapper templateItemPositionRequestMapper;
    private final TemplateItemSizeRequestMapper templateItemSizeRequestMapper;
    private final TemplateItemIconRequestMapper templateItemIconRequestMapper;
    private final TemplateValidator templateValidator;

    @Transactional
    public TemplateResponse createTemplate(TemplateCreateRequest request) {
        User user = validateAndGetUser(getCurrentUserId());

        templateValidator.validateTemplateItemsForCreate(request.getHeight(), request.getItems());

        Template newTemplate = createNewTemplate(request, user);
        List<TemplateItem> newItems = createNewTemplateItems(request, newTemplate);
        newTemplate.setItems(newItems);

        Template savedTemplate = templateRepository.save(newTemplate);

        return convertToTemplateResponse(savedTemplate);
    }

    @Transactional(readOnly = true)
    public List<TemplateListResponse> getOwnedTemplates(String sort, String query) {
        Long userId = getCurrentUserId();
        List<Template> templates = findOwnedTemplates(userId, sort, query);

        return templates.stream()
                .map(this::convertToTemplateListResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TemplateListResponse> getClonedTemplates(String sort, String query) {
        Long userId = getCurrentUserId();
        List<Template> templates = findClonedTemplates(userId, sort, query);

        return templates.stream()
                .map(this::convertToTemplateListResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public TemplateResponse updateTemplate(Long templateId, TemplateUpdateRequest request) {
        Template template = validateAndGetTemplate(templateId, getCurrentUserId());

        templateValidator.validateTemplateItemsForUpdate(request.getHeight(), request.getItems());

        updateTemplateBasicInfo(template, request);
        updateTemplateItems(template, request.getItems());

        return convertToTemplateResponse(template);
    }

    @Transactional
    public void deleteTemplate(Long templateId) {
        Template template = validateAndGetTemplate(templateId, getCurrentUserId());
        softDeleteTemplate(template);
    }

    @Transactional(readOnly = true)
    public TemplateResponse getTemplateDetail(Long templateId) {
        Template template = validateAndGetTemplate(templateId, getCurrentUserId());
        return convertToTemplateResponse(template);
    }

    @Transactional
    public PostedTemplateResponse postTemplate(Long templateId) {
        Template template = validateAndGetTemplate(templateId, getCurrentUserId());
        User user = validateAndGetUser(getCurrentUserId());

        PostedTemplate newPostedTemplate = createPostedTemplate(template, user);
        List<PostedTemplateItem> newPostedTemplateItems = createPostedTemplateItems(template, newPostedTemplate);
        newPostedTemplate.setItems(newPostedTemplateItems);

        PostedTemplate savedPostedTemplate = postedTemplateRepository.save(newPostedTemplate);

        return convertToPostedTemplateResponse(savedPostedTemplate);
    }

    private List<Template> findOwnedTemplates(Long userId, String sort, String query) {
        String sortOrder = (sort != null) ? sort : DEFAULT_SORT_TYPE;
        if (StringUtils.hasText(query)) {
            return templateRepository.findByOwner_UserIdAndClonedFalseAndStatusAndNameContainingOrderBySort(
                    userId, Status.ACTIVE, query, sortOrder);
        }
        return templateRepository.findByOwner_UserIdAndClonedFalseAndStatusOrderBySort(userId, Status.ACTIVE, sortOrder);
    }

    private List<Template> findClonedTemplates(Long userId, String sort, String query) {
        String sortOrder = (sort != null) ? sort : DEFAULT_SORT_TYPE;
        if (StringUtils.hasText(query)) {
            return templateRepository.findByOwner_UserIdAndClonedTrueAndStatusAndNameContainingOrderBySort(
                    userId, Status.ACTIVE, query, sortOrder);
        }
        return templateRepository.findByOwner_UserIdAndClonedTrueAndStatusOrderBySort(userId, Status.ACTIVE, sortOrder);
    }

    private User validateAndGetUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> LinkuException.of(ResponseCode.USER_NOT_FOUND));
    }

    private Template validateAndGetTemplate(Long templateId, Long userId) {
        return templateRepository.findByTemplateIdAndOwner_UserIdAndStatus(templateId, userId, Status.ACTIVE)
                .orElseThrow(() -> LinkuException.of(ResponseCode.TEMPLATE_NOT_FOUND));
    }

    private Template createNewTemplate(TemplateCreateRequest request, User user) {
        return Template.builder()
                .name(request.getName())
                .height(request.getHeight())
                .owner(user)
                .cloned(false)
                .status(Status.ACTIVE)
                .build();
    }

    private List<TemplateItem> createNewTemplateItems(TemplateCreateRequest request, Template template) {
        return request.getItems().stream()
                .map(itemReq -> TemplateItem.builder()
                        .template(template)
                        .name(itemReq.getName())
                        .siteUrl(itemReq.getSiteUrl())
                        .position(templateItemPositionRequestMapper.toEntity(itemReq.getPosition()))
                        .size(templateItemSizeRequestMapper.toEntity(itemReq.getSize()))
                        .icon(templateItemIconRequestMapper.toEntity(itemReq.getIcon()))
                        .status(Status.ACTIVE)
                        .build())
                .collect(Collectors.toList());
    }

    private void updateTemplateBasicInfo(Template template, TemplateUpdateRequest request) {
        template.setName(request.getName());
        template.setHeight(request.getHeight());
    }

    private void updateTemplateItems(Template template, List<TemplateItemUpdateRequest> requestItems) {
        Map<Long, TemplateItemUpdateRequest> itemRequestsMap = requestItems.stream()
                .filter(item -> item.getTemplateItemId() != null)
                .collect(Collectors.toMap(TemplateItemUpdateRequest::getTemplateItemId, item -> item));

        template.getItems().removeIf(item -> !itemRequestsMap.containsKey(item.getTemplateItemId()));

        template.getItems().forEach(item -> {
            TemplateItemUpdateRequest request = itemRequestsMap.get(item.getTemplateItemId());
            item.setName(request.getName());
            item.setSiteUrl(request.getSiteUrl());
            item.setPosition(templateItemPositionRequestMapper.toEntity(request.getPosition()));
            item.setSize(templateItemSizeRequestMapper.toEntity(request.getSize()));
            item.setIcon(templateItemIconRequestMapper.toEntity(request.getIcon()));
        });

        requestItems.stream()
                .filter(item -> item.getTemplateItemId() == null)
                .forEach(itemReq -> template.getItems().add(TemplateItem.builder()
                        .template(template)
                        .name(itemReq.getName())
                        .siteUrl(itemReq.getSiteUrl())
                        .position(templateItemPositionRequestMapper.toEntity(itemReq.getPosition()))
                        .size(templateItemSizeRequestMapper.toEntity(itemReq.getSize()))
                        .icon(templateItemIconRequestMapper.toEntity(itemReq.getIcon()))
                        .status(Status.ACTIVE)
                        .build()));
    }

    private void softDeleteTemplate(Template template) {
        template.setStatus(Status.DELETED);
        template.setDeletedAt(LocalDateTime.now());

        template.getItems().forEach(item -> {
            item.setStatus(Status.DELETED);
            item.setDeletedAt(LocalDateTime.now());
        });
    }

    private PostedTemplate createPostedTemplate(Template template, User user) {
        return PostedTemplate.builder()
                .name(template.getName())
                .height(template.getHeight())
                .owner(user)
                .likesCount(0)
                .usageCount(0)
                .status(Status.ACTIVE)
                .build();
    }

    private List<PostedTemplateItem> createPostedTemplateItems(Template template, PostedTemplate postedTemplate) {
        List<TemplateItem> templateItems = templateItemRepository.findAllByTemplate_TemplateIdAndStatus(
                template.getTemplateId(), Status.ACTIVE);
        
        return templateItems.stream()
                .map(templateItem -> PostedTemplateItem.builder()
                        .postedTemplate(postedTemplate)
                        .name(templateItem.getName())
                        .siteUrl(templateItem.getSiteUrl())
                        .position(templateItem.getPosition())
                        .size(templateItem.getSize())
                        .icon(templateItem.getIcon())
                        .status(Status.ACTIVE)
                        .build())
                .collect(Collectors.toList());
    }

    private TemplateResponse convertToTemplateResponse(Template template) {
        return TemplateResponse.builder()
            .templateId(template.getTemplateId())
            .name(template.getName())
            .height(template.getHeight())
            .cloned(Boolean.TRUE.equals(template.getCloned()))
            .items(templateItemMapper.toResponseList(template.getItems()))
            .build();
    }

    private TemplateListResponse convertToTemplateListResponse(Template template) {
        return TemplateListResponse.builder()
            .templateId(template.getTemplateId())
            .name(template.getName())
            .height(template.getHeight())
            .cloned(Boolean.TRUE.equals(template.getCloned()))
            .items(template.getItems() != null ? template.getItems().size() : 0)
            .build();
    }

    private PostedTemplateResponse convertToPostedTemplateResponse(PostedTemplate postedTemplate) {
        return PostedTemplateResponse.builder()
                .postedTemplateId(postedTemplate.getPostedTemplateId())
                .name(postedTemplate.getName())
                .ownerId(postedTemplate.getOwner().getUserId())
                .ownerName(postedTemplate.getOwner().getName())
                .height(postedTemplate.getHeight())
                .likesCount(postedTemplate.getLikesCount())
                .usageCount(postedTemplate.getUsageCount())
                .items(postedTemplateItemMapper.toResponseList(postedTemplate.getItems()))
                .build();
    }

    private Long getCurrentUserId() {
        // TODO 인증&인가 측 구현 완료 후 SecurityContext 기반 사용자 ID 반환 로직 추가
        return 1L;
    }
}
