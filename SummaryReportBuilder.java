
package myabsences;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import static myabsences.MyAbsences.calcType;
import org.json.simple.JSONObject;

/** class SummaryReportBuilder
 *  This class builds a summary report of absences for the year to be put in the
 *  bottom section of the main screen Border Pane 
 * @author Christopher Burgess
 */
public class SummaryReportBuilder {
    
    String year;                                     // current year on the calendar
    GridPane bottomReport = new GridPane();
    Date today = Calendar.getInstance().getTime();   // Today's Date
    SimpleDateFormat formatDb = new SimpleDateFormat("yyyy-MM-dd");  
    SimpleDateFormat formatCal = new SimpleDateFormat("MM/dd/yyyy");
    String todayStr = formatDb.format(today);   // today's date in db format
    int numRows;                  // number of rows for summary table
    int numColumns;               // number of columns for summary table    
    String[][] summaryTable;      // table of the data for the report 
    ArrayList<JSONObject> futureHours;
    ArrayList<JSONObject> pastHours;
    ArrayList<JSONObject> startBalances;
    ArrayList<JSONObject> absences;
    static ArrayList<JSONObject> stats = new ArrayList<>();   // object to hold stats
    int daysInYear;    
    String lastDate;   // date of last day of this year
    SimpleDateFormat FORMAT_YEAR = new SimpleDateFormat("yyyy");       
    String thisYear = FORMAT_YEAR.format(Calendar.getInstance().getTime());
    double accrualRate = 0;
    double accrualMax = 0;
    double startBalance = 0;
    String accruedToday = "";
    double accruedNow = 0;
    String absenceType = "";
    int absenceID = 0;
    
    // Time Settings
    double hoursInDay = 8;
    double hoursInWeek = 40;
    double daysInWeek = 5;
    int maxWarningLimit = 180;   // days before max accrual to warn
    
    static ComboBox<String> cboCalcType = new ComboBox<>();
    
    /* SummaryReportBuilder Constructer
    *
    *  year - The current year in the main calendar
    *
    * This sets the year, the end of year date, and gets the data needed from the db 
    * into the ArrayLists.  It sets the size of the report and the days in the year for calculations */
    public SummaryReportBuilder (String year) { 
                
        this.year = year;   // Set class variable year from parameter
        
        lastDate = year + "-12-31";   // build string for last day of this year
        
        // get the JSON object arrays from the database
        startBalances = Database.getStartBalances(year);                 
        futureHours = Database.getFutureHours(todayStr, lastDate, year); 
        pastHours = Database.getPastHours(todayStr, year);    
        absences = Database.getAllAbsences(year);  
               
        // set the size and create the summary table array
        numRows = startBalances.size();      // One row for each absence type
        numColumns = 6;                      // number of columns in the report
        summaryTable = new String[numRows+1][numColumns];  // also add a row for column Headers insert
                
        // set the days in the year
        daysInYear = 365;
        int intYear = Integer.parseInt(year);
        if (leapYear(intYear)) {
            daysInYear = 366;
        }

    } // End Constructor
    
