package com.challenge.entity;

import java.util.Set;

public enum Role {
    USER(Set.of(Permission.DOCUMENT_READ)),
    MODERATOR(Set.of(Permission.DOCUMENT_READ, Permission.DOCUMENT_WRITE)),
    ADMIN(Set.of(Permission.DOCUMENT_READ, Permission.DOCUMENT_WRITE, Permission.DOCUMENT_DELETE, Permission.USER_READ, Permission.USER_WRITE));

    private final Set<Permission> permissions;

    Role(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }
}
