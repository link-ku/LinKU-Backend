package com.linku.backend.domain.template.service;

import com.linku.backend.domain.common.enums.Status;
import com.linku.backend.domain.icon.Icon;
import com.linku.backend.domain.icon.repository.IconRepository;
import com.linku.backend.domain.postedIcon.PostedIcon;
import com.linku.backend.domain.postedIcon.PostedIconRepository;
import com.linku.backend.domain.postedtemplate.PostedTemplate;
import com.linku.backend.domain.postedtemplate.PostedTemplateItem;
import com.linku.backend.domain.postedtemplate.dto.PostedTemplateMapper;
import com.linku.backend.domain.postedtemplate.dto.response.PostedTemplateResponse;
import com.linku.backend.domain.postedtemplate.repository.PostedTemplateRepository;
import com.linku.backend.domain.template.Template;
import com.linku.backend.domain.template.TemplateItem;
import com.linku.backend.domain.template.dto.TemplateItemMapper;
import com.linku.backend.domain.template.dto.TemplateMapper;
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
    private final IconRepository iconRepository;
    private final TemplateRepository templateRepository;
    private final TemplateItemRepository templateItemRepository;
    private final PostedTemplateRepository postedTemplateRepository;
    private final PostedIconRepository postedIconRepository;

    private final TemplateValidator templateValidator;

    @Transactional
    public TemplateResponse createTemplate(Long userId, TemplateCreateRequest request) {
        User user = validateAndGetUser(userId);

        templateValidator.validateTemplateItemsForCreate(request.getHeight(), request.getItems());

        Template newTemplate = createNewTemplate(request, user);
        List<TemplateItem> newItems = createNewTemplateItems(userId, request, newTemplate);
        newTemplate.setItems(newItems);

        Template savedTemplate = templateRepository.save(newTemplate);

        return TemplateMapper.toTemplateResponse(savedTemplate);
    }

    @Transactional(readOnly = true)
    public List<TemplateListResponse> getOwnedTemplates(Long userId, String sort, String query) {
        List<Template> templates = findOwnedTemplates(userId, sort, query);

        return templates.stream()
                .map(TemplateMapper::toTemplateListResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TemplateListResponse> getClonedTemplates(Long userId, String sort, String query) {
        List<Template> templates = findClonedTemplates(userId, sort, query);

        return templates.stream()
                .map(TemplateMapper::toTemplateListResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public TemplateResponse updateTemplate(Long userId, Long templateId, TemplateUpdateRequest request) {
        Template template = validateAndGetTemplate(templateId, userId);

        templateValidator.validateTemplateItemsForUpdate(request.getHeight(), request.getItems());

        updateTemplateBasicInfo(template, request);
        updateTemplateItems(userId, template, request.getItems());

        return TemplateMapper.toTemplateResponse(template);
    }

    @Transactional
    public void deleteTemplate(Long userId, Long templateId) {
        Template template = validateAndGetTemplate(templateId, userId);
        softDeleteTemplate(template);
    }

    @Transactional(readOnly = true)
    public TemplateResponse getTemplateDetail(Long userId, Long templateId) {
        Template template = validateAndGetTemplate(templateId, userId);
        return TemplateMapper.toTemplateResponse(template);
    }

    @Transactional
    public PostedTemplateResponse postTemplate(Long userId, Long templateId) {
        Template template = validateAndGetTemplate(templateId, userId);
        User user = validateAndGetUser(userId);

        PostedTemplate newPostedTemplate = createPostedTemplate(template, user);
        List<PostedTemplateItem> newPostedTemplateItems = createPostedTemplateItems(template, newPostedTemplate);
        newPostedTemplate.setItems(newPostedTemplateItems);

        PostedTemplate savedPostedTemplate = postedTemplateRepository.save(newPostedTemplate);

        return PostedTemplateMapper.toPostedTemplateResponse(savedPostedTemplate);
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
        return templateRepository.findTemplateWithItemsByIdAndOwnerIdAndStatus(templateId, userId, Status.ACTIVE)
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

    private List<TemplateItem> createNewTemplateItems(Long userId, TemplateCreateRequest request, Template template) {
        return request.getItems().stream()
                .map(itemReq -> {
                    Icon icon = validateAndGetAccessibleIcon(itemReq.getIconId(), userId);
                    return TemplateItem.builder()
                            .template(template)
                            .name(itemReq.getName())
                            .siteUrl(itemReq.getSiteUrl())
                            .position(TemplateItemMapper.toTemplateItemPosition(itemReq.getPosition()))
                            .size(TemplateItemMapper.toTemplateItemSize(itemReq.getSize()))
                            .icon(icon)
                            .status(Status.ACTIVE)
                            .build();
                })
                .collect(Collectors.toList());
    }

    private void updateTemplateBasicInfo(Template template, TemplateUpdateRequest request) {
        template.setName(request.getName());
        template.setHeight(request.getHeight());
    }

    private void updateTemplateItems(Long userId, Template template, List<TemplateItemUpdateRequest> requestItems) {
        // 모든 iconId를 미리 수집하고 한 번에 조회 및 검증
        Map<Long, Icon> iconMap = requestItems.stream()
                .map(TemplateItemUpdateRequest::getIconId)
                .distinct()
                .collect(Collectors.toMap(
                        iconId -> iconId,
                        iconId -> validateAndGetAccessibleIcon(iconId, userId)
                ));

        Map<Long, TemplateItemUpdateRequest> itemRequestsMap = requestItems.stream()
                .filter(item -> item.getTemplateItemId() != null)
                .collect(Collectors.toMap(TemplateItemUpdateRequest::getTemplateItemId, item -> item));

        template.getItems().removeIf(item -> !itemRequestsMap.containsKey(item.getTemplateItemId()));

        template.getItems().forEach(item -> {
            TemplateItemUpdateRequest request = itemRequestsMap.get(item.getTemplateItemId());
            Icon icon = iconMap.get(request.getIconId());
            item.setName(request.getName());
            item.setSiteUrl(request.getSiteUrl());
            item.setPosition(TemplateItemMapper.toTemplateItemPosition(request.getPosition()));
            item.setSize(TemplateItemMapper.toTemplateItemSize(request.getSize()));
            item.setIcon(icon);
        });

        List<TemplateItem> newItems = requestItems.stream()
                .filter(item -> item.getTemplateItemId() == null)
                .map(itemReq -> {
                    Icon icon = iconMap.get(itemReq.getIconId());
                    return TemplateItem.builder()
                            .template(template)
                            .name(itemReq.getName())
                            .siteUrl(itemReq.getSiteUrl())
                            .position(TemplateItemMapper.toTemplateItemPosition(itemReq.getPosition()))
                            .size(TemplateItemMapper.toTemplateItemSize(itemReq.getSize()))
                            .icon(icon)
                            .status(Status.ACTIVE)
                            .build();
                })
                .collect(Collectors.toList());

        template.getItems().addAll(newItems);
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
                .map(templateItem -> {
                    PostedIcon postedIcon = postedIconRepository.findByOriginalIconId(templateItem.getIcon().getIconId())
                            .orElseGet(() -> postedIconRepository.save(PostedIcon.from(templateItem.getIcon())));

                    return PostedTemplateItem.builder()
                            .postedTemplate(postedTemplate)
                            .name(templateItem.getName())
                            .siteUrl(templateItem.getSiteUrl())
                            .position(templateItem.getPosition())
                            .size(templateItem.getSize())
                            .postedIcon(postedIcon)
                            .status(Status.ACTIVE)
                            .build();
                })
                .collect(Collectors.toList());
    }

    private Icon validateAndGetAccessibleIcon(Long iconId, Long userId) {
        Icon icon = iconRepository.findByIconIdAndStatus(iconId, Status.ACTIVE)
                .orElseThrow(() -> LinkuException.of(ResponseCode.ICON_NOT_FOUND));

        // 기본 아이콘이 아니고, 내 아이콘도 아니면 접근 불가
        if (!icon.getIsDefault() && !icon.getOwner().getUserId().equals(userId)) {
            throw LinkuException.of(ResponseCode.ICON_ACCESS_DENIED);
        }

        return icon;
    }
}