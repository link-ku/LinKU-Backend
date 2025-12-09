package com.linku.backend.domain.departmentConfig.service;

import com.linku.backend.domain.deapartmentConfig.DepartmentConfig;
import com.linku.backend.domain.deapartmentConfig.dto.response.DepartmentConfigListResponse;
import com.linku.backend.domain.deapartmentConfig.repository.DepartmentConfigRepository;
import com.linku.backend.domain.deapartmentConfig.service.DepartmentConfigService;
import com.linku.backend.domain.subscribe.Subscribe;
import com.linku.backend.domain.subscribe.repository.SubscribeRepository;
import com.linku.backend.domain.user.User;
import com.linku.backend.domain.user.repository.UserRepository;
import com.linku.backend.global.exception.LinkuException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DepartmentConfigService 테스트")
class DepartmentConfigServiceTest {

    @InjectMocks
    private DepartmentConfigService departmentConfigService;

    @Mock
    private DepartmentConfigRepository departmentConfigRepository;
    @Mock
    private SubscribeRepository subscribeRepository;
    @Mock
    private UserRepository userRepository;

    private User user;
    private DepartmentConfig departmentConfig;
    private Subscribe subscribe;

    @BeforeEach
    void setUp() {
        user = User.builder().userId(1L).build();
        departmentConfig = DepartmentConfig.builder().id(1L).name("학사").build();
        subscribe = Subscribe.builder().user(user).departmentConfig(departmentConfig).build();
    }

    @Test
    @DisplayName("모든 학과 설정 조회 성공")
    void getAllDepartmentConfigs_returnsAllConfigsSuccessfully() {
        // given
        Sort sort = Sort.by(Sort.Direction.ASC, "id");

        when(departmentConfigRepository.findAll(sort))
                .thenReturn(List.of(departmentConfig));

        // when
        DepartmentConfigListResponse response = departmentConfigService.getAllDepartmentConfigs();

        // then
        assertThat(response.getDepartmentConfigList()).hasSize(1); // <- 이 부분은 너 로직에 맞게 값 맞춰야 함
        assertThat(response.getDepartmentConfigList().get(0).getDepartmentConfigName())
                .isEqualTo("학사");

        verify(departmentConfigRepository).findAll(sort);
    }

    @Test
    @DisplayName("구독한 학과 목록 조회 성공")
    void getAllMyDepartmentConfigs_returnsSubscribedConfigsSuccessfully() {
        // given
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(user));
        when(subscribeRepository.findByUser_UserIdOrderByDepartmentConfig_IdAsc(any(Long.class))).thenReturn(List.of(subscribe));

        // when
        DepartmentConfigListResponse response = departmentConfigService.getAllMyDepartmentConfigs(1L);

        // then
        assertThat(response.getDepartmentConfigList()).hasSize(1);
        assertThat(response.getDepartmentConfigList().get(0).getDepartmentConfigName()).isEqualTo("학사");
        verify(userRepository).findById(1L);
        verify(subscribeRepository).findByUser_UserIdOrderByDepartmentConfig_IdAsc(1L);
    }

    @Test
    @DisplayName("구독한 학과 목록 조회 실패 - 사용자를 찾을 수 없을 경우")
    void getAllMyDepartmentConfigs_throwsException_whenUserNotFound() {
        // given
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        // when & then
        assertThrows(LinkuException.class, () -> departmentConfigService.getAllMyDepartmentConfigs(1L));
        verify(userRepository).findById(1L);
    }

    @Test
    @DisplayName("학과 구독 성공")
    void subscribeDepartment_savesSubscriptionSuccessfully() {
        // given
        // 각각 Optional.of()를 반환하도록 설정하고, subscribeRepository.save()가 subscribe 객체를 반환하도록 준비.
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(user));
        when(departmentConfigRepository.findById(any(Long.class))).thenReturn(Optional.of(departmentConfig));
        when(subscribeRepository.save(any(Subscribe.class))).thenReturn(subscribe);

        // when
        // subscribeDepartment() 메서드 실행.
        departmentConfigService.subscribeDepartment(1L, 1L);

        // then
        // 메서드가 모두 예상대로 한 번씩 호출되었는지 검증.
        verify(userRepository).findById(1L);
        verify(departmentConfigRepository).findById(1L);
        verify(subscribeRepository).save(any(Subscribe.class));
    }

    @Test
    @DisplayName("구독 삭제 성공")
    void deleteSubscription_deletesSubscriptionSuccessfully() {
        // given
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(user));
        when(departmentConfigRepository.findById(any(Long.class))).thenReturn(Optional.of(departmentConfig));
        doNothing().when(subscribeRepository).deleteByUser_UserIdAndDepartmentConfig_Id(any(Long.class), any(Long.class));

        // when
        departmentConfigService.deleteSubscription(1L, 1L);

        // then
        verify(userRepository).findById(1L);
        verify(departmentConfigRepository).findById(1L);
        verify(subscribeRepository).deleteByUser_UserIdAndDepartmentConfig_Id(1L, 1L);
    }
}
