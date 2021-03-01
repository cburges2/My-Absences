
package myabsences;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import javafx.scene.control.Alert;
import static myabsences.MyAbsences.calcType;
import org.json.simple.JSONObject;

/**
 *
 * @author Christopher Burgess
 */
 public class Database {
    
   static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
   static final String DB_URL = "jdbc:sqlite:c:/sqlite/MyAbsences.db";    
   static final String USER = "username";
   static final String PASS = "password";  
   String sql = "";     // used for queries
   
   // Empty constructor
   public Database () {

   }

   /* public getYears
   *
   *
   * get the years there is balance data for in the db 
   * return the ArrayList of years ASC       */
   static public ArrayList<String> getYears() {
       
    Connection conn = null;
    Statement stmt = null;
    ArrayList<String> years = new ArrayList<>();
      
    try{
        Class.forName("org.sqlite.JDBC");    //Register JDBC driver
        conn = DriverManager.getConnection(DB_URL, USER, PASS);    //Open a connection

        //Execute query
        String sql = "SELECT DISTINCT year FROM STARTING_BALANCES ORDER BY year ASC";   
        stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        // loop through the result set
        while (rs.next()) {
            years.add(rs.getString("Year"));
        }

        return years;

        }catch(SQLException se){
           //Handle errors for JDBC
           se.printStackTrace();
        }catch(Exception e){
           //Handle errors for Class.forName
           e.printStackTrace();
        }finally{
           //finally block used to close resources
           try{
              if(stmt!=null)
                 stmt.close();
           }catch(SQLException se2){
           }// nothing we can do
           try{
              if(conn!=null)
                 conn.close();
           }catch(SQLException se){
              se.printStackTrace();
           }//end finally try
        }//end try */

         return null;   // return null if error
    }
    
    /* public getStartBalance 
   *
   *
   *
   *                                  */
    static public ArrayList<JSONObject> getStartBalances (String year) {
    Connection conn = null;
    Statement stmt = null;

    try{    
        Class.forName("org.sqlite.JDBC");    //Register JDBC driver
        conn = DriverManager.getConnection(DB_URL, USER, PASS);    //Open a connection
        
        //Execute query
        String sql = "Select t.absence_type,b.Absence_ID,t.Accrual_RATE,t.COLOR,Max_Accrual, " +
        "starting_balance from Starting_Balances as b " +
        "join absence_types as t " +
        "on t.absence_id = b.absence_id " +
        "Where b.year = " + year + " Order by absence_type ASC"; 
        
        stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
         
        ArrayList<JSONObject> result = new ArrayList<>();

        while (rs.next()) {
            JSONObject record = new JSONObject();
            record.put((String)"Absence_Type",rs.getString("Absence_Type"));
            record.put("Absence_ID",rs.getInt("Absence_ID"));
            record.put("accrual_Rate",rs.getDouble("accrual_Rate"));
            record.put((String)"Color",rs.getString("Color"));
            record.put("Starting_Balance",rs.getDouble("Starting_Balance"));
            record.put("Max_Accrual",rs.getDouble("MAX_ACCRUAL"));
            result.add(record);
        }
               
        return result;

        }catch(SQLException se){
           //Handle errors for JDBC
           se.printStackTrace();
        }catch(Exception e){
           //Handle errors for Class.forName
           e.printStackTrace();
        }finally{
           //finally block used to close resources
           try{
              if(stmt!=null)
                 stmt.close();
           }catch(SQLException se2){
           }// nothing we can do
           try{
              if(conn!=null)
                 conn.close();
           }catch(SQLException se){
              se.printStackTrace();
           }//end finally try
        }//end try */       

       
      return null;        
   }
   
   /* public getPastHours
   *
   *
   *
   *                         */
   static public ArrayList<JSONObject> getPastHours(String date, String year) {
       
    Connection conn = null;
    Statement stmt = null;

    try{    
        Class.forName("org.sqlite.JDBC");    //Register JDBC driver
        conn = DriverManager.getConnection(DB_URL, USER, PASS);    //Open a connection
        
        String thisYear = date.substring(0,4);
        
        // if year is not the current year, use 12/31 of the past year as date range
        if (!thisYear.equals(year)) {
            date = year + "-12-31";
        }

        // Execute query
        String sql = "Select t.Absence_type,sum(Hours) as Past_Hours from Absences as a " +
        "join absence_types as t " +
        "on a.absence_id = t.absence_id " +
        "where a.date BETWEEN '" + year + "-01-01' AND '" + date + "' ";
        if (calcType.equals("Submitted Only")) {sql = sql + "and a.submitted = 1 ";} 
        sql = sql + "Group by absence_type"; 
        
        stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        
        ArrayList<JSONObject> result = new ArrayList<>();
  
        while (rs.next()) {
            JSONObject record = new JSONObject();
            record.put("Absence_Type",rs.getString("Absence_Type"));
            record.put("Past_Hours",rs.getDouble("Past_Hours"));
            result.add(record);
        }
                      
        return result;

        }catch(SQLException se){
           //Handle errors for JDBC
           se.printStackTrace();
        }catch(Exception e){
           //Handle errors for Class.forName
           e.printStackTrace();
        }finally{
           //finally block used to close resources
           try{
              if(stmt!=null)
                 stmt.close();
           }catch(SQLException se2){
           }// nothing we can do
           try{
              if(conn!=null)
                 conn.close();
           }catch(SQLException se){
              se.printStackTrace();
           }//end finally try
        }//end try */       

       
      return null; 
       
   } // end getPastHours
   
   
   /* public getFutureHours
   *
   *
   *                         */
   static public ArrayList<JSONObject> getFutureHours(String startDate, String endDate, String year) {

    Connection conn = null;
    Statement stmt = null;

    try{    
        Class.forName("org.sqlite.JDBC");    //Register JDBC driver
        conn = DriverManager.getConnection(DB_URL, USER, PASS);    //Open a connection
        
        // increment today's date to tomorrow for future hours query
        LocalDate date1 = LocalDate.parse(startDate);
        LocalDate tomorrow = date1.plusDays(1); 
        startDate = tomorrow.toString();
        
        // get current year from date to check if the calendar year is the current year
        String thisYear = startDate.substring(0,4);

        String sql = "Select t.Absence_type,sum(Hours)as Future_Hours from Absences as a " +
        "join absence_types as t " +
        "on a.absence_id = t.absence_id " +
        "where a.date BETWEEN '" + startDate + "' AND '" + endDate + "' ";
        if (calcType.equals("Submitted Only")) {sql = sql + "and a.submitted = 1 ";}         
        sql = sql + "group by absence_type"; 
        
        
        //Execute query
        stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        
        ArrayList<JSONObject> result = new ArrayList<>();
  
        while (rs.next()) {
            JSONObject record = new JSONObject();
            record.put("Absence_Type",rs.getString("Absence_Type"));
            double futureHrs = rs.getDouble("Future_Hours");
            if (!thisYear.equals(year)) {
                record.put("Future_Hours",0);
            } else {
                record.put("Future_Hours",futureHrs);
            }
            result.add(record);
        }
              
        return result;

        }catch(SQLException se){
           //Handle errors for JDBC
           se.printStackTrace();
        }catch(Exception e){
           //Handle errors for Class.forName
           e.printStackTrace();
        }finally{
           //finally block used to close resources
           try{
              if(stmt!=null)
                 stmt.close();
           }catch(SQLException se2){
           }// nothing we can do
           try{
              if(conn!=null)
                 conn.close();
           }catch(SQLException se){
              se.printStackTrace();
           }//end finally try
        }//end try */       

      return null; 
       
   } // end getFutureHours
 
    /* public getAbsence - get the absence for the day in the DayEntry form
     * called from DayEntry to pre-fill the form with absence data for the day
     * 
     * Type, Hours                                                        */
    static public JSONObject getAbsence (String date) { 
    
        Connection conn = null;
        Statement stmt = null;

        try{    
            Class.forName("org.sqlite.JDBC");    //Register JDBC driver
            conn = DriverManager.getConnection(DB_URL, USER, PASS);    //Open a connection

            //Execute query
            String sql = "Select Date,t.Absence_type,t.color,Title,Hours,Submitted,Notes from Absences as a " +
            "join absence_types as t " +
            "on a.absence_id = t.absence_id " +
            "where a.date = '" + date + "'"; 

            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            JSONObject record = new JSONObject();

            while (rs.next()) {
                record.put("Date",rs.getString("Date"));
                record.put("Absence_Type",rs.getString("Absence_Type"));
                record.put("Color",rs.getString("Color"));
                record.put("Title",rs.getString("Title"));
                record.put("Hours",rs.getDouble("Hours"));
                record.put("Submitted",rs.getInt("Submitted"));
                record.put("Notes",rs.getString("Notes"));
            }

            return record;

            }catch(SQLException se){
               //Handle errors for JDBC
               se.printStackTrace();
            }catch(Exception e){
               //Handle errors for Class.forName
               e.printStackTrace();
            }finally{
               //finally block used to close resources
               try{
                  if(stmt!=null)
                     stmt.close();
               }catch(SQLException se2){
               }// nothing we can do
               try{
                  if(conn!=null)
                     conn.close();
               }catch(SQLException se){
                  se.printStackTrace();
               }//end finally try
            }//end try */

             return null;   // return null if error    
        
    }        

   
    /* getAbsences - get the absences for the current year to ArrayList of JSONObjects
     * called before running buildCalendar() method
     * used for marking days with absences in CalendarBuilder and creating the tooltips with hours
     *                                                        */
    static public ArrayList<JSONObject> getAbsences (String year) {

        Connection conn = null;
        Statement stmt = null;

        try{    
            Class.forName("org.sqlite.JDBC");    //Register JDBC driver
            conn = DriverManager.getConnection(DB_URL, USER, PASS);    //Open a connection

            //Execute query
            String sql = "Select Date,t.Absence_type,t.color,Title,Hours,Submitted,Notes from Absences as a " +
            "join absence_types as t " +
            "on a.absence_id = t.absence_id " +
            "where a.date BETWEEN '" + year + "-01-01' AND '" + year + "-12-31' " + 
            "order by date"; 

            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            ArrayList<JSONObject> result = new ArrayList<>();

            while (rs.next()) {
                JSONObject record = new JSONObject();
                record.put("Date",rs.getString("Date"));
                record.put("Absence_Type",rs.getString("Absence_Type"));
                record.put("Color",rs.getString("Color"));
                record.put("Title",rs.getString("Title"));
                record.put("Hours",rs.getDouble("Hours"));
                record.put("Submitted",rs.getInt("Submitted"));
                record.put("Notes",rs.getString("Notes"));
                result.add(record);
            }

            return result;

            }catch(SQLException se){
               //Handle errors for JDBC
               se.printStackTrace();
            }catch(Exception e){
               //Handle errors for Class.forName
               e.printStackTrace();
            }finally{
               //finally block used to close resources
               try{
                  if(stmt!=null)
                     stmt.close();
               }catch(SQLException se2){
               }// nothing we can do
               try{
                  if(conn!=null)
                     conn.close();
               }catch(SQLException se){
                  se.printStackTrace();
               }//end finally try
            }//end try */

             return null;   // return null if error    
    }
    
    /* public getAbsenceTypes
    *
    *
    *
    */
    static public ArrayList<JSONObject> getAbsenceTypes() {

        Connection conn = null;
        Statement stmt = null;
        ArrayList<JSONObject> result = new ArrayList<>();
        
        try{    
            Class.forName("org.sqlite.JDBC");    //Register JDBC driver
            conn = DriverManager.getConnection(DB_URL, USER, PASS);    //Open a connection

            //Execute query
            String sql = "Select Absence_ID, Absence_type,color,absence_id, accrual_rate from Absence_Types " +
                  "order by Absence_type ASC";

            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                JSONObject record = new JSONObject();
                record.put("Absence_ID",rs.getInt("Absence_ID"));
                record.put("Absence_Type",rs.getString("Absence_Type"));
                record.put("Color",rs.getString("Color"));
                record.put("accrual_Rate",rs.getDouble("Accrual_Rate"));
                result.add(record);
            }

            return result;

            }catch(SQLException se){
               //Handle errors for JDBC
               System.out.println("JDBC Error " + se);
            }catch(Exception e){
               //Handle errors for Class.forName
               System.out.println("Class.forName Error " + e);
            }finally{
               //finally block used to close resources
               try{
                  if(stmt!=null)
                     stmt.close();
               }catch(SQLException se2){
               }// nothing we can do
               try{
                  if(conn!=null)
                     conn.close();
               }catch(SQLException se){
                  se.printStackTrace();
               }//end finally try
            }//end try */
            
        return null;         
    }

    /* public getWarnings
   *
   *
   *
   *                                  */
    static public ArrayList<JSONObject> getWarnings() {
    Connection conn = null;
    Statement stmt = null;

    try{    
        Class.forName("org.sqlite.JDBC");    //Register JDBC driver
        conn = DriverManager.getConnection(DB_URL, USER, PASS);    //Open a connection
        
        //Execute query
        String sql = "Select Date, Cal_Date, Warning_Name, t.Absence_Type,t.Color from Warnings as w " +
        "join absence_types as t " +
        "on t.absence_id = w.absence_id " +
        "Order by absence_type ASC"; 
        
        stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
         
        ArrayList<JSONObject> result = new ArrayList<>();

        while (rs.next()) {
            JSONObject record = new JSONObject();
            record.put((String)"Absence_Type",rs.getString("Absence_Type"));
            record.put((String)"Warning_Name",rs.getString("Warning_Name"));
            record.put((String)"Color",rs.getString("Color"));
            record.put("Date",rs.getString("Date"));
            record.put("Cal_Date",rs.getString("Cal_Date"));
            result.add(record);
        }
               
        return result;

        }catch(SQLException se){
           //Handle errors for JDBC
           se.printStackTrace();
        }catch(Exception e){
           //Handle errors for Class.forName
           e.printStackTrace();
        }finally{
           //finally block used to close resources
           try{
              if(stmt!=null)
                 stmt.close();
           }catch(SQLException se2){
           }// nothing we can do
           try{
              if(conn!=null)
                 conn.close();
           }catch(SQLException se){
              se.printStackTrace();
           }//end finally try
        }//end try */       

       
      return null;        
   } // end getWarnings
       
    /* SQLUpdate -> run a query to insert/delete/update with no data returned
     * Returns bool for success 
     *              */
    static public boolean SQLUpdate(String query) {
        
        boolean success = false;
        
        Connection conn = null;
        Statement stmt = null;
        
        try{
             //Register JDBC driver
             Class.forName("org.sqlite.JDBC");

             //Open a connection
             conn = DriverManager.getConnection(DB_URL, USER, PASS);

             //Execute query
             System.out.println("Executing Query...");
             String sql = query;        // set from queries loop
             
             stmt = conn.createStatement();
             stmt.executeUpdate(sql);//ResultSet rs = 

             success = true;
             return success;

            }catch(SQLException se){
                //Handle errors for JDBC
                System.out.println("JDBC Error " + se);
                String error = se.toString();
                if (error.contains("SQLITE_CONSTRAINT_UNIQUE")) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Planned Day already contains an Absence!");
                    alert.setHeaderText("Warning: Not all your absence days were saved");
                    alert.setContentText("You were saving a repeating absence but there was " +
                    "already an absence planned in that time period");
                    alert.showAndWait();
                }
            }catch(Exception e){
               //Handle errors for Class.forName
               System.out.println("Class.forName Error " + e);
             }finally{
                //finally block used to close resources
                try{
                   if(stmt!=null)
                      stmt.close();
                }catch(SQLException se2){
                }// nothing we can do
                try{
                   if(conn!=null)
                      conn.close();
                }catch(SQLException se){
                   se.printStackTrace();
                }//end finally try
             }//end try

        return success;
    }    
    
} // end class