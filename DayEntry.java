/*
 * Christopher Burgess
 */
package myabsences;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
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
import javafx.stage.Stage;
import org.json.simple.JSONObject;

/**
 *
 * @author Christopher Burgess
 */
//Begin Subclass DayEntry
public class DayEntry extends Application {
  
    static Stage dayEntryStage = new Stage();  // stage for the form
    SimpleDateFormat formatDb = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat formatCal = new SimpleDateFormat("EEEEEEE, MMMMMMMM dd, yyyy"); // date format for title
    ArrayList<JSONObject> dayData;           // all hours data for the day
    ArrayList<JSONObject> typesData;         // absence types
    ArrayList<JSONObject> groupDates;        // Dates of a group of absences
    ArrayList<JSONObject> stats;             // summary stats for hours avaialabe
    String dayDate = "";                     // the db format date for the day
    String[] absenceType = new String[6];    // the user's absence type name
    String[] color = new String[6];          // the user's color for the absence type
    String title = "";                       // the user's title of the absence
    double[] decimalHours = new double[6];   // the decimal hours for the day in db format
    int[] hours = new int[6];                // the hours portion for combobox
    int[] minutes = new int[6];              // the minutes portion for combobox
    int submitted = 0;          // submitted flag from db
    String notes = "";          // the user's notes for the absence (if any)
    String group = "";          // absence group for multiple edits
    int repeatDays = 0;         // Days to repeat the absence
    int[] absenceID = new int[6];           // the id of the absence type
    boolean prePopulated = false;           // flag for day already having data on load
    boolean inGroup = false;                // flag for day is part of a group
    double[] currentAvailable = new double[6]; // used for verification that hours are available now
    String[] hoursAvailable = new String[6];   // to populdate label hoursAvailable
    int rowCounter = 2;     // counts the rows for control positioning
    int typeSize = 0;       // number of types in the db table
    int editAdd = 0;        // number of types added while in edit mode
    int numTypeHours = 0;   // number of types displaying in the form
    int lastPosition = 0;   // row position after a group of hours controls are put in gpane
    double height = 300;    // initial height of stage
    int numTypes = 0;       // number of types available to had hours for
    
    // controls
    TextField tfTitle = new TextField();
    ComboBox<String>[] cboType = new ComboBox[6];
    ComboBox[] cboHours = new ComboBox[6];
    ComboBox[] cboMinutes = new ComboBox[6];
    TextArea taNotes = new TextArea();
    CheckBox ckbSubmitted = new CheckBox("Submitted");
    ComboBox cboRepeat = new ComboBox();
    
    // labels
    Label[] lblType = new Label[6];   
    Label[] lblHours = new Label[6]; 
    Label[] lblMinutes = new Label[6]; 
    Label[] lblHoursAvailable = new Label[6];  
    Label lblRepeat = new Label("Create Group for");
    Label lblRepeatDays = new Label("Days");
    Label lblAbsenceGroup = new Label("");
    Label lblNotes = new Label("Notes:");
    
    // buttons
    Button dayEntryCancel = new Button("Cancel");
    Button dayEntryUpdate = new Button("Update Day");
    Button dayEntrySave = new Button("Save");     
    Button dayEntryDelete = new Button("Delete Day");
    Button dayEntryUpdateGroup = new Button("Update Group");
    Button dayEntryDeleteGroup = new Button("Delete Group");
    Button btnAddType = new Button("+");        // button to add another type hours
    Button btnDeleteHours[] = new Button[6];    // delete type hours button
    
    GridPane formGPane = new GridPane();  // main form area

    /* Constructor
    *
    * dDate - the date of the day button the user pressed to get here
    *
    * This constructor sets the dayDate of the absence
    * and sets the starting height for the stage */
    public DayEntry (String dDate) {   //, ArrayList<JSONObject> statistics
        
        this.dayDate = dDate;
        height = 600;           // set intial stage height
 
    }
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        
        // Get absence data for the day and the absnece types
        dayData = Database.getDayAbsence(dayDate);  // single JSONObject of the day data
        typesData = Database.getAbsenceTypes();     // Arraylist of JSONObject type data
        numTypes = typesData.size();                // number of absence Types that can be planned
        stats = SummaryReportBuilder.getStats();    // get stats for hours available
       
        // ************* if there was data for the day set the variables to it **************
        if (dayData.size() > 0) {
            prePopulated = true;
            putData();     // put data from db into the variables that populate controls
        }    

        if (prePopulated) {dayEntryStage.setHeight(400+(85*numTypeHours));}
        else {dayEntryStage.setHeight(450);}        
        
        Date date = formatDb.parse(dayDate);
        String dateEnglish = formatCal.format(date);
        
        // Define Panes
        BorderPane bPane = new BorderPane();
        HBox topDatePane = new HBox();
        HBox hBoxB = new HBox();
        
        // set the form Date Title and add to top Pane
        Text titleDate = new Text();                                                          
        titleDate.setText(dateEnglish);                                                                                 
        titleDate.getStyleClass().add("daytitle");  
        topDatePane.getChildren().add(titleDate);
        
