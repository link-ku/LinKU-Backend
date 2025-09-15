package com.linku.backend.domain.subscribe;

import com.linku.backend.domain.common.BaseEntity;
import com.linku.backend.domain.deapartmentConfig.DepartmentConfig;
import com.linku.backend.domain.page.Page;
import com.linku.backend.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "subscribes")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Subscribe extends BaseEntity {

    @Id
    @Column(name = "subscribe_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "department_conifg_id")
    private DepartmentConfig departmentConfig;

    private String customName;
}
