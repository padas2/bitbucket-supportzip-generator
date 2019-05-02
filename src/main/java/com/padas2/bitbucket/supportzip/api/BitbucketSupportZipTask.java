package com.padas2.bitbucket.supportzip.api;

public class BitbucketSupportZipTask {
    private BitbucketSupportZip bitbucketSupportZip;
    private String taskId ;

    public BitbucketSupportZipTask(BitbucketSupportZip bitbucketSupportZip, String taskId) {
        this.bitbucketSupportZip = bitbucketSupportZip;
        this.taskId = taskId;
    }

    public BitbucketSupportZipTask() {
    }

    public BitbucketSupportZip getBitbucketSupportZip() {
        return bitbucketSupportZip;
    }

    public void setBitbucketSupportZip(BitbucketSupportZip bitbucketSupportZip) {
        this.bitbucketSupportZip = bitbucketSupportZip;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
}
