/*
 * Christopher Burgess
* SDEV 435
 */
package myabsences;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.json.simple.JSONObject;

/**
 *
 * @author Christopher
 */
//Begin Subclass ListReport
public class BalancesForm extends Application {
    
    SimpleDateFormat formatDb = new SimpleDateFormat("yyyy-MM-dd"); // db format 
    SimpleDateFormat formatDp = new SimpleDateFormat("M/d/yyyy");  // date picker format
    
    DateTimeFormatter format = DateTimeFormatter.ofPattern("d/M/yyyy");
    
    /* Instantiate new stage object */
    static Stage startBalanceStage = new Stage();
    ArrayList<JSONObject> absenceTypes = new ArrayList<>();
    ArrayList<JSONObject> startBalances = new ArrayList<>();
    
    int typeSize = 0;      // size of absence types
    int balancesSize = 0;  // number of balances in table for this year
    int year = 0;      // current year. 
    int controlCount = 0;
    boolean prePopulate = false;
    
    // text fields for entering start balance
    TextField[] tfTypeBalance = new TextField[6];   // up to 6 absence types (balances)
    
    // text for title
    Text balancesTitle = new Text("Start Balances");
    
    // labels
    Label[] lblAbsenceName = new Label[6];
    
    // buttons
    Button btnBalancesCancel = new Button("Cancel");
    Button btnBalancesUpdate = new Button("Update");
    Button btnBalancesSave = new Button("Save");

    String[] startingBalances;
    
    GridPane gPane = new GridPane();
    
    DatePicker[] datePicker; 
    
    /* Constructor */
    public BalancesForm() {
        
        // Set Current Year for balances
        final SimpleDateFormat FORMAT_YEAR = new SimpleDateFormat("yyyy");       
        year = Integer.parseInt(FORMAT_YEAR.format(Calendar.getInstance().getTime())); // current year
        
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        
        // set Modality if form object has not yet set the stage visible
        if (!startBalanceStage.isAlwaysOnTop()) {startBalanceStage.initModality(Modality.APPLICATION_MODAL);}
        
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
        
        // get data from db
        absenceTypes = Database.getAbsenceTypes();
        startBalances = Database.getStartBalances(String.valueOf(year));
        
        // get sizes of db data
        typeSize = absenceTypes.size();
        balancesSize = startBalances.size();
        
        // set start Balances data array
        startingBalances = new String[typeSize];
        datePicker = new DatePicker[typeSize]; 

        // set to prepopulate
        if (balancesSize > 0) {
            prePopulate = true;
        }  
        
        // set label names as the absence type name
        for (int i = 0; i < typeSize; i++) {
            lblAbsenceName[i] = new Label((String)absenceTypes.get(i).get("Absence_Type"));
            String colorStyle = "type" + ((String)absenceTypes.get(i).get("Color")).toLowerCase();
            lblAbsenceName[i].getStyleClass().add(colorStyle);
        }
        
        // set Textfields for entering the balances
        for (int i = 0; i < typeSize; i++) {
            tfTypeBalance[i] = new TextField();
            tfTypeBalance[i].setMaxWidth(75);
        }

        // determine what buttons to add to bottom hbox
        if (prePopulate) {
            hBoxB.getChildren().addAll(btnBalancesUpdate,btnBalancesCancel);
            Platform.runLater(() -> {
                btnBalancesCancel.requestFocus();  // Set focus on cancel if prepopulated
            });
        } else {
            hBoxB.getChildren().addAll(btnBalancesSave,btnBalancesCancel);
        }       
        
        addControls();
        
        // ************* Control Handlers ***************
        
        // Save Button event Handler
        btnBalancesSave.setOnAction(e-> {
            try {
              if (getValues()) {
                insertBalances();
                Warnings.removeWarning(0, "ENTER_BALANCES");
                MyAbsences.refresh();
                startBalanceStage.close();                  
              } 
            } catch (Exception save) {
                ErrorHandler.exception(save, "saving the balances");
            }
        });
        
        // Set Title in topPane
        balancesTitle.getStyleClass().add("daytitle");
        topPane.getChildren().add(balancesTitle);        
        
        // Update button handler
        btnBalancesUpdate.setOnAction(e-> {
            try {
                if (getValues()) {
                    updateBalances();
                    startBalanceStage.close(); 
                    MyAbsences.refresh();
                }
            } catch (Exception update) {
                ErrorHandler.exception(update, "updating the balances");
            }
        });  
        
        // cancel button handler
        btnBalancesCancel.setOnAction(e-> {
            try {
                startBalanceStage.close(); 
            } catch (Exception cancel) {
                ErrorHandler.exception(cancel, "cancelling the form");
            }
        });    

        /* set panes in stage and show stage */
        bPane.setTop(topPane);
        bPane.setCenter(gPane);
        bPane.setBottom(hBoxB);
        Scene startBalancesScene = new Scene(bPane);
        startBalancesScene.getStylesheets().add(getClass().getResource("StyleSheet.css").toExternalForm());
        startBalanceStage.setAlwaysOnTop(true);
        startBalanceStage.setMaxHeight(700);
        startBalanceStage.setMaxHeight(550);
        startBalanceStage.setMinWidth(500);
        startBalanceStage.setTitle("Enter Starting Balances");
        startBalanceStage.setScene(startBalancesScene);
        startBalanceStage.show();
    } 
    
