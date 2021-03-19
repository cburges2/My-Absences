/** 
 * @Course: SDEV 435
 * @Author Name: Christopher Burgess -
 * @Assignment Name: cburgess_Project
 * @Date: Jan 28, 2021
 * @Description: MyAbsences - A program to store and track absence hours by a company employee
 */
package myabsences;

//Imports
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.layout.BorderPane;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;

//Begin Class Main
public class MyAbsences extends Application {
    
    // variables
    static ComboBox<String> cboYear = new ComboBox<>(); // year combo
    static ComboBox<String> cboCalcType = new ComboBox<>();
    static Button[][] btnMonth = new Button[12][42];  // days in month button array (month, day)
    static Button[] btnsTop = new Button[4];           // top pane navigattion buttons
    static Button appStart = new Button();
    static ArrayList<String> years = new ArrayList<>(); // years with balances for combobox
    static String calcType = "Calculate All";
    String todayStr = new String();
    
    // Set Static Current Year for initial calendar display
    final static SimpleDateFormat FORMAT_YEAR = new SimpleDateFormat("yyyy");       
    static String year = FORMAT_YEAR.format(Calendar.getInstance().getTime()); // current year
    
    // Create Window Objects
    static ListReport listWindow = new ListReport();           // create ListReport object
               
    @Override
    public void start(Stage primaryStage) throws Exception {
        
        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat formatDb = new SimpleDateFormat("yyyy-MM-dd");  
        todayStr = formatDb.format(today); //note: todayStr will not show on past year calendars

        Validate.checkData(todayStr, year);    // Create a Warning if data entry is needed

        // **** Create Main Border Pane ****  
        BorderPane bPane = new BorderPane();             // Main Layout pane, Top, Middle, Bottom  
        
        // Build the Bottom gPane -> Summary Report
        SummaryReportBuilder summaryReportBuilder = new SummaryReportBuilder(year);
        GridPane bottomReport = summaryReportBuilder.buildReport();   
        cboCalcType = summaryReportBuilder.getCboCalcType();   // get the calc combobox
         
        // Build the Middle bPane -> Calander  
        CalanderBuilder calanderBuilder = new CalanderBuilder(year);
        GridPane calander = calanderBuilder.buildCalander();  
        btnMonth = calanderBuilder.getBtnMonth();               // get the calander buttons
        
        //Build the Top bPane -> TopLineHBox
        TopPaneBuilder topPaneBuilder = new TopPaneBuilder(year);
        HBox topPane = topPaneBuilder.buildTopPane();   
        cboYear = topPaneBuilder.getCboYear();
        btnsTop = topPaneBuilder.getBtnsTop();
        
        /*** Set BorderPane attributes ***/  
        BorderPane.setAlignment(bottomReport,Pos.CENTER);
        bPane.getStyleClass().add("bpane");
        bPane.setTop(topPane);                           // use topVBOx for titles and buttons
        bPane.setCenter(calander);                       // Use calander for Calander
        bPane.setBottom(bottomReport);                   // use botVBox for bottom section exit button 
        bPane.setPadding(new Insets(0,0,10,0));          // top, right, bottom, left

        
        /******************** Control Handlers ********************
        **************** For Main Screen controls *****************/
        
        // Top Buttons Handler
        // top buttons stored in the btnsTop array
        for (int btnNum = 0; btnNum < 4; btnNum++) {
        final int btnNumber = btnNum;
        btnsTop[btnNum].setOnAction(e->{
            try {
               String btnText = btnsTop[btnNumber].getText();
               if (btnText.equals("Setup")) {
                    try {
                        SetupForm setupWindow = new SetupForm();            // create SetupForm object
                        setupWindow.start(null);                            // start secondary stage
                    } catch (Exception ex) {
                        Logger.getLogger(MyAbsences.class.getName()).log(Level.SEVERE, null, ex);
                    }
               }
               if (btnText.equals("Enter Balances")) {
                    try {
                        BalancesForm balanceWindow = new BalancesForm();    // create BalanceForm object
                        balanceWindow.start(null);                          // start secondary stage
                    } catch (Exception ex) {
                        Logger.getLogger(MyAbsences.class.getName()).log(Level.SEVERE, null, ex);
                    }
               }
               if (btnText.equals("List View")) {
                    try {
                        listWindow.start(null);            // start secondary stage
                    } catch (Exception ex) {
                        Logger.getLogger(MyAbsences.class.getName()).log(Level.SEVERE, null, ex);
                    }
               }
               if (btnText.equals("Exit")) { System.exit(1); }
            }
            catch(Exception ex) {
            }
        });
        }              
        
        // Year Combobox Handler - Set a new year and referesh Stage
        cboYear.setOnAction(e->{
            MyAbsences app=new MyAbsences();
            try {
                year = cboYear.getValue(); // get year from combo box   
                app.start(primaryStage);
            }
            catch(Exception ex) {
            }
        });
        
        // cboCalcType Handler - set calc type and referesh Stage
        cboCalcType.setOnAction(e->{
            MyAbsences app=new MyAbsences();
            try {
                calcType = cboCalcType.getValue(); // get type from combo box   
                app.start(primaryStage);
            }
            catch(Exception ex) {
            }
        });        
        
        // Refresh the stage from another object using the refresh method and virtual button
        appStart.setOnAction(e-> {
            MyAbsences app=new MyAbsences();
            try {
                app.start(primaryStage);
            } catch (Exception exit) {
                // handle the error
            }
        });
        
     
        /* Day Buttons Handler - extract the date from button pressed and create the form for input
         * Loop through the button 2d array by month and button number
         * Get year from year, get month from month, get day from button text.
         * Build the db date format to open the day input form
         * Ignore buttons with blank date text  */
        for (int month = 0; month < 12; month++) {
           final int month2 = month+1;  
           for (int bNumber = 0; bNumber < 42 ; bNumber++) {            
                btnMonth[month][bNumber].setOnAction(e->{
                if (!((Button)e.getSource()).getText().equals(" ")) { 
                    // create the date string
                    String bText = ((Button)e.getSource()).getText();
                    if (bText.length() == 1) {bText = "0" + bText;}
                    String monthStr = String.valueOf(month2); 
                    if (monthStr.length() == 1) {monthStr = "0" + monthStr;}
                    String dbDate = year + "-" + monthStr + "-" + bText;
                    try {
                        DayEntry dayEntry = new DayEntry(dbDate,summaryReportBuilder.getStats());    // create BalanceForm object 
                        dayEntry.start(null);               // start secondary stage for DayEntry
                        } catch (Exception ex) {
                            Logger.getLogger(MyAbsences.class.getName()).log(Level.SEVERE, null, ex);
                        }                 
                    }
                });
            }
        }        
        
        /********************** Set Stage and Scene **********************/
        Scene scene = new Scene(bPane); // new scene add the border pane 
        scene.getStylesheets().add(getClass().getResource("StyleSheet.css").toExternalForm());
        primaryStage.setTitle("My Absences");
        primaryStage.setScene(scene);   // set stage
        primaryStage.setHeight(900);    // main window height
        primaryStage.setWidth(1100);    // main window width
        primaryStage.show();            // show the GUI
         
    } // ** End start ** 
    
    /*** Methods ****
    
    /* public refresh
    *
    *
    * refresh the stage from another object */
    static public void refresh() {
        System.out.println("Restarting...");
        appStart.fire();
    };
    
} //End Class MyAbsences