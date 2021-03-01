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
import javafx.stage.Stage;
import static myabsences.MyAbsences.year;
import org.json.simple.JSONObject;

/**
 *
 * @author Christopher
 */
//Begin Subclass ListReport
public class SetupForm extends Application {
  
    /* Instantiate new stage object */
    static Stage setupStage = new Stage();
    static Database myDatabase = new Database();       // create Database instance
    SimpleDateFormat formatDb = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat formatCal = new SimpleDateFormat("MM/dd/yyyy");

    /* Declare fields and buttons to be displayed */

    @Override
    public void start(Stage primaryStage) throws Exception {

        /* Main pane */
        BorderPane bPane = new BorderPane();
        GridPane gPane = new GridPane();
        ScrollPane scrollpane = new ScrollPane();
        
        gPane.setAlignment(Pos.TOP_LEFT);
        gPane.setPadding(new Insets(5, 5, 5, 5));
        bPane.setPadding(new Insets(5, 5, 5, 5));
        gPane.getStyleClass().add("summaryreport");
        gPane.setHgap(20);
        gPane.setVgap(5);
        gPane.setPrefHeight(300);

        
        //scrollpane.hbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.NEVER);
        scrollpane.setContent(gPane);
                      
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
        setupStage.setMaxHeight(700);
        setupStage.setMinHeight(300);
        setupStage.setMinWidth(300);
        setupStage.setMaxWidth(650);
        listReportScene.getStylesheets().add(getClass().getResource("StyleSheet.css").toExternalForm());
        setupStage.setTitle("Setup");
        setupStage.setScene(listReportScene);
        setupStage.show();        
    }
    
    /* private getHoursBreakdown
    *
    */
    private String getHoursBreakdown(String h) {
        
        double hrs = Double.valueOf(h);
        
        double ww = hrs/40;    // work week
        double weeks = Math.floor(ww);
        double days = (int)(5*((ww)-(int)(ww)));
        double hours = Math.floor(8*(5*((ww)-(int)(ww))-days));
        double min = Math.round((60*((8*(5*((ww)-(int)(ww))-days))-(int)(8*(5*((ww)-(int)(ww))-days)))));
        
        if (hours == 1 && min == 60) {hours = 2; min = 0;}
        
        String breakDown = "";
        if ((int)weeks != 0) {breakDown = breakDown + (int)weeks + " Weeks ";}      
        if ((int)days != 0) {breakDown = breakDown + (int)days + " Days ";}
        if ((int)hours != 0) {breakDown = breakDown + (int)hours + " Hours ";}
        if ((int)min != 0) {breakDown = breakDown + (int)min + " Min ";}
        
        if (h.equals("0")) {breakDown = "0";}
        
        return breakDown;
    } // End getHoursBreakdown


    /**
     * Method to close stage and return to main without accepting data
     */
    private static class exitHandler implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            setupStage.close();
        }
    }


    

} //End Subclass SetupForm