        // set labels and delete button
        for (int i= 0; i < 6; i++) {
            lblType[i] = new Label("Absence Type " + (i+1) +":");   
            lblHours[i] = new Label("Hours"); 
            lblMinutes[i] = new Label("Minutes"); 
            lblHoursAvailable[i] = new Label("");  
            btnDeleteHours[i] = new Button("x"); 
            Tooltip ttdelete = new Tooltip("Delete These Type Hours");
            ttdelete.getStyleClass().add("ttred");
            btnDeleteHours[i].setTooltip(ttdelete);
        }
        
        // set comboboxes for types - add data from types date 
        for (int i= 0; i < 6; i++) {
            cboType[i] = new ComboBox();
            for (int t = 0; t < typesData.size(); t++) {
                String aType = (String)typesData.get(t).get("Absence_Type");
                cboType[i].getItems().add(aType); 
            }
            cboType[i].setValue("");
        }
        
        // set values for prepopulated form
        for (int i = 0; i < numTypeHours; i++) {
            cboType[i].setValue(absenceType[i]); 
            lblHoursAvailable[i].setText(hoursAvailable[i] + " Available");
        }
        
        // set text field for absence title
        GridPane.setColumnSpan(tfTitle,14);
        if (!title.isEmpty()) {tfTitle.setText(title);}
        
        // set comboboxes for hours 
        for (int i = 0; i < 6; i++) {
            cboHours[i] = new ComboBox();
            for (int h = 0; h < 9; h++) {
                cboHours[i].getItems().add(h);
            }
            if (hours[i] != 0) {cboHours[i].setValue(hours[i]);} 
            else if (i == 0) {cboHours[i].setValue(8);}     // set first hours cbo to a full day's hours 
            else {cboHours[i].setValue(0);}                 // set remaining hours cbos to 0
            cboHours[i].setPrefWidth(25);
        }

        // set comboboxes for minutes 
        for (int i = 0; i < 6; i++) {
            cboMinutes[i] = new ComboBox();
            for (int m = 0; m < 60; m+=5) {    // add in 5 minute increments
                cboMinutes[i].getItems().add(m);
            }
            cboMinutes[i].setValue(minutes[i]);
            cboMinutes[i].setMinWidth(55);
            }
        
        // set checkbox for submitted
        if (submitted == 1) {
            ckbSubmitted.setSelected(true);
        } else {
            ckbSubmitted.setSelected(false);
        }
        Tooltip ttsubmit = new Tooltip("Check this box if time\n has been submitted "
                + "and approved");
        ttsubmit.getStyleClass().add("ttgray");
        ckbSubmitted.setTooltip(ttsubmit);
        
        // set combobox for repeat days
        for (int i = 0; i < 20; i++) {    
            cboRepeat.getItems().add(i+1);
        }
        cboRepeat.setValue(1);
        cboRepeat.setPrefWidth(55);
        Tooltip ttrepeat = new Tooltip("This creates repeating days for this type " +
        "and hours entered.");
        ttrepeat.getStyleClass().add("ttgray");
        lblRepeat.setTooltip(ttrepeat);
        
        // Set Add Type Hours (+) Button attributes
        Tooltip ttadd = new Tooltip("Add Hours for another type");
        ttadd.getStyleClass().add("ttgray");
        btnAddType.setTooltip(ttadd);
        btnAddType.getStyleClass().add("btnplus");
        btnAddType.setMaxSize(25,25);
        btnAddType.setMinSize(25,25);
        btnAddType.setPadding(new Insets(1,1,1,1)); 
        
        // Set Group Button attributes
        dayEntryUpdateGroup.setMaxHeight(22);
        dayEntryUpdateGroup.setMinHeight(22);
        Tooltip updtGrouptt = new Tooltip("Updates all days that are part of this group\n " +
        "with the type and hours entered");
        updtGrouptt.getStyleClass().add("ttgray");
        dayEntryUpdateGroup.setTooltip(updtGrouptt);
        dayEntryDeleteGroup.setMaxHeight(22);
        dayEntryDeleteGroup.setMinHeight(22);
        dayEntryDeleteGroup.setMinWidth(100);
        Tooltip delGrouptt = new Tooltip("Deletes all days that are part of this group");
        delGrouptt.getStyleClass().add("ttgray");
        dayEntryDeleteGroup.setTooltip(delGrouptt);  
        
        // *************** ADD Fields to GridPane *********************
        // title
        GridPane.setConstraints(tfTitle, 1, 1);
        GridPane.setColumnSpan(tfTitle, 10);
        formGPane.getChildren().add(tfTitle);
        
        // add first empty contol group to empty form
        if (!prePopulated) {   
            numTypeHours = 1;
            addHoursControls(0);
        }   
        
        // add prepopulated controls to the gridpane
        if (prePopulated) {
           for (int i = 0; i < numTypeHours; i++) {   
                // add control colors for prepopulated form
                String type = cboType[i].getValue(); // get type from combo box
                String cboColor = JsonMatch.getJsonString(typesData,"Absence_Type",type,"Color");
                Background background = new Background(getBackgroundFill(cboColor));
                cboType[i].setBackground(background);
                cboHours[i].setBackground(background);
                cboMinutes[i].setBackground(background);
                addHoursControls(i);
           }  
           dayEntryStage.setHeight(dayEntryStage.getHeight()-60);
        }
        
