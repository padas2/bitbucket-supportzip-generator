package com.padas2.bitbucket.supportzip;

import com.padas2.bitbucket.supportzip.response.BitbucketRestApiResponse;
import com.padas2.bitbucket.supportzip.api.BitbucketServerDetails;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

abstract public class BitbucketSupportInteraction {
    protected BitbucketRestApiResponse bitbucketRestApiResponse;
    protected AuthenticationException authenticationException;
    protected IOException ioException;
    protected BitbucketServerDetails bitbucketServerDetails;

    protected BitbucketSupportInteraction(BitbucketServerDetails bitbucketServerDetails) {
        this.bitbucketServerDetails = bitbucketServerDetails;
    }

    protected JSONObject getJsonObjectFromResponse(HttpResponse httpResponse) throws IOException{
        HttpEntity entity = httpResponse.getEntity();
        String json = EntityUtils.toString(entity, StandardCharsets.UTF_8);
        return new JSONObject(json);
    }
}
