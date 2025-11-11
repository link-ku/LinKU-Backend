package com.linku.backend.global.crawler.Parser;

import com.linku.backend.domain.alert.Alert;
import com.linku.backend.domain.deapartmentConfig.DepartmentConfig;

import java.io.IOException;
import java.util.List;

public interface AlertParser {
    List<Alert> parse(DepartmentConfig config) throws IOException;
}
