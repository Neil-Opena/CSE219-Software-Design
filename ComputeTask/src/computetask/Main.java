package computetask;


import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * This class demonstrates the use of javafx.concurrent.Task to perform a
 * long-running compute tasks, with partial results supplied to the GUI
 * at specified intervals.
 * 
 * @author E. Stark
 * @version 20180330
 */

public class Main extends Application {
    
    private ComputeTask<?> task;
   
    private Label statusLabel;
    private Label progressLabel;

    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        HBox buttonBox = new HBox();
        root.setTop(buttonBox);
        
        HBox statusPane = new HBox();
        root.setBottom(statusPane);
        
        statusLabel = new CustomLabel();
        statusPane.getChildren().add(statusLabel);
        
        progressLabel = new CustomLabel();
        statusPane.getChildren().add(progressLabel);
    
        Button piBtn = new Button();
        piBtn.setText("Pi");
        piBtn.setOnAction(e -> {
            if(task != null)
                task.cancel();
            task = new PiTask();
            final CustomLabel currentValueLabel = new CustomLabel();
            root.setCenter(currentValueLabel);
            primaryStage.sizeToScene();
            task.getPartialResultProperty().addListener
                ((obs, os, ns) -> currentValueLabel.setText(ns.toString()));
            task.messageProperty().addListener((obs, ov, nv) -> statusLabel.setText(nv));
            task.progressProperty().addListener
                ((obs, ov, nv) -> progressLabel.setText(((int)(nv.doubleValue() * 100)) + "% complete"));
               
            Thread t = new Thread(task);
            t.setDaemon(true);
            t.start();
        });
        buttonBox.getChildren().add(piBtn);
        
        Button cancelBtn = new Button();
        cancelBtn.setText("Cancel");
        cancelBtn.setOnAction(e -> cancelTask());
        buttonBox.getChildren().add(cancelBtn);
        
        Scene scene = new Scene(root);
        primaryStage.setTitle("Compute Task Demo");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    /**
     * Customization of Label that sets a preferred size.
     */
    private class CustomLabel extends Label {
        public CustomLabel() {
            setPrefWidth(200);
            setPrefHeight(50);
        }
    }
    
    /**
     * Cancel a task, if any is running.
     */
 
    private void cancelTask() {
        if(task != null) {
            task.cancel();
            task = null;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
    
}
