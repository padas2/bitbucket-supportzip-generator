package com.padas2.bitbucket.supportzip.components;

import com.padas2.bitbucket.supportzip.BitbucketSupportTimedLimitedInteraction;
import com.padas2.bitbucket.supportzip.response.BitbucketSupportZipTaskStatusResponse;
import com.padas2.bitbucket.supportzip.api.BitbucketServerDetails;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class BitbucketSupportZipTaskStatusGetter extends BitbucketSupportTimedLimitedInteraction {
    private String taskId;

    public BitbucketSupportZipTaskStatusGetter(BitbucketServerDetails bitbucketServerDetails,
                                               String taskId) {
        super(bitbucketServerDetails);
        this.taskId = taskId;
    }

    @Override
    public void mainMethod() {
        try {
            String url = bitbucketServerDetails.getGitHostUrl() + "/rest/troubleshooting/latest/support-zip/status/task/" + taskId;
            HttpClient client = HttpClientBuilder.create().build();
            UsernamePasswordCredentials creds = new UsernamePasswordCredentials(bitbucketServerDetails.getGitUser(), bitbucketServerDetails.getGitPassWord());
            HttpGet request = new HttpGet(url);
            request.addHeader(new BasicScheme().authenticate(creds, request, null));
            request.setHeader("X-Atlassian-Token", "no-check");
            HttpResponse response = client.execute(request);
            HttpEntity entity = response.getEntity();
            String json = EntityUtils.toString(entity, StandardCharsets.UTF_8);
            JSONObject jsonObject = new JSONObject(json);
            bitbucketRestApiResponse = new BitbucketSupportZipTaskStatusResponse(jsonObject);
        } catch (AuthenticationException a) {
            authenticationException = a;
        } catch (IOException io) {
            ioException = io;
        }
    }
}
