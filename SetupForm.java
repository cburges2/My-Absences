/*
 * Christopher Burgess
 */
package myabsences;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
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
//Begin Subclass SetupForm
public class SetupForm extends Application {
  
    /* Instantiate new stage object */
    static Stage setupStage = new Stage();
    SimpleDateFormat formatDb = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat formatCal = new SimpleDateFormat("MM/dd/yyyy");
    ArrayList<JSONObject> absenceTypes = new ArrayList<>();
    // form field variables (up to 6 types)
    int[] absenceID = new int[6];       // absence IDs from the types table
    String[] absenceType = new String[6];
    String[] absenceColor = new String[6];
    String[] balanceType =  new String[6];
    String[] accrualRate =  new String[6];
    String[] maxAccrual =  new String[6];
    int year = 0;
    boolean prePopulated = false;   // true if data in db at form launch
    String colors[] = new String[] {"Green","Red","Purple","Orange","Blue","Yellow"}; 
    Text setupTitle = new Text("Setup");
    int rowCounter = 1;     // counts the rows for control positioning
    int typesCounter = 0;   // counts types added for control row positioning
    int typeSize = 0;       // number of types in the db table
    int editAdd = 0;        // number of types added when in edit mode
    
    // create textfield and combobox controls
    TextField[] tfAbsenceType = new TextField[6];   // up to 6 absence types (names)
    static ComboBox<String>[] cboAbsenceColor = new ComboBox[6];
    static ComboBox<String>[] cboBalanceType = new ComboBox[6];
    TextField[] tfAccrualRate = new TextField[6];
    TextField tfMaxAccrual[] = new TextField[6];
    
    // create labels
    Label[] lblAbsenceName = new Label[6];
    Label[] lblColor = new Label[6];
    Label[] lblBalanceType = new Label[6];
    Label[] lblAccrualRate = new Label[6];
    Label[] lblMaxAccrual = new Label[6];
    
    // create buttons
    Button btnAddType = new Button("+");   // button to add another Type  
    Button btnSetupCancel = new Button("Cancel");
    Button btnSetupUpdate = new Button("Update");
    Button btnSetupSave = new Button("Save");
    Button btnDelete[] = new Button[6];
    
    GridPane gPane = new GridPane();
    
    /* constructor */
    public SetupForm() {
        
        // Set Current Year for balances insert/update
        final SimpleDateFormat FORMAT_YEAR = new SimpleDateFormat("yyyy");       
        year = Integer.parseInt(FORMAT_YEAR.format(Calendar.getInstance().getTime())); // current year
        
    }
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        
        // set Modality if form object has not yet set the stage visible
        if (!setupStage.isAlwaysOnTop()) {setupStage.initModality(Modality.APPLICATION_MODAL);}
        
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
        Button btnCancel = new Button("Cancel");
        hBoxB.setAlignment(Pos.CENTER);
        hBoxB.setSpacing(50);
        HBox.setMargin(btnCancel, new Insets(5, 5, 5, 5));
        hBoxB.setPrefHeight(35);
        
        // Add Type (+) button attributes
        Tooltip ttadd = new Tooltip("Add another type");
        btnAddType.getStyleClass().add("ttgray");
        btnAddType.setTooltip(ttadd);
        btnAddType.getStyleClass().add("btnplus");    
        btnAddType.setMaxSize(25,25);
        btnAddType.setMinSize(25,25);
        btnAddType.setPadding(new Insets(1,1,1,1));
       
        // **** Set Form Fields *****
        // set comboboxes for colors - add color options
        for (int i = 0; i < 6; i++ ) {
            cboAbsenceColor[i] = new ComboBox<>();
            for (int c = 0; c < 6; c++) {    // add each color
                cboAbsenceColor[i].getItems().add(colors[c]);
                cboAbsenceColor[i].setPrefWidth(55);
                Tooltip clr = new Tooltip("Used for all displays of this type");
                clr.getStyleClass().add("ttgray");
                cboAbsenceColor[i].setTooltip(clr);
            }
        }    
        
