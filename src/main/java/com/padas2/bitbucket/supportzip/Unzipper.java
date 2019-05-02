package com.padas2.bitbucket.supportzip;

import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.core.ZipFile;
import java.util.UUID;

public class Unzipper {
    private String targetFile ;

    private String targetFileRelativeLocation;

    public void setTargetFile(String targetFile) {
        this.targetFile = targetFile;
    }

    public String getTargetFileRelativeLocation() {
        if(targetFileRelativeLocation == null) {
            targetFileRelativeLocation = UUID.randomUUID().toString();
        }
        return targetFileRelativeLocation;
    }

    private String getDestDir() {
        return System.getProperty("user.home") + "/"
                + "support-unzip" + "/" +
                getTargetFileRelativeLocation();
    }

    public String unzip() {
        String destDir = getDestDir();
        try {
            ZipFile zipFile = new ZipFile(targetFile);
            zipFile.extractAll(destDir);
        } catch (ZipException e) {
            e.printStackTrace();
        }
        return destDir;
    }
}
