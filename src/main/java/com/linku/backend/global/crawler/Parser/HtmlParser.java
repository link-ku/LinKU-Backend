package com.linku.backend.global.crawler.Parser;

import com.linku.backend.domain.alert.Alert;
import com.linku.backend.domain.deapartmentConfig.DepartmentConfig;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class HtmlParser implements AlertParser {
    @Override
    public List<Alert> parse(DepartmentConfig config) throws IOException {
        return null;
    }
}
