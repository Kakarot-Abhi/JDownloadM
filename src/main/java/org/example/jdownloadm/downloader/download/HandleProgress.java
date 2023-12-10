package org.example.jdownloadm.downloader.download;

import org.example.jdownloadm.downloader.ui.MainDownloadTracker;

import java.time.Duration;
import java.util.List;
import java.util.TimerTask;


public class HandleProgress extends TimerTask {
    private List<ProgressTracker> progressTrackers;
    private long startTime;
    private long fileSize;
    private final MainDownloadTracker mainDownloadTracker;

    public HandleProgress(long fileSize, List<ProgressTracker> progressTrackers, long startTime, MainDownloadTracker mainDownloadTracker) {
        this.progressTrackers = progressTrackers;
        this.startTime = startTime;
        this.fileSize = fileSize;
        this.mainDownloadTracker = mainDownloadTracker;
    }

    public void handleProgrees(long fileSize, List<ProgressTracker> progressTrackers, long startTime) {
        long totalBytesRecieved = progressTrackers
                .stream()
                .mapToLong(ProgressTracker::getTotalReceived).sum();
        double downloadedFraction = (double) totalBytesRecieved / (double) fileSize;
        int elapsedTime = (int) (System.currentTimeMillis() - startTime);
        long downloadSpeed = (long) ((double) totalBytesRecieved / elapsedTime * 1000); // Bytes per second

        Duration elapsedDuration = Duration.ofMillis(elapsedTime);
        String totalElapsed = DownloadUtils.formatDuration(elapsedDuration);

        String remainingTimeString = "N/A";
        if (downloadSpeed > 0) {
            long remainingTime = (fileSize - totalBytesRecieved) / downloadSpeed;

            Duration remainingDuration = Duration.ofSeconds(remainingTime);
            remainingTimeString = DownloadUtils.formatDuration(remainingDuration);
        }

        mainDownloadTracker.updateElaspedTime(totalElapsed);
        mainDownloadTracker.updateRemaingTime(remainingTimeString);
        mainDownloadTracker.updateProgressBar(downloadedFraction);
        mainDownloadTracker.updateDownloaded(DownloadUtils.humanReadableByteCountSI(totalBytesRecieved));
        mainDownloadTracker.updateCurrentSpeed(DownloadUtils.humanReadableByteCountSI(downloadSpeed) + "/sec");
    }

    @Override
    public void run() {
        handleProgrees(fileSize, progressTrackers, startTime);
    }

}
