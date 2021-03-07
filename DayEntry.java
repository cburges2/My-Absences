/*
 * Christopher Burgess
 */
package myabsences;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
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
//Begin Subclass DayEntry
public class DayEntry extends Application {
  
    static Stage dayEntryStage = new Stage();  // stage for the form
    SimpleDateFormat formatDb = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat formatCal = new SimpleDateFormat("EEEEEEE, MMMMMMMM dd, yyyy"); // date format for title
    JSONObject dayData;         // a specific absence day data
    ArrayList<JSONObject> typesData;
    String dayDate = "";        // the db format date for the day
    String absenceType = "";    // the user's absence type name
    String color = "";          // the user's color for the absence type
    String title = "";          // the user's title of the absence
    double decimalHours = 0;    // the decimal hours for the day in db format
    int hours = 0;              // the hours portion for combobox
    int minutes = 0;            // the minutes portion for combobox
    int submitted = 0;          // submitted flag from db
    boolean holdiay = false;    // flag for Holiday type
    String notes = "";          // the user's notes for the absence (if any)
    int repeatDays = 0;         // Days to repeat the absence
    int absenceID = 0;          // the id of the absence type
    boolean prePopulated = false; // flag for day already having data on load
    // controls
    TextField tfTitle = new TextField();
    ComboBox<String> cboType = new ComboBox();
    ComboBox cboHours = new ComboBox();
    ComboBox cboMinutes = new ComboBox();
    TextArea taNotes = new TextArea();
    CheckBox ckbSubmitted = new CheckBox("Submitted");
    ComboBox cboRepeat = new ComboBox();
    // labels
    Label lblType = new Label("Absence Type:");
    Label lblHours = new Label("Hours");
    Label lblMinutes = new Label("Minutes");
    Label lblRepeat = new Label("Repeat For ");
    Label lblRepeatDays = new Label("Days");
    
    GridPane formGPane = new GridPane();