        // set Balance Type combo boxes - add options
        for (int i = 0; i < 6; i++ ) {
            cboBalanceType[i] = new ComboBox<>();
            cboBalanceType[i].getItems().add("Accrued Hours");
            cboBalanceType[i].getItems().add("Fixed Hours");
            cboBalanceType[i].getItems().add("Add-In Hours");
            cboBalanceType[i].setPrefWidth(55);
            Tooltip bal = new Tooltip("Accrued: Hours accrue at a daily rate\n" +
                "Fixed: Hours are fixed at a starting balance\n" +
                "Add-In: Hours are added as they are put on the calander");
            bal.getStyleClass().add("ttgray");
            cboBalanceType[i].setTooltip(bal);
        }     
        
        // set delete buttons
        for (int i = 0; i < 6; i++ ) {
            btnDelete[i] = new Button("x");
            Tooltip del = new Tooltip("Delete this Type\nand all its data");
            del.getStyleClass().add("ttred");
            btnDelete[i].setTooltip(del);
        }
        
        // set control values to empty (avoid nulls) and help tips
        for (int i = 0; i < 6; i++ ) {
            tfAbsenceType[i] = new TextField("");
            Tooltip type = new Tooltip("Enter a display name for this type");
            type.getStyleClass().add("ttgray");
            tfAbsenceType[i].setTooltip(type);            
            cboAbsenceColor[i].setValue("");
            cboBalanceType[i].setValue(""); 
            tfAccrualRate[i] = new TextField("");
            tfMaxAccrual[i] = new TextField("");
            Tooltip max = new Tooltip("Enter number of hours where hours stop accruing\nZero for N/A");
            max.getStyleClass().add("ttgray");
            tfMaxAccrual[i].setTooltip(max);
            tfMaxAccrual[i].setText("0");
            cboBalanceType[i].setValue("");            
        }
        
        // Set Title in topPane
        topSetupPane.setAlignment(Pos.TOP_CENTER);
        topSetupPane.setPadding(new Insets(5, 5, 5, 5));
        setupTitle.getStyleClass().add("dayformtop");
        topSetupPane.setPrefHeight(35);     
        setupTitle.getStyleClass().add("daytitle"); 
        topSetupPane.getChildren().add(setupTitle);
        
        // ******* Set first set of empty default Controls in the Gridpane ******
        if (!prePopulated) {
            addDefaultControls(0);
        }
        
        // determine what buttons to add to bottom hbox
        if (prePopulated) {
            putValues();
            hBoxB.getChildren().addAll(btnSetupUpdate,btnSetupCancel);
            Platform.runLater(() -> {
                btnSetupCancel.requestFocus();  // Set focus on cancel if prepopulated
            });
        } else {
            hBoxB.getChildren().addAll(btnSetupSave,btnSetupCancel);
        }
        
        /* set panes in stage and show stage */
        bPane.setTop(topSetupPane);
        bPane.setCenter(gPane);
        bPane.setBottom(hBoxB);
        Scene SetupScene = new Scene(bPane); 
        SetupScene.getStylesheets().add(getClass().getResource("StyleSheet.css").toExternalForm());
        setupStage.setAlwaysOnTop(true);
        setupStage.setMaxHeight(550);
        setupStage.setMinWidth(665);
        setupStage.setMaxWidth(665);
        setupStage.setTitle("Setup");
        setupStage.setScene(SetupScene);
        setupStage.show();   
        
        // Save Button event Handler
        btnSetupSave.setOnAction(e-> {
            try {
                if (getValues()) { 
                    insertAbsenceTypes();
                    Warnings.removeWarning(0, "RUN_SETUP");
                    MyAbsences.refresh();
                    setupStage.close(); 
                }
            } catch (Exception save) {
                ErrorHandler.exception(save, "saving types setup");
            }
        });
        
        // Update button handler
        btnSetupUpdate.setOnAction(e-> {
            try {
                if (getValues()) {
                    updateAbsenceTypes();
                    setupStage.close(); 
                    MyAbsences.refresh();
                }
            } catch (Exception update) {
                ErrorHandler.exception(update, "updating types setup");
            }
        });   
        
