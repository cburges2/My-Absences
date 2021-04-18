/*
 * Christopher Burgess
 */
package myabsences;

import javafx.scene.control.CheckBox;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.json.simple.JSONObject;

/**
 * Class Settings
 * @author Christopher Burgess
 * This class provides a form to change the settings for the user's work schedule
 * and to change the look-ahead time for the Max Accrual warning. 
 */
public class Settings extends Application {
       
    /* Instantiate new stage object */
    static Stage settingsStage = new Stage();
    
    JSONObject settings;    // settings from db
    double hoursInDay = 0;  // number of hours in a workday
    int workWeekends = 0;   // flag for if work week includes weekends
    double daysInWeek = 0;  // number of work days in a week
    int maxWarningDays = 0; // max days to warn about Max Accrual day

    // text for title
    Text settingsTitle = new Text("Settings");
    
    // labels
    Label lblHoursInDay = new Label("Hours in Day");
    Label lblDaysInWeek = new Label("Days in Week");
    Label lblMaxWarning = new Label("Months for Max Warning");
    
    // checkbox for work weekends
    CheckBox ckbWeekends = new CheckBox("Work Weekends");
    
    // comboboxes
    ComboBox<Double> cboHoursInDay = new ComboBox();
    ComboBox<Double> cboDaysInWeek = new ComboBox();
    ComboBox <Integer> cboMaxWarning = new ComboBox();

    // buttons
    Button btnSettingsCancel = new Button("Cancel");
    Button btnSettingsUpdate = new Button("Update");
    
    // Gridpane for the form
    GridPane gPane = new GridPane();
      
@Override
    public void start(Stage primaryStage) throws Exception {
        
        // set Modality if form object has not yet set the stage visible
        if (!settingsStage.isAlwaysOnTop()) {settingsStage.initModality(Modality.APPLICATION_MODAL);}
        
        // get Settings object from db
        settings = Database.getSettings();
        
        // get settings values from settings object
        hoursInDay = (double)settings.get("Hours_In_Day");
        daysInWeek = (double)settings.get("Days_In_Week");        
        workWeekends = (int)settings.get("Work_Weekends");
        maxWarningDays = (int)settings.get("Max_Warning_Days");
        
        /* create panes */
        BorderPane bPane = new BorderPane();
        bPane.setPadding(new Insets(5, 5, 5, 5));
        HBox topPane = new HBox();
        
        HBox hBoxB = new HBox();
        
        // set title top pane
        topPane.setAlignment(Pos.TOP_CENTER);
        topPane.setPadding(new Insets(5, 5, 5, 5));
        topPane.getStyleClass().add("formtop");
        topPane.setPrefHeight(35);     
        
        // set form gridpane
        gPane.setAlignment(Pos.TOP_LEFT);
        gPane.setPadding(new Insets(20, 20, 20, 20));
        gPane.getStyleClass().add("setuppane");
        gPane.setHgap(5);
        gPane.setVgap(5);
        
        // set bottom buttons pane        
        hBoxB.setAlignment(Pos.CENTER);
        hBoxB.setSpacing(50);
        hBoxB.setPrefHeight(35);
        
        // Set Title in topPane
        settingsTitle.getStyleClass().add("daytitle");
        topPane.getChildren().add(settingsTitle);   
        
        // Set Buttons in bottom pane
        hBoxB.getChildren().addAll(btnSettingsUpdate,btnSettingsCancel);
        
        // populate cboHoursInDay
        for (double i = 1; i < 12.5; i+=.5) {
            cboHoursInDay.getItems().add(i);
        }
        
        // populate cboDaysInWeek
        for (double i = 1; i < 7.5; i+=.5) {
            cboDaysInWeek.getItems().add(i);
        }    
        
        // populate cboMaxWarning (months)
        for (int i = 1; i < 13; i++) {
            cboMaxWarning.getItems().add(i);
        }        

        // Set focus on Update button
        Platform.runLater(() -> {
            btnSettingsUpdate.requestFocus();  // Set focus on cancel 
        });
   
        // Add controls to the GPane and put DB values in controls
        addControls();
        putValues();
        
        // ************* Control Handlers ***************
            
        // Update button handler
        btnSettingsUpdate.setOnAction(e-> {
            try {
                getValues();
                updateSettings();
                settingsStage.close(); 
                MyAbsences.refresh();
            } catch (Exception update) {
                ErrorHandler.exception(update, "updating settings");
            }
        });  
        
        // cancel button handler
        btnSettingsCancel.setOnAction(e-> {
            try {
                settingsStage.close(); 
            } catch (Exception cancel) {
                ErrorHandler.exception(cancel, "cancelling the form");
            }
        });    

        /* set panes in stage and show stage */
        bPane.setTop(topPane);
        bPane.setCenter(gPane);
        bPane.setBottom(hBoxB);
        Scene setupScene = new Scene(bPane);
        setupScene.getStylesheets().add(getClass().getResource("StyleSheet.css").toExternalForm());
        settingsStage.setAlwaysOnTop(true);
        settingsStage.setMaxHeight(700);
        settingsStage.setMinHeight(300);
        settingsStage.setMinWidth(350);
        settingsStage.setTitle("Settings");
        settingsStage.setScene(setupScene);
        settingsStage.show();
        
    } // End Start
    
