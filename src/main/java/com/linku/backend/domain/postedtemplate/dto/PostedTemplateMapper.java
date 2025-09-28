package com.linku.backend.domain.postedtemplate.dto;

import com.linku.backend.domain.postedtemplate.PostedTemplate;
import com.linku.backend.domain.postedtemplate.dto.response.PostedTemplateListResponse;
import com.linku.backend.domain.postedtemplate.dto.response.PostedTemplateResponse;
import org.springframework.stereotype.Component;

@Component
public class PostedTemplateMapper {

    public static PostedTemplateResponse toPostedTemplateResponse(PostedTemplate postedTemplate) {
        return PostedTemplateResponse.builder()
                .postedTemplateId(postedTemplate.getPostedTemplateId())
                .name(postedTemplate.getName())
                .ownerId(postedTemplate.getOwner().getUserId())
                .ownerName(postedTemplate.getOwner().getName())
                .height(postedTemplate.getHeight())
                .likesCount(postedTemplate.getLikesCount())
                .usageCount(postedTemplate.getUsageCount())
                .items(PostedTemplateItemMapper.toPostedTemplateItemResponseList(postedTemplate.getItems()))
                .build();
    }

    public static PostedTemplateListResponse toPostedTemplateListResponse(PostedTemplate postedTemplate) {
        return PostedTemplateListResponse.builder()
                .postedTemplateId(postedTemplate.getPostedTemplateId())
                .name(postedTemplate.getName())
                .ownerId(postedTemplate.getOwner().getUserId())
                .ownerName(postedTemplate.getOwner().getName())
                .height(postedTemplate.getHeight())
                .likesCount(postedTemplate.getLikesCount())
                .usageCount(postedTemplate.getUsageCount())
                .items(postedTemplate.getItems() != null ? postedTemplate.getItems().size() : 0)
                .build();
    }
}