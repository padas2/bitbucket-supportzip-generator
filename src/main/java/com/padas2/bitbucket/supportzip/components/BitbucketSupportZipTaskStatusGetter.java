package com.padas2.bitbucket.supportzip.components;

import com.padas2.bitbucket.supportzip.api.BitbucketSupportZipTaskStatus;
import com.padas2.bitbucket.supportzip.api.GitServerDetails;
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
import java.util.logging.Logger;

public class BitbucketSupportZipTaskStatusGetter {
    private String taskId;
    private GitServerDetails gitServerDetails;

    public BitbucketSupportZipTaskStatusGetter() {
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public GitServerDetails getGitServerDetails() {
        return gitServerDetails;
    }

    public void setGitServerDetails(GitServerDetails gitServerDetails) {
        this.gitServerDetails = gitServerDetails;
    }

    public BitbucketSupportZipTaskStatusGetter(String taskId, GitServerDetails gitServerDetails) {
        this.taskId = taskId;
        this.gitServerDetails = gitServerDetails;
    }

    public BitbucketSupportZipTaskStatus getStatus() {
        BitbucketSupportZipTaskStatus zipTaskStatus = null;
        try {
            String url = gitServerDetails.getGitHostUrl() + "/rest/troubleshooting/latest/support-zip/status/task/" + taskId;
            HttpClient client = HttpClientBuilder.create().build();
            UsernamePasswordCredentials creds = new UsernamePasswordCredentials(gitServerDetails.getGitUser(), gitServerDetails.getGitPassWord());
            HttpGet request = new HttpGet(url);
            request.addHeader(new BasicScheme().authenticate(creds, request, null));
            request.setHeader("X-Atlassian-Token", "no-check");
            HttpResponse response = client.execute(request);
            HttpEntity entity = response.getEntity();
            String json = EntityUtils.toString(entity, StandardCharsets.UTF_8);
            JSONObject jsonObject = new JSONObject(json);
            zipTaskStatus = new BitbucketSupportZipTaskStatus(jsonObject);
        } catch (AuthenticationException a) {
            Logger.getGlobal().warning(a.toString());
        } catch (IOException io) {
            Logger.getGlobal().warning(io.toString());
        }
        return zipTaskStatus;
    }
}
