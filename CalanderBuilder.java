/** 
 * @Course: SDEV 435 ~ 
 * @Author Name: Christopher Burgess
 * @Date: Feb 6, 2021
 * @Subclass CalanderBuilder - Builds the year calendar out of buttons, and adds the day absence data
 * to the day buttons for coloring and tool tips. 
 */
package myabsences;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import org.json.simple.JSONObject;

/**
 *
 * @author Christopher Burgess
 * 
 * This class builds the calendar grid pane for the main screen.  It is composed 
 * of month grid panes that are built out of buttons for the dates.  A get method 
 * allows the main class to get the button array of all the year days, so that 
 * the control handlers can act on a day button being pushed. */
public class CalanderBuilder {
    
    // Set calander build variables
    String year;                         // year parameter
    GridPane calander = new GridPane();  // GridPane returned
    String todayStr;                     // todays's db date
    String dateString = " ";   // string used to label a button with a date number
    int date = 0;              // the numeric date - incremented and converted to dateString for adding to day buttons  
    int firstDay = 0;          // the first day of the month -> used for column# in row 2 of the month gpane to start date button numbering
    int lastDay = 0;           // last date to label buttons for the month
    String[] monthName = new String[]{ "January","February","March","April","May","June","July","August","September","October","November","December" };
    String[] weekDay = new String[]{ "    S","    M","    T","   W","    T","    F","    S" };   // Month and Weekday Labels for the month headers
    int monthNumber = 0;      // index in monthName array 
    int buildMonth = 0;       // The month being built in calander gpane (Jan = 0)     
    String calMonth = "";     // Two digit String value of calander month value (jan = 01)
    int btnIter = 0;          // incrementor for the button number in a month in the button 2d array 
    ArrayList<JSONObject> absences = new ArrayList<>();
    ArrayList<JSONObject> warnings = new ArrayList<>();
    JSONObject settings = new JSONObject();
    String buildDate;         // the date we are building in the month calendar
    static Button[][] btnMonth = new Button[12][42];  // days in month button array (month, day)
    int workWeekends = 0;
    
    /* Constructor
    * year sets the current year being viewed in the calendar
    * 
    * This sets the year, populates the absneces and warnings arrays from db
    * and sets today's date in db format      */
    public CalanderBuilder(String year) {
        
        this.year = year;
        absences = Database.getAllAbsences(year);
        warnings = Database.getWarnings();
        settings = Database.getSettings();          // for weekend coloring
        workWeekends = (int)settings.get("Work_Weekends");
        // Set Today's date as a string in db format            
        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat formatDb = new SimpleDateFormat("yyyy-MM-dd");  
        todayStr = formatDb.format(today); //note: todayStr will not show on past year calendars 
        
    }    
    
