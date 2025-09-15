package com.linku.backend.domain.deapartmentConfig.service;

import com.linku.backend.domain.deapartmentConfig.DepartmentConfig;
import com.linku.backend.domain.deapartmentConfig.dto.response.DepartmentConfigListResponse;
import com.linku.backend.domain.deapartmentConfig.dto.response.DepartmentConfigResponse;
import com.linku.backend.domain.deapartmentConfig.repository.DepartmentConfigRepository;
import com.linku.backend.domain.subscribe.Subscribe;
import com.linku.backend.domain.subscribe.repository.SubscribeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class DepartmentConfigService {

    private DepartmentConfigRepository departmentConfigRepository;
    private SubscribeRepository subscribeRepository;

    @Transactional(readOnly = true)
    public DepartmentConfigListResponse getAllDepartmentConfigs() {
        List<DepartmentConfig> departmentConfigList = departmentConfigRepository.findAll();

        List<DepartmentConfigResponse> list = departmentConfigList.stream()
                .map(department -> DepartmentConfigResponse.of(department.getId(), department.getName()))
                .toList();
        return DepartmentConfigListResponse.from(list);
    }

    @Transactional(readOnly = true)
    public DepartmentConfigListResponse getAllMyDepartmentConfigs(Long userId) {
        List<Subscribe> subscribeList = subscribeRepository.findByUserId(1L);

        List<DepartmentConfigResponse> list = subscribeList.stream()
                .map(subscribe -> DepartmentConfigResponse.of(subscribe.getDepartmentConfig().getId(), subscribe.getDepartmentConfig().getName()))
                .toList();
        return DepartmentConfigListResponse.from(list);
    }

}
