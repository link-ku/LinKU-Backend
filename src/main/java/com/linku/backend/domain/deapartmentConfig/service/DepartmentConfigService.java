package com.linku.backend.domain.deapartmentConfig.service;

import com.linku.backend.domain.deapartmentConfig.DepartmentConfig;
import com.linku.backend.domain.deapartmentConfig.dto.response.DepartmentConfigListResponse;
import com.linku.backend.domain.deapartmentConfig.dto.response.DepartmentConfigResponse;
import com.linku.backend.domain.deapartmentConfig.repository.DepartmentConfigRepository;
import com.linku.backend.domain.subscribe.Subscribe;
import com.linku.backend.domain.subscribe.repository.SubscribeRepository;
import com.linku.backend.domain.user.User;
import com.linku.backend.domain.user.repository.UserRepository;
import com.linku.backend.global.exception.LinkuException;
import com.linku.backend.global.response.ResponseCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class DepartmentConfigService {

    private final DepartmentConfigRepository departmentConfigRepository;
    private final SubscribeRepository subscribeRepository;
    private final UserRepository userRepository;

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
        User user = userRepository.findById(userId)
                .orElseThrow(()-> LinkuException.of(ResponseCode.USER_NOT_FOUND));

        List<Subscribe> subscribeList = subscribeRepository.findByUserId(userId);

        List<DepartmentConfigResponse> list = subscribeList.stream()
                .map(subscribe -> DepartmentConfigResponse.of(subscribe.getDepartmentConfig().getId(), subscribe.getDepartmentConfig().getName()))
                .toList();
        return DepartmentConfigListResponse.from(list);
    }

}
