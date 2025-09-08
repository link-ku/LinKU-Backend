package com.linku.backend.domain.alert.service;

import com.linku.backend.domain.alert.Alert;
import com.linku.backend.domain.alert.repository.AlertRepository;
import com.linku.backend.domain.deapartmentConfig.DepartmentConfig;
import com.linku.backend.domain.deapartmentConfig.repository.DepartmentConfigRepository;
import com.linku.backend.global.exception.LinkuException;
import com.linku.backend.global.response.ResponseCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlertService {
    private final AlertRepository alertRepository;
    private final DepartmentConfigRepository departmentConfigRepository;

    @Transactional(readOnly = true)
    public boolean isNew(Alert alert) {
        return !alertRepository.existsByUrl(alert.getUrl());
    }

    @Transactional
    public Alert saveWithDept(Alert alert, Long deptConfigId) {
        DepartmentConfig departmentConfig = departmentConfigRepository.findById(deptConfigId)
                        .orElseThrow( () -> LinkuException.of(ResponseCode.DEPARTMENT_NOT_FOUND));
        alert.setDepartmentConfig(departmentConfig);
        return alertRepository.save(alert);
    }
}
