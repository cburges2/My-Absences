
package myabsences;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import static myabsences.MyAbsences.cboCalcType;
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
    ArrayList<JSONObject> warnings = new ArrayList();  // list of warnings for Max Accurals reached.
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
    
    // Constructer
    public SummaryReportBuilder (String year) { 
                
        this.year = year;   // Set class variable year from parameter
        
        lastDate = year + "-12-31";   // build string for last day of this year
        
        // get the JSON object arrays from the database
        startBalances = Database.getStartBalances(year);                 
        futureHours = Database.getFutureHours(todayStr, lastDate, year); 
        pastHours = Database.getPastHours(todayStr, year);    
        absences = Database.getAbsences(year);  
        warnings = Database.getWarnings();
               
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
    */
    public final GridPane buildReport () {

        /* Set bottomReport Attributes */ 
        bottomReport.setPrefHeight(50); 
        bottomReport.setMaxWidth(978);
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

        // add the summaryTable array to the gridpane, while breaking down hours and adding colors and hours tooltips
        bottomReport.setHgap(50);    // Add space between columns
        bottomReport.setVgap(-2);    // Reduce space between rows
        String toolTipText = " ";
        absenceType = "";
        for (int row = 0; row < numRows+1; row++) {
            for (int col = 0; col < numColumns; col++) {
                    if (col == 0 && row == 0) {
                        GridPane.setConstraints(cboCalcType, col, row);
                        bottomReport.getChildren().add(cboCalcType);
                    }
                    if (col == 0 && row > 0) absenceType = summaryTable[row][col]; // get the absence type for the row
                    Label label = new Label(" ");
                    if (col > 0 && row > 0 && !summaryTable[row][col].equals("--")) {
                        label.setText(getHoursBreakdown(summaryTable[row][col])); // breakdown hours for display if hours
                    }
                    else {label.setText(summaryTable[row][col]);}
                    if (row != 0) {toolTipText = summaryTable[row][col] + " Hours";} // set tt tp actual hours
                    if (row == 0) { label.getStyleClass().add("summaryheader"); }    // style headers 
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
     *
    */
    private void buildSummaryTable() {
        
        // iterate through the absence_types in Start Balances to get data from ArrayLists to build the summary table rows
        for (int row=0; row < numRows; row++) {
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
            accrualRate = (double)startBalances.get(row).get("accrual_Rate");
            String accrualType = getaccrualType(accrualRate);  // get the type - accrued, fixed, or not calculated. 
            // set accrued absence type data for summary table and check accrual max
            if (accrualType.equals("Accrued")) { 
                if (thisYear.equals(year)) {
                    accrualMax = (double)startBalances.get(row).get("Max_Accrual");
                    accruedToday = calcAccruedToday(accrualRate); 
                    if (accrualMax != 0) findMaxAccrual();   // set max warning for absence type if there is a max
                } 
                // if not found set to zero 
                accruedToday = calcAccruedToday(accrualRate); 
                summaryTable[row][4] = accruedToday;
                summaryTable[row][5] = calcAccuredRemainingHours(past_Hours,future_Hours,startBalance,accrualRate);
            }          
            // fixed accrual - calc remaining balance (startbalance - (used + planned))           
            if (accrualType.equals("Fixed")) {
                summaryTable[row][4] = "--";    // no accurred today for fixed hours, just show dashes   
                summaryTable[row][5] = calcFixedRemainingHours(past_Hours,future_Hours,startBalance); 
            }        
            // Holidays - not calculated
            if (accrualType.equals("Holiday")) {
                summaryTable[row][1] = Double.toString(past_Hours + future_Hours);
                summaryTable[row][4] =  "--";            // no accurred today for not calculated hours, just show dashes
                summaryTable[row][5] = "--";     // no remaining balance, show just dashes
            }
        }

        // move array elements and add headers to top of summary Table
        String[] headers = new String[]{"          "," Beginning Balance   ","Used  ","Planned ","Accrued To Date  ","Remaining     "};
        for (int row = numRows; row > 0; row--) {
            for (int col = 0; col < numColumns; col++) {
                summaryTable[row][col] = summaryTable[row-1][col]; // move exiting array up on element
            }    
        }        
        System.arraycopy(headers, 0, summaryTable[0], 0, numColumns); // add headers to first element
        
    } // End buildSummaryTable method
    
    /* private findMaxAccrual
    *
    *
    *
    *
    */
    private void findMaxAccrual () {
  
        String dayDate = todayStr;
        double totalHours = accruedNow;
        int warningLimit = 180;   // days before max to warn
        int numDays = 0;          // days counter for warningLimit
        
        boolean done = false;
        while (!done) {
            // increment date
            String nextDay =  LocalDate.parse(dayDate).plusDays(1).toString();
            dayDate = nextDay; 
            totalHours = totalHours += accrualRate;
            
            // check if the day has submitted absence hours to subtract
            if (JsonMatch.getJsonIndex(absences,"Date",dayDate) != -1) {
                // check if the hours are of the current absence type
                if (JsonMatch.getJsonString(absences,"Date",dayDate,"Absence_Type").equals(absenceType)) {
                    // Check if the hours are submitted 
                    int submitted = JsonMatch.getJsonInt(absences, "Date", nextDay, "Submitted");
                    double hours = JsonMatch.getJsonDouble(absences, "Date", nextDay, "Hours");
                    if (submitted == 1) {
                        totalHours = totalHours - hours; // subtract the submitted hours from totalHours  
                    }
                }
            }
            numDays++;
            String calDate = "";
            // Check if the totalHours are now more than the accrual Max
            absenceID = JsonMatch.getJsonInt(startBalances,"Absence_Type",absenceType,"Absence_ID");
            if ((totalHours  > accrualMax) && numDays < warningLimit) {
                try {
                    Date date = formatDb.parse(dayDate);
                    calDate = formatCal.format(date);    // use cal format date for creating the warning
                } catch (ParseException de) {
                    // handle error
                }
                if (JsonMatch.getJsonIndex(warnings,"Absence_Type",absenceType) == -1) {
                    String sql = "INSERT into WARNINGS (Warning_Name, Absence_ID, Date, Cal_Date) " +
                                 "VALUES ('MAX_ACCRUAL','" + absenceID + "', '" + dayDate + "', '" + calDate + "')";
                    Database.SQLUpdate(sql);    
                } else {
                    String sql = "UPDATE WARNINGS " + "SET Date = '" + dayDate + "', Cal_Date = '" + calDate + "' " +
                                 "WHERE Absence_ID = " + absenceID + " and Warning_Name = 'MAX_ACCRUAL'";
                    Database.SQLUpdate(sql);
                }
                done = true;
            }
            // Stop looking after end of this year and Delete any past warnings for absence type
            if (!dayDate.substring(0,4).equals(year)) {
                if (JsonMatch.getJsonIndex(warnings,"Absence_Type",absenceType) != -1) { 
                    String sql = "DELETE from WARNINGS where Absence_ID = '" + absenceID + "' and Warning_Name = 'MAX_ACCRUAL'";
                    Database.SQLUpdate(sql);    
                }     
                done = true;
            }
        }
    } // end findMaxAccrual
    
    /* private getHoursBreakdown
    *
    *
    *                                    */
    private String getHoursBreakdown(String h) {
        
        double hrs = Double.valueOf(h);
        
        double ww = hrs/40;    // work week
        double weeks = Math.floor(ww);  // whole weeks
        double dayValue = 5*((ww)-(int)(ww));
        double days = (int)(dayValue);  // remaining whole days
        double hours = Math.floor(8*(dayValue-days));   // remaining whole hours
        double min = Math.round((60*((8*(dayValue-days))-(int)(8*(dayValue-days)))));
        
        // fix overlow minutes, hours and hours
        if (min == 60) {hours++; min = 0;}
        if (hours == 8) {days++; hours = 0;}
        if (days == 5) {weeks++; days = 0;}

        String breakDown = "";
        if ((int)weeks != 0) {breakDown = (int)weeks + " Weeks ";}      
        if ((int)days != 0) {breakDown = breakDown + (int)days + " Days ";}
        if ((int)hours != 0) {breakDown = breakDown + (int)hours + " Hours ";}
        if ((int)min != 0) {breakDown = breakDown + (int)min + " Min ";}
        
        if (h.equals("0")) {breakDown = "0";}
        
        return breakDown;
    } // End getHoursBreakdown
    
    /*private calcAccruedToday
    *
    *
    *                       */
    private String calcAccruedToday(double accrualRate) {
        
        String startStr = "2021-01-01";
        double duration1 = 0;

        try {
            Date date1 = formatDb.parse(startStr);
            Date date2 = formatDb.parse(todayStr);
            long diff = date2.getTime() - date1.getTime();
            duration1 = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        
        double duration = duration1+1;   // include today
        accruedNow = Math.round((duration) * accrualRate);
        
        return Double.toString(accruedNow);
        
    } // End calcAccrruedToday  
    
    /* private getaccrualType
    *
    *
    *
    *                                 */
    private String getaccrualType(double accrualType) {
        
        String aType = "";
        
        if (accrualType > 0) {
           aType = "Accrued";
        } else if (accrualType == 0) {
           aType = "Fixed"; 
        } else {aType = "Holiday";}
        
        return aType;
    } // End getaccrualType
    
    /* private calcFixedRemainingHours
    *
    *
    *
    *                           */
    private String calcFixedRemainingHours(double usedHours, double plannedHours, double startBalance) {
        
        return String.valueOf(startBalance - (usedHours + plannedHours));
    } // End calcFixedRemainingHours
    
    /* private calcAccuredRemainingHours
    *
    *
    *
    *                  */
    private String calcAccuredRemainingHours(double usedHours, double plannedHours, double startBalance, double accrualRate) {
                    
        double remaining = (daysInYear * accrualRate) - (usedHours + plannedHours) + startBalance;
        return Double.toString(remaining);

    } // End calcAccuredRemainingHours
    
    
    /* private leapYear
    *
    *
    *
    *                      */
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
    
} // end class summaryReportBuilder
