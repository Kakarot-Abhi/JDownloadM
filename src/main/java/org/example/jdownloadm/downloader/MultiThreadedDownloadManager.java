package org.example.jdownloadm.downloader;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j2;
import org.example.jdownloadm.downloader.ui.MainDownloadTracker;
import org.example.jdownloadm.downloader.ui.PartDownloadTracker;

import java.io.IOException;
import java.net.URISyntaxException;

@Log4j2
public class MultiThreadedDownloadManager extends Application {
    private final static int NUM_THREADS = 5;//Runtime.getRuntime().availableProcessors();

//    private DownloadService downloadService;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Multi-Threaded Download Manager");

        MainDownloadTracker mainDownloadTracker = new MainDownloadTracker();
        PartDownloadTracker partDownloadTracker = new PartDownloadTracker(NUM_THREADS);

        TextField urlInput = new TextField("https://download.microsoft.com/download/8/1/d/81d1f546-f951-45c5-964d-56bdbd758ba4/w2k3sp2_3959_usa_x64fre_spcd.iso");
        urlInput.setPromptText("Enter URL");
        Button downloadButton = getDownloadButton(urlInput, mainDownloadTracker, partDownloadTracker);

        VBox root = new VBox(10);
        root.setPadding(new Insets(20, 20, 20, 20));
        root.getChildren().addAll(new HBox(10, urlInput, downloadButton));

        root.getChildren().add(mainDownloadTracker);
        root.getChildren().add(partDownloadTracker);

        Scene scene = new Scene(root, 500, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Button getDownloadButton(TextField urlInput, MainDownloadTracker mainDownloadTracker, PartDownloadTracker partDownloadTracker) {
        Button downloadButton = new Button("Download");
        downloadButton.setOnAction(e -> {
            try {
                new DownloadService(urlInput.getText(), "src/main/resources", NUM_THREADS, mainDownloadTracker, partDownloadTracker).start();
            } catch (URISyntaxException | IOException ex) {
                throw new RuntimeException(ex);
            }
//            downloadService.start();
        });
        return downloadButton;
    }

}
