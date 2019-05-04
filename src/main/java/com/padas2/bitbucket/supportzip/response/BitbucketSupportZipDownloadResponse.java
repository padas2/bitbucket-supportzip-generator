package com.padas2.bitbucket.supportzip.response;

import com.padas2.bitbucket.supportzip.response.BitbucketRestApiResponse;
import org.json.JSONObject;

public class BitbucketSupportZipDownloadResponse extends BitbucketRestApiResponse{
    private String destinationFilePath;

    public BitbucketSupportZipDownloadResponse(JSONObject bitbucketRestApiResponse, String destinationFilePath) {
        super(bitbucketRestApiResponse);
        this.destinationFilePath = destinationFilePath;
    }

    public String getDestinationFilePath() {
        return destinationFilePath;
    }
}
