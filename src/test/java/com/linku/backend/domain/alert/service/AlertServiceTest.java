//package com.linku.backend.domain.alert.service;
//
//import com.linku.backend.domain.alert.Alert;
//import com.linku.backend.domain.alert.dto.response.AlertListResponse;
//import com.linku.backend.domain.alert.dto.response.AlertResponse;
//import com.linku.backend.domain.alert.repository.AlertRepository;
//import com.linku.backend.domain.deapartmentConfig.DepartmentConfig;
//import com.linku.backend.domain.deapartmentConfig.repository.DepartmentConfigRepository;
//import com.linku.backend.domain.subscribe.Subscribe;
//import com.linku.backend.domain.subscribe.repository.SubscribeRepository;
//import com.linku.backend.domain.user.User;
//import com.linku.backend.domain.user.repository.UserRepository;
//import com.linku.backend.global.exception.LinkuException;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.Collections;
//import java.util.List;
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//@DisplayName("AlertService 테스트")
//class AlertServiceTest {
//
//    @InjectMocks
//    private AlertService alertService;
//
//    @Mock
//    private AlertRepository alertRepository;
//    @Mock
//    private DepartmentConfigRepository departmentConfigRepository;
//    @Mock
//    private SubscribeRepository subscribeRepository;
//    @Mock
//    private UserRepository userRepository;
//
//    private User user;
//    private DepartmentConfig departmentConfig;
//    private Alert alert;
//    private Subscribe subscribe;
//
//    @BeforeEach
//    void setUp() {
//        user = User.builder().userId(1L).build();
//        departmentConfig = DepartmentConfig.builder().id(1L).name("학사").isRss(true).build();
//        alert = Alert.builder().id(1L).url("https://example.com/alert/1").departmentConfig(departmentConfig).build();
//        subscribe = Subscribe.builder().user(user).departmentConfig(departmentConfig).build();
//    }
//
//    @Test
//    @DisplayName("새 알림인지 확인 - 신규 알림일 경우")
//    void isNew_whenNewAlert_returnsTrue() {
//        // given
//        when(alertRepository.existsByUrl(any(String.class))).thenReturn(false);
//
//        // when
//        boolean isNew = alertService.isNew(alert);
//
//        // then
//        assertThat(isNew).isTrue();
//        verify(alertRepository).existsByUrl(alert.getUrl());
//    }
//
//    @Test
//    @DisplayName("새 알림인지 확인 - 기존 알림일 경우")
//    void isNew_whenExistingAlert_returnsFalse() {
//        // given
//        when(alertRepository.existsByUrl(any(String.class))).thenReturn(true);
//
//        // when
//        boolean isNew = alertService.isNew(alert);
//
//        // then
//        assertThat(isNew).isFalse();
//        verify(alertRepository).existsByUrl(alert.getUrl());
//    }
//
//    @Test
//    @DisplayName("알림을 학과와 함께 저장 성공")
//    void saveWithDept_savesAlertSuccessfully() {
//        // given
//        when(departmentConfigRepository.findById(any(Long.class))).thenReturn(Optional.of(departmentConfig));
//        when(alertRepository.save(any(Alert.class))).thenReturn(alert);
//
//        // when
//        Alert savedAlert = alertService.saveWithDept(alert, 1L);
//
//        // then
//        assertThat(savedAlert).isNotNull();
//        assertThat(savedAlert.getDepartmentConfig()).isEqualTo(departmentConfig);
//        verify(departmentConfigRepository).findById(1L);
//        verify(alertRepository).save(alert);
//    }
//
//    @Test
//    @DisplayName("알림 저장 실패 - 학과를 찾을 수 없을 경우")
//    void saveWithDept_throwsException_whenDeptNotFound() {
//        // given
//        when(departmentConfigRepository.findById(any(Long.class))).thenReturn(Optional.empty());
//
//        // when & then
//        assertThrows(LinkuException.class, () -> alertService.saveWithDept(alert, 1L));
//        verify(departmentConfigRepository).findById(1L);
//        verify(alertRepository, never()).save(any(Alert.class));
//    }
//
//    @Test
//    @DisplayName("내 알림 가져오기 성공")
//    void getMyAlerts_returnsAlertsSuccessfully() {
//        // given
//        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(user));
//        when(subscribeRepository.findByUser_UserId(any(Long.class))).thenReturn(List.of(subscribe));
//        when(alertRepository.findByDepartmentConfigIdIn(anyList())).thenReturn(List.of(alert));
//
//        // when
//        AlertListResponse response = alertService.getMyAlerts(1L);
//
//        // then
//        assertThat(response.getAlertResponseList()).hasSize(1);
//        AlertResponse alertResponse = response.getAlertResponseList().get(0);
//        assertThat(alertResponse.getUrl()).isEqualTo(alert.getUrl());
//
//        verify(userRepository).findById(1L);
//        verify(subscribeRepository).findByUser_UserId(1L);
//        verify(alertRepository).findByDepartmentConfigIdIn(List.of(1L));
//    }
//
//    @Test
//    @DisplayName("내 알림 가져오기 실패 - 사용자를 찾을 수 없을 경우")
//    void getMyAlerts_throwsException_whenUserNotFound() {
//        // given
//        when(userRepository.findById(any(Long.class))).thenReturn(Optional.empty());
//
//        // when & then
//        assertThrows(LinkuException.class, () -> alertService.getMyAlerts(1L));
//        verify(userRepository).findById(1L);
//        verify(subscribeRepository, never()).findByUser_UserId(any(Long.class));
//    }
//
//    @Test
//    @DisplayName("내 알림 가져오기 - 구독한 학과가 없을 경우 빈 리스트 반환")
//    void getMyAlerts_returnsEmptyList_whenNoSubscriptions() {
//        // given
//        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(user));
//        when(subscribeRepository.findByUser_UserId(any(Long.class))).thenReturn(Collections.emptyList());
//
//        // when
//        AlertListResponse response = alertService.getMyAlerts(1L);
//
//        // then
//        assertThat(response.getAlertResponseList()).isEmpty();
//
//        verify(userRepository).findById(1L);
//        verify(subscribeRepository).findByUser_UserId(1L);
//        verify(alertRepository, times(1)).findByDepartmentConfigIdIn(Collections.emptyList());
//    }
//}
