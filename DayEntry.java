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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
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
    JSONObject dayData;         // a specific absence day data
    ArrayList<JSONObject> typesData;
    ArrayList<JSONObject> groupDates;  // Dates of a group of absences
    ArrayList<JSONObject> stats;
    Validate validate = new Validate();
    String dayDate = "";        // the db format date for the day
    String absenceType = "";    // the user's absence type name
    String color = "";          // the user's color for the absence type
    String title = "";          // the user's title of the absence
    double decimalHours = 0;    // the decimal hours for the day in db format
    int hours = 0;              // the hours portion for combobox
    int minutes = 0;            // the minutes portion for combobox
    int submitted = 0;          // submitted flag from db
    //boolean holdiay = false;    // flag for Holiday type
    String notes = "";          // the user's notes for the absence (if any)
    String group = "";          // absence group for multiple edits
    int repeatDays = 0;         // Days to repeat the absence
    int absenceID = 0;          // the id of the absence type
    boolean prePopulated = false; // flag for day already having data on load
    boolean inGroup = false;      // flag for day part of a group
    double currentAvailable = 0.0; // used for verification that hours are available now
    double currentRemaining = 0.0; // used for verification that hours are available by year end
    
    // controls
    TextField tfTitle = new TextField();
    ComboBox<String> cboType = new ComboBox<>();
    ComboBox cboHours = new ComboBox();
    ComboBox cboMinutes = new ComboBox();
    TextArea taNotes = new TextArea();
    CheckBox ckbSubmitted = new CheckBox("Submitted");
    ComboBox cboRepeat = new ComboBox();
    
    // labels
    Label lblType = new Label("Absence Type:");
    Label lblHours = new Label("Hours");
    Label lblMinutes = new Label("Minutes");
    Label lblRepeat = new Label("Create Group for");
    Label lblRepeatDays = new Label("Days");
    Label lblAbsenceGroup = new Label("");
    Label lblHoursAvailable = new Label("");
    // buttons
    
    Button dayEntryCancel = new Button("Cancel");
    Button dayEntryUpdate = new Button("Update Day");
    Button dayEntrySave = new Button("Save");     
    Button dayEntryDelete = new Button("Delete Day");
    Button dayEntryUpdateGroup = new Button("Update Group");
    Button dayEntryDeleteGroup = new Button("Delete Group");
    
    GridPane formGPane = new GridPane();

    /* Constructor
    *
    * dDate - the date of the day button the user pressed to get here
    *
    * This constructor sets the dayDate of the absence, and gets the day data for the day if there is any
    * it sets the class variables for the form data that was saved in the absences table for the day 
    * it converts the saved decimal hours data to days and minutes*/
    public DayEntry (String dDate, ArrayList<JSONObject> statistics) {
        
        this.dayDate = dDate;
       
        // Get absence data for the day and the absnece types
        dayData = Database.getAbsence(dayDate);  // single JSONObject of the day data
        typesData = Database.getAbsenceTypes();  // Arraylist of JSONObject type data
        stats = statistics;
       
        // if there was data for the day set the variables to it
        if (((String)dayData.get("Absence_Type")) != null) {
            
            prePopulated = true;
            // Set variables from absence data
            absenceType = (String)dayData.get("Absence_Type");
            color = (String)dayData.get("Color");
            title = (String)dayData.get("Title");
            decimalHours = (double)dayData.get("Hours");
            submitted = (int)dayData.get("Submitted");
            notes = (String)dayData.get("Notes");
            group = (String)dayData.get("Absence_Group");
            absenceID = getAbsenceTypeID(absenceType);
            currentAvailable = Double.parseDouble(JsonMatch.getJsonString(stats,"Absence_Type",absenceType,"Remaining_Hours"));
            lblHoursAvailable.setText(JsonMatch.getJsonString(stats,"Absence_Type",absenceType,"Remaining_DayHours")+"Can be Planned");
            
            if (!group.isEmpty()) {inGroup = true;}

            getHoursMinutes();  // convert decimal hours from db to hours and minutes
        }
    }
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        
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
        
        // **** Set Form Fields *****
        
        // set text field for absence title
        GridPane.setColumnSpan(tfTitle,14);
        if (!title.isEmpty()) {tfTitle.setText(title);}
        
        // set combobox for type - add data from types date       
        for (int i = 0; i < typesData.size(); i++) {
            String aType = (String)typesData.get(i).get("Absence_Type");
            cboType.getItems().add(aType); 
        }
        
        // set absenceType in the combobox
        cboType.setValue(absenceType);

        // set combobox for hours 
        for (int i = 0; i < 9; i++) {
            cboHours.getItems().add(i);
        }
        if (hours != 0) {cboHours.setValue(hours);} 
        else {cboHours.setValue(8);}    // set default to a full day's hours 
        cboHours.setPrefWidth(25);

        // set combobox for minutes  
        for (int i = 0; i < 60; i+=5) {    // add in 5 minute increments 
            cboMinutes.getItems().add(i);
        }
        cboMinutes.setValue(minutes);
        cboMinutes.setPrefWidth(55);
        
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
        for (int i = 0; i < 20; i++) {    // add in 5 minute increments 
            cboRepeat.getItems().add(i+1);
        }
        cboRepeat.setValue(1);
        cboRepeat.setPrefWidth(55);
        Tooltip ttrepeat = new Tooltip("This creates repeating days for this type " +
        "and hours entered.");
        ttrepeat.getStyleClass().add("ttgray");
        lblRepeat.setTooltip(ttrepeat);
        
        // Set Group Button attributes
        dayEntryUpdateGroup.setMaxHeight(22);
        dayEntryUpdateGroup.setMinHeight(22);
        Tooltip updtGrouptt = new Tooltip("Updates all days that are part of this group\n " +
        "with the type and hours entered");
        updtGrouptt.getStyleClass().add("ttgray");
        dayEntryUpdateGroup.setTooltip(updtGrouptt);
        dayEntryDeleteGroup.setMaxHeight(22);
        dayEntryDeleteGroup.setMinHeight(22);
        Tooltip delGrouptt = new Tooltip("Deletes all days that are part of this group");
        delGrouptt.getStyleClass().add("ttgray");
        dayEntryDeleteGroup.setTooltip(delGrouptt);        
        
        // *** ADD Fields to GridPane ***
        // title
        GridPane.setConstraints(tfTitle, 1, 1);
        GridPane.setColumnSpan(tfTitle, 10);
        formGPane.getChildren().add(tfTitle);
        // Absence Type label
        GridPane.setConstraints(lblType, 1, 2);
        GridPane.setColumnSpan(lblType, 3);
        formGPane.getChildren().add(lblType);
        // absence type combobox
        GridPane.setConstraints(cboType, 4, 2);
        GridPane.setColumnSpan(cboType, 7);
        formGPane.getChildren().add(cboType);    
        // Hours combobox
        GridPane.setConstraints(cboHours, 1, 3);
        GridPane.setColumnSpan(cboHours, 3);
        formGPane.getChildren().add(cboHours);    
        // hours label
        GridPane.setConstraints(lblHours, 3, 3);
        GridPane.setColumnSpan(lblHours, 3);
        formGPane.getChildren().add(lblHours);        
        // Minutes combobox
        GridPane.setConstraints(cboMinutes, 4, 3);
        GridPane.setColumnSpan(cboMinutes, 3);
        formGPane.getChildren().add(cboMinutes);
        // mintues label
        GridPane.setConstraints(lblMinutes, 7, 3);
        GridPane.setColumnSpan(lblMinutes, 3);
        formGPane.getChildren().add(lblMinutes);
        // Submitted checkbox
        GridPane.setConstraints(ckbSubmitted, 1, 5);
        GridPane.setColumnSpan(ckbSubmitted, 8);
        formGPane.getChildren().add(ckbSubmitted);   
        
        // add optional fields
        if (!prePopulated) {addToggleFields();}  // add repeat day fields
        if (inGroup) {addGroupButtons();}        // add group buttons      
        
        // add label notes 
        Label lblNotes = new Label("Notes:");
        GridPane.setConstraints(lblNotes, 1, 10);
        GridPane.setColumnSpan(lblNotes, 2);
        formGPane.getChildren().add(lblNotes);
        // text field notes
        GridPane.setConstraints(taNotes, 1, 11);
        GridPane.setColumnSpan(taNotes,10);
        taNotes.setPrefWidth(300);
        taNotes.setPrefHeight(100);
        if (!notes.isEmpty()) {taNotes.setText(notes);}
        formGPane.getChildren().add(taNotes);
        
        // add label hours available
        GridPane.setConstraints(lblHoursAvailable, 1, 12);
        GridPane.setColumnSpan(lblHoursAvailable,10);
        formGPane.getChildren().add(lblHoursAvailable);
        
        // determine what buttons to add to bottom hbox
        if (prePopulated) {
            setRemainingHours();  // set remaining hours for prepopulated type
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
        if (prePopulated) {
            String css = "form" + color.toLowerCase();
            formGPane.getStyleClass().add(css);
            topDatePane.getStyleClass().add(css);
        }
        formGPane.getStyleClass().add("dayentry");
        formGPane.setHgap(20);
        formGPane.setVgap(5);
        formGPane.setPrefHeight(300);
                      
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
        dayEntryStage.setMaxHeight(450);
        dayEntryStage.setMinHeight(450);
        dayEntryStage.setMaxWidth(375);
        dayEntryStage.setMinWidth(375);
        dayFormScene.getStylesheets().add(getClass().getResource("StyleSheet.css").toExternalForm());
        dayEntryStage.setTitle("Day Hours Entry");
        dayEntryStage.setScene(dayFormScene);
        dayEntryStage.show();   
        
        // ** Button Event Handlers **
        dayEntrySave.setOnAction(e-> {
            try {
                getValues();   // get current values from the controls
                if (validateData()) {
                    insertAbsence();   
                    MyAbsences.refresh();
                    dayEntryStage.close();
                }
            } catch (Exception save) {
                
            }
        });
        
        dayEntryUpdate.setOnAction(e-> {
            try {
                getValues();   // get current values from the controls
                if (validateData()) {
                    updateAbsence();
                    dayEntryStage.close(); 
                    MyAbsences.refresh();
                }
            } catch (Exception update) {
                
            }
        });
        
        dayEntryUpdateGroup.setOnAction(e-> {
            try {
                getValues();   // get current values from the controls
                if (validateData()) {
                    updateAbsenceGroup();
                    dayEntryStage.close(); 
                    MyAbsences.refresh();
                }
            } catch (Exception update) {
                
            }
        });

        dayEntryDelete.setOnAction(e-> {
            try {
            deleteAbsence();
            dayEntryStage.close(); 
            MyAbsences.refresh();
            } catch (Exception delete) {
                
            }
        });    
        
        dayEntryDeleteGroup.setOnAction(e-> {
            try {
            deleteAbsenceGroup();
            dayEntryStage.close(); 
            MyAbsences.refresh();
            } catch (Exception delete) {
                
            }
        });  
        
        dayEntryCancel.setOnAction(e-> {
            try {
            dayEntryStage.close(); 
            } catch (Exception cancel) {
                
            }
        });
        
        // Type Combobox Handler - Set background color of form to selected type
        cboType.setOnAction(e->{
            try {
                lblType.getStyleClass().clear();
                String type = cboType.getValue(); // get type from combo box
                String cboColor = JsonMatch.getJsonString(typesData,"Absence_Type",type,"Color");
                double aRate = JsonMatch.getJsonDouble(typesData,"Absence_Type",type,"Accrual_Rate");
                if (aRate == -1) {ckbSubmitted.setSelected(true);}
                else {ckbSubmitted.setSelected(false);}
                String css = "form" + cboColor.toLowerCase();
                formGPane.getStyleClass().clear();
                formGPane.getStyleClass().add(css);
                topDatePane.getStyleClass().clear();
                topDatePane.getStyleClass().add(css);
                setRemainingHours();
//                if (aRate >= 0) {
//                    String rHours = JsonMatch.getJsonString(stats,"Absence_Type",type,"Remaining_Hours");
//                    String rDayH = JsonMatch.getJsonString(stats,"Absence_Type",type,"Remaining_DayHours"); 
//                    lblHoursAvailable.setText(rDayH + "Can be Planned");
//                    currentRemaining = Double.parseDouble(rHours);
//                } else {
//                    //String aHours = JsonMatch.getJsonString(stats,"Absence_Type",type,"Available_Hours");
//                    //String aDayH = JsonMatch.getJsonString(stats,"Absence_Type",type,"Available_DayHours");                    
//                    lblHoursAvailable.setText("");  // no remaining to show for Add-In types
//                    //currentAvailable = Double.parseDouble(aHours);
//                }
//                //currentAvailable = Double.parseDouble(aHours);
//                //if (aRate != -1) {lblHoursAvailable.setText(rDayH + "Can be Planned");
//                //} else {lblHoursAvailable.setText("");}   // no hours Remaining reporting on Add-In type
            }
            catch(Exception ex) {
                // catch
            }
        });
        
    }
    
    private void setRemainingHours() {
        
        if (JsonMatch.getJsonDouble(typesData,"Absence_Type",cboType.getValue(),"Accrual_Rate") >= 0) {
           String rHours = JsonMatch.getJsonString(stats,"Absence_Type",cboType.getValue(),"Remaining_Hours");
           String rDayH = JsonMatch.getJsonString(stats,"Absence_Type",cboType.getValue(),"Remaining_DayHours"); 
           lblHoursAvailable.setText(rDayH + "Can be Planned");
           currentRemaining = Double.parseDouble(rHours);
       } else {
           //String aHours = JsonMatch.getJsonString(stats,"Absence_Type",type,"Available_Hours");
           //String aDayH = JsonMatch.getJsonString(stats,"Absence_Type",type,"Available_DayHours");                    
           lblHoursAvailable.setText("");  // no remaining to show for Add-In types
           //currentAvailable = Double.parseDouble(aHours);
       }
       //currentAvailable = Double.parseDouble(aHours);
       //if (aRate != -1) {lblHoursAvailable.setText(rDayH + "Can be Planned");
       //} else {lblHoursAvailable.setText("");}   // no hours Remaining reporting on Add-In type       
        
    }
 
    /* addToggleFields()
    *
    * This method toggles the fields for a repeat entry to only show on empty form */   
    private void addToggleFields() {
        
        // Repeat 
        GridPane.setConstraints(lblRepeat,1,6);
        GridPane.setColumnSpan(lblRepeat, 3);
        formGPane.getChildren().add(lblRepeat);
        // combobox repeat
        GridPane.setConstraints(cboRepeat, 4, 6);
        GridPane.setColumnSpan(cboRepeat, 3);
        formGPane.getChildren().add(cboRepeat);  
        // label days
        GridPane.setConstraints(lblRepeatDays,7,6);
        GridPane.setColumnSpan(lblRepeatDays, 4);
        formGPane.getChildren().add(lblRepeatDays);   

    } // end addToggleFields
    
    /* addGroupButtons()
    *
    * This method adds the fields for update and delete group to a group day */      
    private void addGroupButtons() {

        // update group button
        GridPane.setConstraints(dayEntryUpdateGroup,1,7);
        GridPane.setColumnSpan(dayEntryUpdateGroup, 3);
        formGPane.getChildren().add(dayEntryUpdateGroup);
        // delete group button
        GridPane.setConstraints(dayEntryDeleteGroup, 4, 7);
        GridPane.setColumnSpan(dayEntryDeleteGroup, 3);
        formGPane.getChildren().add(dayEntryDeleteGroup);          
  
    } // end addGroupButtons
    
    /* private insertAbsence
    *
    * Inserts the absence data from the controls into the database if it is a new entry   */ 
    private void insertAbsence() {
         
        if (repeatDays > 1) {group = dayDate;}

        String sql = "INSERT into absences (Date, Absence_ID, Title, Hours, Submitted, Notes, Absence_Group) " +
        "VALUES ('" + dayDate + "', '" + absenceID + "', '" + title + "', '" + decimalHours + "', '"
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
            if (!dayOfWeek.equals("SATURDAY") && !dayOfWeek.equals("SUNDAY")) {
                sql = "INSERT into absences (Date, Absence_ID, Title, Hours, Submitted, Notes, Absence_Group) " +
                "VALUES ('" + dayDate + "', '" + absenceID + "', '" + title + "', '" + decimalHours + "', '"
                + submitted + "', '" + notes + "', '" + group + "')";       
                Database.SQLUpdate(sql);
                i++;
            } 
        }
    } // end insertAbsence 
    
    /* private updateAbsence
    *
    * Updates the absences table with data from the form, if already an absence on the day 
    * builds the update string and calls the database SQLUpdate method*/ 
    private void updateAbsence() {
        
        String sql = "UPDATE absences " +
        "SET Title = '" + title + "', Absence_ID = '" + absenceID + "', Hours = '" + decimalHours + "', Submitted = '"
        + submitted + "', Notes = '" + notes + "' " +
        "WHERE Date = '" + dayDate + "'";
        Database.SQLUpdate(sql);
         
    } // end updateAbsence
    
    /* private updateAbsenceGroup
    *
    * Updates the absences table with data from the form, for a group of absence dates 
    * builds the update string and calls the database SQLUpdate method*/ 
    private void updateAbsenceGroup() {
        
        String sql = "UPDATE absences " +
        "SET Title = '" + title + "', Absence_ID = '" + absenceID + "', Hours = '" + decimalHours + "', Submitted = '"
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
        
        String sql = "DELETE from absences " + 
        "WHERE Absence_Group = '" + group + "'";
        
        Database.SQLUpdate(sql);
        
    } // end deleteAbsenceGroup
    
    /* private getValues
     *
     * Gets the values currently in the form controls to the class variables       */ 
    private void getValues() {
           
        title = tfTitle.getText();
        absenceType = (cboType.getValue());
        absenceID = getAbsenceTypeID(absenceType);
        hours = (int)cboHours.getValue();
        minutes = (int)cboMinutes.getValue();
        setHoursDecimal();   // sets decimalHours
        if (ckbSubmitted.isSelected()){submitted = 1;}
            else {submitted = 0;}
        notes = taNotes.getText();
        repeatDays = (int)cboRepeat.getValue();
        
    } // end getValues
    
    /* private validateData
    *
    * ==> true if data from controls is validated
    *
    * The method validates that an absence type name was selected from the combobox, 
    * and that the hours selected are avaialble for the absence type. 
    * Checked in event handlers for update, save and insert */
    private boolean validateData () {
        
        boolean validated = false;
        double aRate = JsonMatch.getJsonDouble(typesData,"Absence_Type",cboType.getValue(),"Accrual_Rate");
        
        double hoursToValidate = decimalHours * (int)repeatDays;
        if (aRate == -1) {hoursToValidate = 0;}  // Don't validate Add-In hours
        if (Validate.notEmpty("Absence Type", absenceType)) {
             if (Validate.availableHours(hoursToValidate,currentRemaining)) {
               validated = true; 
            } else {
                lblHoursAvailable.getStyleClass().add("lblverify"); 
                Platform.runLater(() -> {
                    cboHours.requestFocus();  // Set focus on type if not chosen                
                });                
            }             
        } else {   
            Platform.runLater(() -> {
                cboType.requestFocus();  // Set focus on type if not chosen                
            });
            lblType.getStyleClass().add("lblverify"); 
        }       
        return validated;
        
    } // end validateData
   
    
    /* private getHoursMin 
    *
    * Returns a string breaking down class variable decimal hours to 
    * class variables hours and minutes*/
    private void getHoursMinutes () {
        
        hours = (int)decimalHours;
        double fractional = decimalHours - hours;
        minutes = (int)Math.rint(fractional * 60.0);

    } // End getHoursMin

    /* private setHoursDecimal
    * 
    * Opposite of getHoursMinutes, coverts class variables hours and minutes*
    * to update the class variable decimalHours */
    private void setHoursDecimal() {
        
        // use flost for accuracy of mintue conversion
        float dblMinutes = Float.valueOf(minutes);
        float decMinutes = dblMinutes/60;
        float dblHours = Float.valueOf(hours);
        float decHours = (dblHours + decMinutes);
        decimalHours = decHours;
 
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
} // end class DayEntry 
