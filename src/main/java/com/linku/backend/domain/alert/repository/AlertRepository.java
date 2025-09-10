package com.linku.backend.domain.alert.repository;

import com.linku.backend.domain.alert.Alert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlertRepository extends JpaRepository<Alert, Long> {
    boolean existsByUrl(String url);
    List<Alert> findByDepartmentConfigIdIn(List<Long> departmentConfigIds);
}
