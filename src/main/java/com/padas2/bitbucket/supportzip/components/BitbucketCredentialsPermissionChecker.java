package com.padas2.bitbucket.supportzip.components;

import com.padas2.bitbucket.supportzip.BitbucketSupportTimedLimitedInteraction;
import com.padas2.bitbucket.supportzip.api.BitbucketServerDetails;
import com.padas2.bitbucket.supportzip.response.BitbucketCredentialPermissionCheckResponse;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;

import java.io.IOException;

public class BitbucketCredentialsPermissionChecker extends BitbucketSupportTimedLimitedInteraction{
    public BitbucketCredentialsPermissionChecker(BitbucketServerDetails bitbucketServerDetails) {
        super(bitbucketServerDetails);
    }

    private String getAdminUser = "rest/api/1.0/admin/permissions/users";

    @Override
    public void mainMethod() {
        try {
            String url = bitbucketServerDetails.getGitHostUrl();
            HttpClient client = HttpClientBuilder.create().build();
            UsernamePasswordCredentials creds = new UsernamePasswordCredentials(bitbucketServerDetails.getGitUser(), bitbucketServerDetails.getGitPassWord());
            HttpGet request = new HttpGet(url + "/rest/api/1.0/admin/permissions/users");
            request.addHeader(new BasicScheme().authenticate(creds, request, null));
            HttpResponse response = client.execute(request);
            JSONObject jsonObject = getJsonObjectFromResponse(response);
            bitbucketRestApiResponse = new BitbucketCredentialPermissionCheckResponse(jsonObject);
            BitbucketCredentialPermissionCheckResponse permissionCheckResponse = (BitbucketCredentialPermissionCheckResponse)bitbucketRestApiResponse;
            Integer lastPage = null;
            while(!jsonObject.has("errors") && !jsonObject.getBoolean("isLastPage")) {
                request = new HttpGet(url + "/rest/api/1.0/admin/permissions/users?start=" + jsonObject.get("nextPageStart"));
                request.addHeader(new BasicScheme().authenticate(creds, request, null));
                response = client.execute(request);
                jsonObject = getJsonObjectFromResponse(response);
                permissionCheckResponse.addInput(jsonObject);
                lastPage = jsonObject.has("nextPageStart") ? jsonObject.getInt("nextPageStart") : null;
            }
            request = new HttpGet(url + "/rest/api/1.0/admin/permissions/users?start=" + lastPage);
            request.addHeader(new BasicScheme().authenticate(creds, request, null));
            response = client.execute(request);
            jsonObject = getJsonObjectFromResponse(response);
            permissionCheckResponse.addInput(jsonObject);
            permissionCheckResponse.setCredentialsForAccessChecking(bitbucketServerDetails);
        } catch (IOException io) {
            ioException = io;
        } catch (AuthenticationException ae) {
            authenticationException = ae;
        }
    }
}
