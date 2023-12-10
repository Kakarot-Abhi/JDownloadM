package org.example.jdownloadm.downloader.download;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Download implements Runnable {

    public String downloadUrl;
    public String downloadBasePath;
    public String fileName;


    public long fileSize;

    public Download(String downloadUrl, String downloadBasePath) throws URISyntaxException, IOException {
        this.downloadUrl = downloadUrl;
        this.downloadBasePath = downloadBasePath;
        this.fileName = DownloadUtils.getFileNameFromUrl(downloadUrl);
        fileSize = DownloadUtils.getFileSize(downloadUrl);
    }

    public Download() {

    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();

        try {

            int availableProcessors = Runtime.getRuntime().availableProcessors();

            long chunkSize = fileSize / availableProcessors;
            ExecutorService executor = getExecutor();

            List<ProgressTracker> progressTrackers = new ArrayList<>();

            for (int i = 0; i < availableProcessors; i++) {
                long startByte = i * chunkSize;
                long endByte = (i == availableProcessors - 1) ? fileSize - 1 : (i + 1) * chunkSize - 1;

                ProgressTracker progressTracker = new ProgressTracker("Part-" + i);
                progressTrackers.add(progressTracker);
                executor.submit(new PartDownload(downloadUrl, startByte, endByte, DownloadUtils.getFilePartName(i, downloadBasePath, fileName), progressTracker));
            }

            Timer timer = new Timer("ProgressTimer", true);
//            HandleProgress handleProgress = new HandleProgress(fileSize, progressTrackers, startTime, mainProgressBar, mainPercentageLabel);

            long start = System.currentTimeMillis();
            System.err.println("all task submitted...");
//            timer.schedule(handleProgress, 0, 1000);
//            executor.close();
            PartsMerging partsMerging = new PartsMerging(fileName, downloadBasePath, availableProcessors);

            partsMerging.startMerge();

            System.err.println("All Task Completed!" + " : " + DownloadUtils.formatDuration(Duration.ofMillis((System.currentTimeMillis() - startTime))));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    public ExecutorService getExecutor() {
        return Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() - 1);
    }
}
