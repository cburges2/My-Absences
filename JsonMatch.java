/*
 * Christopher Burgess
 */
package myabsences;

import java.util.ArrayList;
import org.json.simple.JSONObject;

/**
 *
 * @author Christopher
 */
public class JsonMatch {
    
    /* private getJsonIndex: 
    * 
    * json - The ArrayList of JSON objects
    * columnName - The column name data is under
    * item - The record we are looking for in the column
    * 
    * Example:
    * getJsonIndex(absences,"Absence_ID",1);
    * * => -1 (not found)
    * * =>  3 (found at index 3)
    *
    * Returns the index in the ArrayList where the int record is found in the column   */
    static public int getJsonIndex (ArrayList<JSONObject> json, String columnName, int item) {
        
        int pos = -1;  
        for (int i=0; i < json.size(); i++) {
            if (json.get(i).get(columnName).equals(item)) {
                pos = i;
            }
        }           
        return pos;
    } // End getJsonIndex      
    
    /* private getJsonIndex: 
    * 
    * json - The ArrayList of JSON objects
    * columnName - The column name data is under
    * item - The record we are looking for in the column
    * 
    * Example:
    * getJsonIndex(absences,"Absence_Type","Vacation");
    * * => -1 (not found)
    * * =>  3 (found at index 3)
    *
    * Returns the index in the ArrayList where the string record is found in the column   */
    static public int getJsonIndex (ArrayList<JSONObject> json, String columnName, String item) {
        
        int pos = -1;  
        for (int i=0; i < json.size(); i++) {
            if (json.get(i).get(columnName).equals(item)) {
                pos = i;
            }
        }           
        return pos;
    } // End getJsonIndex  
  
    /* private getJsonIndex: 
    * 
    * json - The ArrayList of JSON objects
    * columnName - The column name data is under
    * item - The record we are looking for in the column (double)
    * 
    * Example:
    * getJsonIndex(futureHours,"Future_Hours",30.083333492279053);
    * * => -1 (not found)
    * * =>  3 (found at index 3)
    *
    * Returns the index in the ArrayList where the double record is found in the column   */
    static public int getJsonIndex (ArrayList<JSONObject> json, String columnName, double item) {

        int pos = -1;  
        for (int i=0; i < json.size(); i++) {
            if ((json.get(i).get(columnName)).equals(item)) {
                pos = i;
            }
        }           
        return pos;
    } // End getJsonIndex    

    /* private getJsonString: 
    * 
    * json - The ArrayList of JSON objects
    * columnName - The column name data is under
    * item - The record we are looking for in the column
    * matchCol - The record we want from the JSONObject (String)
    * 
    * Example:
    * getJsonIndex(absences,"Date","2021-01-01","Absence_Type");
    * * => "" (Empty String if not found)
    * * =>  Vacation (value found)
    *
    * Returns the String value from the column in the JSONObject in the ArrayList where
    * another item is found in the given column of the same JSONObject  */
    static public String getJsonString (ArrayList<JSONObject> json, String columnName, String item, String matchCol) {
        
        int pos = -1;  
        for (int i=0; i < json.size(); i++) {
            if (json.get(i).get(columnName).equals(item)) {
                pos = i;
            }
        }          
        
        String result;
        if (pos != -1) {
            result = (String)json.get(pos).get(matchCol);
        } else {result = "";}
        
        return result;
        
    } // End getJsonString   
    
    /* private getJsonDouble: 
    * 
    * json - The ArrayList of JSON objects
    * columnName - The column name data is under
    * item - The record we are looking for in the column
    * matchCol - the column we want the record from in the JSONObject
    * 
    * Example:
    * getJsonDouble(absences,"Absence_Type","Vacation");
    * * => 0 (not found)
    * * =>  3.5 (value found)
    *
    * Returns the double value from the column in the JSONObject in the ArrayList where
    * another item is found in the given column of the same JSONObject  */
    static public double getJsonDouble (ArrayList<JSONObject> json, String columnName, String item, String matchCol) {
        
        int pos = -1;  
        for (int i=0; i < json.size(); i++) {
            if (json.get(i).get(columnName).equals(item)) {
                pos = i;
            }
        }       
        
        double result;
        if (pos != -1) {
            result = (double)json.get(pos).get(matchCol);
        } else {result = 0;}
        
        return result;
        
    } // End getJsonDouble   
  
    /* private getJsonInt: 
    * 
    * json - The ArrayList of JSON objects
    * columnName - The column name data is under
    * item - The record we are looking for in the column
    * matchCol - the column we want the record from in the JSONObject
    * 
    * Example:
    * getJsonInt(twoWeekHours,"Absence_Type",absenceType,"Future_Hours");
    * * => 0 (not found)
    * * =>  24 (value found)
    *
    * Returns the Int value from the column in the JSONObject in the ArrayList where
    * another item is found in the given column of the same JSONObject  */
    static public int getJsonInt (ArrayList<JSONObject> json, String columnName, String item, String matchCol) {
        
        int pos = -1;  
        for (int i=0; i < json.size(); i++) {
            if (json.get(i).get(columnName).equals(item)) {
                pos = i;
            }
        }       
        
        int result;
        if (pos != -1) {
            result = (Integer)json.get(pos).get(matchCol);
        } else {result = 0;}
        
        return result;
        
    } // End getJsonInt 
    
}
