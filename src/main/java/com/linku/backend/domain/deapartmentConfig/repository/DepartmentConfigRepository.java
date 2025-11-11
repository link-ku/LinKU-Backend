package com.linku.backend.domain.deapartmentConfig.repository;

import com.linku.backend.domain.deapartmentConfig.DepartmentConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DepartmentConfigRepository extends JpaRepository<DepartmentConfig, Long> {
    List<DepartmentConfig> findByNameIn(List<String> names);
}