    /* private addControls
     *
     * adds the controls to the gridpane    */
    private void addControls() {
        
        controlCount = 0;  // number of controls put in gridpane (no Add-In hours)
        for (int i = 0; i < typeSize; i++) {
            
            double arate = (Double)absenceTypes.get(i).get("Accrual_Rate");
            
            // Do not add add-in types
            if (arate != -1) {
                // add labels
                GridPane.setConstraints(lblAbsenceName[i], 1, i); 
                GridPane.setColumnSpan(lblAbsenceName[i], 4);
                gPane.getChildren().add(lblAbsenceName[i]);

                // add textfields
                GridPane.setConstraints(tfTypeBalance[i], 5, i); 
                GridPane.setColumnSpan(tfTypeBalance[i], 4);
                gPane.getChildren().add(tfTypeBalance[i]);
                controlCount++;
            }

            // add on date label and a datepicker for accrued types
            if (arate > 0) {  //&& !prePopulate
                Label onDate = new Label("On Date: ");
                GridPane.setConstraints(onDate, 9, i); 
                GridPane.setColumnSpan(onDate, 4);
                gPane.getChildren().add(onDate);
                datePicker[i] = new DatePicker();
                datePicker[i].setMaxWidth(100);
                String janOne = "01/01/" + year; // current year
                LocalDate date = LocalDate.parse(janOne,format);
                datePicker[i].setValue(date);
                
                ///calDate = formatDp.format(date);    // use cal format date for creating the warning
                GridPane.setConstraints(datePicker[i], 13, i); 
                GridPane.setColumnSpan(datePicker[i], 4);
                gPane.getChildren().add(datePicker[i]);
                controlCount++;
            }
        }
        
        if (prePopulate) {putValues();}
    }
    
    /* private putValues
    *
    * This puts the values from the database into the controls if balances have
    * already been entered. Allows user to then edit existing data. */
    private void putValues() {
        
        // set data in the controls that was already saved 
        for (int i = 0; i < typeSize; i++) {
            String absenceName = (String)absenceTypes.get(i).get("Absence_Type");
            double balance = JsonMatch.getJsonDouble(startBalances,"Absence_Type",absenceName,"Starting_Balance");
            tfTypeBalance[i].setText(String.valueOf(balance));        
        }       
        
    } // end putValues
    
