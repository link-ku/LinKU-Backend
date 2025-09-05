package com.linku.backend.global.crawler;

import lombok.Getter;

@Getter
public class DepartmentConfig {
    private String name;
    private String url;
    private boolean isRss;
    private String contentSelector; // HTML 파서용
}
