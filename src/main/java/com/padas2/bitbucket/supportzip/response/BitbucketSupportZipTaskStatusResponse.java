package com.padas2.bitbucket.supportzip.response;

import com.padas2.bitbucket.supportzip.response.BitbucketRestApiResponse;
import com.padas2.bitbucket.supportzip.api.BitbucketSupportZipTaskStatus;
import org.json.JSONObject;

public class BitbucketSupportZipTaskStatusResponse extends BitbucketRestApiResponse{
    private BitbucketSupportZipTaskStatus bitbucketSupportZipTaskStatus;

    public BitbucketSupportZipTaskStatusResponse(JSONObject jsonObject) {
        super(jsonObject);
        this.bitbucketSupportZipTaskStatus = new BitbucketSupportZipTaskStatus(jsonObject);
    }

    public BitbucketSupportZipTaskStatus getBitbucketSupportZipTaskStatus() {
        return bitbucketSupportZipTaskStatus;
    }
}
