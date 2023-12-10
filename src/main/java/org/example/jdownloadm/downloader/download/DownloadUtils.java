package org.example.jdownloadm.downloader.download;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.time.Duration;

public class DownloadUtils {
    public static long getFileSize(String dowloadUrl) throws IOException {
        URL url = new URL(dowloadUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        long fileSize = connection.getContentLengthLong();
        if(fileSize <= 0)
            throw new RuntimeException("Not A valid file or size cannot be determine");
        System.err.println("File size : " + humanReadableByteCountSI(fileSize));
        return fileSize;
    }

    public static String humanReadableByteCountSI(long bytes) {
        if (-1000 < bytes && bytes < 1000) {
            return bytes + " B";
        }
        CharacterIterator ci = new StringCharacterIterator("kMGTPE");
        while (bytes <= -999_950 || bytes >= 999_950) {
            bytes /= 1000;
            ci.next();
        }
        return String.format("%.1f %cB", bytes / 1000.0, ci.current());
    }

    public static String getFilePartName(int i, String basePath, String name) {
        return new File(basePath, name + "_part" + i).getAbsolutePath();
    }

    public static String getFileNameFromUrl(String url) throws URISyntaxException {
        URI uri = new URI(url);

        // Get the path part of the URL
        String path = uri.getPath();

        // Use Paths to extract the file name
        Path fileNamePath = FileSystems.getDefault().getPath(path).getFileName();
        return fileNamePath.toString();
    }

    public static String formatDuration(Duration duration) {
        long seconds = duration.getSeconds();
        long absSeconds = Math.abs(seconds);
        String positive = String.format(
                "%02d:%02d:%02d",
                absSeconds / 3600,
                (absSeconds % 3600) / 60,
                absSeconds % 60);

        return seconds < 0 ? "-" + positive : positive;
    }

    public static String getElaspedTimeInString(long startTime) {
        Duration duration = Duration.ofMillis((System.currentTimeMillis() - startTime));
        long seconds = duration.getSeconds();
        long absSeconds = Math.abs(seconds);
        String positive = String.format(
                "%02d:%02d:%02d",
                absSeconds / 3600,
                (absSeconds % 3600) / 60,
                absSeconds % 60);

        return seconds < 0 ? "-" + positive : positive;
    }
}
