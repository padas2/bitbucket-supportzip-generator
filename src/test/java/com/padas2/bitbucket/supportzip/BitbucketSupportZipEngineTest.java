package com.padas2.bitbucket.supportzip;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

public class BitbucketSupportZipEngineTest extends BitbucketSupportZipTest{

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
    public void testBitbucketSupportZipEngine() {
        try {
            BitbucketSupportZipEngine engine = new BitbucketSupportZipEngine(bitbucketServerDetails);
            engine.start();
            Assert.assertTrue(engine.getFinalResultDir().exists());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
