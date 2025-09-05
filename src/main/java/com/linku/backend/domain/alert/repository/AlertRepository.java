package com.linku.backend.domain.alert.repository;

import com.linku.backend.domain.alert.Alert;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertRepository extends JpaRepository<Alert, Long> {
    boolean existsByUrl(String url);
}