        // Delete type button handler - delete an Absence_Type and refresh
        for (int i = 0; i < 6; i++) {
            final int num = i;
            btnDelete[i].setOnAction(e->{
                try {
                    SetupForm app=new SetupForm();
                    String absence_type = tfAbsenceType[num].getText();
                    if (Validate.confirmDeleteType(absence_type)) {
                         deleteAbsenceType(num);
                         MyAbsences.refresh();
                         app.start(primaryStage);
                     } else {}
                } catch(Exception delete) {
                    ErrorHandler.exception(delete, "deleting a type");
                }
            });      
        }        
        
        // Cancel button handler
        btnSetupCancel.setOnAction(e-> {
            try {
                setupStage.close(); 
            } catch (Exception cancel) {
                ErrorHandler.exception(cancel, "cancelling setup");
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
            } catch (Exception add) {
                ErrorHandler.exception(add, "adding a type");
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
                    ErrorHandler.exception(ex, "changing the balance type");
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
                    ErrorHandler.exception(ex, "changing a type color");
                }
            });      
        }        
    
    } // end start
    
    /* private getBackgroundFill
    *
    * color - the color name to change the background to
    * ==> a BackgroundFill object set to the color
    *
    * This method returns a BackgroundFill object for a color */
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
       
    } // end getBackgroundFill
    
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
        if (num < 5) {   // 6 is max number of types
            btnAddType.setMinWidth(10);
            GridPane.setConstraints(btnAddType, 1, rowCounter+2);
            GridPane.setColumnSpan(btnAddType, 8);
            gPane.getChildren().add(btnAddType);  
        }
        if (editAdd > 0) {  // types added during edit mode
            // set focus on new control group
            tfAbsenceType[num].requestFocus();
                rowCounter+=3;
                typesCounter++; 
        }
    } // end addDefaultControls
    
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
        tfMaxAccrual[num].setMaxWidth(50);
        tfMaxAccrual[num].setMinWidth(50);
        GridPane.setConstraints(tfMaxAccrual[num], 22, place);
        GridPane.setColumnSpan(tfMaxAccrual[num], 3);
        gPane.getChildren().add(tfMaxAccrual[num]);  
        
    } // end add accrued controls
    
    /* private removeAccruedControls
    *
    * num - the number of the control group to remove accrued controls for
    *
    * This removed the accured controls when balance type is switched away from "Accrued"    */
    private void removeAccruedControls(int num) {
        
        gPane.getChildren().remove(lblAccrualRate[num]);
        gPane.getChildren().remove(tfAccrualRate[num]); 
        gPane.getChildren().remove(lblMaxAccrual[num]); 
        gPane.getChildren().remove(tfMaxAccrual[num]);
    } // end removeAccruedControls
    
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
                if (arate == 0) {
                    cboBalanceType[i].setValue("Fixed Hours");
                    tfMaxAccrual[i].setText("0");
                }
                if (arate == -1) {
                    cboBalanceType[i].setValue("Add-In Hours");
                    tfMaxAccrual[i].setText("0");
                }
                rowCounter+=3;
                typesCounter++;
            }            
    } // end putValues
    
    /* private getValues
    *
    * ==> valided flag for all data validated and ready
    * 
    * This method gets the values from the controls to variable arrays, to insert into
    * or update the db table Absence_Types if all fields pass verification.    */