    /* public buildCalendar Method - Build the calendar and return the GridPane
    *
    * Examples:
    * buildCalendar()
    * * => Gridpane containing the calendar year from constructor with absences and today colored
    * 
    * Returns the calendar GridPane for the main screen border pane middle  */
    public GridPane buildCalander () {   
        
        /*Set Calander GridPane attributes */
        calander.setMaxWidth(900);
        calander.setMaxHeight(600);
        calander.getStyleClass().add("calendar");
        calander.setAlignment(Pos.TOP_CENTER);      
        calander.setHgap(0);
        calander.setVgap(0);    

        // ** Build Months **
        for (int monthRow = 0; monthRow < 3; monthRow++) { 
            for (int monthColumn = 0; monthColumn < 4 ; monthColumn++) {
                // Start a new Month gridpade
                GridPane month = new GridPane();
                month.getStyleClass().add("calmonth");
                date = 0; // reset numeric date for new month numbering
               
                // call methods to get month being built, first day of month and number of days in the month
                buildMonth = getBuildMonth(monthColumn,monthRow);  // also used in button array for each month
                calMonth = String.valueOf(buildMonth+1);    
                if (calMonth.length() == 1) {calMonth = "0" + calMonth;}   // to build todays date string for today coloring
                firstDay = getMonthStartDay(buildMonth,Integer.parseInt(year));
                lastDay = getNumberOfDays(buildMonth,Integer.parseInt(year));
                btnIter = 0;  // reset day button counter at start of building each month
                
                // Build Month Gridpane in the Calendar gridpane
                for (int dayRow = 0 ; dayRow < 8; dayRow++) {      // build the dayRow (a week)               
                    if (dayRow == 0) {    // row 1 used for Month title label
                        Label lblMonth = new Label (monthName[monthNumber]);                        
                        lblMonth.getStyleClass().add("monthtitle");
                        GridPane.setColumnSpan(lblMonth, 7);  // Month Title to span 7 columns
                        month.getChildren().add(lblMonth);
                        monthNumber++;  // increment the month number for next Month label
                    } 
                    // Set the row columns with the days for each month               
                    for (int dayColumn = 0; dayColumn < 7; dayColumn++) {
                        // row 2 of a month build used for the weekday labels
                        if (dayRow == 1) {  
                            Label lblWeekDay = new Label(weekDay[dayColumn]);
                            GridPane.setConstraints(lblWeekDay, dayColumn, dayRow);
                            lblWeekDay.setAlignment(Pos.CENTER);
                            month.getChildren().add(lblWeekDay);                           
                        } else if (dayRow > 1) {      // all rows after the weekday labels are date fields 
                            if (dayRow == 2 && dayColumn < firstDay & firstDay != 7) {   // firstDay=7 will cause calander to start numbering on dayRow 3 without this
                                dateString = " ";    // label buttons blank until the 1st of the month on row 3, else increment the date on buttons as month is built
                            } else {
                               date++;
                               dateString = String.valueOf(date);   // else set the date number as the button text
                            }
                            // label buttons as blank after the last day of the month
                            if (date > lastDay) {dateString=" ";}
                            // add the day button and force size
                            btnMonth[buildMonth][btnIter] = new Button(dateString); // create button in the array labled with day number
                            btnMonth[buildMonth][btnIter].setMaxSize(34,25);
                            btnMonth[buildMonth][btnIter].setMinSize(34,25);
                            // build the date string for the day being added
                            String calDate = dateString;
                            if (calDate.length() == 1 && !calDate.equals(" ")) {calDate = "0" + calDate;}
                            buildDate = year + "-" + calMonth + "-" + calDate;
                            if ((dayColumn == 0 || dayColumn == 6) && workWeekends == 0) {
                                btnMonth[buildMonth][btnIter].getStyleClass().add("btnweekend"); // grey date number for weekends
                            }
                            String toolTipText = ""; 
                            // Add Max Accrual warning to max accrual day if there is one
                            if (JsonMatch.getJsonIndex(warnings,"Date",buildDate) != -1) {
                                if (JsonMatch.getJsonIndex(warnings,"Warning_Name","MAX_ACCRUAL") != -1) {
                                    String dateColor  = JsonMatch.getJsonString(warnings, "Date", buildDate, "Color").toLowerCase();
                                    String warnColor = "btnwarn" + dateColor;
                                    btnMonth[buildMonth][btnIter].getStyleClass().add(warnColor);
                                    String ttw = JsonMatch.getJsonString(warnings, "Date", buildDate, "Absence_Type") + " Max Accrual Date" + "\n";
                                    // check if there is not an absence on date already to add to its tt
                                    if (JsonMatch.getJsonIndex(absences,"Date",buildDate) == -1) {
                                        Tooltip wtp = new Tooltip(ttw);   // create a new tooltip for day
                                        wtp.getStyleClass().add("tt" + dateColor);
                                        btnMonth[buildMonth][btnIter].setTooltip(wtp);
                                    } else {
                                        toolTipText = toolTipText + ttw;
                                    }
                                }
                            }
                            // Add Absence Data to the day for day coloring and tooltip 
                            String color = JsonMatch.getJsonString(absences, "Date", buildDate, "Color");  // button border color is last color
                            if (JsonMatch.getJsonIndex(absences,"Date",buildDate) != -1) {   // check if the date is in absences 
                                ArrayList<JSONObject> dayAbsences = Database.getDayAbsence(buildDate);  // get all the absence type hours for the day
                                toolTipText = toolTipText + JsonMatch.getJsonString(absences, "Date", buildDate, "Title") + "\n";
                                String[] colors = new String[dayAbsences.size()];   // array of colors for color gradient
                                for (int i = 0; i < dayAbsences.size(); i++ ) {
                                    String absenceType = (String)dayAbsences.get(i).get("Absence_Type");
                                    toolTipText = toolTipText + absenceType + "\n";
                                    double dblHours = (double)dayAbsences.get(i).get("Hours");
                                    String hoursMin = getHoursMinutes(dblHours);
                                    toolTipText = toolTipText + hoursMin + "\n"; 
                                    colors[i] = (String)dayAbsences.get(i).get("Color");  // add the color to the array for gradient coloring
                                }
                                int submit = JsonMatch.getJsonInt(absences, "Date", buildDate, "Submitted");
                                if (submit == 1) {   
                                    toolTipText = toolTipText + "Submitted";
                                } else {
                                    toolTipText = toolTipText + "Not Submitted";
                                    btnMonth[buildMonth][btnIter].getStyleClass().add("underline"); 
                                }
                                Tooltip btp = new Tooltip(toolTipText);
                                String ttStyle = "";
                                String gradient = getColorGradient(colors);

                                String btnStyle = "-fx-background-color: linear-gradient(" + gradient + ")"
                                        + "; -fx-background-radius: 1 1 1 1; -fx-border-color: " + color + "; -fx-border-width: .8;";
                                btnMonth[buildMonth][btnIter].setStyle(btnStyle);              // set background color
                                ttStyle = "-fx-background-color: linear-gradient(" + gradient + ")"
                                        + "; -fx-background-radius: 7 7 7 7; -fx-text-fill: White;";
                                btp.setStyle(ttStyle);              // set tt background
                                btnMonth[buildMonth][btnIter].setTooltip(btp);  // set the tooltip text
                            } else { 
                                btnMonth[buildMonth][btnIter].getStyleClass().add("btnday"); // else use default day styling for empty days 
                            }  
                            if (buildDate.equals(todayStr)) {btnMonth[buildMonth][btnIter].getStyleClass().add("btntoday");} // Mark Today's date as bold blue Outline
                         
                            GridPane.setConstraints(btnMonth[buildMonth][btnIter], dayColumn, dayRow);  // set button position in the month gridpane
                            month.getChildren().add(btnMonth[buildMonth][btnIter]);                     // add the button to the month gridpane
                            btnIter++;    // increment counter for button array for current month
                          }
                    } // end dayColumn loop
                } // end dayRow loop
                GridPane.setConstraints(month, monthColumn, monthRow);
                calander.getChildren().add(month);   // add the month to the calander gridpane
            }
        } 
        return calander;
}
 
