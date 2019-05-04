package com.padas2.bitbucket.supportzip.components;

import com.padas2.bitbucket.supportzip.BitbucketSupportTimedLimitedInteraction;
import com.padas2.bitbucket.supportzip.response.BitbucketSupportZipDownloadResponse;
import com.padas2.bitbucket.supportzip.api.BitbucketServerDetails;
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

public class BitbucketSupportZipDownloader extends BitbucketSupportTimedLimitedInteraction {
    private String supportZipName ;

    public BitbucketSupportZipDownloader(BitbucketServerDetails bitbucketServerDetails, String supportZipName) {
        super(bitbucketServerDetails);
        this.supportZipName = supportZipName;
    }

    @Override
    public void mainMethod() {
        String destinationFilePath;
        FileOutputStream fos = null;
        InputStream is = null;
        CloseableHttpClient client = null;
        try {
            UsernamePasswordCredentials creds = new UsernamePasswordCredentials(bitbucketServerDetails.getGitUser(),
                    bitbucketServerDetails.getGitPassWord());
            client = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet(getZipDownloadUrl());
            request.addHeader(new BasicScheme().authenticate(creds, request, null));
            request.setHeader("X-Atlassian-Token", "no-check");
            HttpResponse response = client.execute(request);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();
            new File(System.getProperty("user.home") + "/support-zip").mkdirs();
            destinationFilePath = System.getProperty("user.home") + "/support-zip/" + supportZipName;;
            File file = new File(destinationFilePath);
            file.createNewFile();
            fos = new FileOutputStream(file);
            int inByte;
            while ((inByte = is.read()) != -1) {
                fos.write(inByte);
            }
            bitbucketRestApiResponse = new BitbucketSupportZipDownloadResponse(null, destinationFilePath);
        } catch (AuthenticationException a) {
            authenticationException = a;
        } catch (IOException io) {
            ioException = io;
        } finally {
            try {
                is.close();
                fos.close();
                client.close();
            } catch (IOException io) {
                ioException = io;
            }
        }
    }

    private String getZipDownloadUrl() {
        return bitbucketServerDetails.getGitHostUrl() +
                "/rest/troubleshooting/latest/support-zip/download/" +
                supportZipName;
    }
}
