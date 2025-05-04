package com.linku.backend.domain.icon;

import com.linku.backend.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "icons")
@AllArgsConstructor
@Builder
@Getter @Setter
public class Icon extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long iconId;

    private String name;

    private String url;
}
