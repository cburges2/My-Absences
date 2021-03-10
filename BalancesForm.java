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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
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
    Button btnBalancesExit = new Button("Exit");
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
            hBoxB.getChildren().addAll(btnBalancesUpdate,btnBalancesExit);
            Platform.runLater(() -> {
                btnBalancesExit.requestFocus();  // Set focus on exit if prepopulated
            });
        } else {
            hBoxB.getChildren().addAll(btnBalancesSave,btnBalancesExit);
        }       
        
        addControls();
        
        // ************* Control Handlers ***************
        
        // Save Button event Handler
        btnBalancesSave.setOnAction(e-> {
            try {
              getValues(); 
              insertBalances();
              Warnings.removeWarning(0, "ENTER_BALANCES");
              MyAbsences.refresh();
              startBalanceStage.close(); 
            } catch (Exception save) {
                
            }
        });
        
        // Set Title in topPane
        balancesTitle.getStyleClass().add("daytitle");
        topPane.getChildren().add(balancesTitle);        
        
        // Update button handler
        btnBalancesUpdate.setOnAction(e-> {
            try {
                getValues();
                updateBalances();
                startBalanceStage.close(); 
                MyAbsences.refresh();
            } catch (Exception update) {
                
            }
        });  
        
        // exit button handler
        btnBalancesExit.setOnAction(e-> {
            try {
                startBalanceStage.close(); 
            } catch (Exception exit) {
                
            }
        });    

     
        /* set panes in stage and show stage */
        bPane.setTop(topPane);
        bPane.setCenter(gPane);
        bPane.setBottom(hBoxB);
        Scene startBalancesScene = new Scene(bPane);
        startBalancesScene.getStylesheets().add(getClass().getResource("StyleSheet.css").toExternalForm());
        startBalanceStage.setMaxHeight(550);
        startBalanceStage.setMinWidth(500);
        //startBalanceStage.setMaxWidth(660);
        startBalanceStage.setTitle("Enter Starting Balances");
        startBalanceStage.setScene(startBalancesScene);
        startBalanceStage.show();
    } 
    
    
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
            if (arate > 0 && !prePopulate) {
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
    * already been entered. Allows user to edit existing data. */
    private void putValues() {
        
        // set data in the controls that was already saved 
        for (int i = 0; i < typeSize; i++) {
            String absenceName = (String)absenceTypes.get(i).get("Absence_Type");
            double balance = JsonMatch.getJsonDouble(startBalances,"Absence_Type",absenceName,"Starting_Balance");
            if (balance != 0) {
                tfTypeBalance[i].setText(String.valueOf(balance));
            }           
        }       
        
    } // end putValues
    
    /* private getValues
    * 
    * This method gets the values from the controls to variable arrays, to insert into
    * or update the db table Starting_Balances     */
    private void getValues() {
        
        // Get the control values into the variable arrays
        for (int i = 0; i < typeSize; i++) {            
            
            double arate = (Double)absenceTypes.get(i).get("Accrual_Rate");
            
            // calculate accrued from date else use entered value
            if (arate > 0) {
                if (prePopulate) {
                    startingBalances[i] = tfTypeBalance[i].getText(); 
                } else {startingBalances[i] = String.valueOf(calcAccruedStart(i, arate)); }
            } else if (arate == -1) {
                startingBalances[i] = "0";
            }
            else {startingBalances[i] = tfTypeBalance[i].getText();}
            
            // TODO - valididae these values for nulls
        }
    } // end getValues
    
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
    }
    
    private void insertBalances () {
        
        for (int i = 0; i < typeSize; i++) {  
            
            int absenceID = (Integer)absenceTypes.get(i).get("Absence_ID");
            String sql = "INSERT into Starting_Balances (Year, Absence_ID, Starting_Balance) " +
            "VALUES ('" + year + "', '" + absenceID + "', '" + startingBalances[i] + "')"; 
            Database.SQLUpdate(sql);        
        }        
                
    }
    
    private void updateBalances () {
 
        // update those existing in database
        for (int i = 0; i < balancesSize; i++) {  
            
            int absenceID = (Integer)absenceTypes.get(i).get("Absence_ID");
            String sql = "UPDATE Starting_Balances " +
                "SET Absence_ID = '" + absenceID + "', Starting_Balance = '" + startingBalances[i] +  "' " +
                "WHERE Absence_ID = '" + absenceID + "' and Year = '" + year + "'"; 
            Database.SQLUpdate(sql);        
        }
        
        // insert new abasenceTypes balance
        if (typeSize > balancesSize) {
            int diffSize = (typeSize - balancesSize);
            for (int i = 0; i < diffSize; i++) {
                int addIndex = (i + balancesSize);
                int absenceID = (Integer)absenceTypes.get(addIndex).get("Absence_ID");
                String sql = "INSERT into Starting_Balances (Year, Absence_ID, Starting_Balance) " +
                    "VALUES ('" + year + "', '" + absenceID + "', '" + startingBalances[balancesSize + i] + "')"; 
                Database.SQLUpdate(sql); 
            } 
        }
        
        
    }
  
} //End Subclass ListReport