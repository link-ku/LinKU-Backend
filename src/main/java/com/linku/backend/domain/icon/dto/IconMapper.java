package com.linku.backend.domain.icon.dto;

import com.linku.backend.domain.icon.Icon;
import com.linku.backend.domain.icon.dto.response.IconInfoResponse;
import org.springframework.stereotype.Component;

@Component
public class IconMapper {

    public static IconInfoResponse toIconInfoResponse(Icon icon) {
        return IconInfoResponse.builder()
                .id(icon.getIconId())
                .name(icon.getName())
                .imageUrl(icon.getImageUrl())
                .build();
    }
}