package com.padas2.bitbucket.supportzip.response;

import org.json.JSONObject;

public class BitbucketCredentialsValidatorResponse extends BitbucketRestApiResponse{
    public BitbucketCredentialsValidatorResponse(JSONObject bitbucketRestApiResponse) {
        super(bitbucketRestApiResponse);
    }

    public boolean doesCredentialExist() {
        boolean doesCredentialExist = false;
        if(bitbucketRestApiResponse.has("size"))
            doesCredentialExist = true;
        return doesCredentialExist;
    }

    @Override
    public String toString() {
        return "Passed credentials " + (doesCredentialExist() ? "do exist " : "do not exist");
    }
}
