package com.linku.backend.domain.deapartment;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table
@Getter
@NoArgsConstructor
@Setter
public class DepartmentConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private String url;
    private boolean isRss;
    private String lastModified; // 최종 수정 시간 저장
    private String contentSelector; // HTML 파서용
}
