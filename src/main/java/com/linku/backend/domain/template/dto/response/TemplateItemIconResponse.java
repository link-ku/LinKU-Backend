package com.linku.backend.domain.template.dto.response;

import lombok.Getter;
import lombok.Builder;

@Getter
@Builder
public class TemplateItemIconResponse {

    private Long iconId;
    private String iconName;
    private String iconUrl;
}
