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
import org.springframework.data.domain.Sort;
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
        List<DepartmentConfig> departmentConfigList = departmentConfigRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));

        List<DepartmentConfigResponse> list = departmentConfigList.stream()
                .map(department -> DepartmentConfigResponse.of(department.getId(), department.getName()))
                .toList();
        return DepartmentConfigListResponse.from(list);
    }

    @Transactional(readOnly = true)
    public DepartmentConfigListResponse getAllMyDepartmentConfigs(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> LinkuException.of(ResponseCode.USER_NOT_FOUND));

        List<Subscribe> subscribeList = subscribeRepository.findByUser_UserIdOrderByDepartmentConfig_IdAsc(userId);

        List<DepartmentConfigResponse> list = subscribeList.stream()
                .map(subscribe -> DepartmentConfigResponse.of(subscribe.getDepartmentConfig().getId(), subscribe.getDepartmentConfig().getName()))
                .toList();
        return DepartmentConfigListResponse.from(list);
    }

    @Transactional
    public void subscribeDepartment(Long userId, Long departmentConfigId) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> LinkuException.of(ResponseCode.USER_NOT_FOUND));
        DepartmentConfig departmentConfig = departmentConfigRepository.findById(departmentConfigId)
                .orElseThrow(()-> LinkuException.of(ResponseCode.DEPARTMENT_NOT_FOUND));

        boolean exists = subscribeRepository.existsByUser_UserIdAndDepartmentConfig_Id(userId, departmentConfigId);

        if (exists) {
            throw LinkuException.of(ResponseCode.ALREADY_SUBSCRIBED);
        }

        Subscribe subscribe = Subscribe.builder()
                .user(user)
                .departmentConfig(departmentConfig)
                .build();
        subscribeRepository.save(subscribe);
    }

    @Transactional
    public void deleteSubscription(Long userId, Long departmentConfigId) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> LinkuException.of(ResponseCode.USER_NOT_FOUND));
        DepartmentConfig departmentConfig = departmentConfigRepository.findById(departmentConfigId)
                .orElseThrow(()-> LinkuException.of(ResponseCode.DEPARTMENT_NOT_FOUND));

        subscribeRepository.deleteByUser_UserIdAndDepartmentConfig_Id(userId, departmentConfigId);
    }
}
