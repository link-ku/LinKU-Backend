package com.linku.backend.domain.template.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class TemplateItemUpdateRequest {

    private Long templateItemId;

    @NotBlank
    private String name;

    @NotBlank
    private String siteUrl;

    private Long iconId;

    @Valid
    @NotNull
    private TemplateItemPositionRequest position;

    @Valid
    @NotNull
    private TemplateItemSizeRequest size;
}
