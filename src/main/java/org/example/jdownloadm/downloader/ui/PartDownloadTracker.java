package org.example.jdownloadm.downloader.ui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PartDownloadTracker extends StackPane {

    private Label availableConnectionsLabel;
    private ArrayList<partProgress> partsProgressBars;
    private static final List<String> GRADIENT_COLORS = Arrays.asList(
            "#FF0000", "#FF3300", "#FF6600", "#FF9900", "#FFCC00",
            "#FFFF00", "#CCFF00", "#99FF00", "#66FF00", "#33FF00", "#00FF00"
    );

    public PartDownloadTracker(int numThreads) {
        availableConnectionsLabel = new Label("Available Connections");

        partsProgressBars = new ArrayList<>(numThreads);

        HBox vBox = new HBox();
        vBox.setSpacing(0);

        for (int i = 0; i < numThreads; i++) {
            partProgress partProgress = new partProgress("Connection " + (i + 1), i);
            partsProgressBars.add(partProgress);
            vBox.getChildren().add(partProgress);
        }

        VBox box = new VBox(10);
        box.getChildren().addAll(availableConnectionsLabel, vBox);

        getChildren().add(box);
    }

    public partProgress getPartProgres(int index) {
        return partsProgressBars.get(index);
    }

    public class partProgress extends VBox {

        private Label connectionLabel;
        private ProgressBar partProgressBar;

        private TextField dataReceived;

        private Button retry;

        public partProgress(String connectionName, int i) {
            setVisible(false);
            connectionLabel = new Label(connectionName);
            connectionLabel.setPrefWidth(120);
            connectionLabel.setVisible(false);
            partProgressBar = new ProgressBar(0);
            partProgressBar.setPrefWidth(70);
            String color = GRADIENT_COLORS.get(i % GRADIENT_COLORS.size());
            partProgressBar.setStyle("-fx-accent: " + color + ";");
            dataReceived = new TextField("0");
            dataReceived.setPrefWidth(30);
            dataReceived.setEditable(false);
            dataReceived.setVisible(true);
            retry = new Button("Retry");
            retry.setVisible(false);

            setSpacing(0);
            getChildren().addAll(connectionLabel, partProgressBar, dataReceived, retry);
        }

        public void reset(){
            updatePartProgressBar(0);
            updateDataRecieved("0KB");
        }

        public void updatePartProgressBar(double value) {
            javafx.application.Platform.runLater(() -> partProgressBar.setProgress(value));
        }

        public void updateDataRecieved(String value) {
            javafx.application.Platform.runLater(() -> {
                dataReceived.setText(value);
            });
        }

        public void setRetryHandler(EventHandler<ActionEvent> actionEventEventHandler){
            retry.setOnAction(actionEventEventHandler);
        }

        public void enabledRetry() {
            retry.setVisible(true);
//            EventHandler<ActionEvent> actionEventEventHandler = rr -> {
//
//            };
//            retry.setOnAction(actionEventEventHandler);
        }

        public void showPartConnection() {
            setVisible(true);
        }
    }

}