package org.example.jdownloadm.downloader.download;

public class ProgressTracker {
    private long totalReceived;
    private String id;

    private double partTotal;

    public void setTotalReceived(long totalReceived) {
        this.totalReceived = totalReceived;
    }

    public double getPartTotal() {
        return partTotal;
    }

    public void setPartTotal(double partTotal) {
        this.partTotal = partTotal;
    }

    public double getDownloaded() {
        return downloaded;
    }

    public void setDownloaded(double downloaded) {
        this.downloaded = downloaded;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    private double downloaded;
    private double percentage;

    public ProgressTracker(String id) {
        this.id = id;
    }

    public long getTotalReceived() {
        return totalReceived;
    }

    public void updateTotalReceived(long totalReceived) {
        this.totalReceived += totalReceived;

        percentage = totalReceived/partTotal * 100;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
