package com.padas2.bitbucket.supportzip;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class DirFlattener {
    private List<File> dirsToBeDeleted = new ArrayList<>();

    public void setRootDir(File rootDir) {
        this.rootDir = rootDir;
    }

    private File rootDir;

    public void flatten() {
        flatten(rootDir);
        deleteResidualDirs();
    }

    public File flattenedDir() {
        return rootDir;
    }

    private void flatten(File destDir) {
        for (final File fileEntry : destDir.listFiles()) {
            if (fileEntry.isDirectory()) {
                dirsToBeDeleted.add(fileEntry);
                flatten(fileEntry);
            } else {
                moveFile(fileEntry, rootDir);
            }
        }
    }

    private void moveFile(File file, File dest) {
        try {
            Path sourceFilePath = Paths.get(file.getAbsolutePath());
            Path destFilePath   = Paths.get(dest.getAbsolutePath() + "/" + file.getName());
            Files.move(sourceFilePath, destFilePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteResidualDirs() {
        for(File file : dirsToBeDeleted)
            file.delete();
    }
}
