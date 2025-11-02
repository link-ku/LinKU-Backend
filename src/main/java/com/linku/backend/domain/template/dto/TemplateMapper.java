package com.linku.backend.domain.template.dto;

import com.linku.backend.domain.template.Template;
import com.linku.backend.domain.template.dto.response.TemplateListResponse;
import com.linku.backend.domain.template.dto.response.TemplateResponse;
import org.springframework.stereotype.Component;

@Component
public class TemplateMapper {

    public static TemplateResponse toTemplateResponse(Template template) {
        return TemplateResponse.builder()
                .templateId(template.getTemplateId())
                .name(template.getName())
                .height(template.getHeight())
                .cloned(Boolean.TRUE.equals(template.getCloned()))
                .items(TemplateItemMapper.toTemplateItemResponseList(template.getItems()))
                .build();
    }

    public static TemplateListResponse toTemplateListResponse(Template template) {
        return TemplateListResponse.builder()
                .templateId(template.getTemplateId())
                .name(template.getName())
                .height(template.getHeight())
                .cloned(Boolean.TRUE.equals(template.getCloned()))
                .items(template.getItems() != null ? template.getItems().size() : 0)
                .build();
    }
}