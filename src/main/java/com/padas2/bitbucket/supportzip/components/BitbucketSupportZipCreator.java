package com.padas2.bitbucket.supportzip.components;

import com.padas2.bitbucket.supportzip.api.GitServerDetails;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

public class BitbucketSupportZipCreator {
    public BitbucketSupportZipCreator() {
    }

    public GitServerDetails getGitServerDetails() {
        return gitServerDetails;
    }

    public void setGitServerDetails(GitServerDetails gitServerDetails) {
        this.gitServerDetails = gitServerDetails;
    }

    private GitServerDetails gitServerDetails;

    public BitbucketSupportZipCreator(GitServerDetails gitServerDetails) {
        this.gitServerDetails = gitServerDetails;
    }

    public String triggerSupportZipCreation() {
        String taskId = null;
        try {
            String url = gitServerDetails.getGitHostUrl() + "/rest/troubleshooting/latest/support-zip/local";
            HttpClient client = HttpClientBuilder.create().build();
            HttpPost post = new HttpPost(url);
            post.setHeader("X-Atlassian-Token", "no-check");
            UsernamePasswordCredentials creds = new UsernamePasswordCredentials(gitServerDetails.getGitUser(), gitServerDetails.getGitPassWord());
            post.addHeader(new BasicScheme().authenticate(creds, post, null));
            HttpResponse response = client.execute(post);
            HttpEntity entity = response.getEntity();
            String json = EntityUtils.toString(entity, StandardCharsets.UTF_8);
            JSONObject jsonObject = new JSONObject(json);
            System.out.println(jsonObject);
            taskId = (String)jsonObject.get("taskId");
        } catch (AuthenticationException a) {
            Logger.getGlobal().warning(a.toString());
        } catch (IOException io) {
            Logger.getGlobal().warning(io.toString());
        }
        return taskId;
    }
}
