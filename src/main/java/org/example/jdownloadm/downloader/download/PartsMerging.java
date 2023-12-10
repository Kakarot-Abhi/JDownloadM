package org.example.jdownloadm.downloader.download;

import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.CompletableFuture;

@Log4j2
public class PartsMerging {

    private String downloadedFileName;
    private String downloadedPath;
    int availableProcessors;

    public PartsMerging(String downloadedFileName, String downloadedPath, int availableProcessors) {
        this.downloadedFileName = downloadedFileName;
        this.downloadedPath = downloadedPath;
        this.availableProcessors = availableProcessors;
    }

    public void startMerge() throws IOException {
        long startTime = System.currentTimeMillis();
        File finalDownloadedFile = new File(downloadedPath, downloadedFileName);
        String absolutePath = finalDownloadedFile.getAbsolutePath();
        Path downloadedFilePath = finalDownloadedFile.toPath();
        for (int i = 0; i < availableProcessors; i++) {
            String partID = "_part" + i;
            Path path = new File(absolutePath + partID).toPath();
            log.info("reading={}", partID);
            byte[] bytes = Files.readAllBytes(path);
            log.info("reading completed for {}", partID);
            CompletableFuture.runAsync(()->{
                try {
                    Files.deleteIfExists(path);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            log.info("writing to {}", downloadedFilePath);
            StandardOpenOption append = (i == 0) ? StandardOpenOption.CREATE : StandardOpenOption.APPEND;
            Files.write(downloadedFilePath, bytes, append);
            log.info("writing completed for {}", downloadedFilePath);
        }
        System.err.println("Merging Time : " + DownloadUtils.getElaspedTimeInString(startTime));
    }
}
