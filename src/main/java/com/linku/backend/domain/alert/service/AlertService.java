package com.linku.backend.domain.alert.service;

import com.linku.backend.domain.alert.Alert;
import com.linku.backend.domain.alert.repository.AlertRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlertService {
    private final AlertRepository alertRepository;

    @Transactional(readOnly = true)
    public boolean isNew(Alert alert) {
        return !alertRepository.existsByUrl(alert.getUrl());
    }

    @Transactional
    public Alert save(Alert alert) {
        return alertRepository.save(alert);
    }
}
