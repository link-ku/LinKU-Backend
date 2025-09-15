package com.linku.backend.domain.alert;

import com.linku.backend.domain.common.BaseEntity;
import com.linku.backend.domain.deapartmentConfig.DepartmentConfig;
import com.linku.backend.domain.page.Page;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "alerts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Alert extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name= "alert_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_config_id")
    private DepartmentConfig departmentConfig;

    private String url;

    private String title;

    private LocalDateTime postTime; // created_at과 다른 게시글 올라온 시간 -> 크롤링 시에 치환해주는 로직 필요

    private String content;
}
