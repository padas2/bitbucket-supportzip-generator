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

    private BitbucketSupportZipCreatorResponse fireSupportZipCreationAndGetResponse()
            throws InterruptedException, TimeoutException, ExecutionException, AuthenticationException, IOException{
        setState(STATE.SUPPORT_ZIP_CREATION_INITIATED);
        BitbucketSupportZipCreator zipCreator = new BitbucketSupportZipCreator(bitbucketServerDetails);
        BitbucketRestApiResponse bitbucketRestApiResponse = zipCreator.run();
        BitbucketSupportZipCreatorResponse response = (BitbucketSupportZipCreatorResponse)bitbucketRestApiResponse;
        return response;
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

    private BitbucketSupportZipDownloadResponse fireDownloadSupportZipRequestAndGetResponse(String supportZipName)
            throws InterruptedException, TimeoutException, ExecutionException, AuthenticationException, IOException{
        setState(STATE.SUPPORT_ZIP_DOWNLOAD_INITIATED);
        BitbucketSupportZipDownloader downloader = new BitbucketSupportZipDownloader(bitbucketServerDetails, supportZipName);
        BitbucketRestApiResponse restApiResponse = downloader.run();
        BitbucketSupportZipDownloadResponse downloadResponse = (BitbucketSupportZipDownloadResponse)restApiResponse ;
        setState(STATE.SUPPORT_ZIP_DOWNLOAD_FINISHED);
        return downloadResponse;
    }

    private String unzip(String downloadedZipFileLocation) {
        setState(STATE.SUPPORT_ZIP_UNZIPPING_INITIATED);
        Unzipper unzipper = new Unzipper();
        unzipper.setTargetFile(downloadedZipFileLocation);
        String unzippedDirLocation = unzipper.unzip();
        setState(STATE.SUPPORT_ZIP_UNZIPPING_FINISHED);
        return unzippedDirLocation;
    }

    private File flattenDirIfSpecified(String unzippedDirLocation) {
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

    private BitbucketCredentialPermissionCheckResponse fireCredentialPermissionCheckAndGetResponse()
            throws InterruptedException, TimeoutException, ExecutionException, AuthenticationException, IOException{
        setState(STATE.CREDENTIALS_PERMISSION_CHECK_INITIATED);
        BitbucketCredentialsPermissionChecker checker = new BitbucketCredentialsPermissionChecker(bitbucketServerDetails);
        BitbucketCredentialPermissionCheckResponse response = (BitbucketCredentialPermissionCheckResponse)checker.run();
        setState(STATE.CREDENTIALS_PERMISSION_CHECK_FINISHED);

        return response;
    }

    private BitbucketCredentialsExistenceCheckResponse fireCredentialExistenceCheckAndGetResponse()
            throws InterruptedException, TimeoutException, ExecutionException, AuthenticationException, IOException{
        setState(STATE.CREDENTIALS_VALIDITY_CHECK_INITIATED);
        BitbucketCredentialsValidator validator = new BitbucketCredentialsValidator(bitbucketServerDetails);
        BitbucketRestApiResponse bitbucketRestApiResponse = validator.run();
        setState(STATE.CREDENTIALS_VALIDITY_CHECK_FINISHED);

        return (BitbucketCredentialsExistenceCheckResponse) bitbucketRestApiResponse;
    }

    private BitbucketHealthCheckResponse fireBitbucketHealthCheckAndGetResponse()
            throws InterruptedException, TimeoutException, ExecutionException, AuthenticationException, IOException{
        setState(STATE.BITBUCKET_SERVER_HEALTH_CHECK_INITIATED);
        BitbucketHealthChecker healthChecker = new BitbucketHealthChecker(bitbucketServerDetails);
        BitbucketRestApiResponse bitbucketRestApiResponse = healthChecker.run();
        setState(STATE.BITBUCKET_SERVER_HEALTH_CHECK_FINISHED);

        return (BitbucketHealthCheckResponse) bitbucketRestApiResponse;
    }

    private void waitForSupportZipTaskCompletion(String taskId)
            throws InterruptedException, TimeoutException, ExecutionException, AuthenticationException, IOException{
        BitbucketSupportZipTaskStatusGetter taskStatusGetter = getTaskStatusGetterFor(taskId);
        BitbucketSupportZipTaskStatus status ;
        while(true) {
            status = getTaskStatusUsing(taskStatusGetter);
            if(status.getProgressPercentage() == 100)
                break;
        }
        setState(STATE.SUPPORT_ZIP_CREATION_FINISHED);
    }

    private BitbucketSupportZipTaskStatus getTaskStatusUsing(BitbucketSupportZipTaskStatusGetter statusGetter)
            throws InterruptedException, TimeoutException, ExecutionException, AuthenticationException, IOException{
        return ((BitbucketSupportZipTaskStatusResponse)statusGetter.run()).getBitbucketSupportZipTaskStatus();
    }


    public void start() throws InterruptedException, TimeoutException, ExecutionException, AuthenticationException, IOException {
        BitbucketHealthCheckResponse healthCheckResponse = fireBitbucketHealthCheckAndGetResponse();
        if(!healthCheckResponse.isInstanceHealthy()) {
            System.out.println(healthCheckResponse.toString());
            return;
        }


        BitbucketCredentialsExistenceCheckResponse existenceCheckResponse = fireCredentialExistenceCheckAndGetResponse();
        if(!existenceCheckResponse.doesCredentialExist()) {
            System.out.println(existenceCheckResponse.toString());
            return;
        }


        BitbucketCredentialPermissionCheckResponse permissionCheckResponse = fireCredentialPermissionCheckAndGetResponse();
        if(!permissionCheckResponse.doesCredentialHaveAdminAccess()) {
            System.out.println(permissionCheckResponse.toString());
            return;
        }


        BitbucketSupportZipCreatorResponse zipCreatorResponse = fireSupportZipCreationAndGetResponse();
        String supportZipTaskId = zipCreatorResponse.getSupportZipTaskId();

        waitForSupportZipTaskCompletion(supportZipTaskId);

        BitbucketSupportZipTaskStatusGetter statusGetter = getTaskStatusGetterFor(supportZipTaskId);
        BitbucketSupportZipTaskStatus finalStatus = getTaskStatusUsing(statusGetter);

        BitbucketSupportZipDownloadResponse downloadResponse = fireDownloadSupportZipRequestAndGetResponse(
                                                                    finalStatus
                                                                   .getBitbucketSupportZipTask()
                                                                   .getBitbucketSupportZip()
                                                                   .getZipFileName());
        String downloadedZipFileLocation = downloadResponse.getDestinationFilePath();
        
        String unzippedDirLocation = unzip(downloadedZipFileLocation);
        File flattenedDir = flattenDirIfSpecified(unzippedDirLocation);
        setFinalResultDir(flattenedDir);
    }

    public static void main(String[] args) throws IOException, AuthenticationException, InterruptedException, TimeoutException, ExecutionException {
        BitbucketServerDetails bitbucketServerDetails = new BitbucketServerDetails();
        BitbucketSupportZipEngine b = new BitbucketSupportZipEngine(bitbucketServerDetails);
        b.flattenUnzippedDir();
        b.start();
    }
}
