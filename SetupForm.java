/*
 * Christopher Burgess
 */
package myabsences;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
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
import javafx.stage.Stage;
import org.json.simple.JSONObject;

/**
 *
 * @author Christopher
 */
//Begin Subclass SetupForm
public class SetupForm extends Application {
  
    /* Instantiate new stage object */
    static Stage setupStage = new Stage();
    SimpleDateFormat formatDb = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat formatCal = new SimpleDateFormat("MM/dd/yyyy");
    ArrayList<JSONObject> absenceTypes = new ArrayList<>();
    // form field variables (up to 6 types)
    int[] absenceID = new int[6];
    String[] absenceType = new String[6];
    String[] absenceColor = new String[6];
    String[] balanceType =  new String[6];
    String[] accrualRate =  new String[6];
    String[] maxAccrual =  new String[6];
    String year = "";
    boolean prePopulated = false;
    String colors[] = new String[] {"Green","Red","Purple","Orange","Blue","Yellow"}; 
    Text setupTitle = new Text("Setup");
    int rowCounter = 1;
    int typesCounter = 0;
    int typeSize = 0;
    int editAdd = 0;
    
    // controls
    TextField[] tfAbsenceType = new TextField[6];   // up to 6 absence types (names)
    static ComboBox<String> cboAbsenceColor[] = new ComboBox[6];
    static ComboBox<String>[] cboBalanceType = new ComboBox[6];
    TextField[] tfAccrualRate = new TextField[6];
    TextField tfMaxAccrual[] = new TextField[6];
    
    // labels
    Label[] lblAbsenceName = new Label[6];
    Label[] lblColor = new Label[6];
    Label[] lblBalanceType = new Label[6];
    Label[] lblAccrualRate = new Label[6];
    Label[] lblMaxAccrual = new Label[6];
    
    // buttons
    Button btnAddType = new Button("+");   // button to add another Type
    Button btnSetupExit = new Button("Exit");
    Button btnSetupUpdate = new Button("Update");
    Button btnSetupSave = new Button("Save");
    Button btnDelete[] = new Button[6];
    
    GridPane gPane = new GridPane();
    //BorderPane bPane = new BorderPane();
    
    // empty constructor
    public SetupForm() {
        
    }
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        
        // get absenceType data from database
        absenceTypes = Database.getAbsenceTypes();  // Arraylist of JSONObject type data
        typeSize = absenceTypes.size();
        if (typeSize > 0) {
            prePopulated = true;
        }    
        
        // Get the refrence to existing absence IDs for updating the table
        for (int i = 0; i < typeSize; i++) {
            absenceID[i] = (Integer)absenceTypes.get(i).get("Absence_ID");
        }        
        
        /* Main pane */
        BorderPane bPane = new BorderPane();
        HBox topSetupPane = new HBox();
        topSetupPane.getStyleClass().add("formtop");
        gPane = new GridPane();
        
        bPane.setPadding(new Insets(5, 5, 5, 5));
        gPane.setAlignment(Pos.TOP_LEFT);
        gPane.setPadding(new Insets(5, 5, 5, 5));
        gPane.setMinHeight(400);
        gPane.getStyleClass().add("setuppane");
        gPane.setHgap(20);
        gPane.setVgap(4);
                      
        HBox hBoxB = new HBox();
        Button btnExit = new Button("Exit");
        hBoxB.setAlignment(Pos.CENTER);
        hBoxB.setSpacing(50);
        HBox.setMargin(btnExit, new Insets(5, 5, 5, 5));
        hBoxB.setPrefHeight(35);
        

        // **** Form Fields *****
        // comboboxes for colors - add color options
        for (int i = 0; i < 6; i++ ) {
            cboAbsenceColor[i] = new ComboBox();
            for (int c = 0; c < 6; c++) {    // add each color
                cboAbsenceColor[i].getItems().add(colors[c]);
                cboAbsenceColor[i].setPrefWidth(55);
            }
        }    
        
        // Balance Type combo boxes - add options
        for (int i = 0; i < 6; i++ ) {
            cboBalanceType[i] = new ComboBox();
            cboBalanceType[i].getItems().add("Accrued Hours");
            cboBalanceType[i].getItems().add("Fixed Hours");
            cboBalanceType[i].getItems().add("Add-In Hours");
            cboBalanceType[i].setPrefWidth(55);
        }     
        
