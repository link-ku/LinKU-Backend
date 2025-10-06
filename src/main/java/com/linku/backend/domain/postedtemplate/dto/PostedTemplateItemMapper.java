package com.linku.backend.domain.postedtemplate.dto;

import com.linku.backend.domain.postedtemplate.PostedTemplateItem;
import com.linku.backend.domain.postedtemplate.dto.response.PostedTemplateItemIconResponse;
import com.linku.backend.domain.postedtemplate.dto.response.PostedTemplateItemPositionResponse;
import com.linku.backend.domain.postedtemplate.dto.response.PostedTemplateItemResponse;
import com.linku.backend.domain.postedtemplate.dto.response.PostedTemplateItemSizeResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PostedTemplateItemMapper {

    public static PostedTemplateItemResponse toPostedTemplateItemResponse(PostedTemplateItem postedTemplateItem) {
        return PostedTemplateItemResponse.builder()
                .postedTemplateItemId(postedTemplateItem.getPostedTemplateItemId())
                .name(postedTemplateItem.getName())
                .siteUrl(postedTemplateItem.getSiteUrl())
                .position(toPostedTemplateItemPositionResponse(postedTemplateItem))
                .size(toPostedTemplateItemSizeResponse(postedTemplateItem))
                .icon(toPostedTemplateItemIconResponse(postedTemplateItem))
                .build();
    }

    public static List<PostedTemplateItemResponse> toPostedTemplateItemResponseList(List<PostedTemplateItem> postedTemplateItems) {
        if (postedTemplateItems == null) {
            return null;
        }
        return postedTemplateItems.stream()
                .map(PostedTemplateItemMapper::toPostedTemplateItemResponse)
                .collect(Collectors.toList());
    }

    private static PostedTemplateItemPositionResponse toPostedTemplateItemPositionResponse(PostedTemplateItem postedTemplateItem) {
        if (postedTemplateItem.getPosition() == null) {
            return null;
        }
        return PostedTemplateItemPositionResponse.builder()
                .x(postedTemplateItem.getPosition().getX())
                .y(postedTemplateItem.getPosition().getY())
                .build();
    }

    private static PostedTemplateItemSizeResponse toPostedTemplateItemSizeResponse(PostedTemplateItem postedTemplateItem) {
        if (postedTemplateItem.getSize() == null) {
            return null;
        }
        return PostedTemplateItemSizeResponse.builder()
                .width(postedTemplateItem.getSize().getWidth())
                .height(postedTemplateItem.getSize().getHeight())
                .build();
    }

    private static PostedTemplateItemIconResponse toPostedTemplateItemIconResponse(PostedTemplateItem postedTemplateItem) {
        if (postedTemplateItem.getPostedIcon() == null) {
            return null;
        }
        return PostedTemplateItemIconResponse.builder()
                .iconName(postedTemplateItem.getPostedIcon().getName())
                .iconUrl(postedTemplateItem.getPostedIcon().getImageUrl())
                .build();
    }
}