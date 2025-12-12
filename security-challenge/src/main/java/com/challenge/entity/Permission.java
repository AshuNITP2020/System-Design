package com.challenge.entity;

public enum Permission {
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
}

