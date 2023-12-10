package org.example.jdownloadm.downloader;

import javafx.application.Platform;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import lombok.extern.log4j.Log4j2;
import org.example.jdownloadm.downloader.download.DownloadUtils;
import org.example.jdownloadm.downloader.download.HandleProgress;
import org.example.jdownloadm.downloader.download.PartsMerging;
import org.example.jdownloadm.downloader.download.ProgressTracker;
import org.example.jdownloadm.downloader.ui.MainDownloadTracker;
import org.example.jdownloadm.downloader.ui.PartDownloadTracker;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.*;

@Log4j2
public class DownloadService extends Thread {
    private String downloadUrl;
    private String downloadBasePath;
    public String fileName;
    public long fileSize;
    private int threadNums;
    private MainDownloadTracker mainDownloadTracker;
    private PartDownloadTracker partDownloadTracker;

    ConcurrentHashMap<Integer,DownloadThread> downloadPartsMap = new ConcurrentHashMap<>();

    public DownloadService(String downloadUrl, String downloadBasePath, int threadNums, MainDownloadTracker mainDownloadTracker, PartDownloadTracker partDownloadTracker) throws URISyntaxException, IOException {
        this.downloadUrl = downloadUrl;
        this.downloadBasePath = downloadBasePath;
        this.threadNums = threadNums;
        this.fileName = DownloadUtils.getFileNameFromUrl(downloadUrl);
        fileSize = DownloadUtils.getFileSize(downloadUrl);
        this.mainDownloadTracker = mainDownloadTracker;
        this.mainDownloadTracker.updateFileSize(DownloadUtils.humanReadableByteCountSI(fileSize));
        this.partDownloadTracker = partDownloadTracker;
    }

    public void run() {
        long startTime = System.currentTimeMillis();

        try {

            int availableProcessors = threadNums;

            long chunkSize = fileSize / availableProcessors;
            ExecutorService executor = getExecutor();

            List<ProgressTracker> progressTrackers = new ArrayList<>();

            CountDownLatch downLatch = new CountDownLatch(availableProcessors);

            for (int i = 0; i < availableProcessors; i++) {
                long startByte = i * chunkSize;
                long endByte = (i == availableProcessors - 1) ? fileSize - 1 : (i + 1) * chunkSize - 1;

                ProgressTracker progressTracker = new ProgressTracker("Part-" + i);
                progressTrackers.add(progressTracker);
                DownloadThread downloadThread = new DownloadThread(DownloadUtils.getFilePartName(i, downloadBasePath, fileName), downloadUrl, startByte, endByte, progressTracker
                        , partDownloadTracker.getPartProgres(i), downLatch, i, downloadPartsMap); // Thread percentage label

                executor.submit(downloadThread);
            }

            Timer timer = new Timer("ProgressTimer", true);
            HandleProgress handleProgress = new HandleProgress(fileSize, progressTrackers, startTime, mainDownloadTracker);

            log.info("All tasks submitted");

            timer.schedule(handleProgress, 0, 1000);
            downLatch.await();

            executor.shutdown();

            if (executor.awaitTermination(2, TimeUnit.MINUTES)) {
                timer.cancel();
            }
            log.info("Parts Downloading finished");

            PartsMerging partsMerging = new PartsMerging(fileName, downloadBasePath, availableProcessors);

            partsMerging.startMerge();
            String totalFormatedTime = DownloadUtils.getElaspedTimeInString(startTime);

            Platform.runLater(() -> {
                Dialog<String> dialog = new Dialog<>();
                dialog.setTitle("Download Completed !!");
                dialog.setContentText(String.format("Downloading took %s.\n File saved at location %s", totalFormatedTime, new File(downloadBasePath, fileName).getAbsolutePath()));
                ButtonType close = new ButtonType("Close", ButtonBar.ButtonData.OK_DONE);
                dialog.getDialogPane().getButtonTypes().add(close);
                dialog.showAndWait();
            });

            log.debug("All task complete in {}", totalFormatedTime);
        } catch (Exception e) {
            log.error("Error occurred in DownloadService ", e);
        }
    }


    public ExecutorService getExecutor() {
        return Executors.newFixedThreadPool(threadNums);
    }

}