    /* private: Get the month being built in calander gpane 
    * 
    * col - The month column being built in the calander
    * row - The row column being built in the calander
    * 
    * Examples:
    * getBuildMonth(1,0)
    * * => 1 
    *
    * Returns the month value (0-11, jan=0)  */
    private int getBuildMonth (int col, int row) {

        int month = 0;

        // Get Month number from row,column: 
        if (col==0 && row==0) {
            month = 0;    
        } else if (col==1 && row==0) {
            month = 1;
        } else if (col==2 && row==0) {
            month = 2;
        } else if (col==3 && row==0) {
            month = 3;
        } else if (col==0 && row==1) {
            month = 4;
        } else if (col==1 && row==1) {
            month = 5;
        } else if (col==2 && row==1) {
            month = 6;
        } else if (col==3 && row==1) {
            month = 7;
        } else if (col==0 && row==2) {
            month = 8;
        } else if (col==1 && row==2) {
            month = 9;
        } else if (col==2 && row==2) {
            month = 10;
        } else if (col==3 && row==2) {
            month = 11;
        }    

        return month;

    } // end function getBuildMonth

    /* private: Get the starting column for the first day of a month
    * 
    * month - The month returned from getBuildMonth
    * year - The year that is currently being built in the calander
    * 
    * Examples:
    * getMonthStartDay(1,2021)
    * * => 1 
    *
    * Returns the weekday column of the first day of the month (0-6, Sunday=0) */
    private int getMonthStartDay(int month, int year) {

        int startDay = 0;

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DATE, 25);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.YEAR, year);

        cal.set(Calendar.DAY_OF_MONTH, 1);
        Date firstDayOfMonth = cal.getTime();  


        DateFormat sdf = new SimpleDateFormat("u");    
        startDay = Integer.parseInt(sdf.format(firstDayOfMonth));       

        return startDay;
    } // end function getMonthStartDay

    /* private: Get the last date for a month
    * 
    * month - The month column being built in the calander (jan=0)
    * year - The row column being built in the calander
    * 
    * Examples:
    * getNumberOfDays(1,2021)
    * * => 28 
    *
    * Returns the number of days in the month  */
    private int getNumberOfDays (int month, int year) {

        int numDays = 0;

        switch (month) {
            case 0: 
                numDays = 31;
                break;
            case 1:
                numDays = 28;
                break;
            case 2:
                numDays = 31;
                break;
            case 3:
                numDays = 30;
                break;
            case 4:
                numDays = 31;
                break;
            case 5:
                numDays = 30;
                break;
            case 6:
                numDays = 31;
                break;    
            case 7:
                numDays = 31;
                break;
            case 8:
                numDays = 30;
                break;
            case 9:
                numDays = 31;
                break;
            case 10:
                numDays = 30;
                break;
            case 11:
                numDays = 31;
                break;
            default:
                numDays = 31;
        }

        // Check if Leap year for February
        if (month == 1) {
            if (year % 4 == 0) {
                if (year % 100 == 0) { 
                    if (year % 400 == 0) {
                       numDays = 29;
                    }
                } else {numDays=29;}
            } 
        } 

        return numDays;        
    } // end function getNumberOfDays
    
    /* private getHoursMin 
    *
    * decimalHours - the hours in decimal format
    *
    * Example:
    * getHoursMinutes(4.25);
    * * => 4 Hours 15 Min
    *
    * Returns a string that says the Hours and Minutes */
    private String getHoursMinutes (double decimalHours) {
        
        int hours = (int)decimalHours;
        double fractional = decimalHours - hours;
        int minutes = (int)(fractional * 60.0);
        
        String hrsMin = hours + " Hours ";
        if (minutes !=0) {hrsMin = hrsMin + minutes + " Min";}
        
        return hrsMin;

    } // End getHoursMin   
    
    /* public getButtons
    *
    * Returns the buttons 2D array of calander days 
    * used for button event handler in MyAbsences main class*/
    public Button[][] getBtnMonth() {
        
        return btnMonth;
        
    }    
    
    /* private getButtonGradient
    *
    * colors - an array of colors to put on the button or tt
    *
    * This method sets the gradient colors for the absence types with hours */
    private String getColorGradient(String[] colors) {
        
        String gradient = "";
        String gradient1 = "";
        String gradient2 = "";
        int numColors = colors.length;
        
        for(int i = 0; i < numColors; i++) {
              if (i < numColors && i > 0) {
                  gradient += ",";
                  gradient1 += ",";
                  gradient2 += ",";
              }
              switch (colors[i]) {
                case "Red": 
                     gradient += "#f0a99e,#e34444";
                     gradient1 += "#e34444";
                     gradient2 += "#eb7b73";
                    break;
                case "Blue":
                    gradient += "deepskyblue,#8da5e3";
                    gradient1 += "#8da5e3";
                    gradient2 += "#7387eb"; 
                    break;
                case "Orange":
                    gradient += "#f0c49e,darkorange";
                    gradient1 += "darkorange";
                    gradient2 += "#e8944a";
                    break;
               case "Purple":
                    gradient += "violet,#c39ce6";
                    gradient1 += "#c39ce6";
                    gradient2 += "#c39ce6"; 
                    break;
               case "Green":
                    gradient += "#9de69c,#32a632";
                    gradient1 += "#32a632";
                    gradient2 += "#50db25";
                    break;
                case "Yellow":
                    gradient += "#f0ec24,#d9d621";
                    gradient1 += "#d9d621";
                    gradient2 += "#e2e69c"; 
                   break;
            } 
        }
        
        // Use a single color gradient for a single color
        if (numColors == 1) {return gradient;}
        else {return gradient2;}  // use multi-color gradient for multi color
       
    } // end getColorGradient
    
} // end DayEntry class
