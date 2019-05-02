package com.padas2.bitbucket.supportzip.api;

import org.json.JSONObject;

public class BitbucketSupportZipTaskStatus {
    private int progressPercentage ;
    private String progressMessage ;
    private BitbucketSupportZipTask bitbucketSupportZipTask;

    public BitbucketSupportZipTaskStatus(JSONObject jsonObject) {
        BitbucketSupportZip b = new BitbucketSupportZip();
        b.setZipFileName((String)jsonObject.get("fileName"));

        BitbucketSupportZipTask bTask = new BitbucketSupportZipTask();
        bTask.setBitbucketSupportZip(b);
        bTask.setTaskId((String)jsonObject.get("taskId"));

        this.bitbucketSupportZipTask = bTask;
        this.progressPercentage = (Integer) jsonObject.get("progressPercentage");
        this.progressMessage = (String)jsonObject.get("progressMessage");

    }

    public void print() {

    }

    public int getProgressPercentage() {
        return progressPercentage;
    }

    public void setProgressPercentage(int progressPercentage) {
        this.progressPercentage = progressPercentage;
    }

    public String getProgressMessage() {
        return progressMessage;
    }

    public void setProgressMessage(String progressMessage) {
        this.progressMessage = progressMessage;
    }

    public BitbucketSupportZipTask getBitbucketSupportZipTask() {
        return bitbucketSupportZipTask;
    }

    public void setBitbucketSupportZipTask(BitbucketSupportZipTask bitbucketSupportZipTask) {
        this.bitbucketSupportZipTask = bitbucketSupportZipTask;
    }
}
