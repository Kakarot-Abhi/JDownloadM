package org.example.jdownloadm.downloader;

import lombok.extern.log4j.Log4j2;
import org.example.jdownloadm.downloader.download.DownloadUtils;
import org.example.jdownloadm.downloader.download.ProgressTracker;
import org.example.jdownloadm.downloader.ui.PartDownloadTracker;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

@Log4j2
public class DownloadThread extends Thread implements Cloneable{
    private String fileURL;
    private long startByte;
    private long endByte;
    private String outputFilePath;
    private PartDownloadTracker.partProgress partDownloadTracker;
    private CountDownLatch downLatch;
    private ConcurrentHashMap<Integer, DownloadThread> downloadPartsMap;
    private ProgressTracker progressTracker;
    private String partId;

    public DownloadThread(String outputFilePath, String fileURL, long startByte, long endByte, ProgressTracker progressTracker,
                          PartDownloadTracker.partProgress partDownloadTracker, CountDownLatch downLatch, int id, ConcurrentHashMap<Integer, DownloadThread> downloadPartsMap) {
        this.fileURL = fileURL;
        this.startByte = startByte;
        this.endByte = endByte;
        this.progressTracker = progressTracker;
        this.outputFilePath = outputFilePath;
        this.partDownloadTracker = partDownloadTracker;
        this.downLatch = downLatch;
        this.downloadPartsMap = downloadPartsMap;
        partDownloadTracker.showPartConnection();
        partId = "part_" + id;

        progressTracker.setPartTotal(endByte - startByte);
    }

    public DownloadThread(){

    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        Timer timer = new Timer("ProgressTimer", true);
        try {
            log.info("{} started", partId);
            handlePartProgress handleProgress = new handlePartProgress(partDownloadTracker, progressTracker);

            timer.schedule(handleProgress, 0, 500);

            URL url = new URL(fileURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // Set a longer timeout (in milliseconds)
            connection.setConnectTimeout(30000); // 5 seconds
            connection.setReadTimeout(30000);    // 5 seconds

            connection.setRequestProperty("Range", "bytes=" + startByte + "-" + endByte);

            try (BufferedInputStream in = new BufferedInputStream(connection.getInputStream());
                 FileOutputStream out = new FileOutputStream(outputFilePath)) {

                byte[] buffer = new byte[2048];
                int bytesRead;

                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                    progressTracker.updateTotalReceived(bytesRead);
                }
            }
            timer.cancel();
        } catch (IOException e) {
            partDownloadTracker.enabledRetry();
            timer.cancel();
            log.error("Execption while processing part num {}", outputFilePath, e);
            downloadPartsMap.put(downloadPartsMap.size(), this);
            return;
        }
        log.info("{} finished in {}", partId, DownloadUtils.getElaspedTimeInString(startTime));
        downLatch.countDown();
    }

    @Override
    public DownloadThread clone() {
        DownloadThread clonedThread = new DownloadThread();
        clonedThread.downloadPartsMap = this.downloadPartsMap;
        clonedThread.fileURL = this.fileURL;
        clonedThread.startByte = this.startByte;
        clonedThread.endByte = this.endByte;
        clonedThread.outputFilePath = this.outputFilePath;
        clonedThread.partDownloadTracker = this.partDownloadTracker;
        clonedThread.downLatch = this.downLatch;
        clonedThread.progressTracker = this.progressTracker;
        clonedThread.partId = this.partId;
        return clonedThread;
    }
}

class handlePartProgress extends TimerTask {
    private final PartDownloadTracker.partProgress partProgress;
    private final ProgressTracker progressTracker;

    public handlePartProgress(PartDownloadTracker.partProgress partProgress, ProgressTracker progressTracker) {

        this.partProgress = partProgress;
        this.progressTracker = progressTracker;
    }

    @Override
    public void run() {
        updatePartsStatus();
    }

    private void updatePartsStatus() {
        partProgress.updateDataRecieved(DownloadUtils.humanReadableByteCountSI(progressTracker.getTotalReceived()));
        partProgress.updatePartProgressBar((progressTracker.getTotalReceived() / progressTracker.getPartTotal()));
    }
}