private boolean getValues() {
        
        boolean validated = false;
        boolean validatedCommon = false;
        boolean validatedAccrued = false;
       
        if (!prePopulated) {typeSize = typesCounter+1;}
        
        // validate the common fields
        for (int i = 0; i < typeSize; i++) { 
            // Get and Valididae the values 
            int num = i+1;
            if (Validate.notEmpty("Absence Name " + num, tfAbsenceType[i].getText())) {
                absenceType[i] = tfAbsenceType[i].getText();
                lblAbsenceName[i].setTextFill(Color.BLACK);
                if (Validate.notEmpty("Display Color " + num,cboAbsenceColor[i].getValue())) {
                    absenceColor[i] = cboAbsenceColor[i].getValue();
                    lblColor[i].setTextFill(Color.BLACK);
                    if (Validate.notEmpty("Balance Type " + num,cboBalanceType[i].getValue())) {
                        lblBalanceType[i].setTextFill(Color.BLACK);
                        validatedCommon = true;             // **** all common validations passed ****
                      } else {      // Balance failed
                         validatedCommon = false;                         
                         lblBalanceType[i].setTextFill(Color.RED); 
                    }   
                 } else {           // color failed
                     validatedCommon = false;
                     lblColor[i].setTextFill(Color.RED);
                 } 
            } else {    // name failed       
                validatedCommon = false;
                lblAbsenceName[i].setTextFill(Color.RED);
            }
        }    
        
        // verify accrued types values
        if (validatedCommon == true) {
            for (int i = 0; i < typeSize; i++) {   
                int num = i+1;  
                String balType = cboBalanceType[i].getValue();
                if (balType.equals("Fixed Hours")) {accrualRate[i] = "0";}
                if (balType.equals("Add-In Hours")) {accrualRate[i] = "-1";}
                if (balType.equals("Accrued Hours")) {
                    if (Validate.isPosDecimal("Accrual Rate " + num,tfAccrualRate[i].getText(),8)) {
                        lblAccrualRate[i].setTextFill(Color.BLACK);    
                        accrualRate[i] = (tfAccrualRate[i].getText());
                        if (Validate.isPosDecimal("Max Accrual " + num,tfMaxAccrual[i].getText(),500)) {
                            lblMaxAccrual[i].setTextFill(Color.BLACK);                                     
                            maxAccrual[i] = tfMaxAccrual[i].getText();
                            validatedAccrued = true;                      // **** all Accrued validations passed ****                            
                        } else {    // Max Accrual Failed
                            validatedAccrued = false;
                            lblMaxAccrual[i].setTextFill(Color.RED); 
                        }
                    } else {    // Accrual Rate failed
                        validatedAccrued = false;
                        lblAccrualRate[i].setTextFill(Color.RED); 
                    }
                }    
            }
        }
        
        if (validatedCommon && validatedAccrued) {
            validated = true;       // **** All validations Passed ****
        }
        
        return validated;
        
    } // end getValues
    
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

            // update a zero balance to Starting_Balances for changed to Add-In types
            if (accrualRate[i].equals("-1")) {
                sql = "UPDATE Starting_Balances " +
                "SET Absence_ID = '" + absenceID[i] + "', Starting_Balance = '" + 0 +  "' " +
                "WHERE Absence_ID = '" + absenceID[i] + "' and Year = '" + year + "'"; 
                Database.SQLUpdate(sql);
            }            
        }
        
        // insert newly added to Absence_Types
        for (int a = i; a < typeSize; a++) { 
            String sql = "INSERT into Absence_Types (Absence_Type, Color, Accrual_Rate, Max_Accrual) " +
                "VALUES ('" + absenceType[a] + "', '" + absenceColor[a] + "', '" + accrualRate[a] +
                "', '" + maxAccrual[a] + "')"; 
            Database.SQLUpdate(sql);
            
            // Insert a zero balance to Starting_Balances for new Add-In type
            if (accrualRate[i].equals("-1")) {
                int absID = Database.getAbsenceID(absenceType[a]);
                sql = "INSERT into Starting_Balances (Year, Absence_ID, Starting_Balance) " +
                    "VALUES ('" + year + "', '" + absID + "', '" + 0 + "')"; 
                Database.SQLUpdate(sql);     
            }
        }      
    } // end updateAbsenceTypes
    
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
            
            // Insert a zero balance to Starting_Balances for new Add-In type
            if (accrualRate[i].equals("-1")) {
                int absID = Database.getAbsenceID(absenceType[i]);
                sql = "INSERT into Starting_Balances (Year, Absence_ID, Starting_Balance) " +
                    "VALUES ('" + year + "', '" + absID + "', '" + 0 + "')"; 
                Database.SQLUpdate(sql);     
            }
        }        
    } // end insertAbsenceTypes
    
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
        
    }   // end deleteAbsenceType 
    
} //End Subclass SetupForm