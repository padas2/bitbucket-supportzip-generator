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

    public void setState(STATE state) {
        this.state = state;
    }

    private void printEngineState() {
        System.out.println(this.state);
    }

    private void setStateAndPrintTheSame(STATE state) {
        setState(state);
        printEngineState();
    }

    public BitbucketSupportZipEngine(BitbucketServerDetails bitbucketServerDetails) {
        this.bitbucketServerDetails = bitbucketServerDetails;
    }

    private BitbucketSupportZipCreatorResponse fireSupportZipCreationAndGetResponse()
            throws InterruptedException, TimeoutException, ExecutionException, AuthenticationException, IOException{
        setStateAndPrintTheSame(STATE.BITBUCKET_SUPPORT_ZIP_CREATION_INITIATED);
        BitbucketSupportZipCreator zipCreator = new BitbucketSupportZipCreator(bitbucketServerDetails);
        zipCreator.setTimeLimit(10);
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
        setStateAndPrintTheSame(STATE.BITBUCKET_SUPPORT_ZIP_CREATION_FINISHED);
        printEngineState();
        return status;
    }

    private BitbucketSupportZipDownloadResponse fireDownloadSupportZipRequestAndGetResponse(String supportZipName)
            throws InterruptedException, TimeoutException, ExecutionException, AuthenticationException, IOException{
        setStateAndPrintTheSame(STATE.BITBUCKET_SUPPORT_ZIP_DOWNLOAD_INITIATED);
        BitbucketSupportZipDownloader downloader = new BitbucketSupportZipDownloader(bitbucketServerDetails, supportZipName);
        downloader.setTimeLimit(10);
        BitbucketRestApiResponse restApiResponse = downloader.run();
        BitbucketSupportZipDownloadResponse downloadResponse = (BitbucketSupportZipDownloadResponse)restApiResponse ;
        setStateAndPrintTheSame(STATE.BITBUCKET_SUPPORT_ZIP_DOWNLOAD_FINISHED);
        return downloadResponse;
    }

    private String unzip(String downloadedZipFileLocation) {
        setStateAndPrintTheSame(STATE.BITBUCKET_SUPPORT_ZIP_UNZIPPING_INITIATED);
        Unzipper unzipper = new Unzipper();
        unzipper.setTargetFile(downloadedZipFileLocation);
        String unzippedDirLocation = unzipper.unzip();
        setStateAndPrintTheSame(STATE.BITBUCKET_SUPPORT_ZIP_UNZIPPING_FINISHED);
        return unzippedDirLocation;
    }

    private File flattenDirIfSpecified(String unzippedDirLocation) {
        File finalDir;
        if(flattenUnzippedDir) {
            setStateAndPrintTheSame(STATE.BITBUCKET_UNZIPPED_DIR_FLATTENING_INITIATED);
            DirFlattener dirFlattener = new DirFlattener();
            dirFlattener.setRootDir(new File(unzippedDirLocation));
            dirFlattener.flatten();
            setStateAndPrintTheSame(STATE.BITBUCKET_UNZIPPED_DIR_FLATTENING_FINISHED);
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
        setStateAndPrintTheSame(STATE.BITBUCKET_CREDENTIALS_PERMISSION_CHECK_INITIATED);
        BitbucketCredentialsPermissionChecker checker = new BitbucketCredentialsPermissionChecker(bitbucketServerDetails);
        BitbucketCredentialPermissionCheckResponse response = (BitbucketCredentialPermissionCheckResponse)checker.run();
        setStateAndPrintTheSame(STATE.BITBUCKET_CREDENTIALS_PERMISSION_CHECK_FINISHED);
        return response;
    }

    private BitbucketCredentialsExistenceCheckResponse fireCredentialExistenceCheckAndGetResponse()
            throws InterruptedException, TimeoutException, ExecutionException, AuthenticationException, IOException{
        setStateAndPrintTheSame(STATE.BITBUCKET_CREDENTIALS_EXISTENCE_CHECK_INITIATED);
        BitbucketCredentialsExistenceChecker checker = new BitbucketCredentialsExistenceChecker(bitbucketServerDetails);
        checker.setTimeLimit(2);
        BitbucketRestApiResponse bitbucketRestApiResponse = checker.run();
        setStateAndPrintTheSame(STATE.BITBUCKET_CREDENTIALS_EXISTENCE_CHECK_FINISHED);
        return (BitbucketCredentialsExistenceCheckResponse) bitbucketRestApiResponse;
    }

    private BitbucketHealthCheckResponse fireBitbucketHealthCheckAndGetResponse()
            throws InterruptedException, TimeoutException, ExecutionException, AuthenticationException, IOException{
        setStateAndPrintTheSame(STATE.BITBUCKET_SERVER_HEALTH_CHECK_INITIATED);
        BitbucketHealthChecker healthChecker = new BitbucketHealthChecker(bitbucketServerDetails);
        healthChecker.setTimeLimit(1);
        BitbucketRestApiResponse bitbucketRestApiResponse = healthChecker.run();
        setStateAndPrintTheSame(STATE.BITBUCKET_SERVER_HEALTH_CHECK_FINISHED);
        return (BitbucketHealthCheckResponse) bitbucketRestApiResponse;
    }

    private void waitForSupportZipTaskCompletion(String taskId)
            throws InterruptedException, TimeoutException, ExecutionException, AuthenticationException, IOException{
        BitbucketSupportZipTaskStatusGetter taskStatusGetter = getTaskStatusGetterFor(taskId);
        taskStatusGetter.setTimeLimit(10);
        BitbucketSupportZipTaskStatus status ;
        while(true) {
            status = getTaskStatusUsing(taskStatusGetter);
            if(status.getProgressPercentage() == 100)
                break;
        }
        setStateAndPrintTheSame(STATE.BITBUCKET_SUPPORT_ZIP_CREATION_FINISHED);
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
            setStateAndPrintTheSame(STATE.BITBUCKET_CREDENTIALS_EXISTENCE_CHECK_FAILED);
            return;
        }

        BitbucketCredentialPermissionCheckResponse permissionCheckResponse = fireCredentialPermissionCheckAndGetResponse();
        if(!permissionCheckResponse.doesCredentialHaveAdminAccess()) {
            System.out.println(permissionCheckResponse.toString());
            setStateAndPrintTheSame(STATE.BITBUCKET_CREDENTIALS_PERMISSION_CHECK_FAILED);
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
        System.out.println("Final result dir can be found @ " + getFinalResultDir().getAbsolutePath());
    }

    public static void main(String[] args) throws IOException, AuthenticationException, InterruptedException, TimeoutException, ExecutionException {
        BitbucketServerDetails bitbucketServerDetails = new BitbucketServerDetails();
        BitbucketSupportZipEngine b = new BitbucketSupportZipEngine(bitbucketServerDetails);
        b.flattenUnzippedDir();
        b.start();
        File resultFile = b.getFinalResultDir();
    }
}
