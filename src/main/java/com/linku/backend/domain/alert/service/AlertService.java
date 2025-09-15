package com.linku.backend.domain.alert.service;

import com.linku.backend.domain.alert.Alert;
import com.linku.backend.domain.alert.dto.response.AlertListResponse;
import com.linku.backend.domain.alert.dto.response.AlertResponse;
import com.linku.backend.domain.alert.repository.AlertRepository;
import com.linku.backend.domain.deapartmentConfig.DepartmentConfig;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class AlertService {
    private final AlertRepository alertRepository;
    private final DepartmentConfigRepository departmentConfigRepository;
    private final SubscribeRepository subscribeRepository;
    private final UserRepository userRepository;

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

    @Transactional(readOnly = true)
    public AlertListResponse getMyAlerts(){
        Long userId = 1L;
        // 1) 구독 정보 조회
        List<Subscribe> subscribes = subscribeRepository.findByUserId(userId);

        // 2) 구독한 departmentConfigId 리스트 뽑기
        List<Long> departmentConfigIds = subscribes.stream()
                .map(subscribe -> subscribe.getDepartmentConfig().getId())
                .toList();

        // 3) 해당 departmentConfigId에 속하는 알림들 조회
        List<Alert> alerts = alertRepository.findByDepartmentConfigIdIn(departmentConfigIds);

        // 4) Alert → AlertResponse 변환
        List<AlertResponse> alertResponses = alerts.stream()
                .map(AlertResponse::from) // 정적 팩토리 메서드가 있다고 가정
                .toList();

        // 5) 최종 Response 반환
        return AlertListResponse.from(alertResponses);
    }

    public void subscribeDepartment(Long userId, Long departmentConfigId) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> LinkuException.of(ResponseCode.USER_NOT_FOUND));
        DepartmentConfig departmentConfig = departmentConfigRepository.findById(departmentConfigId)
                .orElseThrow(()-> LinkuException.of(ResponseCode.DEPARTMENT_NOT_FOUND));

        Subscribe subscribe = Subscribe.builder()
                .user(user)
                .departmentConfig(departmentConfig)
                .build();
        subscribeRepository.save(subscribe);
    }

    public void deleteSubscription(Long userId, Long departmentConfigId) {
        subscribeRepository.deleteByUser_IdAndDepartmentConfig_Id(userId, departmentConfigId);
    }
}
