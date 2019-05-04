package com.padas2.bitbucket.supportzip.api;

public class BitbucketServerDetails {
    private String gitHostUrl = "http://localhost:7990";
    private String gitUser = "a";
    private String gitPassWord = "b";

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
