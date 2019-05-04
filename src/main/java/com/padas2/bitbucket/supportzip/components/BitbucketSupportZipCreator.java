package com.padas2.bitbucket.supportzip.components;

import com.padas2.bitbucket.supportzip.BitbucketSupportTimedLimitedInteraction;
import com.padas2.bitbucket.supportzip.response.BitbucketSupportZipCreatorResponse;
import com.padas2.bitbucket.supportzip.api.BitbucketServerDetails;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;
import java.io.IOException;

public class BitbucketSupportZipCreator extends BitbucketSupportTimedLimitedInteraction {
    public BitbucketSupportZipCreator(BitbucketServerDetails bitbucketServerDetails) {
        super(bitbucketServerDetails);
    }

    @Override
    public void mainMethod() {
        try {
            String url = bitbucketServerDetails.getGitHostUrl() + "/rest/troubleshooting/latest/support-zip/local";
            HttpClient client = HttpClientBuilder.create().build();
            HttpPost post = new HttpPost(url);
            post.setHeader("X-Atlassian-Token", "no-check");
            UsernamePasswordCredentials creds = new UsernamePasswordCredentials(bitbucketServerDetails.getGitUser(), bitbucketServerDetails.getGitPassWord());
            post.addHeader(new BasicScheme().authenticate(creds, post, null));
            HttpResponse response = client.execute(post);
            JSONObject jsonObject = getJsonObjectFromResponse(response);
            bitbucketRestApiResponse = new BitbucketSupportZipCreatorResponse(jsonObject);
        } catch (AuthenticationException a) {
            authenticationException = a;
        } catch (IOException io) {
            ioException = io;
        }
    }
}
