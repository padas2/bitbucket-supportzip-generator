package com.padas2.bitbucket.supportzip;

import com.jcabi.aspects.Timeable;
import com.padas2.bitbucket.supportzip.api.BitbucketSupportZipTaskStatus;
import com.padas2.bitbucket.supportzip.api.GitServerDetails;
import com.padas2.bitbucket.supportzip.components.BitbucketSupportZipCreator;
import com.padas2.bitbucket.supportzip.components.BitbucketSupportZipDownloader;
import com.padas2.bitbucket.supportzip.components.BitbucketSupportZipTaskStatusGetter;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.HttpClientBuilder;
import java.io.File;
import java.io.IOException;
import java.sql.Time;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class BitbucketSupportZipEngine implements Runnable{
    private GitServerDetails gitServerDetails;
    private STATE state = STATE.IDLE;
    private boolean flattenUnzippedDir = false;
    private File finalResultDir ;

    private void setFinalResultDir(File finalResultDir) {
        this.finalResultDir = finalResultDir;
    }

    public File getFinalResultDir() {
        return finalResultDir;
    }

    public String state() {
        return state.toString();
    }

    public void flattenUnzippedDir() {
        this.flattenUnzippedDir = true;
    }

    private void setState(STATE state) {
        this.state = state;
        System.out.println(this.state.toString());
    }

    public BitbucketSupportZipEngine(GitServerDetails gitServerDetails) {
        this.gitServerDetails = gitServerDetails;
    }

    private String triggerZipCreation() {
        setState(STATE.SUPPORT_ZIP_CREATION_INITIATED);
        BitbucketSupportZipCreator zipCreator = new BitbucketSupportZipCreator();
        zipCreator.setGitServerDetails(gitServerDetails);
        String supportZipTaskId = zipCreator.triggerSupportZipCreation();
        return supportZipTaskId;
    }

    private BitbucketSupportZipTaskStatus waitTillZipIsCreated(BitbucketSupportZipTaskStatusGetter taskStatusGetter) {
        BitbucketSupportZipTaskStatus status ;
        while(true) {
            status = taskStatusGetter.getStatus();
            if(status.getProgressPercentage() == 100)
                break;
        }
        setState(STATE.SUPPORT_ZIP_CREATION_FINISHED);
        return status;
    }

    private String downloadSupportZip(String supportZipName) throws IOException, AuthenticationException{
        setState(STATE.SUPPORT_ZIP_DOWNLOAD_INITIATED);
        BitbucketSupportZipDownloader downloader = new BitbucketSupportZipDownloader();
        downloader.setGitServerDetails(gitServerDetails);
        downloader.setSupportZipName(supportZipName);
        String downloadedZipFileLocation = downloader.download();
        setState(STATE.SUPPORT_ZIP_DOWNLOAD_FINISHED);
        return downloadedZipFileLocation;
    }

    private String unzip(String downloadedZipFileLocation) {
        setState(STATE.SUPPORT_ZIP_UNZIPPING_INITIATED);
        Unzipper unzipper = new Unzipper();
        unzipper.setTargetFile(downloadedZipFileLocation);
        String unzippedDirLocation = unzipper.unzip();
        setState(STATE.SUPPORT_ZIP_UNZIPPING_FINISHED);
        return unzippedDirLocation;
    }

    private File flattenDir(String unzippedDirLocation) {
        File finalResultDir ;
        if(flattenUnzippedDir) {
            setState(STATE.UNZIPPED_DIR_FLATTENING_INITIATED);
            DirFlattener dirFlattener = new DirFlattener();
            dirFlattener.setRootDir(new File(unzippedDirLocation));
            dirFlattener.flatten();
            setState(STATE.UNZIPPED_DIR_FLATTENING_FINISHED);
            finalResultDir = dirFlattener.flattenedDir();
        } else
            finalResultDir = new File(unzippedDirLocation);
        return finalResultDir;
    }

    private BitbucketSupportZipTaskStatusGetter getTaskStatusGetter(String taskId) {
        BitbucketSupportZipTaskStatusGetter taskStatusGetter = new BitbucketSupportZipTaskStatusGetter();
        taskStatusGetter.setGitServerDetails(gitServerDetails);
        taskStatusGetter.setTaskId(taskId);
        return taskStatusGetter;
    }

    private void checkBitbucketServerStatus() throws IOException, AuthenticationException{
        try {
            System.out.println("Triggered check");
            String url = gitServerDetails.getGitHostUrl();
            HttpClient client = HttpClientBuilder.create().build();
            UsernamePasswordCredentials creds = new UsernamePasswordCredentials(gitServerDetails.getGitUser(), gitServerDetails.getGitPassWord());
            HttpGet request = new HttpGet(url);
            request.addHeader(new BasicScheme().authenticate(creds, request, null));
            HttpResponse response = client.execute(request);
            response.getStatusLine().getStatusCode();
            System.out.println("Check successful");
        } catch (AuthenticationException | IOException a) {
            Logger.getGlobal().warning(a.toString());
            throw a;
        }
    }

    public void start() throws IOException, AuthenticationException {
        checkBitbucketServerStatus();
        String supportZipTaskId = triggerZipCreation();
        BitbucketSupportZipTaskStatusGetter taskStatusGetter = getTaskStatusGetter(supportZipTaskId);
        BitbucketSupportZipTaskStatus status  = waitTillZipIsCreated(taskStatusGetter);
        String downloadedZipFileLocation = downloadSupportZip(status.getBitbucketSupportZipTask().getBitbucketSupportZip().getZipFileName());
        String unzippedDirLocation = unzip(downloadedZipFileLocation);
        File flattenedDir = flattenDir(unzippedDirLocation);
        setFinalResultDir(flattenedDir);
    }

    @Override
    public void run() {
        try {
            start();
        } catch (IOException | AuthenticationException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException, AuthenticationException{
        GitServerDetails gitServerDetails = new GitServerDetails();
        BitbucketSupportZipEngine b = new BitbucketSupportZipEngine(gitServerDetails);
        b.start();
    }
}
