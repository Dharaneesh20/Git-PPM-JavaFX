package com.ppm.gitppm.model;

public enum GitAuthType {
    USERNAME_PASSWORD("Username & Password"),
    PERSONAL_ACCESS_TOKEN("Personal Access Token"),
    GITHUB_OAUTH("Sign in with GitHub");

    private final String displayName;

    GitAuthType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