    /* private addControls
     *
     * adds the controls to the gridpane    */
    private void addControls() {

        // add label for hours in day
        GridPane.setConstraints(lblHoursInDay, 1, 1); 
        GridPane.setColumnSpan(lblHoursInDay, 4);
        gPane.getChildren().add(lblHoursInDay);

        // add combobox for hours in day
        GridPane.setConstraints(cboHoursInDay, 10, 1); 
        GridPane.setColumnSpan(cboHoursInDay, 4);
        gPane.getChildren().add(cboHoursInDay);

        // add label for days in week
        GridPane.setConstraints(lblDaysInWeek, 1, 2); 
        GridPane.setColumnSpan(lblDaysInWeek, 4);
        gPane.getChildren().add(lblDaysInWeek);

        // add combobox for days in week
        GridPane.setConstraints(cboDaysInWeek, 10, 2); 
        GridPane.setColumnSpan(cboDaysInWeek, 4);
        gPane.getChildren().add(cboDaysInWeek);
        
        // add label for max warning
        GridPane.setConstraints(lblMaxWarning, 1, 3); 
        GridPane.setColumnSpan(lblMaxWarning, 4);
        gPane.getChildren().add(lblMaxWarning);

        // add combobox for max warning
        GridPane.setConstraints(cboMaxWarning, 10, 3); 
        GridPane.setColumnSpan(cboMaxWarning, 4);
        gPane.getChildren().add(cboMaxWarning);        
        
        // add checkBox for work weekends
        GridPane.setConstraints(ckbWeekends, 1, 4); 
        GridPane.setColumnSpan(ckbWeekends, 4);
        gPane.getChildren().add(ckbWeekends);        

    } // end addControls
    
    /* private putValues
    *
    * This puts the values from the database into the controls 
    * Allows user to then edit existing data. */
    private void putValues() {
        
        // set data in the controls that was already saved 
        cboHoursInDay.setValue(hoursInDay);
        cboDaysInWeek.setValue(daysInWeek);
        
        int maxWarnMonths = maxWarningDays/30; // convert days to months
        cboMaxWarning.setValue(maxWarnMonths);
        
        if (workWeekends == 1) {ckbWeekends.setSelected(true);}
        else {ckbWeekends.setSelected(false);}
        
    } // end putValues
    
    /* private getValues
    * 
    * This method gets the values from the controls to variables, 
    * to update the Settings db table    */
    private void getValues() {
        
        hoursInDay = cboHoursInDay.getValue();
        daysInWeek = cboDaysInWeek.getValue();
        maxWarningDays = cboMaxWarning.getValue() * 30;
        System.out.println("maxWarningDays is " + maxWarningDays);
        
        if (ckbWeekends.isSelected()) {workWeekends = 1;}
        else {workWeekends = 0;}

    } // end getValues
    
    /* private updateSettings
     *
     * Updates the Settings table with values from the variables */
    private void updateSettings () {

        // absence ID in blances - update
        String sql = "UPDATE Settings " +
        "SET Hours_In_Day = '" + hoursInDay + "', Days_In_Week = '" + daysInWeek +
        "', Work_Weekends = '" + workWeekends + "', Max_Warning_Days = '" + maxWarningDays + "'";
        Database.SQLUpdate(sql);

    } // end method updateSettings    

} // end class Settings
