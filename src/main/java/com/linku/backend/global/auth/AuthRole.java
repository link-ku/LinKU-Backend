package com.linku.backend.global.auth;

public enum AuthRole {
    GUEST("GUEST"),
    MEMBER("MEMBER"),
    ADMIN("ADMIN");

    AuthRole(String value) {
        this.role = PREFIX + value;
    }

    private static final String PREFIX = "ROLE_";
    private final String role;

    public String getValue() {
        return role.replace(PREFIX, "");
    }
}
