package org.example.jdownloadm.downloader.ui;

import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class MainDownloadTracker extends StackPane {

    private Label overalProgressLabel;
    private ProgressBar overAllProgressBar;

    private Label speedLabel;

    private Label elaspedTimeLabel;
    private TextField elaspedTime;
    private Label remainingTimeLabel;
    private TextField remainingTime;

    private Label downloadedLabel;
    private TextField downloaded;

    private Label fileSizeLabel;
    private TextField fileSize;

    public MainDownloadTracker() {
        overalProgressLabel = new Label("Downloading Status");
        overAllProgressBar = new ProgressBar(0);
        overAllProgressBar.setPrefWidth(200);

        speedLabel = new Label("0KB/Sec");

        fileSizeLabel = new Label("File Size");
        fileSize = new TextField("N/A");
        downloadedLabel = new Label("Downloaded");
        downloaded = new TextField("N/A");

        elaspedTimeLabel = new Label("Elasped Time");
        elaspedTime = new TextField("N/A");
        elaspedTime.setPrefWidth(70);
        elaspedTime.setEditable(false);

        remainingTimeLabel = new Label("Remaining Time");
        remainingTime = new TextField("N/A");
        remainingTime.setPrefWidth(70);
        remainingTime.setEditable(false);

        HBox hBox = new HBox(10);
        hBox.getChildren().addAll(elaspedTimeLabel, elaspedTime, remainingTimeLabel, remainingTime);

        VBox vBox = new VBox(10);

        HBox hhBox = new HBox(5);

        hhBox.getChildren().addAll(overAllProgressBar, speedLabel);

        vBox.getChildren().addAll(overalProgressLabel, hhBox, hBox, fileSizeLabel, fileSize, downloadedLabel, downloaded);

        getChildren().add(vBox);
    }

    public void updateProgressBar(double value) {
        javafx.application.Platform.runLater(() -> overAllProgressBar.setProgress(value));
    }

    public void updateElaspedTime(String value) {
        javafx.application.Platform.runLater(() -> elaspedTime.setText(value));
    }

    public void updateRemaingTime(String value) {
        javafx.application.Platform.runLater(() -> remainingTime.setText(value));
    }

    public void updateDownloaded(String value) {
        javafx.application.Platform.runLater(() -> downloaded.setText(value));
    }

    public void updateFileSize(String value) {
        javafx.application.Platform.runLater(() -> fileSize.setText(value));
    }

    public void updateCurrentSpeed(String value) {
        javafx.application.Platform.runLater(() -> speedLabel.setText(value));
    }

}


