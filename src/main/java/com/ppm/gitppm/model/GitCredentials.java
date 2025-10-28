package com.ppm.gitppm.model;

public class GitCredentials {
    private GitAuthType authType;
    private String username;
    private String password;
    private String personalAccessToken;
    private String repositoryPath;
    private String remoteUrl;

    public GitCredentials() {
    }

    public GitCredentials(GitAuthType authType, String username, String password) {
        this.authType = authType;
        this.username = username;
        this.password = password;
    }

    public GitAuthType getAuthType() {
        return authType;
    }

    public void setAuthType(GitAuthType authType) {
        this.authType = authType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPersonalAccessToken() {
        return personalAccessToken;
    }

    public void setPersonalAccessToken(String personalAccessToken) {
        this.personalAccessToken = personalAccessToken;
    }

    public String getRepositoryPath() {
        return repositoryPath;
    }

    public void setRepositoryPath(String repositoryPath) {
        this.repositoryPath = repositoryPath;
    }

    public String getRemoteUrl() {
        return remoteUrl;
    }

    public void setRemoteUrl(String remoteUrl) {
        this.remoteUrl = remoteUrl;
    }

    public String getEffectivePassword() {
        return authType == GitAuthType.PERSONAL_ACCESS_TOKEN ? personalAccessToken : password;
    }
}
