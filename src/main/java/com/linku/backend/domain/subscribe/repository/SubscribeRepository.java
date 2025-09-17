package com.linku.backend.domain.subscribe.repository;

import com.linku.backend.domain.subscribe.Subscribe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubscribeRepository extends JpaRepository<Subscribe, Long> {
    List<Subscribe> findByUser_UserId(Long userId);
    void deleteByUser_UserIdAndDepartmentConfig_Id(Long userId, Long departmentConfigId);
}
