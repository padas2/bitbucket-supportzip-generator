package com.padas2.bitbucket.supportzip;

public enum  STATE {
    IDLE,
    BITBUCKET_SERVER_HEALTH_CHECK_INITIATED,
    BITBUCKET_SERVER_HEALTH_CHECK_FINISHED,
    BITBUCKET_SUPPORT_ZIP_CREATION_INITIATED,
    BITBUCKET_SUPPORT_ZIP_CREATION_FINISHED,
    BITBUCKET_SUPPORT_ZIP_DOWNLOAD_INITIATED,
    BITBUCKET_SUPPORT_ZIP_DOWNLOAD_FINISHED,
    BITBUCKET_SUPPORT_ZIP_UNZIPPING_INITIATED,
    BITBUCKET_SUPPORT_ZIP_UNZIPPING_FINISHED,
    BITBUCKET_UNZIPPED_DIR_FLATTENING_INITIATED,
    BITBUCKET_UNZIPPED_DIR_FLATTENING_FINISHED,
    BITBUCKET_CREDENTIALS_EXISTENCE_CHECK_INITIATED,
    BITBUCKET_CREDENTIALS_EXISTENCE_CHECK_FINISHED,
    BITBUCKET_CREDENTIALS_EXISTENCE_CHECK_FAILED,
    BITBUCKET_CREDENTIALS_PERMISSION_CHECK_INITIATED,
    BITBUCKET_CREDENTIALS_PERMISSION_CHECK_FINISHED,
    BITBUCKET_CREDENTIALS_PERMISSION_CHECK_FAILED;
}
