package com.linku.backend.global.crawler;

import com.linku.backend.domain.alert.Alert;

import java.io.IOException;
import java.util.List;

public interface AlertParser {
    List<Alert> parse(DepartmentConfig config) throws IOException;
}