    /* public buildReport: Build a report of absences balances for the year  
     *
     * balances: The starting balances from the db Starting Balances table  
     * absences: The absence data from the db Absences table
     *
     * => GridPane
     *
     * The constructs the GridPane that contains the summpary report*/ 
    public final GridPane buildReport () {

        /* Set bottomReport Attributes */ 
        bottomReport.setMinHeight(114);
        bottomReport.setMaxWidth(1034); // 979
        bottomReport.setAlignment(Pos.CENTER);
        bottomReport.getStyleClass().add("summaryreport");   //
        GridPane.setMargin(bottomReport, new Insets(10,0,10,0));   // top, right, bottom, left
        bottomReport.getStyleClass().add("bpane");
        
        // build the summaryTable array of data
        buildSummaryTable();   
        
        // set SubmitOnly combo box
        if (cboCalcType.getItems().isEmpty()) { 
            cboCalcType.getItems().add("Calculate All");
            cboCalcType.getItems().add("Submitted Only");
        }
        cboCalcType.setPadding(new Insets(-5,0,-5,0)); // shrink height
        cboCalcType.setValue(calcType);
        Tooltip calcType = new Tooltip("Calculate all hours or submitted hours only");
        calcType.getStyleClass().add("ttgray");
        cboCalcType.setTooltip(calcType);

        // add the summaryTable array to the gridpane, while breaking down hours and adding colors and hours tooltips
        bottomReport.setHgap(40);    // Add space between columns
        bottomReport.setVgap(-2);    // Reduce space between rows
        String toolTipText = " ";
        for (int row = 0; row < numRows+1; row++) {
            for (int col = 0; col < numColumns; col++) {
                    if (col == 0 && row == 0) {
                        GridPane.setConstraints(cboCalcType, col, row);
                        bottomReport.getChildren().add(cboCalcType);
                    }
                    Label label = new Label(" ");
                    if (col == 0 && row > 0) {      // add tooltip to type name showing balance type
                        String typett = getAccrualType((Double)startBalances.get(row-1).get("Accrual_Rate"));
                        Tooltip type = new Tooltip(typett);
                        label.setTooltip(type);
                        String cssColor = "tt" + ((String)startBalances.get(row-1).get("Color")).toLowerCase();
                        type.getStyleClass().add(cssColor);
                    } 
                    if (col > 0 && row > 0 && !summaryTable[row][col].equals("--")) {
                        label.setText(getHoursBreakdown(summaryTable[row][col])); // breakdown hours for display if hours     
                    }
                    else {label.setText(summaryTable[row][col]);}
                    if (row != 0) {toolTipText = summaryTable[row][col] + " Hours";} // set tt tp actual hours
                    if (row == 0) {label.getStyleClass().add("summaryheader");}      // style headers 
                    Tooltip ltp = new Tooltip(toolTipText);
                    if (row > 0 && col == 0) {                  // absence type column color by type
                        String cssColor = "type" + ((String)startBalances.get(row-1).get("Color")).toLowerCase();
                        label.getStyleClass().add(cssColor);
                    }
                    if (row > 0 && col >= 0) {
                        if (col > 0) label.getStyleClass().add("lbldata");  // color data items 
                        String cssColor = "tt" + ((String)startBalances.get(row-1).get("Color")).toLowerCase();
                        ltp.getStyleClass().add(cssColor);    // color the tooltip by type color
                    }
                    // set tool tip for elements with hours data only
                    if (row > 0 && col > 0 && !summaryTable[row][col].equals("--")) label.setTooltip(ltp);
                    GridPane.setConstraints(label, col, row);
                    bottomReport.getChildren().add(label);
                }  
            }
        
        return bottomReport;
        
    } // end method buildReport
    
    /* private buildSummaryTable()
     *
     *
     * Called from buildReport - Builds a 2D array of calculated data to be put the a GridPane */
    private void buildSummaryTable() {
        
        // iterate through the absence_types in Start Balances to get data from ArrayLists to build the summary table rows
        for (int row=0; row < numRows; row++) {
            JSONObject record = new JSONObject();   // record of stats for dayform to get
            absenceType = (String)startBalances.get(row).get("Absence_Type");
            summaryTable[row][0] = absenceType;                   // set first table column to absenceType name
            // get start balance for type
            startBalance = (double)startBalances.get(row).get("Starting_Balance");
            summaryTable[row][1] = Double.toString(startBalance);                                                  
            double past_Hours = JsonMatch.getJsonDouble(pastHours,"Absence_Type",absenceType,"Past_Hours");
            summaryTable[row][2] = Double.toString(past_Hours);
            // get future hours for type
            double future_Hours = JsonMatch.getJsonDouble(futureHours,"Absence_Type",absenceType,"Future_Hours");
            summaryTable[row][3] = Double.toString(future_Hours);
            accrualRate = (double)startBalances.get(row).get("Accrual_Rate");
            String accrualType = getAccrualType(accrualRate);  // get the type - accrued, fixed, or not calculated. 
            // set accrued absence type data for summary table and check accrual max
            if (accrualType.equals("Accrued")) {
                if (thisYear.equals(year)) {
                    accrualMax = (double)startBalances.get(row).get("Max_Accrual");
                    accruedToday = calcAvailableToday(accrualRate, startBalance, past_Hours); 
                    if (accrualMax != 0) findMaxAccrual();   // set max warning for absence type if there is a max
                } 
                accruedToday = calcAvailableToday(accrualRate, startBalance, past_Hours); 
                if (thisYear.equals(year)) {summaryTable[row][4] = accruedToday;} else {summaryTable[row][4] = "0";}
                summaryTable[row][5] = calcAccuredRemainingHours(past_Hours, future_Hours, startBalance,accrualRate);
            }          
            // fixed accrual - calc remaining balance (startbalance - (used + planned))           
            if (accrualType.equals("Fixed")) {
                if (thisYear.equals(year)) {summaryTable[row][4] = calcAvailableToday(startBalance,past_Hours);}
                else {summaryTable[row][4] = "0";}
                summaryTable[row][5] = calcFixedRemainingHours(past_Hours, future_Hours, startBalance); 
            }        
            // Holidays - not calculated
            if (accrualType.equals("Added")) {
                summaryTable[row][1] = Double.toString(past_Hours + future_Hours);
                if (thisYear.equals(year)) {summaryTable[row][4] = Double.toString(future_Hours);}
                else {summaryTable[row][4] = "0";}
                summaryTable[row][5] = "0 ";     // no remaining balance, show use zero
            }
            // add data to a JSON record object and then add the record to stats array
            record.put("Absence_Type",absenceType);
            record.put("Future_Hours", future_Hours);
            record.put("Available_DayHours",getHoursBreakdown(summaryTable[row][4]));
            record.put("Available_Hours",summaryTable[row][4]);
            record.put("Remaining_Hours",summaryTable[row][5]);
            record.put("Remaining_DayHours",getHoursBreakdown(summaryTable[row][5]));
            stats.add(record);   // add record to stats arraylist
        }

        // move array elements and add headers to top of summary Table
        String[] headers = new String[]{"","Beginning Balance      ","Used                   ","Planned                ","Available Today        ","Remaining              "};
        for (int row = numRows; row > 0; row--) {
            for (int col = 0; col < numColumns; col++) {
                summaryTable[row][col] = summaryTable[row-1][col]; // move exiting array up on element
            }    
        }        
        System.arraycopy(headers, 0, summaryTable[0], 0, numColumns); // add headers to first element
        
    } // End buildSummaryTable method
    