        for (int i = 0; i < 6; i++ ) {
            btnDelete[i] = new Button("x");
        }
        
        // Set Title in topPane
        topSetupPane.setAlignment(Pos.TOP_CENTER);
        topSetupPane.setPadding(new Insets(5, 5, 5, 5));
        setupTitle.getStyleClass().add("dayformtop");
        topSetupPane.setPrefHeight(35);     
        setupTitle.getStyleClass().add("daytitle"); 
        topSetupPane.getChildren().add(setupTitle);
        
        // ***** Set first set of default Controls in the Gridpane ****
        if (!prePopulated) {
            addDefaultControls(0);
        }
        
        // determine what buttons to add to bottom hbox
        if (prePopulated) {
            putValues();
            hBoxB.getChildren().addAll(btnSetupUpdate,btnSetupExit);
            Platform.runLater(() -> {
                btnSetupExit.requestFocus();  // Set focus on exit if prepopulated
            });
        } else {
            hBoxB.getChildren().addAll(btnSetupSave,btnSetupExit);
        }
        
        /* set panes in stage and show stage */
        bPane.setTop(topSetupPane);
        bPane.setCenter(gPane);
        bPane.setBottom(hBoxB);
        Scene SetupScene = new Scene(bPane); 
        SetupScene.getStylesheets().add(getClass().getResource("StyleSheet.css").toExternalForm());
        setupStage.setMaxHeight(550);
        setupStage.setMinWidth(665);
        setupStage.setMaxWidth(665);
        setupStage.setTitle("Setup");
        setupStage.setScene(SetupScene);
        setupStage.show();   
        
        // Save Button event Handler
        btnSetupSave.setOnAction(e-> {
            try {
            getValues(); 
            insertAbsenceTypes();
            Warnings.removeWarning(0, "RUN_SETUP");
            MyAbsences.refresh();
            setupStage.close(); 
            } catch (Exception save) {
                
            }
        });
        
        // Update button handler
        btnSetupUpdate.setOnAction(e-> {
            try {
                getValues();
                updateAbsenceTypes();
                setupStage.close(); 
                MyAbsences.refresh();
            } catch (Exception update) {
                
            }
        });   
        
        // Delete type button handler - delete an Absence_Type and refresh
        for (int i = 0; i < 6; i++) {
            final int num = i;
            btnDelete[i].setOnAction(e->{
                try {
                    String absence_type = tfAbsenceType[num].getText();
                    ButtonType cancel = new ButtonType("Cancel");
                    ButtonType delete = new ButtonType("Delete");
                    Alert a = new Alert(AlertType.WARNING, "Delete Absence Type", cancel, delete);
                    a.setHeaderText("Do you really want to Delete " + absence_type + "?\n"
                            + "This will delete all absences for " + absence_type + " on the Calendar,\n"
                            + "as well as the " + absence_type + " starting balances!");
                    a.setResizable(true);
                    a.setContentText("Press Delete to confirm:");
                    SetupForm app=new SetupForm();
                    a.showAndWait().ifPresent(response -> {
                        try {
                            if (response == delete) {
                                deleteAbsenceType(num);
                                MyAbsences.refresh();
                                app.start(primaryStage);
                            } else if (response == cancel) {}
                        } catch (Exception exit) {}     
                    });
                } catch(Exception ex) {
                    
                }
            });      
        }        
        
        // exit button handler
        btnSetupExit.setOnAction(e-> {
            try {
                setupStage.close(); 
            } catch (Exception exit) {
                
            }
        });
        
        // Add another type (+) button handler
        btnAddType.setOnAction(e-> {
            try {   
                if (prePopulated) {
                    typeSize++;
                    editAdd++;
                }
                 else {
                    rowCounter+=3;
                    typesCounter++; 
                }
                addDefaultControls(typesCounter);
            } catch (Exception exit) {
                
            }
        });        
        