    /* private getValues
    *
    * ==> true if validated
    * 
    * This method gets the values from the controls to variable arrays, to insert into
    * or update the db table Starting_Balances     */
    private boolean getValues() {
        
        boolean validated = false;
        
        // Get the validated control values into the variable arrays
        for (int i = 0; i < typeSize; i++) {  
            double arate = (Double)absenceTypes.get(i).get("Accrual_Rate");
            // validate if not a holiday
            if (arate == -1) {startingBalances[i] = "0";}  // set add-in types to zero
            if (arate != -1) {                   
                if (Validate.isPosDecimal((lblAbsenceName[i].getText() + " balance"),tfTypeBalance[i].getText(),500)) {
                   validated = true;   
                   // calculate accrued from date else use entered value
                   if (arate > 0) {startingBalances[i] = String.valueOf(calcAccruedStart(i, arate)); }
                   if (arate == 0){startingBalances[i] = tfTypeBalance[i].getText();}      
                } else {                    // balance failed for not a positive decimal
                    validated = false;
                    setFieldRed(i);
                    i = typeSize;           // stop validating additonal types
                }
            } 
        }
        
        return validated;

    } // end getValues
    
    /* private calcAccruedStart
     *
     * index - index in array of accrued type
     * rate - Accural rate for the accured type
     *
     * Example: calcAccruedStart(1, 1.052) (date in control 1/2/21)
     * ==> .526
     *
     * Returns the starting balance for an accurued type on Jan 1st based on a
     * later balance at a later date, based on the accrual rate */
    private double calcAccruedStart(int index, double rate) {
        
        // get entered value for starting balance
        double balanceValue = Double.valueOf(tfTypeBalance[index].getText());      

        // get chosen date from the control
        LocalDate targetDate = datePicker[index].getValue();     
                
        // calculate days from Jan 1st to date in control
	LocalDate dateBefore = LocalDate.of(year, 1, 1);
	long daysBetween = ChronoUnit.DAYS.between(dateBefore, targetDate);
        double numDays = (double)daysBetween;
        
        // get rate on Jan 1st
        double start = balanceValue - (rate * numDays);
        return start;
    } // end method calcAccruedStart
    
    /* private setFieldRed
    *
    * i - the index of the field that needs to turn red
    *
    * This method sets the background of the textbox Red if it failed
    * validation, and puts focus on that field        */
    private void setFieldRed(int i) {
        
            BackgroundFill backgroundFill = new BackgroundFill(
            Color.valueOf("#f0a99e"),
            new CornerRadii(5),
            new Insets(1)
            );
            
            Background background = new Background(backgroundFill);
            tfTypeBalance[i].setBackground(background);
            
            tfTypeBalance[i].requestFocus();
        
    }
    
    /* private insertBalances
     *
    * Inserts the balances in the controls into the starting_Balances table
    * when the form was not prepopulated*/
    private void insertBalances () {
        
        for (int i = 0; i < typeSize; i++) {  
            
            int absenceID = (Integer)absenceTypes.get(i).get("Absence_ID");
            String sql = "INSERT into Starting_Balances (Year, Absence_ID, Starting_Balance) " +
            "VALUES ('" + year + "', '" + absenceID + "', '" + startingBalances[i] + "')"; 
            Database.SQLUpdate(sql);        
        }         
    }  // end method insert Balances
    
    /* private updateBalances
     *
     * Updates the balances when form is prepopulated, and inserts any 
     * new absence type balances that were added in settings and set in the control */
    private void updateBalances () {
 
        // update those existing in database
        for (int i = 0; i < typeSize; i++) {  
            
            int absenceID = (Integer)absenceTypes.get(i).get("Absence_ID");
            
            if (JsonMatch.getJsonIndex(startBalances,"Absence_ID",absenceID) == -1 ) {
                // absence ID not in balances - insert
                String sql = "INSERT into Starting_Balances (Year, Absence_ID, Starting_Balance) " +
                    "VALUES ('" + year + "', '" + absenceID + "', '" + startingBalances[i] + "')"; 
                Database.SQLUpdate(sql); 
            } else {
                // absence ID in blances - update
                String sql = "UPDATE Starting_Balances " +
                "SET Absence_ID = '" + absenceID + "', Starting_Balance = '" + startingBalances[i] +  "' " +
                "WHERE Absence_ID = '" + absenceID + "' and Year = '" + year + "'"; 
                Database.SQLUpdate(sql);
            }   
        }
    } // end method updateBalances
  
} //End Subclass ListReport