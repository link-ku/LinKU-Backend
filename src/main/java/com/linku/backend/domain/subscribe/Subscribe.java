package com.linku.backend.domain.subscribe;

import com.linku.backend.domain.common.BaseEntity;
import com.linku.backend.domain.deapartmentConfig.DepartmentConfig;
import com.linku.backend.domain.page.Page;
import com.linku.backend.domain.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "subscribes")
@Getter
@Setter
public class Subscribe extends BaseEntity {

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Id
    @ManyToOne
    @JoinColumn(name = "department_conifg_id")
    private DepartmentConfig departmentConfig;

    private String customName;
}
