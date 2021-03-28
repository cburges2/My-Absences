
package myabsences;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
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
   
   // Empty constructor
   public Database () {

   }
   
public static void createNewDatabase(String fileName) {

        String url = "jdbc:sqlite:C:/sqlite/db/" + fileName;

        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("A new database has been created.");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        createNewDatabase("test.db");
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

            rs.close();
            stmt.close();
            conn.close();
            return years;

            }catch(SQLException se){
               //Handle errors for JDBC
               ErrorHandler.JDBCError(se);
            }catch(Exception e){
               //Handle errors for Class.forName
               ErrorHandler.classForNameError(e);
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
                  ErrorHandler.closeConnectionError(se);
               }//end finally try
            }//end try */

             return null;   // return null if error
    }
   
   /* public getAbsenceID
   *
   * absenceID - The type name to get the ID for
   * ==> The absnce ID for the type name
   *
   * This method returns the absenceID from type name   */
    static public int getAbsenceID(String absenceType) {
        
        Connection conn = null;
        Statement stmt = null;
        int id = 0;

        try{    
            Class.forName("org.sqlite.JDBC");    //Register JDBC driver
            conn = DriverManager.getConnection(DB_URL, USER, PASS);    //Open a connection


            // Execute query
            String sql = "select Absence_ID from Absence_Types " +
                          "where Absence_Type = '" + absenceType + "'"; 

            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                id = rs.getInt("Absence_ID");
            }

            rs.close();
            stmt.close();
            conn.close();
            return id;

            }catch(SQLException se){
               //Handle errors for JDBC
               ErrorHandler.JDBCError(se);
            }catch(Exception e){
               //Handle errors for Class.forName
               ErrorHandler.classForNameError(e);
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
                  ErrorHandler.closeConnectionError(se);
               }//end finally try
            }//end try */       

        return 0;        
   }   

   /* public getNumDayTypeHours
   *
   * absenceID - The Absence ID to get the hours for
   * date - The date of the Absence ID 
   * ==> The number of hours planned for the Absence ID on the date
   *
   * This method returns the number of hours planned on a date for an absence ID   */
    static public double getNumDayTypeHours(int absenceID, String date) {
        
        Connection conn = null;
        Statement stmt = null;
        double hours = 0;

        try{    
            Class.forName("org.sqlite.JDBC");    //Register JDBC driver
            conn = DriverManager.getConnection(DB_URL, USER, PASS);    //Open a connection


            // Execute query
            String sql = "Select Hours From Hours " +
                    "Where Date = '" + date + "' "
                    + "and Absence_ID = " + absenceID;

            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                hours = rs.getDouble("Hours");
            }

            return hours;

            }catch(SQLException se){
               //Handle errors for JDBC
               ErrorHandler.JDBCError(se);
            }catch(Exception e){
               //Handle errors for Class.forName
               ErrorHandler.classForNameError(e);
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
                  ErrorHandler.closeConnectionError(se);
               }//end finally try
            }//end try */       

        return 0;        
   }    
    
   /* public getNumRows
   *
   * table - The table name to get the size for
   * ==> The number of rows in the table (int)
   *
   * This method returns the number of rows ina table   */
    static public int getNumRows(String table) {
        
        Connection conn = null;
        Statement stmt = null;
        int size = 0;

        try{    
            Class.forName("org.sqlite.JDBC");    //Register JDBC driver
            conn = DriverManager.getConnection(DB_URL, USER, PASS);    //Open a connection


            // Execute query
            String sql = "select count(*) as SIZE " +
                          "from " + table; 

            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                size = rs.getInt("Size");
            }

            return size;

            }catch(SQLException se){
               //Handle errors for JDBC
               ErrorHandler.JDBCError(se);
            }catch(Exception e){
               //Handle errors for Class.forName
               ErrorHandler.classForNameError(e);
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
                  ErrorHandler.closeConnectionError(se);
               }//end finally try
            }//end try */       

        return 0;        
   }
    
   /* public getGroupSize
   *
   * group - The group name to count
   * ==> The number of rows in the table matching group name
   *
   * This method returns the number of rows in hours that share a group   */
    static public int getGroupSize(String group) {
        
        Connection conn = null;
        Statement stmt = null;
        int size = 0;

        try{    
            Class.forName("org.sqlite.JDBC");    //Register JDBC driver
            conn = DriverManager.getConnection(DB_URL, USER, PASS);    //Open a connection


            // Execute query
            String sql = "select count(Absence_Group) as SIZE " 
                         + "from Absences where Absence_Group = '" + group + "'";

            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                size = rs.getInt("Size");
            }

            return size;

            }catch(SQLException se){
               //Handle errors for JDBC
               ErrorHandler.JDBCError(se);
            }catch(Exception e){
               //Handle errors for Class.forName
               ErrorHandler.classForNameError(e);
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
                  ErrorHandler.closeConnectionError(se);
               }//end finally try
            }//end try */       

        return 0;        
   }    // end getGroupSize
    
