package com.padas2.bitbucket.supportzip.response;

import org.json.JSONObject;

public class BitbucketHealthCheckResponse extends BitbucketRestApiResponse{
    public BitbucketHealthCheckResponse(JSONObject bitbucketRestApiResponse) {
        super(bitbucketRestApiResponse);
    }

    public boolean isInstanceHealthy() {
        boolean isInstanceHealthy = false;
        if(bitbucketRestApiResponse.has("version"))
            isInstanceHealthy = true;
        return isInstanceHealthy;
    }

    @Override
    public String toString() {
        return "Current Instance versioned at : " + bitbucketRestApiResponse.get("version") + " is " +
                (isInstanceHealthy() ? " healthy " : "unhealthy");
    }
    
}
