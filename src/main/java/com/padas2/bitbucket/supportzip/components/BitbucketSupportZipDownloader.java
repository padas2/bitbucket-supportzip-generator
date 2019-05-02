package com.padas2.bitbucket.supportzip.components;

import com.padas2.bitbucket.supportzip.api.GitServerDetails;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class BitbucketSupportZipDownloader {
    private GitServerDetails gitServerDetails;
    private String supportZipName ;

    public BitbucketSupportZipDownloader() {
    }

    public GitServerDetails getGitServerDetails() {
        return gitServerDetails;
    }

    public void setGitServerDetails(GitServerDetails gitServerDetails) {
        this.gitServerDetails = gitServerDetails;
    }

    public String getSupportZipName() {
        return supportZipName;
    }

    public void setSupportZipName(String supportZipName) {
        this.supportZipName = supportZipName;
    }

    public BitbucketSupportZipDownloader(GitServerDetails gitServerDetails, String supportZipName) {
        this.gitServerDetails = gitServerDetails;
        this.supportZipName = supportZipName;
    }

    public String download() throws AuthenticationException, IOException{
        UsernamePasswordCredentials creds = new UsernamePasswordCredentials(gitServerDetails.getGitUser(),
                gitServerDetails.getGitPassWord());
        CloseableHttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(getZipDownloadUrl());
        request.addHeader(new BasicScheme().authenticate(creds, request, null));
        request.setHeader("X-Atlassian-Token", "no-check");
        HttpResponse response = client.execute(request);
        HttpEntity entity = response.getEntity();
        InputStream is = entity.getContent();
        new File(System.getProperty("user.home") + "/support-zip").mkdirs();
        String destinationFilePath = System.getProperty("user.home") + "/support-zip/" + supportZipName;;
        File file = new File(destinationFilePath);
        file.createNewFile();
        FileOutputStream fos = new FileOutputStream(file);
        int inByte;
        while ((inByte = is.read()) != -1) {
            fos.write(inByte);
        }
        is.close();
        fos.close();
        client.close();
        return destinationFilePath;
    }

    private String getZipDownloadUrl() {
        return gitServerDetails.getGitHostUrl() +
                "/rest/troubleshooting/latest/support-zip/download/" +
                supportZipName;
    }
}
