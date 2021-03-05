/*
 * Christopher Burgess
 */
package myabsences;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
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
    double[] accrualRate =  new double[6];
    double[] maxAccrual =  new double[6];
    String year = "";
    boolean prePopulated = false;
    String colors[] = new String[] {"Green","Red","Purple","Orange","Blue","Yellow"}; 
    Text setupTitle = new Text("Setup");
    int rowCounter = 1;
    int typesCounter = 0;
    int typeSize = 0;
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
    
    GridPane gPane = new GridPane();
    
    /* Constructor
    *
    *
    *                   */
    public SetupForm(String year) {
        
        this.year = year;
        
        // get absenceType data from database
        absenceTypes = Database.getAbsenceTypes();  // Arraylist of JSONObject type data
        System.out.println("types size is " + absenceTypes.size());
        typeSize = absenceTypes.size();
        if (typeSize > 0) {
            System.out.println("Form is PrePopulated");
            prePopulated = true;
        }    
    }   
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        
        /* Main pane */
        BorderPane bPane = new BorderPane();
        
        HBox topSetupPane = new HBox();
        
        gPane.setAlignment(Pos.TOP_LEFT);
        gPane.setPadding(new Insets(5, 5, 5, 5));
        bPane.setPadding(new Insets(5, 5, 5, 5));
        gPane.getStyleClass().add("summaryreport");
        gPane.setHgap(20);
        gPane.setVgap(4);
        //gPane.setPrefHeight(300);
                      
        HBox hBoxB = new HBox();
        Button btnExit = new Button("Exit");
        hBoxB.setAlignment(Pos.CENTER);
        hBoxB.setSpacing(50);
        HBox.setMargin(btnExit, new Insets(5, 5, 5, 5));
        hBoxB.setPrefHeight(35);
        

        // **** Form Fields *****
        // comboboxes for colors  - add color options
        for (int i = 0; i < 6; i++ ) {
            cboAbsenceColor[i] = new ComboBox();
            for (int c = 0; c < 6; c++) {    // add each color
                cboAbsenceColor[i].getItems().add(colors[c]);
                cboAbsenceColor[i].setPrefWidth(55);
            }
        }    
        
        //cboBalanceType[0].getItems().add("Accrued Hours");
        // Balance Type combo boxes - add options
        for (int i = 0; i < 6; i++ ) {
            cboBalanceType[i] = new ComboBox();
            cboBalanceType[i].getItems().add("Accrued Hours");
            cboBalanceType[i].getItems().add("Fixed Hours");
            cboBalanceType[i].getItems().add("Add-In Hours");
            cboBalanceType[i].setPrefWidth(55);
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
        
        if (prePopulated) {
            putValues();
        }
        

        // determine what buttons to add to bottom hbox
        if (prePopulated) {
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
        Scene listReportScene = new Scene(bPane); 
        setupStage.setMaxHeight(600);
        setupStage.setMinHeight(600);
        setupStage.setMinWidth(660);
        setupStage.setMaxWidth(660);
        listReportScene.getStylesheets().add(getClass().getResource("StyleSheet.css").toExternalForm());
        setupStage.setTitle("Setup");
        setupStage.setScene(listReportScene);
        setupStage.show();   
        
           // ** Button Event Handlers **
        btnSetupSave.setOnAction(e-> {
            try {
            //insertTypes();   
            MyAbsences.refresh();
            setupStage.close(); 
            } catch (Exception save) {
                
            }
        });
        
        btnSetupUpdate.setOnAction(e-> {
            try {
                //updateTypes();
                setupStage.close(); 
                MyAbsences.refresh();
            } catch (Exception update) {
                
            }
        });   
        
        btnSetupExit.setOnAction(e-> {
            try {
                setupStage.close(); 
            } catch (Exception exit) {
                
            }
        });
        
        btnAddType.setOnAction(e-> {
            try {
                rowCounter+=3;
                typesCounter++;
                addDefaultControls(typesCounter);
            } catch (Exception exit) {
                
            }
        });        
        
        // Type Combobox Handler
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
    
    } // end start
    
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
        //cboAbsenceColor[num] = new ComboBox();
        cboAbsenceColor[num].setMinWidth(100);
        GridPane.setConstraints(cboAbsenceColor[num], 17, rowCounter);
        GridPane.setColumnSpan(cboAbsenceColor[num], 5);
        gPane.getChildren().add(cboAbsenceColor[num]);
        // Balance Type label
        lblBalanceType[num] = new Label();
        lblBalanceType[num].setText("Balance Type");
        GridPane.setConstraints(lblBalanceType[num], 1, rowCounter+1);
        GridPane.setColumnSpan(lblBalanceType[num], 4);
        gPane.getChildren().add(lblBalanceType[num]); 
        // Balance Type combobox
        //cboBalanceType[num] = new ComboBox();
        cboBalanceType[num].setMinWidth(125);
        GridPane.setConstraints(cboBalanceType[num], 5, rowCounter+1);
        GridPane.setColumnSpan(cboBalanceType[num], 5);
        gPane.getChildren().add(cboBalanceType[num]);        
        // Add the + button
        if (num < 5) {
            btnAddType.setMinWidth(10);
            GridPane.setConstraints(btnAddType, 1, rowCounter+2);
            GridPane.setColumnSpan(btnAddType, 8);
            gPane.getChildren().add(btnAddType);  
        }
    }
    
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
        GridPane.setColumnSpan(tfAccrualRate[num], 3);
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
        GridPane.setConstraints(tfMaxAccrual[num], 23, place);
        GridPane.setColumnSpan(tfMaxAccrual[num], 2);
        gPane.getChildren().add(tfMaxAccrual[num]);  
    }
    
    private void removeAccruedControls(int num) {
        
        gPane.getChildren().remove(lblAccrualRate[num]);
        gPane.getChildren().remove(tfAccrualRate[num]); 
        gPane.getChildren().remove(lblMaxAccrual[num]); 
        gPane.getChildren().remove(tfMaxAccrual[num]);
    }
    
    private void putValues() {
        
            // set data in the controls that was already saved 
            for (int i = 0; i < typeSize; i++) {
                addDefaultControls(i);
                tfAbsenceType[i].setText((String)absenceTypes.get(i).get("Absence_Type"));
                System.out.println("Absence Color " + i + " is " + (String)absenceTypes.get(i).get("Color"));
                cboAbsenceColor[i].setValue((String)absenceTypes.get(i).get("Color"));
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
    
    private void getValues() {
    //            // set data in the controls that was already saved 
//            for (int i = 0; i < typeSize; i++) {
//                
//                
//                absenceID[i] = (Integer)absenceTypes.get(i).get("Absence_ID");
//                absenceType[i] = 
//                absenceColor[i] = 
//                accrualRate[i] = 
//                maxAccrual[i] = 
//                double arate = 
//                if (arate > 0) {balanceType[i] = "Accrued Hours";}
//                if (arate = 0) {balanceType[i] = "Fixed Hours";}
//                if (arate = -1) {balanceType[i] = "Add-In Hours";}
//            }
    }
    
} //End Subclass SetupForm