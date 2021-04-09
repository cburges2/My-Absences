/*
 * Christopher Burgess
 */
package myabsences;

import java.util.ArrayList;
import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import org.json.simple.JSONObject;

/**
 *
 * @author Christopher Burgess
 * This class contains static methods to validate data inputs
 * Gives information and gets confirmation from the user through alert dialogues.
 */
public class Validate {
    
    /* public textSize
    *
    * fieldName - the field being validated
    * text - the text length being checked
    * size - the max length allowed for the text
    *
    * This method checks that a String is within a max length    */
    public static boolean textSize(String fieldName, String text, int size) {
        
        if (text.length() > size) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("Too much text in the textbox!");
            alert.setContentText("You added more text than is allowed for " + fieldName + "\n"
                    + "Please try again and limit text to " + size + " characters.");
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.setAlwaysOnTop(true);            
            alert.showAndWait();             
            return false;
        } else {return true;} 
    }
   
    /* public availableHours
    *
    * fieldName - the field being validated
    * hours - the hours being scehduled
    * available - the hours available 
    *
    * ==> Returns true if hours are available 
    *
    * Validates that the the type has the available balance to cover the hours */  
    public static boolean availableHours(String fieldName, double hours, double available) {

        if (hours > available) { 
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("Not enough Available Hours!");
            alert.setContentText("You are short by " + (hours - available) +" hours\n"
                    + "For " + fieldName + "\n"
                    + "Please try again.");
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.setAlwaysOnTop(true);            
            alert.showAndWait(); 
            return false;           
        } else {
            return true;
        }
    }  // end availableHours   

    /* public hoursInDay
    *
    * date - the day being validated
    * hours - total hours planned for day being scehduled
    * dayHours - the hours in a day
    *
    * ==> Returns true if hours are available 
    *
    * Validates that the the type has the available balance to cover the hours */  
    public static boolean hoursInDay(String date, double hours, double dayHours) {

        if (hours > dayHours) { 
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("More than " + dayHours + " hours scheduled!");
            alert.setContentText("You are over by " + (hours - dayHours) +" hours\n"
                    + "For " + date + "\n"
                    + "Please try again.");
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.setAlwaysOnTop(true);            
            alert.showAndWait(); 
            return false;           
        } else {
            return true;
        }
    }  // end availableHours  
    
    /* public notEmpty
    *
    * fieldName - the name of the field being checked
    * checkString - the String in the field to be checked. 
    *
    * ==> Returns true if field is not empty 
    *
    * Validates that the string is not empty */  
    public static boolean notEmpty(String fieldName, String checkString) {

            if (checkString.length() == 0) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning");
                alert.setHeaderText("No " + fieldName + " was Entered!");
                alert.setContentText("You need to select or enter a value\n"
                        + "Please try again.");
                Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                stage.setAlwaysOnTop(true);
                alert.showAndWait(); 
                return false;
            } else {return true;}
            
    } // end notEmpty
    
    /* public isPosDecimal
    *
    * fieldName - the name of the field being checked
    * decimal - the String in the field to be checked. 
    * max - the max allowed decimal number
    *
    * ==> Returns true if a decimal number
    *
    * Validates that the string represents a positive decimal number within Max  */    
    public static boolean isPosDecimal (String fieldName, String decimal, double max) {
        
        boolean pass = true;
        
        // check if string is a positive decimal number format
        if (!decimal.matches("^(0|[1-9]\\d*)?(\\.\\d+)?(?<=\\d)$")) {

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText(fieldName + " is not a number!");
            alert.setContentText("Enter a number for " + fieldName + "\n"
                    + "Please try again.");
                            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.setAlwaysOnTop(true);
            stage.showAndWait();
            //alert.showAndWait(); 
            pass = false;
        } else {
            if (Double.valueOf(decimal) > max) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning");
                alert.setHeaderText(fieldName + " is too big!");
                alert.setContentText("Enter a number for " + fieldName + " less than " + max + "\n"
                        + "Please try again.");
                Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                stage.setAlwaysOnTop(true);
                stage.showAndWait();
                //alert.showAndWait(); 
                pass = false;            
            }
        }
        
        return pass;
        
        
    } // end isPosDecimal
    
    /* public confirmDeleteTypeHours
    *
    * absence_type - the type that user selected to delete the Type Hours for
    * 
    * ==> Returns true if user confirms delete from all days in the group
    * ==> Returns false if user chooses to delete only the type hours on the day
    * 
    * This method confirms that the user wants to delete the Type Hours from 
    * all the days in the group, else only the single day hour will be deleted */    
    public static boolean confirmDeleteTypeHours(String absence_type) {
        
        boolean delete = false;
        
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, "Delete Absence Type Hours");
        a.setHeaderText(absence_type + " hours are part of an Absence Group!\n");
        a.setResizable(true);
        a.setContentText("Select to Delete from all days in group or only from the day:");
        ((Button) a.getDialogPane().lookupButton(ButtonType.OK)).setText("Delete from Group");
        ((Button) a.getDialogPane().lookupButton(ButtonType.CANCEL)).setText("Delete from Day");
        Stage stage = (Stage) a.getDialogPane().getScene().getWindow();
        stage.setAlwaysOnTop(true);     
        Optional<ButtonType> result = a.showAndWait();
        if(!result.isPresent()) {
            delete = false;
        } else if(result.get() == ButtonType.OK) {
            delete = true;
        } else if (result.get() == ButtonType.CANCEL) {
            delete = false;
        }
        
        return delete;
        
    } // end method confirmDeleteTypeHours    

    /* public confirmAddToGroup
    *
    * absence_type - the type that user selected to delete the Type Hours for
    * 
    * ==> Returns true if user confirms make the day part of the group
    * ==> Returns false if user wants to not make the day part of the group
    * 
    * This method confirms that the user wants to incorporate a single day into a group  
    * when adding a group and there is already a day planned in the group time period.  */    
    public static boolean confirmAddToGroup(String date) {
        
        boolean delete = false;
        
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, "Overwriting a Day");
        a.setHeaderText(date + " already has planned hours!\n");
        a.setResizable(true);
        a.setContentText("Select to either overwrite the day, or to skip it");
        ((Button) a.getDialogPane().lookupButton(ButtonType.OK)).setText("Overwrite");
        ((Button) a.getDialogPane().lookupButton(ButtonType.CANCEL)).setText("Skip Day");
        Stage stage = (Stage) a.getDialogPane().getScene().getWindow();
        stage.setAlwaysOnTop(true);     
        Optional<ButtonType> result = a.showAndWait();
        if(!result.isPresent()) {
            delete = false;
        } else if(result.get() == ButtonType.OK) {
            delete = true;
        } else if (result.get() == ButtonType.CANCEL) {
            delete = false;
        }
        
        return delete;
        
    } // end method confirmDeleteTypeHours
    
    /* public confirmDeleteType
    *
    * absence_type - the type that user selected to delete
    * 
    * ==> Returns true if user confirms delete
    * 
    * This method confirms that the user wants to delete an absence type in setup */    
    public static boolean confirmDeleteType(String absence_type) {
        
        boolean delete = false;
        
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, "Delete Absence Type");
        a.setHeaderText("Do you really want to Delete " + absence_type + "?\n"
                + "This will delete all absences for " + absence_type + " on the Calendar,\n"
                + "as well as the " + absence_type + " starting balances!");
        a.setResizable(true);
        a.setContentText("Press OK to confirm:");
        Stage stage = (Stage) a.getDialogPane().getScene().getWindow();
        stage.setAlwaysOnTop(true);           
        Optional<ButtonType> result = a.showAndWait();
        if(!result.isPresent()) {
            delete = false;
        } else if(result.get() == ButtonType.OK) {
            delete = true;
        } else if (result.get() == ButtonType.CANCEL) {
            delete = false;
        }
        
        return delete;
        
    } // end method confirmDeleteType
    
    /* checkData
     *
     * todayStr - Today's date in DB format
     *
     * Checks if Setup has been run and if each absence type has a starting balance for this year
     * also checks if a balance type changed and it now needs a balance entered.  */
    public static void checkData(String todayStr, String year) {
        
        String todayYear = todayStr.substring(0,4);
        String dateStr = todayStr;
        if (!year.equals(todayYear)) {
            dateStr = year + "-01-01";   // create a valid date for Warnings in off years
        } 

        int numTypes = Database.getNumRows("Absence_Types");
        System.out.println("There are " + numTypes + " Types");
        
        // Set a Setup warning
        if (numTypes == 0) {
            Warnings.addWarning(0, dateStr, "RUN_SETUP");
        } else {Warnings.removeWarning(0,"RUN_SETUP");}
    
        int startCount = Database.getStartBalanceCount(year);
        System.out.println("There are " + startCount + " Balances");
        
        // Set a Enter Balances warning
        if (numTypes > startCount) {
            Warnings.addWarning(0, dateStr, "ENTER_BALANCES");
        } else {Warnings.removeWarning(0,"ENTER_BALANCES");} 
        
        // Check for zero balances on fixed or accrued (due to changing accrual type)
        if (numTypes == startCount) { 
            ArrayList<JSONObject> startBalances = Database.getStartBalances(year);
            for (int i =0; i < startBalances.size(); i++) {
                // check all non-add-In types for zero balances
                if ((double)(startBalances.get(i).get("Accrual_Rate")) >= 0) {
                    int absenceID = (int)startBalances.get(i).get("Absence_ID");
                    if ((double)startBalances.get(i).get("Starting_Balance") == 0) {
                        // set warning for zero balance
                        Warnings.addWarning(absenceID, dateStr, "ZERO_BALANCE");
                    } else {Warnings.removeWarning(absenceID,"ZERO_BALANCE");}
                } 
            }
        }
    }  
    
} // end class Validate