    /* Constructor
    *
    * dDate - the date of the day button the user pressed to get here
    *
    * This constructor sets the dayDate of the absence, and gets the day data for the day if there is any
    * it sets the class variables for the form data that was saved in the absences table for the day 
    * it converts the saved decimal hours data to days and minutes*/
    public DayEntry (String dDate) {
        
        this.dayDate = dDate;
       
        // Get absence data for the day and the absnece types
        dayData = Database.getAbsence(dayDate);  // single JSONObject of the day data
        typesData = Database.getAbsenceTypes();  // Arraylist of JSONObject type data
       
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
            absenceID = getAbsenceTypeID(absenceType);  

            // convert decimal hours from db to hours and minutes
            getHoursMinutes(); 
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
        
        // **** Form Fields *****
        
        // text field for absence title
        GridPane.setColumnSpan(tfTitle,4);
        if (!title.isEmpty()) {tfTitle.setText(title);}
        GridPane.setConstraints(tfTitle, 1, 1);
        formGPane.getChildren().add(tfTitle);
        
        if (absenceType.equals("Company Holiday")) {
            ckbSubmitted.setSelected(true);
        }
        
        // combobox for type - add data from types date       
        for (int i = 0; i < typesData.size(); i++) {
            String aType = (String)typesData.get(i).get("Absence_Type");
            cboType.getItems().add(aType); 
        }
        
        if (!absenceType.isEmpty()) {cboType.setValue(absenceType);}

        // combobox for hours 
        for (int i = 0; i < 9; i++) {
            cboHours.getItems().add(i);
        }
        if (hours != 0) {cboHours.setValue(hours);} 
        else {cboHours.setValue(8);}    // set default to a full day's hours 
        cboHours.setPrefWidth(25);

        // combobox for minutes  
        for (int i = 0; i < 60; i+=5) {    // add in 5 minute increments 
            cboMinutes.getItems().add(i);
        }
        cboMinutes.setValue(minutes);
        cboMinutes.setPrefWidth(55);
        
        // checkbox for submitted
        if (submitted == 1) {
            ckbSubmitted.setSelected(true);
        } else {
            ckbSubmitted.setSelected(false);
        }
        
        // combobox for repeat days
        for (int i = 0; i < 20; i++) {    // add in 5 minute increments 
            cboRepeat.getItems().add(i+1);
        }
        cboRepeat.setValue(1);
        cboRepeat.setPrefWidth(55);
        
        // *** ADD Fields to GridPane ***
        // Absence Type
        GridPane.setConstraints(lblType,1,5);
        GridPane.setColumnSpan(lblType, 2);
        formGPane.getChildren().add(lblType);
        GridPane.setConstraints(cboType, 3, 5);
        GridPane.setColumnSpan(cboType, 2);
        formGPane.getChildren().add(cboType);    
        // Hours
        GridPane.setConstraints(lblHours, 2, 7);
        formGPane.getChildren().add(lblHours);        
        GridPane.setConstraints(cboHours, 1, 7);
        formGPane.getChildren().add(cboHours);
        // Minutes
        GridPane.setConstraints(lblMinutes, 4, 7);
        formGPane.getChildren().add(lblMinutes);
        GridPane.setConstraints(cboMinutes, 3, 4);
        GridPane.setConstraints(cboMinutes, 3, 7);
        formGPane.getChildren().add(cboMinutes); 
        // Submitted
        GridPane.setColumnSpan(ckbSubmitted, 2);
        GridPane.setConstraints(ckbSubmitted, 1, 9);
        formGPane.getChildren().add(ckbSubmitted);   
        
        // add the repeat entry fields  for an empty form
        addToggleFields(); 
        
        // text field for notes 
        Label lblNotes = new Label("Notes:");
        GridPane.setConstraints(lblNotes, 1, 13);
        formGPane.getChildren().add(lblNotes);
        GridPane.setColumnSpan(taNotes,4);
        taNotes.setPrefWidth(300);
        taNotes.setPrefHeight(100);
        if (!notes.isEmpty()) {taNotes.setText(notes);}
        GridPane.setConstraints(taNotes, 1, 14);
        formGPane.getChildren().add(taNotes);
        
        // Create the Buttons
        Button dayEntryExit = new Button("Exit");
        Button dayEntryUpdate = new Button("Update");
        Button dayEntrySave = new Button("Save");     
        Button dayEntryDelete = new Button("Delete");
        
        // determine what buttons to add to bottom hbox
        if (prePopulated) {
            hBoxB.getChildren().addAll(dayEntryDelete,dayEntryUpdate,dayEntryExit);
            Platform.runLater(() -> {
                dayEntryExit.requestFocus();  // Set focus on exit if prepopulated
            });
        } else {
            hBoxB.getChildren().addAll(dayEntrySave,dayEntryExit);
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
        dayEntryExit.setMaxWidth(50);
        HBox.setMargin(dayEntryExit, new Insets(5, 5, 5, 5));
        
        
        /**** set panes in stage and show stage ****/
        bPane.setCenter(formGPane);
        bPane.setTop(topDatePane);
        bPane.setBottom(hBoxB);
        Scene dayFormScene = new Scene(bPane); 
        dayEntryStage.setMaxHeight(600);
        dayEntryStage.setMinHeight(500);
        dayEntryStage.setMaxWidth(650);
        dayEntryStage.setMinWidth(350);
        dayFormScene.getStylesheets().add(getClass().getResource("StyleSheet.css").toExternalForm());
        dayEntryStage.setTitle("Day Hours Entry");
        dayEntryStage.setScene(dayFormScene);
        dayEntryStage.show();   
        
        // ** Button Event Handlers **
        dayEntrySave.setOnAction(e-> {
            try {
            insertAbsence();   
            MyAbsences.refresh();
            dayEntryStage.close(); 
            } catch (Exception save) {
                
            }
        });
        
        dayEntryUpdate.setOnAction(e-> {
            try {
            updateAbsence();
            dayEntryStage.close(); 
            MyAbsences.refresh();
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
        
        dayEntryExit.setOnAction(e-> {
            try {
            dayEntryStage.close(); 
            } catch (Exception exit) {
                
            }
        });
        
        // Type Combobox Handler - Set background color of form to selected type
        cboType.setOnAction(e->{
            try {
                String type = cboType.getValue(); // get type from combo box
                //int typePos = JsonMatch.getJsonIndex(typesData,"Absence_Type",type);
                //String cboColor = (String)typesData.get(typePos).get("Color");
                String cboColor = JsonMatch.getJsonString(typesData,"Absence_Type",type,"Color");
                double aRate = JsonMatch.getJsonDouble(typesData,"Absence_Type",type,"Accrual_Rate");
                if (aRate == -1) {ckbSubmitted.setSelected(true);}
                else {ckbSubmitted.setSelected(false);}
                String css = "form" + cboColor.toLowerCase();
                formGPane.getStyleClass().clear();
                formGPane.getStyleClass().add(css);
                topDatePane.getStyleClass().clear();
                topDatePane.getStyleClass().add(css);
            }
            catch(Exception ex) {
            }
        });
        
    }
 
    /* addToggleFields()
    *
    * This method toggles the fields for a repeat entry to only show on empty form */   
    private void addToggleFields() {
        
        // Repeat 
        if (!prePopulated) {
            GridPane.setConstraints(lblRepeat,1,10);
            formGPane.getChildren().add(lblRepeat);
            GridPane.setConstraints(cboRepeat, 2, 10);
            formGPane.getChildren().add(cboRepeat);  
            GridPane.setConstraints(lblRepeatDays,3,10);
            formGPane.getChildren().add(lblRepeatDays);   
        }
        
    }
    
    /* private insertAbsence
    *
    * Inserts the absence data from the controls into the database if it is a new entry   */ 
    private void insertAbsence() {
        
        getValues();   // get current values from the controls

        System.out.println("Adding " + dayDate);
        String sql = "INSERT into absences (Date, Absence_ID, Title, Hours, Submitted, Notes) " +
        "VALUES ('" + dayDate + "', '" + absenceID + "', '" + title + "', '" + decimalHours + "', '"
        + submitted + "', '" + notes + "')";       
        Database.SQLUpdate(sql);
        
        // Repeat Add Days
        int i = 0;
        while (i < repeatDays-1) {
            // increment date
            String nextDay =  LocalDate.parse(dayDate).plusDays(1).toString();
            LocalDate date1 = LocalDate.parse(nextDay);
            String dayOfWeek = date1.getDayOfWeek().toString();
            dayDate = nextDay;
            if (!dayOfWeek.equals("SATURDAY") && !dayOfWeek.equals("SUNDAY")) {
                System.out.println("Adding " + dayDate);
                sql = "INSERT into absences (Date, Absence_ID, Title, Hours, Submitted, Notes) " +
                "VALUES ('" + dayDate + "', '" + absenceID + "', '" + title + "', '" + decimalHours + "', '"
                + submitted + "', '" + notes + "')"; 
                Database.SQLUpdate(sql);
                i++;
            } 
        }
    } // end insertAbsence

    /* private addDay
    *
    * date - the date to add the days to
    * days - how many days to add
    * => the Date days number in the future
    *
    * Adds days to a data to get the new date days in the future   */     
    private Date addDay(Date date, int days)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return cal.getTime();
    }    
    
    /* private updateAbsence
    *
    * Updates the absences table with data from the form, if already an absence on the day 
    * builds the update string and calls the database SQLUpdate method*/ 
    private void updateAbsence() {
        
        getValues();   // get current values from the controls
        
        String sql = "UPDATE absences " +
        "SET Title = '" + title + "', Absence_ID = '" + absenceID + "', Hours = '" + decimalHours + "', Submitted = '"
        + submitted + "', Notes = '" + notes + "' " +
        "WHERE Date = '" + dayDate + "'";
        
        System.out.println(sql);
        
        Database.SQLUpdate(sql);
        
    }

    /* private deleteAbsence
    *
    * Deletes the absence in the absences table for the dayDate  
    * Builds the delete string and calls the Database SQLUpdate method */ 
    private void deleteAbsence() {
        
        getValues();   // get current values from the controls
        
        String sql = "DELETE from absences " + 
        "WHERE Date = '" + dayDate + "'";
        
        Database.SQLUpdate(sql);
        
    }    
    
    /* private getValues
     *
     * Gets the values currently in the form controls to the class variables       */ 
    private void getValues() {
        
        title = tfTitle.getText();
        System.out.println("title is " + title);                //TEST
        absenceType = (cboType.getValue());
        System.out.println("Absence Type is " + absenceType);   //TEST
        absenceID = getAbsenceTypeID(absenceType);
        System.out.println("Absence Type ID is " + absenceID);  //TEST
        hours = (int)cboHours.getValue();
        System.out.println("Hours is " + hours);                //TEST
        minutes = (int)cboMinutes.getValue();
        System.out.println("Minutes is " + minutes);            //TEST
        getHoursDecimal();   // sets decimalHours
        if (ckbSubmitted.isSelected()){submitted = 1;}
            else {submitted = 0;}
        System.out.println("submitted is " + submitted);        //TEST
        notes = taNotes.getText();
        System.out.println("Notes is " + notes);                //TEST
        repeatDays = (int)cboRepeat.getValue();
        System.out.println("Repeat is " + repeatDays);          //TEST
               
        // TODO -> valdidate these before sending to DB
        
    }
    
    /* private getHoursMin 
    *
    * Returns a string breaking down class variable decimal hours to 
    * class variables hours and minutes*/
    private void getHoursMinutes () {
        
        hours = (int)decimalHours;
        double fractional = decimalHours - hours;
        minutes = (int)Math.rint(fractional * 60.0);
        
        //test
        System.out.println("Decimal Hours = " + decimalHours);
        System.out.println("Hours = " + hours);
        System.out.println("Minutes = " + minutes);

    } // End getHoursMin
    
    
    /* private getHoursDecimal
    * 
    * Opposite of getHoursMinutes, coverts class variables hours and minutes*
    * to update the class variable decimalHours */
    private void getHoursDecimal() {
        
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
} // end lcass 