        // Add the bottom conrols
        addBottomControls();

        // determine what buttons to add to bottom hbox
        if (prePopulated) {
            hBoxB.getChildren().addAll(dayEntryDelete,dayEntryUpdate,dayEntryCancel);
            Platform.runLater(() -> {
                dayEntryCancel.requestFocus();  // Set focus on cancel if prepopulated
            });
        } else {
            hBoxB.getChildren().addAll(dayEntrySave,dayEntryCancel);
        }
        
        // **** set topDatePane attributes ****
        topDatePane.setAlignment(Pos.TOP_CENTER);
        topDatePane.setPadding(new Insets(5, 5, 5, 5));
        topDatePane.getStyleClass().add("formtop");
        topDatePane.setPrefHeight(35);        

        // **** set formGPane attributes ****
        formGPane.setAlignment(Pos.TOP_CENTER);
        formGPane.setPadding(new Insets(5, 5, 5, 5));
        bPane.setPadding(new Insets(5, 5, 5, 5));
        formGPane.getStyleClass().add("dayentry");
        formGPane.setHgap(20);
        formGPane.setVgap(5);
        formGPane.setMinHeight(200);
                      
        // **** set hBoxB (bottom button Pane) attributes ****
        hBoxB.setAlignment(Pos.CENTER);
        hBoxB.setSpacing(20);
        dayEntryCancel.setMaxWidth(60);
        HBox.setMargin(dayEntryCancel, new Insets(5, 5, 5, 5));
        
        
        /**** set panes in stage and show stage ****/
        bPane.setCenter(formGPane);
        bPane.setTop(topDatePane);
        bPane.setBottom(hBoxB);
        Scene dayFormScene = new Scene(bPane); 
        dayEntryStage.setMinHeight(475);
        dayEntryStage.setMaxWidth(375);
        dayEntryStage.setMinWidth(375);
        dayFormScene.getStylesheets().add(getClass().getResource("StyleSheet.css").toExternalForm());
        dayEntryStage.setTitle("Day Hours Entry");
        dayEntryStage.setScene(dayFormScene);
        dayEntryStage.show();   
        
        // ******** Controls Event Handlers *************
        
        // Delete type hours button handler(x) - delete a type hours entry
        for (int i = 0; i < 6; i++) {
            final int num = i;
            btnDeleteHours[i].setOnAction(e->{
                try {
                    DayEntry app=new DayEntry(dayDate);
                    deleteTypeHours(num);
                    numTypeHours--;         // Decrease number of type hours in form
                    MyAbsences.refresh();
                    dayEntryStage.close();
                    app.start(primaryStage);
                } catch(Exception ex) {
                    // catch
                }
            });      
        }         
        
        // Save day button handler
        dayEntrySave.setOnAction(e-> {
            try {
                if (validateData("Save Day")) {
                    getValues();   // get validated values from the controls
                    insertAbsence();   
                    MyAbsences.refresh();
                    dayEntryStage.close();
                }
            } catch (Exception save) {
                
            }
        });
        
        // Update day button handler
        dayEntryUpdate.setOnAction(e-> {
            try {
                if (validateData("Update Day")) {
                    updateAbsenceType();  // update hours table if type was changed
                    getValues();          // get validated values from the controls
                    updateAbsence();
                    dayEntryStage.close(); 
                    MyAbsences.refresh();
                }
            } catch (Exception update) {
                
            }
        });
        
        // Update group button handler
        dayEntryUpdateGroup.setOnAction(e-> {
            try {
                if (validateData("Update Group")) {
                    getValues();   // get validated values from the controls
                    updateAbsenceGroup();
                    dayEntryStage.close(); 
                    MyAbsences.refresh();
                }
            } catch (Exception update) {
                
            }
        });

        // Delete day button handler
        dayEntryDelete.setOnAction(e-> {
            try {
            deleteAbsence();
            dayEntryStage.close(); 
            MyAbsences.refresh();
            } catch (Exception delete) {
                
            }
        });    
        
        // Delete Group button handler
        dayEntryDeleteGroup.setOnAction(e-> {
            try {
            deleteAbsenceGroup();
            dayEntryStage.close(); 
            MyAbsences.refresh();
            } catch (Exception delete) {
                
            }
        });  
        
        // Cancel button handler
        dayEntryCancel.setOnAction(e-> {
            try {
            dayEntryStage.close(); 
            } catch (Exception cancel) {
                
            }
        });
        
        // Type Combobox Handler - Set background color of controls to selected type color
        for (int i = 0; i < 6; i++) {
            final int num = i;
            cboType[i].setOnAction(e->{
                try {
                    lblType[num].getStyleClass().clear();
                    String type = cboType[num].getValue(); // get type from combo box
                    double aRate = JsonMatch.getJsonDouble(typesData,"Absence_Type",type,"Accrual_Rate");
                    if (aRate == -1) {ckbSubmitted.setSelected(true);}
                    else {ckbSubmitted.setSelected(false);}
                    String cboColor = JsonMatch.getJsonString(typesData,"Absence_Type",type,"Color");
                    Background background = new Background(getBackgroundFill(cboColor));
                    cboType[num].setBackground(background);
                    cboHours[num].setBackground(background);
                    cboMinutes[num].setBackground(background);
                    setRemainingHours(num);
                }
                catch(Exception ex) {
                    // catch
                }
            });
        }
        
