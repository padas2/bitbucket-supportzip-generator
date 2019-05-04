package com.padas2.bitbucket.supportzip;

import com.padas2.bitbucket.supportzip.response.*;
import com.padas2.bitbucket.supportzip.api.BitbucketSupportZipTaskStatus;
import com.padas2.bitbucket.supportzip.api.BitbucketServerDetails;
import com.padas2.bitbucket.supportzip.components.*;
import org.apache.http.auth.AuthenticationException;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class BitbucketSupportZipEngine {
    private BitbucketServerDetails bitbucketServerDetails;
    private STATE state = STATE.IDLE;
    private boolean flattenUnzippedDir = false;
    private File finalResultDir ;

    public void setFinalResultDir(File finalResultDir) {
        this.finalResultDir = finalResultDir;
        System.out.println("Final Result Dir can be found @ " + finalResultDir);
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

    public BitbucketSupportZipEngine(BitbucketServerDetails bitbucketServerDetails) {
        this.bitbucketServerDetails = bitbucketServerDetails;
    }

    private String triggerZipCreation() throws InterruptedException, TimeoutException, ExecutionException, AuthenticationException, IOException{
        setState(STATE.SUPPORT_ZIP_CREATION_INITIATED);
        BitbucketSupportZipCreator zipCreator = new BitbucketSupportZipCreator(bitbucketServerDetails);
        BitbucketRestApiResponse bitbucketRestApiResponse = zipCreator.run();
        BitbucketSupportZipCreatorResponse response = (BitbucketSupportZipCreatorResponse)bitbucketRestApiResponse;
        System.out.println(response.toString());
        return response.getSupportZipTaskId();
    }

    private BitbucketSupportZipTaskStatus waitTillZipIsCreated(BitbucketSupportZipTaskStatusGetter taskStatusGetter)
            throws InterruptedException, TimeoutException, ExecutionException, AuthenticationException, IOException{
        BitbucketSupportZipTaskStatus status ;
        while(true) {
            status = ((BitbucketSupportZipTaskStatusResponse)taskStatusGetter.run()).getBitbucketSupportZipTaskStatus();
            if(status.getProgressPercentage() == 100)
                break;
        }
        setState(STATE.SUPPORT_ZIP_CREATION_FINISHED);
        return status;
    }

    private String downloadSupportZip(String supportZipName)
            throws InterruptedException, TimeoutException, ExecutionException, AuthenticationException, IOException{
        setState(STATE.SUPPORT_ZIP_DOWNLOAD_INITIATED);
        BitbucketSupportZipDownloader downloader = new BitbucketSupportZipDownloader(bitbucketServerDetails, supportZipName);
        BitbucketRestApiResponse restApiResponse = downloader.run();
        BitbucketSupportZipDownloadResponse downloadResponse = (BitbucketSupportZipDownloadResponse)restApiResponse ;
        setState(STATE.SUPPORT_ZIP_DOWNLOAD_FINISHED);
        return downloadResponse.getDestinationFilePath();
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
        File finalDir;
        if(flattenUnzippedDir) {
            setState(STATE.UNZIPPED_DIR_FLATTENING_INITIATED);
            DirFlattener dirFlattener = new DirFlattener();
            dirFlattener.setRootDir(new File(unzippedDirLocation));
            dirFlattener.flatten();
            setState(STATE.UNZIPPED_DIR_FLATTENING_FINISHED);
            finalDir = dirFlattener.flattenedDir();
        } else
            finalDir = new File(unzippedDirLocation);
        return finalDir;
    }

    private BitbucketSupportZipTaskStatusGetter getTaskStatusGetterFor(String taskId) {
        return new BitbucketSupportZipTaskStatusGetter(bitbucketServerDetails, taskId);
    }

    private void checkBitbucketHealth() throws InterruptedException, TimeoutException, ExecutionException, AuthenticationException, IOException{
        setState(STATE.BITBUCKET_SERVER_HEALTH_CHECK_INITIATED);
        BitbucketHealthChecker healthChecker = new BitbucketHealthChecker(bitbucketServerDetails);
        BitbucketRestApiResponse bitbucketRestApiResponse = healthChecker.run();
        System.out.println(bitbucketRestApiResponse.toString());
        setState(STATE.BITBUCKET_SERVER_HEALTH_CHECK_FINISHED);
    }

    private void checkIfProvidedCredentialsAreValid()
            throws InterruptedException, TimeoutException, ExecutionException, AuthenticationException, IOException{
        setState(STATE.CREDENTIALS_VALIDITY_CHECK_INITIATED);
        BitbucketCredentialsValidator validator = new BitbucketCredentialsValidator(bitbucketServerDetails);
        BitbucketRestApiResponse bitbucketRestApiResponse = validator.run();
        System.out.println(bitbucketRestApiResponse.toString());
        setState(STATE.CREDENTIALS_VALIDITY_CHECK_FINISHED);
    }

    private void checkIfProvidedCredentialsHaveAdminAccess()
            throws InterruptedException, TimeoutException, ExecutionException, AuthenticationException, IOException{
        setState(STATE.CREDENTIALS_PERMISSION_CHECK_INITIATED);
        BitbucketCredentialsPermissionChecker checker = new BitbucketCredentialsPermissionChecker(bitbucketServerDetails);
        BitbucketCredentialPermissionCheckResponse response = (BitbucketCredentialPermissionCheckResponse)checker.run();
        System.out.println(response.doesCredentialHaveAdminAccess());
        setState(STATE.CREDENTIALS_PERMISSION_CHECK_FINISHED);
    }

    public void start() throws InterruptedException, TimeoutException, ExecutionException, AuthenticationException, IOException {
        //checkBitbucketHealth();
        //checkIfProvidedCredentialsAreValid();
        checkIfProvidedCredentialsHaveAdminAccess();
        //String supportZipTaskId = triggerZipCreation();
        //BitbucketSupportZipTaskStatusGetter taskStatusGetter = getTaskStatusGetterFor(supportZipTaskId);
        //BitbucketSupportZipTaskStatus finalStatus  = waitTillZipIsCreated(taskStatusGetter);
        //String downloadedZipFileLocation = downloadSupportZip(finalStatus.getBitbucketSupportZipTask().getBitbucketSupportZip().getZipFileName());
        //String unzippedDirLocation = unzip(downloadedZipFileLocation);
        //File flattenedDir = flattenDir(unzippedDirLocation);
        //setFinalResultDir(flattenedDir);
    }

    public static void main(String[] args) throws IOException, AuthenticationException, InterruptedException, TimeoutException, ExecutionException {
        BitbucketServerDetails bitbucketServerDetails = new BitbucketServerDetails();
        BitbucketSupportZipEngine b = new BitbucketSupportZipEngine(bitbucketServerDetails);
        b.flattenUnzippedDir();
        b.start();
    }
}
