package com.padas2.bitbucket.supportzip.response;

import com.padas2.bitbucket.supportzip.api.BitbucketServerDetails;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class BitbucketCredentialPermissionCheckResponse extends BitbucketRestApiResponse{
    ArrayList<JSONObject> jsonObjects = new ArrayList<>();
    private BitbucketServerDetails bitbucketServerDetails ;

    public BitbucketCredentialPermissionCheckResponse(JSONObject bitbucketRestApiResponse) {
        super(bitbucketRestApiResponse);
        addInput(bitbucketRestApiResponse);
    }

    public void setCredentialsForAccessChecking(BitbucketServerDetails bitbucketServerDetails) {
        this.bitbucketServerDetails = bitbucketServerDetails;
    }

    public void addInput(JSONObject jsonObject) {
        jsonObjects.add(jsonObject);
    }

    public boolean doesCredentialHaveAdminAccess() {
        boolean doesCredentialHaveAdminAccess = false;
        if(bitbucketRestApiResponse.has("errors"))
            doesCredentialHaveAdminAccess = false;
        else {
            for(JSONObject object : jsonObjects) {
                JSONArray jsonArray = (JSONArray)object.get("values");
                for( int i=0; i<jsonArray.length() ;i++) {
                    JSONObject object1 = ((JSONObject)jsonArray.get(i));
                    String user = object1.getJSONObject("user").getString("name");
                    String permission = object1.getString("permission");
                    if(user.equals(bitbucketServerDetails.getGitUser()) &&
                            (permission.equals("ADMIN") || permission.equals("SYS_ADMIN"))) {
                        doesCredentialHaveAdminAccess = true;
                        break;
                    }
                }
            }
        }
        return doesCredentialHaveAdminAccess;
    }
}