        // Add another type (+) button handler
        btnAddType.setOnAction(e-> {
            try {   
                removeBottomControls(); // remove bottom controls to make room for new hours entry
                if (prePopulated) {editAdd++;}              // increse number of hour types added when editing prepopulated form 
                updateComboBoxes();
                addHoursControls(numTypeHours); 
                numTypeHours++;  // increase number of hour types in form
                addBottomControls();
                dayEntryStage.setHeight(dayEntryStage.getHeight()+85);
            } catch (Exception cancel) {
                
            }
        });     

    }
    
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
    
    private void setRemainingHours(int i) {
        
        if (JsonMatch.getJsonDouble(typesData,"Absence_Type",cboType[i].getValue(),"Accrual_Rate") >= 0) {
           String rHours = JsonMatch.getJsonString(stats,"Absence_Type",cboType[i].getValue(),"Remaining_Hours");
           double dHours = Double.valueOf(JsonMatch.getJsonString(stats,"Absence_Type",cboType[i].getValue(),"Remaining_Hours"));
           String rDayH = JsonMatch.getJsonString(stats,"Absence_Type",cboType[i].getValue(),"Remaining_DayHours"); 
           if (dHours > 0) {rDayH = rDayH + "Available";} 
           else {rDayH = "No Time Available!";}
           lblHoursAvailable[i].setText(rDayH);
           currentAvailable[i] = Double.parseDouble(rHours);
        } else {                
           lblHoursAvailable[i].setText("");  // no remaining to show for Add-In types
        }    
    } // end setRemainingHours
    
    
    /* private addHoursControls
    *
    * num - the index of the hours controls being added
    * 
    * This method adds one set of type hours controls between the top and bottom 
    * controls, placed by row number using rowCounter  */
    private void addHoursControls(int num) {

        // ********* Hours controls group ****************
        // Absence Type label
        GridPane.setConstraints(lblType[num], 1, rowCounter);
        GridPane.setColumnSpan(lblType[num], 3);
        formGPane.getChildren().add(lblType[num]);
        // absence type combobox
        GridPane.setConstraints(cboType[num], 4, rowCounter);
        GridPane.setColumnSpan(cboType[num], 7);
        formGPane.getChildren().add(cboType[num]);    
        // Hours combobox
        GridPane.setConstraints(cboHours[num], 1, rowCounter+1);
        GridPane.setColumnSpan(cboHours[num], 3);
        formGPane.getChildren().add(cboHours[num]);    
        // hours label
        GridPane.setConstraints(lblHours[num], 3, rowCounter+1);
        GridPane.setColumnSpan(lblHours[num], 3);
        formGPane.getChildren().add(lblHours[num]);        
        // Minutes combobox
        GridPane.setConstraints(cboMinutes[num], 4, rowCounter+1);
        GridPane.setColumnSpan(cboMinutes[num], 2);
        formGPane.getChildren().add(cboMinutes[num]);
        // mintues label
        GridPane.setConstraints(lblMinutes[num], 7, rowCounter+1);
        GridPane.setColumnSpan(lblMinutes[num], 3);
        formGPane.getChildren().add(lblMinutes[num]);
        // add delete type hours button
        if (prePopulated && editAdd == 0) {
            GridPane.setConstraints(btnDeleteHours[num], 10, rowCounter+1);
            btnDeleteHours[num].setMinSize(20, 20);
            btnDeleteHours[num].setMaxSize(20, 20);
            btnDeleteHours[num].getStyleClass().add("btndelete");
            GridPane.setColumnSpan(btnDeleteHours[num], 2);  
            formGPane.getChildren().add(btnDeleteHours[num]);
        }        
        // add label hours available
        GridPane.setConstraints(lblHoursAvailable[num], 1, rowCounter+2);
        GridPane.setColumnSpan(lblHoursAvailable[num],10);
        formGPane.getChildren().add(lblHoursAvailable[num]); 

        // set focus on new control group
        cboType[num].requestFocus();
        rowCounter+=3;      // increase rowCounter for next row to add
  
        lastPosition = rowCounter;  // lost position in form used
    }
    
    /* private addBottomControls
    * 
    * This method adds the common controls to the bottom of the form.  They are 
    * added separately after the hours fields for each type are added since they 
    * will change position after adding or removing hours entries. */
    private void addBottomControls() {
 
        // *******  Add at end of hour entries **********
        // Add the + button   
        if (numTypeHours < numTypes) {   // limited to max number of hour types ---
            btnAddType.setMinWidth(10);
            GridPane.setConstraints(btnAddType, 1, lastPosition);
            GridPane.setColumnSpan(btnAddType, 8);
            formGPane.getChildren().add(btnAddType);  
            lastPosition++;
        }  

        // Submitted checkbox
        GridPane.setConstraints(ckbSubmitted, 1, lastPosition);
        GridPane.setColumnSpan(ckbSubmitted, 8);
        formGPane.getChildren().add(ckbSubmitted);   
        
        // add optional fields
        if (!prePopulated) {addToggleFields();}  // add repeat day fields
        if (inGroup) {addGroupButtons();}        // add group buttons      
        
        // add label notes 
        
        GridPane.setConstraints(lblNotes, 1, lastPosition+1);
        GridPane.setColumnSpan(lblNotes, 2);
        formGPane.getChildren().add(lblNotes);
        // text field notes
        GridPane.setConstraints(taNotes, 1, lastPosition+2);
        GridPane.setColumnSpan(taNotes,10);
        taNotes.setPrefWidth(300);
        taNotes.setPrefHeight(100);
        if (!notes.isEmpty()) {taNotes.setText(notes);}
        formGPane.getChildren().add(taNotes);
          
    } // end addBottomContols
    
    /* private removeBottomControls
    *
    * This method removes the controls under the hours entries, to make room for 
    * new hours entries when added. They are then replace using addBottomControls() */
    private void removeBottomControls() {
        
        formGPane.getChildren().remove(btnAddType);
        formGPane.getChildren().remove(ckbSubmitted);
        formGPane.getChildren().remove(lblNotes);
        formGPane.getChildren().remove(taNotes);
        formGPane.getChildren().remove(lblRepeat);
        formGPane.getChildren().remove(cboRepeat);
        formGPane.getChildren().remove(lblRepeatDays);
        formGPane.getChildren().remove(dayEntryUpdateGroup);
        formGPane.getChildren().remove(dayEntryDeleteGroup);        
    } // end removeBottomControls
 
    /* addToggleFields()
    *
    * This method toggles the fields for a repeat entry to only show on empty form */   
    private void addToggleFields() {
        
        // Repeat 
        GridPane.setConstraints(lblRepeat,1,lastPosition+1);
        GridPane.setColumnSpan(lblRepeat, 3);
        formGPane.getChildren().add(lblRepeat);
        // combobox repeat
        GridPane.setConstraints(cboRepeat, 4, lastPosition+1);
        GridPane.setColumnSpan(cboRepeat, 3);
        formGPane.getChildren().add(cboRepeat);  
        // label days
        GridPane.setConstraints(lblRepeatDays,7,lastPosition+1);
        GridPane.setColumnSpan(lblRepeatDays, 4);
        formGPane.getChildren().add(lblRepeatDays); 
        
        lastPosition++;

    } // end addToggleFields
    
    /* addGroupButtons()
    *
    * This method adds the fields for update and delete group to a group day */      
    private void addGroupButtons() {

        // update group button
        GridPane.setConstraints(dayEntryUpdateGroup,1,lastPosition+2);
        GridPane.setColumnSpan(dayEntryUpdateGroup, 3);
        formGPane.getChildren().add(dayEntryUpdateGroup);
        // delete group button
        GridPane.setConstraints(dayEntryDeleteGroup, 4, lastPosition+2);
        GridPane.setColumnSpan(dayEntryDeleteGroup, 5);
        formGPane.getChildren().add(dayEntryDeleteGroup);     
        
        lastPosition+=2;
  
    } // end addGroupButtons

    /* private putData
    *
    * This method puts the data from the database into the variables used to 
    * populate the form when day is prePopulated. */
    private void putData() {
        
        numTypeHours = dayData.size();

        // Set common variables from dayData
        title = (String)dayData.get(0).get("Title");
        submitted = (int)dayData.get(0).get("Submitted");
        notes = (String)dayData.get(0).get("Notes");
        group = (String)dayData.get(0).get("Absence_Group");
        if (!group.isEmpty()) {inGroup = true;}

        // set hours specific data from dayData
        for (int i = 0; i < numTypeHours; i++) {    
            absenceType[i] = (String)dayData.get(i).get("Absence_Type");
            color[i] = (String)dayData.get(i).get("Color");
            decimalHours[i] = (double)dayData.get(i).get("Hours");
            absenceID[i] = getAbsenceTypeID(absenceType[i]); 
            hoursAvailable[i] = (JsonMatch.getJsonString(stats,"Absence_Type",absenceType[i],"Remaining_DayHours"));
            // add the already scheduled hours to availabe so on update existing hours will still pass verification if all hours are used
            currentAvailable[i] = Double.valueOf(JsonMatch.getJsonString(stats,"Absence_Type",absenceType[i],"Remaining_Hours"))+decimalHours[i];
        }
        setHoursMinutes();  // convert decimal hours from db to hours and minutes
        
    } // end putData
    
    /* private insertAbsence
    *
    * Inserts the absence data from the controls into the database if it is a new entry   */ 
    private void insertAbsence() {
         
        if (repeatDays > 1) {group = dayDate;}
        String sql = "";
        
        // iterate through the hours entries and insert to Hours table  
        for (int h = 0; h < numTypeHours; h++) {
            sql = "INSERT into Hours (Date, Absence_ID, Hours, Absence_Group) " +
            "VALUES ('" + dayDate + "', '" + absenceID[h] + "', '" + decimalHours[h] 
            + "', '" + group + "')";      
            Database.SQLUpdate(sql); 
        }    
        // insert common fields to Absences table
        sql = "INSERT into absences (Date, Title, Submitted, Notes, Absence_Group) " +
        "VALUES ('" + dayDate + "', '" + title + "', '"
        + submitted + "', '" + notes + "', '" + group + "')";       
        Database.SQLUpdate(sql);

        // Repeat insert for Add Days
        int i = 0;
        while (i < repeatDays-1) {
            // increment date
            String nextDay =  LocalDate.parse(dayDate).plusDays(1).toString();
            LocalDate date1 = LocalDate.parse(nextDay);
            String dayOfWeek = date1.getDayOfWeek().toString();
            dayDate = nextDay;   
            // iterate through the hours entries and insert to Hours table
            for (int h = 0; h < numTypeHours; h++) {
                if (!dayOfWeek.equals("SATURDAY") && !dayOfWeek.equals("SUNDAY")) {
                    sql = "INSERT into Hours (Date, Absence_ID, Hours, Absence_Group) " +
                    "VALUES ('" + dayDate + "', '" + absenceID[h] + "', '" + decimalHours[h]
                    + "', '" + group + "')";       
                    Database.SQLUpdate(sql);                    
                }
            }
            // insert common fields to Absences table
            if (!dayOfWeek.equals("SATURDAY") && !dayOfWeek.equals("SUNDAY")) {
                // insert common fields to Absences table
                sql = "INSERT into absences (Date, Title, Submitted, Notes, Absence_Group) " +
                "VALUES ('" + dayDate + "', '" + title + "', '"
                + submitted + "', '" + notes + "', '" + group + "')";       
                Database.SQLUpdate(sql);
                i++;
            } 
        }
    } // end insertAbsence 
    
    /* private updateAbsenceType
    *
    * Updates the hours table if an existing type was changed since form loaded.
    * Deletes the old type and inserts the new type user changed the type to  */ 
    private void updateAbsenceType() {
    
    for (int h = 0; h < numTypeHours-editAdd; h++) {  
            if (!absenceType[h].equals(cboType[h].getValue())) {
                // Delete the previous entry
               String sql = "DELETE from Hours " + 
               "WHERE Date = '" + dayDate + "' and Absence_ID = '" + absenceID[h] + "'";
               Database.SQLUpdate(sql);
               // insert the new absence type to Hours
               sql = "INSERT into Hours (Date, Absence_ID, Hours, Absence_Group) " +
               "VALUES ('" + dayDate + "', '" + getAbsenceTypeID(cboType[h].getValue()) + "', '" + decimalHours[h] 
               + "', '" + group + "')";   
               Database.SQLUpdate(sql);       
            }
        }
    } // end updateAbsenceType
    
    /* private updateAbsence
    *
    * Updates the absences table with data from the form, if already an absence on the day 
    * builds the update string and calls the database SQLUpdate method*/ 
    private void updateAbsence() {
        
        int updateNum = numTypeHours-editAdd;

        // update existing hours in Hours - if changed delete and insert new type
        for (int h = 0; h < updateNum; h++) {
            String sql = "UPDATE Hours " + "SET Absence_ID = '" 
            + absenceID[h] + "', Hours = '" + decimalHours[h] + "', Absence_Group = '" + group + "' "
            + "WHERE Date = '" + dayDate + "' and Absence_ID = '" + absenceID[h] +  "'";
            Database.SQLUpdate(sql);
        }
        
        // insert new hours to Hours added to prePopulated form
        for (int h = updateNum; h < numTypeHours; h++) {
            String sql = "INSERT into Hours (Date, Absence_ID, Hours, Absence_Group) " +
            "VALUES ('" + dayDate + "', '" + absenceID[h] + "', '" + decimalHours[h] 
            + "', '" + group + "')";      
            Database.SQLUpdate(sql); 
        }           
        
        // update absence in Absences
        String sql = "UPDATE Absences " +
        "SET Title = '" + title + "', Submitted = '"
        + submitted + "', Notes = '" + notes + "', " +  " Absence_Group = '" + group + "' "
        + "WHERE Date = '" + dayDate + "'";
        Database.SQLUpdate(sql);
         
    } // end updateAbsence
    
    /* private updateAbsenceGroup
    *
    * Updates the absences table with data from the form, for a group of absence dates 
    * builds the update string and calls the database SQLUpdate method*/ 
    private void updateAbsenceGroup() {
        
        int updateNum = numTypeHours-editAdd;
        
        // update existing hours in Hours table that are part of the group
        for (int h = 0; h < updateNum; h++) {
            String sql = "UPDATE Hours " + "SET Absence_ID = '"  //Date = '" + dayDate + "', 
            + absenceID[h] + "', Hours = '" + decimalHours[h] + "' "
            + "WHERE Absence_Group = '" + group + "' and Absence_ID = '" + absenceID[h] +  "'";
            Database.SQLUpdate(sql);
        }

        // insert new hours to Hours added to prePopulated form that are in group
        // Repeat insert for Add Days        
        int repeatInsert = Database.getGroupSize(group);
        System.out.println("repeat insert is " + repeatInsert);
        
        String today =  LocalDate.parse(dayDate).plusDays(0).toString();
        LocalDate date1 = LocalDate.parse(today);
        String dayOfWeek = date1.getDayOfWeek().toString();       
        int i = 0;
        while (i < repeatInsert) {
            // iterate through the hours entries and insert to Hours table
            for (int h = updateNum; h < numTypeHours; h++) {
                if (!dayOfWeek.equals("SATURDAY") && !dayOfWeek.equals("SUNDAY")) {
                    String sql = "INSERT into Hours (Date, Absence_ID, Hours, Absence_Group) " +
                    "VALUES ('" + dayDate + "', '" + absenceID[h] + "', '" + decimalHours[h]
                    + "', '" + group + "')"; 
                    Database.SQLUpdate(sql);                    
                } else {i--;}    // don't count a weekend day as a repeat insert
            }
            // increment date
            String nextDay =  LocalDate.parse(dayDate).plusDays(1).toString();
            date1 = LocalDate.parse(nextDay);
            dayOfWeek = date1.getDayOfWeek().toString();
            dayDate = nextDay; 
            i++;           
        }
    
        // update common fields in Absences table that are in group
        String sql = "UPDATE absences " +
        "SET Title = '" + title + "', Submitted = '"
        + submitted + "', Notes = '" + notes + "' " +
        "WHERE Absence_Group = '" + group + "' ";
        Database.SQLUpdate(sql);
     
    } // end updateAbsenceGroup

    /* private deleteAbsence
    *
    * Deletes the absence in the absences table for the dayDate  
    * Builds the delete string and calls the Database SQLUpdate method */ 
    private void deleteAbsence() {
        
        getValues();   // get current values from the controls
        
        // Delete all type hours in Hours table
        for (int h = 0; h < numTypeHours; h++) {
            String sql = "DELETE from Hours " + 
            "WHERE Date = '" + dayDate + "'";
            Database.SQLUpdate(sql);
        }
        
        // Delete from Absences table
        String sql = "DELETE from absences " + 
        "WHERE Date = '" + dayDate + "'";
        Database.SQLUpdate(sql);
         
    }   // end deleteAbsences
    
    /* private deleteAbsence
    *
    * Deletes the absence in the absences table for the dayDate  
    * Builds the delete string and calls the Database SQLUpdate method */ 
    private void deleteAbsenceGroup() {
        
        getValues();   // get current values from the controls
        
        // Delete all type hours in the Hours table in group
        for (int h = 0; h < numTypeHours; h++) {
            String sql = "DELETE from Hours " + 
            "WHERE Absence_Group = '" + group + "'";
            Database.SQLUpdate(sql);            
        }
        // delete all the days that are in the group
        String sql = "DELETE from absences " + 
        "WHERE Absence_Group = '" + group + "'";
        Database.SQLUpdate(sql);
        
    } // end deleteAbsenceGroup
    
    private void deleteTypeHours(int i) {
        
        int repeatDelete = 0;
        if (!group.isEmpty()) {
            repeatDelete = Database.getGroupSize(group);
        } 
        
        boolean deleteAll = false;
        if (repeatDelete > 0) {
            if (Validate.confirmDeleteTypeHours(absenceType[i])) {
                deleteAll = true;
            }
        }
        
        if (deleteAll) {
            String sql = "DELETE from Hours " + 
            "WHERE Absence_Group = '" + group + "' and Absence_ID = '" +
            absenceID[i] + "'";
            Database.SQLUpdate(sql);             
        } else {
            String sql = "DELETE from Hours " + 
            "WHERE Date = '" + dayDate + "' and Absence_ID = '" +
            absenceID[i] + "'";
            Database.SQLUpdate(sql);
        }

        // if this is the last type hours left, delete the day from absences as well
        if ((numTypeHours-editAdd) == 1) {
            // Delete from Absences table
            String sql = "DELETE from absences " + 
            "WHERE Date = '" + dayDate + "'";
            Database.SQLUpdate(sql);    
        }

    }
    
    /* private getValues
     *
     * Gets the values currently in the form controls to the class variables 
     * which will get validated before being put in the database */ 
    private void getValues() {
         
        for (int i = 0; i < numTypeHours; i++) {
            absenceType[i] = (cboType[i].getValue());
            absenceID[i] = getAbsenceTypeID(absenceType[i]);
            hours[i] = (int)cboHours[i].getValue();
            minutes[i] = (int)cboMinutes[i].getValue();
            setHoursDecimal(i);   // sets decimalHours
        }    
            title = tfTitle.getText();
            if (ckbSubmitted.isSelected()){submitted = 1;}
                else {submitted = 0;}
            notes = taNotes.getText();
            repeatDays = (int)cboRepeat.getValue();
        
    } // end getValues
    
    /* private validateData
    *
    * buttonPushed - the button event that called the method, needed for 
    *                knowing if updating a group for repeat days hours validation. 
    * ==> true if data from controls is validated
    *
    * The method validates the data in the form. 
    * It validates that an absence type name was selected from the combobox, 
    * and that the hours selected are avaialble for the absence type. 
    * Called in event handlers for update, save and insert */
    private boolean validateData (String buttonPushed) {
        
        boolean validated = false;
        int numValidate = numTypeHours + 1;
        boolean[] valid = new boolean[numValidate];
        double totalHours = 0.0;     // total hours planned on day

        for (int i = 0; i < numTypeHours; i++) {
            valid[i] = false;
            double aRate = JsonMatch.getJsonDouble(typesData,"Absence_Type",cboType[i].getValue(),"Accrual_Rate");
            if (editAdd > 0 && buttonPushed.equals("Update Group")) {repeatDays = Database.getGroupSize(dayDate);}    // for validating a new type added to a group
            else {repeatDays = (int)(cboRepeat.getValue());}
            double hoursDecimal = getHoursDecimal((int)cboHours[i].getValue(),(int)cboMinutes[i].getValue());
            double hoursToValidate = hoursDecimal * repeatDays;           
            System.out.println("Hours to validate is " + hoursToValidate);
            totalHours+=hoursDecimal;         // add all the hours being scheduled in the day, for validation after loop
            if (aRate == -1) {hoursToValidate = 0;}  // Don't validate Add-In hours
            if (Validate.notEmpty("Absence Type " + (i+1), cboType[i].getValue())) {  
                lblType[i].setTextFill(Color.BLACK);
                if (Validate.availableHours(cboType[i].getValue(),hoursToValidate,currentAvailable[i])) {
                    lblHoursAvailable[i].setTextFill(Color.BLACK);
                    valid[i] = true; 
                } else {
                    lblHoursAvailable[i].setTextFill(Color.RED);
                    cboHours[i].requestFocus();  // Set focus on type if not chosen 
                }             
            } else {   
                cboType[i].requestFocus();  // Set focus on type if not chosen
                lblType[i].setTextFill(Color.RED); 
            }     
        }
        
        // validate hours in the day are not over hours available in a day
        valid[numTypeHours] = false;
        if (Validate.hoursInDay(dayDate, totalHours, 8.0)) {
            valid[numTypeHours] = true;
        }

        // check that all types validated
        int numValidated = 0;
        for (int v = 0; v < numValidate; v++) {
            if (valid[v]) {numValidated++;}
        }
        if (numValidated == numValidate) {validated = true;}
        return validated;
        
    } // end validateData
   
    
    /* private setHoursMinutes 
    *
    * Sets values in the arrays of hours and minutes from the 
    * decimal hours values in the decimalHours array */
    private void setHoursMinutes () {
        
    for (int i = 0; i < dayData.size(); i++) {
        hours[i] = (int)decimalHours[i];
        double fractional = decimalHours[i] - hours[i];
        minutes[i] = (int)Math.rint(fractional * 60.0);
    }

    } // End setHoursMinutes

    /* private getHoursDecimal
    *
    * hours - hours that were set
    * mintues - minutes that were set
    * 
    * returns a decimal hours converstion from hours and mintues */
    private double getHoursDecimal(int hours, int minutes) {
        
        float dblMinutes = Float.valueOf(minutes);
        float decMinutes = dblMinutes/60;
        float dblHours = Float.valueOf(hours);
        float decHours = (dblHours + decMinutes);
        double decimalHours = decHours;
        
        return decimalHours;
 
    } // End getHoursDecimal
    
    /* private setHoursDecimal
    * 
    * i - the array element index 
    *
    * Converts an element in the hours and minutes arrays
    * to decimalHours, and stores result in the decimalHours array element */
    private void setHoursDecimal(int i) {
        
        float dblMinutes = Float.valueOf(minutes[i]);
        float decMinutes = dblMinutes/60;
        float dblHours = Float.valueOf(hours[i]);
        float decHours = (dblHours + decMinutes);
        decimalHours[i] = decHours;
 
    } // End getHoursDecimal
    
    /* private getAbsenceTypeID
    *
    * absence_Type - the absence type of the absence
    * => The absenceID for the type
    *
    * Gets the absence_ID for an absence type              */
    private int getAbsenceTypeID(String absence_Type) {
        
        int ID = 0;
        
        for (int i = 0; i < typesData.size(); i++) {
            if (typesData.get(i).get("Absence_Type").equals(absence_Type)) {
                ID = (Integer)typesData.get(i).get("Absence_ID");
            }
        }
        
        return ID;
    }
    /* private updateComboBoxes
    *
    * This method updates remaining type comboboxes to remove items selected in previous comboboxes
    * when the + button is clicked to add new type hours*/
    private void updateComboBoxes() {
        
        ArrayList<String> used = new ArrayList<>();
        
        // create a list of used absence types
        for (int i = 0; i < numTypeHours; i++) {
            used.add(cboType[i].getValue());
        }
        
        // add types to remaining comboboxes only if type is not already used
        for (int i = numTypeHours; i < 6; i++) {
            cboType[i].getItems().clear();
            for (int t = 0; t < numTypes; t++) {
                if (!(used.contains((String)typesData.get(t).get("Absence_Type")))) {
                   cboType[i].getItems().add((String)typesData.get(t).get("Absence_Type"));
               }
            }
        }
    }
    
} // end class DayEntry 
