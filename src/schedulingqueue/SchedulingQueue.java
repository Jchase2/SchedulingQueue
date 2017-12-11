// Author: James Chase II
// CSC 331 -- Operating Systems.
// Description: Scheduling Queue simulation, shortest job first. 
// File: SchedulingQueue.java
package schedulingqueue;

// Imports. 
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SchedulingQueue extends Application {

    int processCounter = 0; // Number of processes created (for naming). 
    int finProcess = 0; // Number of processes that have finished. 

    // Creates a process object with random interval as execution time. 
    // This simulates a process. 
    public class AProcess {
        Random rand = new Random();
        int execTime = rand.nextInt(5000) + 200;
        String execTimeString = "Burst Time: " + Double.toString(execTime / 1000.0);
        String name;
        long mytime = -1; // Time since put on queue. Value given later. 
        long processedTime = 0; // Amount of time spent processing. Incremented later. 
    }

    // This is a "comparator" class and method that helps sort processes by execTime later.
    public class ProcessSort implements Comparator<AProcess> {

        @Override
        public int compare(AProcess one, AProcess another) {
            int returnVal = 0;
            if (one.execTime < another.execTime) {
                returnVal = -1;
            } else if (one.execTime > another.execTime) {
                returnVal = 1;
            } else if (one.execTime == another.execTime) {
                returnVal = 0;
            }
            return returnVal;
        }
    }

    @Override
    public void start(Stage primaryStage) {
        // Create an ArrayList populated by objects created by clicking. 
        ArrayList<AProcess> objList = new ArrayList<>();

        // Add process button. 
        Button btn = new Button();
        btn.setText("Add a Process");
        btn.setLayoutX(600);
        btn.setLayoutY(100);
        // Lambda expression instead of anonymous inner class. 
        btn.setOnAction((ActionEvent event) -> {
            processCounter++; // Used for naming the process. 
            AProcess nProcess = new AProcess(); // Create a process object.
            nProcess.name = "Process " + processCounter; // Name the process.
            nProcess.mytime = System.currentTimeMillis(); // Give it initialization time.
            objList.add(nProcess); // Add object ref to objList. 
        });

        // Creates listView and Hbox objects.
        // HBox holds both ListViews horizontally. 
        // Each listview is a List, queueRep => process names, 
        // runtimeRep => Execution time of the process (randomly given). 
        ListView queueRep = new ListView();
        ListView runtimeRep = new ListView();
        HBox queueHbox = new HBox(queueRep, runtimeRep);

        // Creates a bnch of text objects for display.. 
        Text totalTimerText = new Text();
        Text processingTimerText = new Text();
        Text keyText = new Text();
        Text finishedProcesses = new Text();
        // Specify the location for all the text objects. . 
        totalTimerText.setX(600);
        totalTimerText.setY(80);
        processingTimerText.setX(600);
        processingTimerText.setY(60);
        keyText.setX(600);
        keyText.setY(40);
        keyText.setText("Key: One second = One Milisecond");
        finishedProcesses.setX(600);
        finishedProcesses.setY(20);

        // Get time since we start running the "cpu". 
        long sTime = System.currentTimeMillis();

        // This is the recommended way using javaFX to do something every x miliseconds. 
        Timeline timeline = new Timeline(new KeyFrame(
                Duration.millis(1000),
                ev -> {
                    // Get readable total time since we started. 
                    // Then set the text field value. 
                    Long totalTime = (System.currentTimeMillis() - sTime);
                    totalTimerText.setText("Total Time: " + Double.toString(totalTime / 1000.0));

                    // Only perform these operations when there's something to process. 
                    if (objList.size() > 0) {
                        // On every iteration of this timeline, 
                        // add 1000 ms's to the 0th objects processedTime field.
                        objList.get(0).processedTime = objList.get(0).processedTime + 1000;
                        // Get readable time since added to queue. 
                        Long finTime = (objList.get(0).mytime - sTime);
                        // Set text for current processing time.
                        processingTimerText.setText("Current Job Processing Time: "
                                + Double.toString(objList.get(0).processedTime / 1000.0));
                        // Clear the Lists so we can repopulate them.
                        // This is the easiest / only way I've found to refresh the list :/
                        /// Most other methods seem Android specific. 
                        queueRep.getItems().clear();
                        runtimeRep.getItems().clear();
                        // If a processes processed time is >= execution time, it's done. Remove it.
                        if (objList.get(0).processedTime >= objList.get(0).execTime) {
                            finProcess++;
                            finishedProcesses.setText("Finished Processes: " + Integer.toString(finProcess));
                            objList.remove(0);
                            // Sort the list of objects in the queue. 
                            // Since shortest job first is non-preemptive, can only 
                            // sort when the job in position 0 is finished. 
                            Collections.sort(objList, new ProcessSort());
                        }
                        // Read through object list and add everything back. 
                        for (int i = 0; i < objList.size(); i++) {
                            queueRep.getItems().add(objList.get(i).name);
                            runtimeRep.getItems().add(objList.get(i).execTimeString);
                        }
                    }
                }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

        //Creating a Group object  
        // This generates a "group" of children added to the scene. 
        Group root = new Group();
        root.getChildren().addAll(queueHbox, btn, totalTimerText, processingTimerText, keyText, finishedProcesses);
        Scene scene = new Scene(root, 900, 500);

        // Scene -- The application window. 
        primaryStage.setTitle("Scheduling Queue - SJF");
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
