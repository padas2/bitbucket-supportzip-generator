package com.padas2.bitbucket.supportzip.response;

import com.padas2.bitbucket.supportzip.response.BitbucketRestApiResponse;
import org.json.JSONObject;

public class BitbucketSupportZipCreatorResponse extends BitbucketRestApiResponse{
    public BitbucketSupportZipCreatorResponse(JSONObject bitbucketRestApiResponse) {
        super(bitbucketRestApiResponse);
    }

    public String getSupportZipTaskId() {
        return (String)bitbucketRestApiResponse.get("taskId");
    }

    @Override
    public String toString() {
        return "Request to create support zip has been submitted . Task Id : " + getSupportZipTaskId();
    }
}
