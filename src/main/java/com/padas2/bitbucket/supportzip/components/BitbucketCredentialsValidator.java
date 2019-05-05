package com.padas2.bitbucket.supportzip.components;

import com.padas2.bitbucket.supportzip.BitbucketSupportTimedLimitedInteraction;
import com.padas2.bitbucket.supportzip.response.BitbucketCredentialsExistenceCheckResponse;
import com.padas2.bitbucket.supportzip.api.BitbucketServerDetails;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;
import java.io.IOException;

public class BitbucketCredentialsValidator extends BitbucketSupportTimedLimitedInteraction{
    public BitbucketCredentialsValidator(BitbucketServerDetails bitbucketServerDetails) {
        super(bitbucketServerDetails);
    }

    @Override
    public void mainMethod() {
        try {
            String url = bitbucketServerDetails.getGitHostUrl() + "/rest/api/1.0/users?limit=1";
            HttpClient client = HttpClientBuilder.create().build();
            HttpGet get = new HttpGet(url);
            get.setHeader("X-Atlassian-Token", "no-check");
            UsernamePasswordCredentials creds = new UsernamePasswordCredentials(bitbucketServerDetails.getGitUser(), bitbucketServerDetails.getGitPassWord());
            get.addHeader(new BasicScheme().authenticate(creds, get, null));
            HttpResponse response = client.execute(get);
            JSONObject jsonObject = getJsonObjectFromResponse(response);
            bitbucketRestApiResponse = new BitbucketCredentialsExistenceCheckResponse(jsonObject);
        } catch (AuthenticationException a) {
            authenticationException = a;
        } catch (IOException io) {
            ioException = io;
        }
    }
}