/* public getStartBalanceCount
   *
   * year - The year to get the balance count for
   * ==> The number of starting balances for the year
   *
   * This method returns number of starting balances in the Starting_Balances tabel   */
    static public int getStartBalanceCount(String year) {
        
        Connection conn = null;
        Statement stmt = null;
        int count = 0;

        try{    
            Class.forName("org.sqlite.JDBC");    //Register JDBC driver
            conn = DriverManager.getConnection(DB_URL, USER, PASS);    //Open a connection


            // Execute query
            String sql = "select count(absence_id) as Start_Count " +
                         "from Starting_Balances " + 
                         "where year = '" + year + "'"; 

            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                count = rs.getInt("Start_Count");
            }

            rs.close();
            stmt.close();
            conn.close();
            return count;

            }catch(SQLException se){
               //Handle errors for JDBC
               ErrorHandler.JDBCError(se);
            }catch(Exception e){
               //Handle errors for Class.forName
               ErrorHandler.classForNameError(e);
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
                  ErrorHandler.closeConnectionError(se);
               }//end finally try
            }//end try */       

        return 0;        
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
            String sql = "Select t.absence_type,b.Absence_ID,t.Accrual_Rate,t.Color,Max_Accrual, " +
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
                record.put("Accrual_Rate",rs.getDouble("Accrual_Rate"));
                record.put((String)"Color",rs.getString("Color"));
                record.put("Max_Accrual",rs.getDouble("Max_Accrual"));
                record.put("Starting_Balance",rs.getDouble("Starting_Balance"));
                result.add(record);
            }

            rs.close();
            stmt.close();
            conn.close();
            return result;

            }catch(SQLException se){
               //Handle errors for JDBC
               ErrorHandler.JDBCError(se);
            }catch(Exception e){
               //Handle errors for Class.forName
               ErrorHandler.classForNameError(e);
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
                  ErrorHandler.closeConnectionError(se);
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
            String sql = "Select t.Absence_type,sum(h.Hours) as Past_Hours from HOURS as h " +
                    "join Absences as a " +
                    "on h.date = a.date " +
                    "join absence_types as t " +
                    "on h.absence_id = t.absence_id " +
                    "where h.date BETWEEN '" + year + "-01-01' AND '" + date + "' ";
            if (calcType.equals("Submitted Only")) {sql = sql + "and a.submitted = 1 ";} 
            sql = sql + "Group by t.absence_type";                 

            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            ArrayList<JSONObject> result = new ArrayList<>();

            while (rs.next()) {
                JSONObject record = new JSONObject();
                record.put("Absence_Type",rs.getString("Absence_Type"));
                record.put("Past_Hours",rs.getDouble("Past_Hours"));
                result.add(record);
            }

            rs.close();
            stmt.close();
            conn.close();
            return result;

            }catch(SQLException se){
               //Handle errors for JDBC
               ErrorHandler.JDBCError(se);
            }catch(Exception e){
               //Handle errors for Class.forName
               ErrorHandler.classForNameError(e);
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
                  ErrorHandler.closeConnectionError(se);
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

            String sql = "Select t.Absence_type,sum(h.Hours) as Future_Hours from HOURS as h " +
                    "join Absences as a " +
                    "on h.date = a.date " +
                    "join absence_types as t " +
                    "on h.absence_id = t.absence_id " +
                    "where h.date BETWEEN '" + startDate + "' AND '" + endDate + "' ";
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

            rs.close();
            stmt.close();
            conn.close();
            return result;

            }catch(SQLException se){
               //Handle errors for JDBC
               ErrorHandler.JDBCError(se);
            }catch(Exception e){
               //Handle errors for Class.forName
               ErrorHandler.classForNameError(e);
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
                  ErrorHandler.closeConnectionError(se);
               }//end finally try
            }//end try */       

          return null; 
       
   } // end getFutureHours
 
    /* public getAbsence - get the absence for the day in the DayEntry form
     * called from DayEntry to pre-fill the form with absence data for the day
     * 
     * Type, Hours                                                        */
    static public ArrayList<JSONObject> getDayAbsence (String date) { 
    
        Connection conn = null;
        Statement stmt = null;

        try{    
            Class.forName("org.sqlite.JDBC");    //Register JDBC driver
            conn = DriverManager.getConnection(DB_URL, USER, PASS);    //Open a connection

            //Execute query
            String sql = "Select a.Date,t.Absence_type,t.color,a.Title,a.Submitted,a.Notes,a.Absence_Group,h.Hours " +
            "from Absences as a " +
            "join HOURS as h " +
            "on h.DATE = a.DATE " +
            "join absence_types as t " +
            "on h.absence_id = t.absence_id " +
            "where a.date = '" + date + "'"; 

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
                record.put("Absence_Group",rs.getString("Absence_Group"));
                result.add(record);                
            }

            rs.close();
            stmt.close();
            conn.close();
            return result;

            }catch(SQLException se){
               //Handle errors for JDBC
               ErrorHandler.JDBCError(se);
            }catch(Exception e){
               //Handle errors for Class.forName
               ErrorHandler.classForNameError(e);
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
                  ErrorHandler.closeConnectionError(se);
               }//end finally try
            }//end try */

             return null;   // return null if error    
        
    }        
   
    /* getAbsences - get the absences for the current year to ArrayList of JSONObjects
     * called before running buildCalendar() method
     * used for marking days with absences in CalendarBuilder and creating the tooltips with hours
     *                                                        */
    static public ArrayList<JSONObject> getAllAbsences (String year) {

        Connection conn = null;
        Statement stmt = null;

        try{    
            Class.forName("org.sqlite.JDBC");    //Register JDBC driver
            conn = DriverManager.getConnection(DB_URL, USER, PASS);    //Open a connection

            //Execute query
            String sql = "Select a.Date,t.Absence_type,t.color,a.Title,a.submitted,a.Notes,a.Absence_Group,h.Hours " +
            "from Absences as a " +
            "join HOURS as h " +    
            "on h.DATE = a.DATE " + 
            "join absence_types as t " +
            "on h.absence_id = t.absence_id " +
            "where a.date BETWEEN '" + year + "-01-01' AND '" + year + "-12-31' " + 
            "order by a.date"; 

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
                record.put("Absence_Group",rs.getString("Absence_Group"));
                result.add(record);
            }

            rs.close();
            stmt.close();
            conn.close();
            return result;

            }catch(SQLException se){
               //Handle errors for JDBC
               ErrorHandler.JDBCError(se);
            }catch(Exception e){
               //Handle errors for Class.forName
               ErrorHandler.classForNameError(e);
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
                  ErrorHandler.closeConnectionError(se);
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
            String sql = "Select Absence_ID, Absence_type,color,absence_id, " +
                  "accrual_rate, max_accrual from Absence_Types " +
                  "order by Absence_type ASC";

            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                JSONObject record = new JSONObject();
                record.put("Absence_ID",rs.getInt("Absence_ID"));
                record.put("Absence_Type",rs.getString("Absence_Type"));
                record.put("Color",rs.getString("Color"));
                record.put("Accrual_Rate",rs.getDouble("Accrual_Rate"));
                record.put("Max_Accrual",rs.getDouble("Max_Accrual"));
                result.add(record);
            }
            
            rs.close();
            stmt.close();
            conn.close();
            return result;

            }catch(SQLException se){
               //Handle errors for JDBC
               ErrorHandler.JDBCError(se);
            }catch(Exception e){
               //Handle errors for Class.forName
               ErrorHandler.classForNameError(e);
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
                  ErrorHandler.closeConnectionError(se);
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
            String sql = "Select t.Absence_ID, Date, Cal_Date, Warning_Name, t.Absence_Type,t.Color from Warnings as w " +
            "join absence_types as t " +
            "on t.absence_id = w.absence_id " + "UNION " +
            "Select  Absence_ID, Date, cal_date, Warning_Name, 'None', 'None' from Warnings " +
            "where absence_ID = 0 " +       
            "Order by absence_type ASC"; 

            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            ArrayList<JSONObject> result = new ArrayList<>();

            while (rs.next()) {
                JSONObject record = new JSONObject();
                record.put((String)"Absence_Type",rs.getString("Absence_Type"));
                record.put("Absence_ID",rs.getInt("Absence_ID"));
                record.put("Warning_Name",rs.getString("Warning_Name"));
                record.put((String)"Color",rs.getString("Color"));
                record.put("Date",rs.getString("Date"));
                record.put("Cal_Date",rs.getString("Cal_Date"));
                result.add(record);
            }

            return result;

            }catch(SQLException se){
               //Handle errors for JDBC
               ErrorHandler.JDBCError(se);
            }catch(Exception e){
               //Handle errors for Class.forName
               ErrorHandler.classForNameError(e);
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
                  ErrorHandler.closeConnectionError(se);
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
             String sql = query;        // set from queries loop
             
             stmt = conn.createStatement();
             stmt.executeUpdate(sql);//ResultSet rs = 

             success = true;
             
            stmt.close();
            conn.close();
            return success;

            }catch(SQLException se){
                //Handle errors for JDBC
                ErrorHandler.JDBCError(se);
            }catch(Exception e){
               //Handle errors for Class.forName
               ErrorHandler.classForNameError(e);
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
                   ErrorHandler.closeConnectionError(se);
                }//end finally try
             }//end try

        return success;
    }    
    
} // end class