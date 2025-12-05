package com.linku.backend.domain.template.dto;

import com.linku.backend.domain.common.template.TemplateItemPosition;
import com.linku.backend.domain.common.template.TemplateItemSize;
import com.linku.backend.domain.template.TemplateItem;
import com.linku.backend.domain.template.dto.request.TemplateItemPositionRequest;
import com.linku.backend.domain.template.dto.request.TemplateItemSizeRequest;
import com.linku.backend.domain.template.dto.response.TemplateItemIconResponse;
import com.linku.backend.domain.template.dto.response.TemplateItemPositionResponse;
import com.linku.backend.domain.template.dto.response.TemplateItemResponse;
import com.linku.backend.domain.template.dto.response.TemplateItemSizeResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TemplateItemMapper {

    public static TemplateItemPosition toTemplateItemPosition(TemplateItemPositionRequest request) {
        return TemplateItemPosition.builder()
                .x(request.getX())
                .y(request.getY())
                .build();
    }

    public static TemplateItemSize toTemplateItemSize(TemplateItemSizeRequest request) {
        return TemplateItemSize.builder()
                .width(request.getWidth())
                .height(request.getHeight())
                .build();
    }

    public static TemplateItemResponse toTemplateItemResponse(TemplateItem templateItem) {
        return TemplateItemResponse.builder()
                .templateItemId(templateItem.getTemplateItemId())
                .name(templateItem.getName())
                .siteUrl(templateItem.getSiteUrl())
                .position(toTemplateItemPositionResponse(templateItem.getPosition()))
                .size(toTemplateItemSizeResponse(templateItem.getSize()))
                .icon(toTemplateItemIconResponse(templateItem))
                .build();
    }

    public static List<TemplateItemResponse> toTemplateItemResponseList(List<TemplateItem> templateItems) {
        if (templateItems == null) {
            return null;
        }
        return templateItems.stream()
                .map(TemplateItemMapper::toTemplateItemResponse)
                .collect(Collectors.toList());
    }

    private static TemplateItemPositionResponse toTemplateItemPositionResponse(TemplateItemPosition position) {
        if (position == null) {
            return null;
        }
        return TemplateItemPositionResponse.builder()
                .x(position.getX())
                .y(position.getY())
                .build();
    }

    private static TemplateItemSizeResponse toTemplateItemSizeResponse(TemplateItemSize size) {
        if (size == null) {
            return null;
        }
        return TemplateItemSizeResponse.builder()
                .width(size.getWidth())
                .height(size.getHeight())
                .build();
    }

    private static TemplateItemIconResponse toTemplateItemIconResponse(TemplateItem templateItem) {
        if (templateItem.getIcon() == null) {
            return null;
        }
        return TemplateItemIconResponse.builder()
                .iconId(templateItem.getIcon().getIconId())
                .iconName(templateItem.getIcon().getName())
                .iconUrl(templateItem.getIcon().getImageUrl())
                .build();
    }
}