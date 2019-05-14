package com.padas2.bitbucket.supportzip;

import com.padas2.bitbucket.supportzip.api.BitbucketSupportZipTaskStatus;
import com.padas2.bitbucket.supportzip.components.*;
import com.padas2.bitbucket.supportzip.response.*;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import java.io.File;
import java.io.IOException;

public class BitbucketSupportZipComponentsTest extends BitbucketSupportZipTest{

    @BeforeClass
    public static void checkForBitbucketServerExistence() {
        initBitbucketServerDetails();
        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet(bitbucketServerDetails.getGitHostUrl());
            HttpResponse response = client.execute(request);
            int responseCode = response.getStatusLine().getStatusCode();
            Assert.assertEquals(200, responseCode);
        } catch (IOException ie) {
            ie.printStackTrace();
        } catch (NullPointerException ne) {
            System.out.println("Looks like the atleast some of Git Server details " +
                               " which have to be set as part of environment variables" +
                               " have not been set");
        }
    }

    @Test
    public void testBitbucketServerHealthChecker() {
        try {
            BitbucketHealthChecker checker = new BitbucketHealthChecker(bitbucketServerDetails);
            checker.setTimeLimit(1);
            BitbucketRestApiResponse restApiResponse = checker.run();
            BitbucketHealthCheckResponse checkResponse = (BitbucketHealthCheckResponse)restApiResponse ;
            Assert.assertEquals(checkResponse.isInstanceHealthy(), true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testBitbucketCredentialsExistenceChecker() {
        try {
            BitbucketCredentialsExistenceChecker checker = new BitbucketCredentialsExistenceChecker(bitbucketServerDetails);
            checker.setTimeLimit(2);
            BitbucketRestApiResponse restApiResponse = checker.run();
            BitbucketCredentialsExistenceCheckResponse checkResponse = (BitbucketCredentialsExistenceCheckResponse)restApiResponse ;
            Assert.assertEquals(checkResponse.doesCredentialExist(), true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testBitbucketCredentialsPermissionChecker() {
        try {
            BitbucketCredentialsPermissionChecker checker = new BitbucketCredentialsPermissionChecker(bitbucketServerDetails);
            checker.setTimeLimit(2);
            BitbucketRestApiResponse restApiResponse = checker.run();
            BitbucketCredentialPermissionCheckResponse checkResponse = (BitbucketCredentialPermissionCheckResponse)restApiResponse ;
            Assert.assertEquals(checkResponse.doesCredentialHaveAdminAccess(), true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private BitbucketSupportZipCreatorResponse createSupportZipAndGetResponse() {
        try {
            BitbucketSupportZipCreator creator = new BitbucketSupportZipCreator(bitbucketServerDetails);
            creator.setTimeLimit(2);
            BitbucketRestApiResponse restApiResponse = creator.run();
            BitbucketSupportZipCreatorResponse checkResponse = (BitbucketSupportZipCreatorResponse) restApiResponse ;
            return checkResponse;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Test
    public void testBitbucketSupportZipCreator() {
        BitbucketSupportZipCreatorResponse response = createSupportZipAndGetResponse();
        Assert.assertNotNull(response);
        Assert.assertNotNull(response.getSupportZipTaskId());
        Assert.assertTrue(!response.getSupportZipTaskId().isEmpty());
    }

    public BitbucketSupportZipTaskStatusGetter getTaskStatusGetter(String supportZipTaskId) {
        BitbucketSupportZipTaskStatusGetter zipTaskStatusGetter = new BitbucketSupportZipTaskStatusGetter(bitbucketServerDetails, supportZipTaskId);
        zipTaskStatusGetter.setTimeLimit(2);
        return zipTaskStatusGetter;
    }

    @Test
    public void testBitbucketSupportZipTaskStatusGetter() {
        try {
            String supportZipTaskId = createSupportZipAndGetResponse().getSupportZipTaskId();
            BitbucketSupportZipTaskStatusGetter taskStatusGetter = getTaskStatusGetter(supportZipTaskId);
            BitbucketSupportZipTaskStatusResponse taskStatusResponse = (BitbucketSupportZipTaskStatusResponse)taskStatusGetter.run();
            Assert.assertNotNull(taskStatusResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testBitbucketSupportZipDownloader() {
        try {
            String supportZipTaskId = createSupportZipAndGetResponse().getSupportZipTaskId();
            BitbucketSupportZipTaskStatusGetter taskStatusGetter = getTaskStatusGetter(supportZipTaskId);
            BitbucketSupportZipTaskStatus status;
            while(true) {
                status = ((BitbucketSupportZipTaskStatusResponse)taskStatusGetter.run()).getBitbucketSupportZipTaskStatus();
                if(status.getProgressPercentage() == 100)
                    break;
            }
            status = ((BitbucketSupportZipTaskStatusResponse)taskStatusGetter.run()).getBitbucketSupportZipTaskStatus();
            String finalZipFileName = status.getBitbucketSupportZipTask().getBitbucketSupportZip().getZipFileName();

            BitbucketSupportZipDownloader downloader = new BitbucketSupportZipDownloader(bitbucketServerDetails, finalZipFileName);
            BitbucketSupportZipDownloadResponse downloadResponse = (BitbucketSupportZipDownloadResponse)downloader.run();
            File finalZipFile = new File(downloadResponse.getDestinationFilePath());
            System.out.println("Final Zip file is @ " + finalZipFile.getAbsolutePath());
            Assert.assertTrue(finalZipFile.exists());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