        // Type Combobox Handler - add in new controls for accrued types
        for (int i = 0; i < 6; i++) {
            final int num = i;
            final String type = cboBalanceType[i].getValue(); // get type from combo box
            cboBalanceType[i].setOnAction(e->{
                try {
                    if (((ComboBox)e.getSource()).getValue().equals("Accrued Hours")) {
                        addAccruedControls(num);
                    }
                    if (!((ComboBox)e.getSource()).getValue().equals("Accrued Hours")) {
                        tfAccrualRate[num].clear();
                        tfMaxAccrual[num].clear();
                        removeAccruedControls(num);
                    }
                }
                catch(Exception ex) {
                }
            });      
        }
        
        // Color Combobox Handler - change color of comboBox and Type
        for (int i = 0; i < 6; i++) {
            final int num = i;
            
            cboAbsenceColor[i].setOnAction((ActionEvent e)->{
                try {
                        String color = cboAbsenceColor[num].getValue();
                        Background background = new Background(getBackgroundFill(color));
                        cboAbsenceColor[num].setBackground(background);
                        tfAbsenceType[num].setBackground(background);
                } catch(Exception ex) {
                
                }
            });      
        }        
    
    } // end start
    
    private BackgroundFill getBackgroundFill(String color) {
        
        switch(color) {
            case "Red":
                color = "#f0a99e";
                break;
            case "Orange":
                color = "#f0c49e";
                break;
            case "Blue":
                color = "#9cbce6";
                break;
            case "Yellow":
                color = "#e2e69c";
                break;
            case "Green":
                color = "#9de69c";
                break;
            case "Purple":
                color = "#c39ce6";
                break;
            case "Gray":
                color = "#c4c1c0";
            default: 

        }

        BackgroundFill backgroundFill = new BackgroundFill(
            Color.valueOf(color),
            new CornerRadii(5),
            new Insets(1)
            );

        return backgroundFill;                    
       
    }
    
    /* private addDefaultControls
     * 
     * num - the number of the control group being added
    *
    * Adds the default control groups to the pane for each entry */
    private void addDefaultControls(int num) {
        
        // Type Label
        if (num != 0) {gPane.getChildren().remove(btnAddType);}
        int absenceNumber = num + 1;
        lblAbsenceName[num] = new Label();
        lblAbsenceName[num].setText("Absence Name " + absenceNumber);
        GridPane.setConstraints(lblAbsenceName[num], 1, rowCounter);
        GridPane.setColumnSpan(lblAbsenceName[num], 4);
        gPane.getChildren().add(lblAbsenceName[num]);    
        // Type textfield
        tfAbsenceType[num] = new TextField();
        tfAbsenceType[num].setMaxWidth(150);
        GridPane.setConstraints(tfAbsenceType[num], 5, rowCounter);
        GridPane.setColumnSpan(tfAbsenceType[num], 8);
        gPane.getChildren().add(tfAbsenceType[num]); 
        // Color label
        lblColor[num] = new Label();
        lblColor[num].setText("Display Color");
        GridPane.setConstraints(lblColor[num], 13, rowCounter);
        GridPane.setColumnSpan(lblColor[num], 4);
        gPane.getChildren().add(lblColor[num]);  
        // Color combobox
        cboAbsenceColor[num].setMinWidth(100);
        GridPane.setConstraints(cboAbsenceColor[num], 17, rowCounter);
        GridPane.setColumnSpan(cboAbsenceColor[num], 4);
        gPane.getChildren().add(cboAbsenceColor[num]);
        // Add Delete Button for prePopulated form
        if (prePopulated) {
            GridPane.setConstraints(btnDelete[num], 21, rowCounter);
            btnDelete[num].setMinSize(20, 20);
            btnDelete[num].setMaxSize(20, 20);
            btnDelete[num].getStyleClass().add("btndelete");
            GridPane.setColumnSpan(btnDelete[num], 4);  
            gPane.getChildren().add(btnDelete[num]);
        }
        // Balance Type label
        lblBalanceType[num] = new Label();
        lblBalanceType[num].setText("Balance Type");
        GridPane.setConstraints(lblBalanceType[num], 1, rowCounter+1);
        GridPane.setColumnSpan(lblBalanceType[num], 4);
        gPane.getChildren().add(lblBalanceType[num]); 
        // Balance Type combobox
        cboBalanceType[num].setMinWidth(125);
        String color = "Gray";
        Background background = new Background(getBackgroundFill(color));
        cboBalanceType[num].setBackground(background);
        GridPane.setConstraints(cboBalanceType[num], 5, rowCounter+1);
        GridPane.setColumnSpan(cboBalanceType[num], 5);
        gPane.getChildren().add(cboBalanceType[num]);        
        // Add the + button
        if (num < 5) {  // && !editAdd
            btnAddType.setMinWidth(10);
            GridPane.setConstraints(btnAddType, 1, rowCounter+2);
            GridPane.setColumnSpan(btnAddType, 8);
            gPane.getChildren().add(btnAddType);  
        }
        if (editAdd > 0) {
            // set focus on new control group
            tfAbsenceType[num].requestFocus();
                rowCounter+=3;
                typesCounter++; 
        }
    }
    
    /* private addAccruedControls
    *
    * num - the number of the control group in array being added (the absence type)
    *
    * This method adds the accrual rate and max accrual lables and textboxes
    * to the pane for the specific absence type control group when combo box 
    * is changed to "Accrued"      */
    private void addAccruedControls(int num) {
        
        int place = (num+1)*2;
        if (num > 0) {place=place+num;}
        // Accrual Rate label
        lblAccrualRate[num] = new Label();
        lblAccrualRate[num].setText("Accrual Rate");
        GridPane.setConstraints(lblAccrualRate[num], 13, place); 
        GridPane.setColumnSpan(lblAccrualRate[num], 4);
        gPane.getChildren().add(lblAccrualRate[num]);  
        // Accrual Rate textfield
        tfAccrualRate[num] = new TextField();
        tfAccrualRate[num].setMaxWidth(50);
        GridPane.setConstraints(tfAccrualRate[num], 17, place);
        GridPane.setColumnSpan(tfAccrualRate[num], 4);
        gPane.getChildren().add(tfAccrualRate[num]); 
        // Accrual Max label
        lblMaxAccrual[num] = new Label();
        lblMaxAccrual[num].setText("Accrual Max");
        GridPane.setConstraints(lblMaxAccrual[num], 20, place);
        GridPane.setColumnSpan(lblMaxAccrual[num], 4);
        gPane.getChildren().add(lblMaxAccrual[num]);  
        // Accrual Max textfield
        tfMaxAccrual[num] = new TextField();
        tfMaxAccrual[num].setMaxWidth(50);
        tfMaxAccrual[num].setMinWidth(50);
        GridPane.setConstraints(tfMaxAccrual[num], 22, place);
        GridPane.setColumnSpan(tfMaxAccrual[num], 3);
        gPane.getChildren().add(tfMaxAccrual[num]);  
    }
    
    private void removeAccruedControls(int num) {
        
        gPane.getChildren().remove(lblAccrualRate[num]);
        gPane.getChildren().remove(tfAccrualRate[num]); 
        gPane.getChildren().remove(lblMaxAccrual[num]); 
        gPane.getChildren().remove(tfMaxAccrual[num]);
    }
    
    /* private putValues
    *
    * This puts the values from the database into the controls if setup has already
    * been run.  Calls addDefaultControls and addAccruedControls as needed to add
    * more controls for the data found in db. Allows user to edit existing data. */
    private void putValues() {
        
            // set data in the controls that was already saved 
            for (int i = 0; i < typeSize; i++) {
                addDefaultControls(i);
                tfAbsenceType[i].setText((String)absenceTypes.get(i).get("Absence_Type"));
                cboAbsenceColor[i].setValue((String)absenceTypes.get(i).get("Color"));
                String color = cboAbsenceColor[i].getValue();
                Background background = new Background(getBackgroundFill(color));
                cboAbsenceColor[i].setBackground(background);  
                tfAbsenceType[i].setBackground(background);
                double arate = (Double)absenceTypes.get(i).get("Accrual_Rate");
                if (arate > 0) {
                    addAccruedControls(i);
                    cboBalanceType[i].setValue("Accrued Hours");
                    tfAccrualRate[i].setText( Double.toString((Double)absenceTypes.get(i).get("Accrual_Rate")) );
                    tfMaxAccrual[i].setText ( Double.toString((Double)absenceTypes.get(i).get("Max_Accrual"))  );
                }
                if (arate == 0) {cboBalanceType[i].setValue("Fixed Hours");}
                if (arate == -1) {cboBalanceType[i].setValue("Add-In Hours");}
                rowCounter+=3;
                typesCounter++;
            }            
    } // end putValues
    
    /* private getValues
    * 
    * This method gets the values from the controls to variable arrays, to insert into
    * or update the db table Absence_Types     */
    private void getValues() {

        // Get the control values into the variable arrays
        if (!prePopulated) {typeSize = typesCounter+1;}
        for (int i = 0; i < typeSize; i++) {               
            absenceType[i] = tfAbsenceType[i].getText();
            absenceColor[i] = cboAbsenceColor[i].getValue();
            String balType = cboBalanceType[i].getValue();
            if (balType.equals("Accrued Hours")) {accrualRate[i] = (tfAccrualRate[i].getText());}
            if (balType.equals("Fixed Hours")) {accrualRate[i] = "0";}
            if (balType.equals("Add-In Hours")) {accrualRate[i] = "-1";}
            String aMax = "";
            try {
                aMax = tfMaxAccrual[i].getText();
            } catch(Exception ex) {
                aMax = "";
            } 
            if (aMax.isEmpty()) {maxAccrual[i] = "0";}
            else {maxAccrual[i] = tfMaxAccrual[i].getText();}
            
            // TODO - valididae these values for nulls
        }
    }
    
    /* private updateAbsenceTypes
     *
     *
     * This method updates any changed data for existing absence types displayed
     * and it inserts any new types that were added to the edit form 
     * before pressing the update button    */
    private void updateAbsenceTypes() {

        int i = 0;
        // update existing in Absence_Types
        for (i = 0; i < (typesCounter-editAdd); i++) { 
            String sql = "UPDATE Absence_Types " +
                "SET Absence_Type = '" + absenceType[i] + "', Color = '" + absenceColor[i] + "', Accrual_Rate = '" + 
                accrualRate[i] + "', Max_Accrual = '" + maxAccrual[i] + "' " +
                "WHERE Absence_ID = '" + absenceID[i] + "'"; 
            Database.SQLUpdate(sql);
        }
        
        // insert newly added to Absence_Types
        for (int a = i; a < typeSize; a++) { 
            String sql = "INSERT into Absence_Types (Absence_Type, Color, Accrual_Rate, Max_Accrual) " +
                "VALUES ('" + absenceType[a] + "', '" + absenceColor[a] + "', '" + accrualRate[a] +
                "', '" + maxAccrual[a] + "')"; 
            Database.SQLUpdate(sql);
        }      
    }
    
    /* private insert AbsenceType
     *
     *
     * This method inserts only newly-added types into the absence_types table 
     * when the form started as empty and was not pre populated */
    private void insertAbsenceTypes() {
        
        // insert to 
        for (int i = 0; i < typeSize; i++) { 
            String sql = "INSERT into Absence_Types (Absence_Type, Color, Accrual_Rate, Max_Accrual) " +
                "VALUES ('" + absenceType[i] + "', '" + absenceColor[i] + "', '" + accrualRate[i] +
                "', '" + maxAccrual[i] + "')"; 
            Database.SQLUpdate(sql);
        }   
        
    }
    
    /* private deleteAbsenceType
     *
     * i = the index of the absence ID to delete
     *
     * This method deletes an absence ID and its reference rows from all tables    */
    private void deleteAbsenceType(int i) {
        
        // Delete all absences in absences table associated with the type id
        String sql = "DELETE from absences " + 
        "WHERE Absence_ID = '" + absenceID[i] + "'";
        Database.SQLUpdate(sql);
        
        // Delete all in starting_balances table associatesd with the type id
        sql = "DELETE from Starting_Balances " + 
        "WHERE Absence_ID = '" + absenceID[i] + "'";
        Database.SQLUpdate(sql);
        
        // Delete the absence type for the id
        sql = "DELETE from Absence_Types " + 
        "WHERE Absence_ID = '" + absenceID[i] + "'";
        Database.SQLUpdate(sql);
    }    
    
} //End Subclass SetupForm