package com.padas2.bitbucket.supportzip.components;

import com.padas2.bitbucket.supportzip.BitbucketSupportTimedLimitedInteraction;
import com.padas2.bitbucket.supportzip.response.BitbucketHealthCheckResponse;
import com.padas2.bitbucket.supportzip.api.BitbucketServerDetails;
import org.apache.http.HttpResponse;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;

import java.io.IOException;

public class BitbucketHealthChecker extends BitbucketSupportTimedLimitedInteraction {
    public BitbucketHealthChecker(BitbucketServerDetails bitbucketServerDetails) {
        super(bitbucketServerDetails);
    }

    @Override
    protected void mainMethod() {
        try {
            String url = bitbucketServerDetails.getGitHostUrl();
            HttpClient client = HttpClientBuilder.create().build();
            UsernamePasswordCredentials creds = new UsernamePasswordCredentials(bitbucketServerDetails.getGitUser(), bitbucketServerDetails.getGitPassWord());
            HttpGet request = new HttpGet(url + "/rest/api/1.0/application-properties");
            HttpResponse response = client.execute(request);
            JSONObject jsonObject = getJsonObjectFromResponse(response);
            bitbucketRestApiResponse = new BitbucketHealthCheckResponse(jsonObject);
        } catch (IOException io) {
            ioException = io;
        }
    }
}
