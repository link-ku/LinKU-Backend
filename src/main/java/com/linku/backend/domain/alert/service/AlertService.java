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
    public AlertListResponse getMyAlerts(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(()-> LinkuException.of(ResponseCode.USER_NOT_FOUND));

        // 1) 구독 정보 조회
        List<Subscribe> subscribes = subscribeRepository.findByUser_UserId(userId);

        // 2) 구독한 departmentConfigId 리스트 뽑기
        List<Long> departmentConfigIds = subscribes.stream()
                .map(subscribe -> subscribe.getDepartmentConfig().getId())
                .toList();

        // 3) 해당 departmentConfigId에 속하는 알림들 조회
        List<Alert> alerts = alertRepository.findByDepartmentConfigIdInOrderByPostTimeDesc(departmentConfigIds);

        List<AlertResponse> alertResponses = alerts.stream()
                .map(AlertResponse::from)
                .toList();
        return AlertListResponse.from(alertResponses);
    }

    @Transactional(readOnly = true)
    public AlertListResponse getMyAlertsWithDepartments(Long userId, List<String> departmentNames) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> LinkuException.of(ResponseCode.USER_NOT_FOUND));

        // 1) 내 구독 DepartmentConfig ID들
        List<Long> subscribedIds = subscribeRepository.findByUser_UserId(userId).stream()
                .map(s -> s.getDepartmentConfig().getId())
                .toList();


        if (subscribedIds.isEmpty()) {
            // 구독한게 없으면 빈 리스트 반환
            return AlertListResponse.from(List.of());
        }

        // 2) 전달받은 학과 이름 -> DepartmentConfig ID들
        List<Long> filterIds = departmentConfigRepository.findByNameIn(departmentNames).stream()
                .map(DepartmentConfig::getId)
                .toList();

        if (filterIds.isEmpty()) {
            // 전달된 학과명이 모두 없으면 빈 결과
            return AlertListResponse.from(List.of());
        }

        // 3) 내 구독과 요청 필터의 교집합
        List<Long> targetIds = filterIds.stream()
                .filter(subscribedIds::contains)
                .toList();

        if (targetIds.isEmpty()) {
            // 내가 구독하지 않은 학과만 요청한 경우
            return AlertListResponse.from(List.of());
        }

        // 4) 교집합에 해당하는 알림 조회 (정렬 포함)
        List<Alert> alerts = alertRepository.findByDepartmentConfigIdInOrderByPostTimeDesc(targetIds);

        return AlertListResponse.from(alerts.stream().map(AlertResponse::from).toList());
    }
}
