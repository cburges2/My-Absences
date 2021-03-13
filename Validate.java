/*
 * Christopher Burgess
 */
package myabsences;

import javafx.scene.control.Alert;

/**
 *
 * @author Christopher
 */
public class Validate {
    
    public static boolean availableHours(double hours, double available) {

        System.out.println("Hours is " + hours + " available is " + available);
        if (hours > available) { 
            System.out.println("Hours not available");
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("Not enough Available Hours!");
            alert.setContentText("You are short by " + (hours - available) +" hours\n"
                    + "Please try again.");
            alert.showAndWait(); 
            return false;           
        } else {
            System.out.println("Hours Available");
            return true;
        }
    }     
    
    /* 
    
    * Returns false if empty  */
    public static boolean notEmpty(String checkString) {
        
        System.out.println("In notEmpty");

            if (checkString.length() == 0) {
                System.out.println("Empty");
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning");
                alert.setHeaderText("No Absence Type was Chosen!");
                alert.setContentText("You need to choose a type from the dropdown\n"
                        + "Please try again.");
                alert.showAndWait(); 
                return false;
            } else {
                System.out.println("Not Empty");
                return true;
            }

    }
    
} // end class Validate
