/*
 * Christopher Burgess
 */
package myabsences;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import org.json.simple.JSONObject;

/**
 *
 * @author Christopher
 */
public class Warnings {
    
    /* static public addWarning
    *
    * AbsenceID
    * DayDate
    * warningName
    *
    * Example:
    * addWarning(1, 2021-3-15, MAX_ACCRUAL)
    * ==> Adds the warning to the  warning table for absenceID 1 on the date for MAX_ACCURAL warning
    *
    * This method adds a warning to the table for MAX_ACCRUAL. If the absenceID already exists in
    * the table it will just update it with the new date*/
    static public void addWarning(int absenceID, String dayDate, String warningName) {
        
        ArrayList<JSONObject> warnings =  Database.getWarnings();  // list of warnings 
        SimpleDateFormat formatCal = new SimpleDateFormat("MM/dd/yyyy");
        SimpleDateFormat formatDb = new SimpleDateFormat("yyyy-MM-dd");
        String calDate = "";
        
        try {
            Date date = formatDb.parse(dayDate);
            calDate = formatCal.format(date);    // use cal format date for creating the warning
        } catch (ParseException de) {
            // handle error
        }
        
        if (warningName.equals("MAX ACCRUAL")) {
            // check if there is a warning already for the absence_ID
            if (JsonMatch.getJsonIndex(warnings,"Absence_ID",absenceID) == -1) {
                String sql = "INSERT into WARNINGS (Warning_Name, Absence_ID, Date, Cal_Date) " +
                             "VALUES ('" + warningName + "','" + absenceID + "', '" + dayDate + "', '" + calDate + "')";
                Database.SQLUpdate(sql);    
            } else {
                String sql = "UPDATE WARNINGS " + "SET Date = '" + dayDate + "', Cal_Date = '" + calDate + "' " +
                             "WHERE Absence_ID = " + absenceID + " and Warning_Name = '" +warningName + "'";
                Database.SQLUpdate(sql);
            }
        }
    }
    
    /* static public addWarning
    *
    * warningName
    *
    * Example:
    * addWarning(RUN_SETUP)
    * ==> Adds the warning to the warning table with absenceID 0 for RUN_SETUP
    *
    * This method adds a warning to the table. If the warningName already exists in
    * the table it will not add it again For RUN_SETUP and ENTER_BALANCES warnings */
    static public void addWarning(String warningName) {
        
        ArrayList<JSONObject> warnings =  Database.getWarnings();  // list of warnings 

        if (JsonMatch.getJsonIndex(warnings,"Warning_Name",warningName) == -1) {
            String sql = "INSERT into WARNINGS (Warning_Name, Absence_ID) " +
                         "VALUES ('" + warningName + "','0')";
            Database.SQLUpdate(sql);    
        }
    }    

    /* static public getWarnings
    *
    * ==> ArrayList<String> warnMessages 
    *
    *
    * This method gets all warnings from Warnings table and makes unique strings for each
    * warning type found. It returns all the warning message strings built as ArrayList */
    static public ArrayList<String> getWarnings() {
        
        ArrayList<String> warnMessages = new ArrayList<>();
        ArrayList<JSONObject> warnings =  Database.getWarnings();  // list of warnings

        if (warnings.size() > 0) {
            
            for (int i = 0; i < warnings.size(); i++) {
                // Check for MAX_ACCRUAL warnings
                if (((String)warnings.get(i).get("Warning_Name")).equals("MAX_ACCRUAL")) {
                    String absenceType = (String)warnings.get(i).get("Absence_Type");
                    String calDate = (String)warnings.get(i).get("Cal_Date");      
                    warnMessages.add(absenceType + " Max Accrual on " + calDate);
                }
                if (((String)warnings.get(i).get("Warning_Name")).equals("RUN_SETUP")) {
                    warnMessages.add("Enter Starting Balances");
                }
                if (((String)warnings.get(i).get("Warning_Name")).equals("ENTER_BALANCES")) {
                    warnMessages.add("Enter Setup to Define Absence Types");
                }
            }
        }
        return warnMessages; 
    }

    /* static public removeWarning
    *
    * absence ID - the absenceID there is a warning for in the warnings table
    * warningName - the name of the warning type 
    *
    * Example: 
    * removeWarning(1, MAX_ACCRUAL)
    * ==> Database removes the warning with absenceID of 1 named MAX_ACCRUAL
    *
    * This method removes a warning from the warning table     */
    static public void removeWarning(int absenceID, String warningName) {
        
        ArrayList<JSONObject> warnings =  Database.getWarnings();  // list of warnings for Max Accurals reached.

        if (JsonMatch.getJsonIndex(warnings,"Absence_ID",absenceID) != -1) { 
            String sql = "DELETE from WARNINGS where Absence_ID = '" + absenceID + "' and Warning_Name = '" + warningName + "'";
            Database.SQLUpdate(sql);    
        }        
        
    } // end removeWarning
}
