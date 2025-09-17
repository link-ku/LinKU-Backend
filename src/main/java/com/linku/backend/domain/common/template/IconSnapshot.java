package com.linku.backend.domain.common.template;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class IconSnapshot {
    private String iconUrl;
    private String iconName;
}