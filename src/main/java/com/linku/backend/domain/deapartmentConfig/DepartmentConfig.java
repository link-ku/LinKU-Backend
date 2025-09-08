package com.linku.backend.domain.deapartmentConfig;

import com.linku.backend.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "department_configs")
@Getter
@NoArgsConstructor
@Setter
public class DepartmentConfig extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name= "department_config_id")
    private Long id;

    private String name;
    private String url;

    @Column(name= "is_rss")
    private boolean isRss;

    @Column(name= "last_modified")
    private String lastModified; // 최종 수정 시간 저장
    // private String contentSelector; // HTML 파서용
}
