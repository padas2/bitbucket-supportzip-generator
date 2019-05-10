package com.padas2.bitbucket.supportzip;

import com.padas2.bitbucket.supportzip.api.BitbucketServerDetails;
import com.padas2.bitbucket.supportzip.components.*;
import com.padas2.bitbucket.supportzip.response.*;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import java.io.IOException;

public class BitbucketSupportZipComponentsTest {
    private static BitbucketServerDetails bitbucketServerDetails = new BitbucketServerDetails();

    private static void printRequiredEnvVariablesMessage() {
        System.out.println("#######################################################################");
        System.out.println("###");
        System.out.println("### The following environment variables have to be set to their ");
        System.out.println("### corresponding values to run unit tests");
        System.out.println("### BITBUCKET_URL , BITBUCKET_ADMIN_USER , BITBUCKET_ADMIN_PWD");
        System.out.println("###");
        System.out.println("#######################################################################");
    }

    private static void initBitbucketServerDetails() {
        printRequiredEnvVariablesMessage();
        bitbucketServerDetails.setGitHostUrl(System.getenv().get("BIBTUCKET_URL"));
        bitbucketServerDetails.setGitUser(System.getenv().get("BITBUCKET_ADMIN_USER"));
        bitbucketServerDetails.setGitPassWord(System.getenv().get("BITBUCKET_ADMIN_PWD"));
    }

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

    @Test
    public void testBitbucketSupportZipTaskStatusGetter() {
        try {
            String supportZipTaskId = createSupportZipAndGetResponse().getSupportZipTaskId();
            BitbucketSupportZipTaskStatusGetter zipTaskStatusGetter = new BitbucketSupportZipTaskStatusGetter(bitbucketServerDetails, supportZipTaskId);
            zipTaskStatusGetter.setTimeLimit(2);
            BitbucketSupportZipTaskStatusResponse taskStatusResponse = (BitbucketSupportZipTaskStatusResponse)zipTaskStatusGetter.run();
            Assert.assertNotNull(taskStatusResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
