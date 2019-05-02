package com.padas2.bitbucket.supportzip.api;

public class GitServerDetails {
    private String gitHostUrl = "https://git.dev.pega.io";
    private String gitUser = "user";
    private String gitPassWord = "password";

    public String getGitHostUrl() {
        return gitHostUrl;
    }

    public void setGitHostUrl(String gitHostUrl) {
        this.gitHostUrl = gitHostUrl;
    }

    public String getGitUser() {
        return gitUser;
    }

    public void setGitUser(String gitUser) {
        this.gitUser = gitUser;
    }

    public String getGitPassWord() {
        return gitPassWord;
    }

    public void setGitPassWord(String gitPassWord) {
        this.gitPassWord = gitPassWord;
    }

    @Override
    public String toString() {
        return gitHostUrl + " : " + gitUser + " : " + gitPassWord;
    }
}
