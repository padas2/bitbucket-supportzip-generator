package com.padas2.bitbucket.supportzip.api;

public class GitServerDetails {
    private String gitHostUrl = "https://git.pega.io";
    private String gitUser = "padas2";
    private String gitPassWord = "Uncertain@2020";

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
