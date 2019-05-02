package com.padas2.bitbucket.supportzip.api;

public class BitbucketSupportZip {
    private String zipFileName ;

    public BitbucketSupportZip() {
    }

    public BitbucketSupportZip(String zipFileName) {
        this.zipFileName = zipFileName;
    }

    public String getZipFileName() {
        return zipFileName;
    }

    public void setZipFileName(String zipFileName) {
        this.zipFileName = zipFileName;
    }
}
