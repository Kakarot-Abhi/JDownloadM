package org.example.jdownloadm.downloader.download;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class PartDownload implements Runnable {
    private String fileURL;
    private long startByte;
    private long endByte;
    private String outputFilePath;

    public String id;

    private long total;

    private ProgressTracker progressTracker;

    public PartDownload(String fileURL, long startByte, long endByte, String outputFilePath, ProgressTracker progressTracker) {
        this.fileURL = fileURL;
        this.startByte = startByte;
        this.endByte = endByte;
        this.outputFilePath = outputFilePath;
        this.progressTracker = progressTracker;
    }

    @Override
    public void run() {
        try {
            System.err.println("started " + outputFilePath );
            URL url = new URL(fileURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Range", "bytes=" + startByte + "-" + endByte);

            try (BufferedInputStream in = new BufferedInputStream(connection.getInputStream());
                 FileOutputStream out = new FileOutputStream(outputFilePath)) {

                byte[] buffer = new byte[1024];
                int bytesRead;

                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                    total+=bytesRead;
//                    String format = String.format("Total : %s, Thread = %s, Out=%s", Main.humanReadableByteCountSI(total), Thread.currentThread().getName(), outputFilePath);
//                    System.err.println(format);
                    progressTracker.updateTotalReceived(bytesRead);
                }
                System.err.println("ended " + outputFilePath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
