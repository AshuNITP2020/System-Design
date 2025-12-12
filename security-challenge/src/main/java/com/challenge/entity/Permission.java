package com.challenge.entity;

import org.springframework.security.core.GrantedAuthority;

public enum Permission implements GrantedAuthority {
    DOCUMENT_READ("document:read"),
    DOCUMENT_WRITE("document:write"),
    DOCUMENT_DELETE("document:delete"),
    USER_READ("user:read"),
    USER_WRITE("user:write");

    private final String permission;

    Permission(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }

    @Override
    public String getAuthority() {
        return permission;
    }
}