    /* private findMaxAccrual
    *
    * This method steps through the days of the year until it finds the day of Max Accrual for an accrued type with a Max 
    * It checks each day to see if there is submitted time on that day, and subtracts it from the running total of accrued hours
    * Once day is found, it adds an entry to the Warnings table, to be read by TopPaneBuilder to add the warning 
    * If there is already a warning for the absence type, it will update it  
    * if there no warning, it will insert it to the table 
    * if no max found to warn about, it will delete the warning if there was one in the table from before  */
    private void findMaxAccrual () {
  
        String dayDate = todayStr;
        double totalHours = accruedNow;
        //int maxWarningLimit = 180;   // days before max to warn
        int numDays = 0;          // days counter for warningLimit
        
        boolean done = false;
        while (!done) {
            // increment date
            String nextDay =  LocalDate.parse(dayDate).plusDays(1).toString();
            dayDate = nextDay; 
            totalHours = totalHours += accrualRate;
            
            // check if the day has absence hours to subtract if submitted
            if (JsonMatch.getJsonIndex(absences,"Date",dayDate) != -1) {
                // Get the hours for the type on day and subtract them if the hours are submitted 
                double hours = Database.getNumDayTypeHours(absenceID, dayDate); 
                int submitted = JsonMatch.getJsonInt(absences, "Date", dayDate, "Submitted");
                if (submitted == 1) {
                    totalHours = totalHours - hours; // subtract the submitted hours from totalHours  
                }
            }
            numDays++;
            // Check if the totalHours are now more than the accrual Max
            String warningName = "MAX_ACCRUAL";
            absenceID = JsonMatch.getJsonInt(startBalances,"Absence_Type",absenceType,"Absence_ID");
            if ((totalHours  > accrualMax) && numDays < maxWarningLimit) {
                // add or update the warning
                Warnings.addWarning(absenceID, dayDate, warningName);
                done = true;
            }
            // Stop looking after end of this year and Delete any past warnings for absence type
            if (!dayDate.substring(0,4).equals(year)) {
                Warnings.removeWarning(absenceID, warningName);
                done = true;
            }
        }
    } // end findMaxAccrual
    
    /* private getHoursBreakdown
    *
    * h - decimal hours string
    * => A string breaking down Weeks, Days, Hours and Minutes
    *
    * returns a string reprentation of hours in Weeks, Days, Hours and Minutes format   */
    private String getHoursBreakdown(String h) {
        
        double hrs = Double.valueOf(h);
        
        double ww = hrs/hoursInWeek;    // work week (default 40 hrs in week)
        double weeks = Math.floor(ww);  // whole weeks
        double dayValue = daysInWeek*((ww)-(int)(ww)); // (default 5 days in week)
        double days = (int)(dayValue);  // remaining whole days
        double hours = Math.floor(hoursInDay*(dayValue-days));   // (default 8 hours in day) remaining whole hours
        double min = Math.round((60*((hoursInDay*(dayValue-days))-(int)(hoursInDay*(dayValue-days)))));
        
        // fix overlow minutes, hours and hours
        if (min == 60) {hours++; min = 0;}
        if (hours == hoursInDay) {days++; hours = 0;}
        if (days == daysInWeek) {weeks++; days = 0;}

        String breakDown = "";
        if ((int)weeks != 0) {breakDown = (int)weeks + " Weeks ";}      
        if ((int)days != 0) {breakDown = breakDown + (int)days + " Days ";}
        if ((int)hours != 0) {breakDown = breakDown + (int)hours + " Hrs ";}
        if ((int)min != 0) {breakDown = breakDown + (int)min + " Min";}
        
        if (h.equals("0")) {breakDown = "0";}
        
        return breakDown;
    } // End getHoursBreakdown

