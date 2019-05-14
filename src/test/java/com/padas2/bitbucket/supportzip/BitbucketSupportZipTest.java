package com.padas2.bitbucket.supportzip;

import com.padas2.bitbucket.supportzip.api.BitbucketServerDetails;

public abstract class BitbucketSupportZipTest {
    protected static BitbucketServerDetails bitbucketServerDetails = new BitbucketServerDetails();

    protected static void printRequiredEnvVariablesMessage() {
        System.out.println("#######################################################################");
        System.out.println("###");
        System.out.println("### The following environment variables have to be set to their ");
        System.out.println("### corresponding values to run unit tests");
        System.out.println("### BITBUCKET_URL , BITBUCKET_ADMIN_USER , BITBUCKET_ADMIN_PWD");
        System.out.println("###");
        System.out.println("#######################################################################");
    }

    protected static void initBitbucketServerDetails() {
        printRequiredEnvVariablesMessage();
        bitbucketServerDetails.setGitHostUrl(System.getenv().get("BIBTUCKET_URL"));
        bitbucketServerDetails.setGitUser(System.getenv().get("BITBUCKET_ADMIN_USER"));
        bitbucketServerDetails.setGitPassWord(System.getenv().get("BITBUCKET_ADMIN_PWD"));
    }
}
