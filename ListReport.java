/*
 * Christopher Burgess
 */
package myabsences;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
//import static myabsences.MyAbsences.myDatabase;
import static myabsences.MyAbsences.year;
import org.json.simple.JSONObject;

/**
 *
 * @author Christopher
 */
//Begin Subclass ListReport
public class ListReport extends Application {
  
    static Stage reportStage = new Stage();
    SimpleDateFormat formatDb = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat formatCal = new SimpleDateFormat("MM/dd/yyyy");

    @Override
    public void start(Stage primaryStage) throws Exception {

        // set Modality if this instance has not yet set the stage visible
        if (!reportStage.isAlwaysOnTop()) {reportStage.initModality(Modality.APPLICATION_MODAL);}
        
        /* Main pane */
        BorderPane bPane = new BorderPane();
        GridPane gPane = new GridPane();
        ScrollPane scrollpane = new ScrollPane();
        
        gPane.setAlignment(Pos.TOP_LEFT);
        gPane.setPadding(new Insets(5, 25, 5, 25));
        bPane.setPadding(new Insets(5, 5, 5, 5));
        gPane.getStyleClass().add("summaryreport");
        gPane.setHgap(20);
        gPane.setVgap(5);
        gPane.setPrefHeight(600);

        // get absence data to arraylist
        ArrayList<JSONObject> absences = Database.getAllAbsences(year); 
        
        int numRows = absences.size()+1;    // add in the header row
        int numCols = 6;  // number of column in report
        
        // Build the Gridpane report from absences arraylist
        String lastDate = "";
        String[] headers = new String[] {"Date   ","Absence Type  ","Title   ","Time   ","Submitted","Notes     "};
        for (int row = 0; row < numRows; row++) {
            boolean multiHours = false;
            for (int col = 0; col < numCols; col++) {  
                Label label = new Label();
                if (row == 0) {
                    label.setText(headers[col]);
                    label.getStyleClass().add("summaryheader");
                }    
                if (row > 0) {
                    
                    switch (col) {  // build the column data for each row
                        case 0:
                            Date date = formatDb.parse((String)(absences.get(row-1).get("Date")));
                            String date2 = formatCal.format(date);
                            if (!lastDate.equals(date2)) {label.setText(date2);}
                            else {label.setText(" "); multiHours = true;}
                            lastDate = date2;
                            break;
                        case 1:
                            label.setText((String)(absences.get(row-1).get("Absence_Type")));
                            String color = (String)absences.get(row-1).get("Color");
                            String cssColor = "type" + color.toLowerCase();
                            label.getStyleClass().add(cssColor);  
                            break;
                        case 2:
                            if (!multiHours) {label.setText((String)(absences.get(row-1).get("Title")));}
                            else {label.setText(" ");}
                            break;
                        case 3:
                            double hours = (double)(absences.get(row-1).get("Hours"));
                            String hoursMin = getHoursMinutes(hours);
                            label.setText(hoursMin);
                            break;
                        case 4:
                            int submit = (Integer)(absences.get(row-1).get("Submitted"));
                            if (multiHours) {label.setText(" ");}
                            else {
                                if (submit == 1) {
                                    label.setText("    Yes");
                                }
                                else if (submit == 0) {
                                    label.setText("    No");
                                    label.getStyleClass().add("typered");
                                }
                            }
                            break;
                        case 5:
                            if (!multiHours) {label.setText((String)(absences.get(row-1).get("Notes")));}
                            else {label.setText(" ");}
                            break;   
                    }
 
                }
                GridPane.setConstraints(label, col, row);
                gPane.getChildren().add(label); 
            }
        }
        
        // Add the gridpane to the scroll pane
        scrollpane.setContent(gPane);
        scrollpane.setMaxHeight(700);
                      
        HBox hBoxB = new HBox();
        Button btnExit = new Button("Exit");
        btnExit.setOnAction(new exitHandler());
        hBoxB.setAlignment(Pos.CENTER);
        btnExit.setMaxWidth(50);
        HBox.setMargin(btnExit, new Insets(5, 5, 5, 5));
        hBoxB.getChildren().addAll(btnExit);

        
        /* set panes in stage and show stage */
        bPane.setCenter(scrollpane);
        bPane.setBottom(hBoxB);
        Scene listReportScene = new Scene(bPane); 
        reportStage.setAlwaysOnTop(true);
        reportStage.setMaxHeight(700);
        reportStage.setMaxWidth(750);
        listReportScene.getStylesheets().add(getClass().getResource("StyleSheet.css").toExternalForm());
        reportStage.setTitle("List View Report");
        reportStage.setScene(listReportScene);
        reportStage.show();        
    }
    
    /* private getHoursMin 
    *
    *
    */
    private String getHoursMinutes (double decimalHours) {
        
        int hours = (int)decimalHours;
        double fractional = decimalHours - hours;
        int minutes = (int)(fractional * 60.0);
        
        String hrsMin = hours + " Hours ";
        if (minutes !=0) {hrsMin = hrsMin + minutes + " Min";}
        
        return hrsMin;

    } // End getHoursMin    
    
    /**
     * Method to close stage and return to main without accepting data
     */
    private static class exitHandler implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            reportStage.close();
        }
    }
    
} //End Subclass ListReport