    /* private calcAvailableToday (fixed types)
    *
    * startBalance - The starting balance for the absence type
    * pastHOurs - hours used in the past up to today for absence type
    * 
    * Example: calcAvailableToday (80, 8)  
    * => 77
    *
    * Calulates hours today for fixed types - (start - used)  */
    private String calcAvailableToday(double startBalance, double pastHours) {

        double availableToday = 0;

        availableToday = startBalance - pastHours;
        
        return Double.toString(availableToday); // return availible today
        
    } // End calcAccrruedToday 
    
    /*private calcAvailableToday (accrued types)
    *
    * accrualRate - the absence type accrual rate
    * startBalance - The starting balance for the absence type
    * pastHOurs - hours used in the past up to today for absence type
    * 
    * Example: calcAvailableToday (.526, 94.5, 0)  
    * => 131.32
    *
    * Calulates hours accrued from the start of the year + startbalance - used  */
    private String calcAvailableToday(double accrualRate ,double startBalance, double pastHours) {
        
        double duration = 0;
        double availableToday = 0;
        
        // calculate days from Jan 1st to date in control
        int currentYear = Integer.parseInt(FORMAT_YEAR.format(Calendar.getInstance().getTime())); // current year
	LocalDate dateBefore = LocalDate.of(currentYear, 1, 1);
        LocalDate dateNow = LocalDate.now();
	long daysBetween = ChronoUnit.DAYS.between(dateBefore, dateNow);
        duration = (double)daysBetween;
        
        duration = duration++;                  // include today
        accruedNow = (duration * accrualRate);  // accured to today
        availableToday = ((duration * accrualRate) + startBalance) - pastHours;
        
        return Double.toString(availableToday); // return availible today
        
    } // End calcAccrruedToday  
    
    /* private getaccrualType
    *
    * accrualType - type data from the db table
    *
    * Example"
    * getaccrualType(0)
    * => "Fixed"
    *
    * Returns the accrualType for the absence type in Absence_Types table       */
    private String getAccrualType(double accrualType) {
        
        String aType = "";
        
        if (accrualType > 0) {
           aType = "Accrued";
        } else if (accrualType == 0) {
           aType = "Fixed"; 
        } else {aType = "Added";}
        
        return aType;
    } // End getaccrualType
    
    /* private calcFixedRemainingHours
    *
    * usedHours - hours used up to current date for type
    * plannedHours - hours planned for type from tomorrow to end of year
    * startBalance - the starting balance for the type
    * => returns the calculation (Start - (used + planned))
    *
    * Calcs the remining hours at the end of the year for a fixed type                          */
    private String calcFixedRemainingHours(double usedHours, double plannedHours, double startBalance) {
        
        return String.valueOf(startBalance - (usedHours + plannedHours));
    } // End calcFixedRemainingHours
    
    /* private calcAccuredRemainingHours
    *
    * usedHours - hours used up to current date for type
    * plannedHours - hours planned for type from tomorrow to end of year
    * startBalance - the starting balance for the type
    * accrualRAte - the accrual rate for the absence type
    * => returns the calculation ((days in year * rate) - (used + planned) + starting balance)
    *
    * returns the hours remaining at the end of the year for an accrued type          */
    private String calcAccuredRemainingHours(double usedHours, double plannedHours, double startBalance, double accrualRate) {
                    
        double remaining = (daysInYear * accrualRate) - (usedHours + plannedHours) + startBalance;
        return Double.toString(remaining);

    } // End calcAccuredRemainingHours
    
    /* public getStats
    *
    * Returns the stats arraylist of JSONOjects for DayForm parameter used in MyAbsences  */
    public static ArrayList<JSONObject> getStats() {
        
        return stats;
    }
    
    /* private leapYear
    *
    * year - the current year
    * => true if leap year, false if not
    *
    * For calculating accrued types remaining balance for a year  */
    private boolean leapYear (int year) {
        
        boolean isLeap = false;
        
        if (year % 4 == 0) {
            if (year % 100 == 0) { 
                if (year % 400 == 0) {
                   isLeap = true;
                }
            } else {isLeap = true;}
        } 
        
        return isLeap;
    } // End leapYear
    
    public ComboBox<String> getCboCalcType() {
        
        return cboCalcType;
        
    }
    
} // end class summaryReportBuilder
