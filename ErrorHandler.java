/*
 * Christopher Burgess
 */
package myabsences;

import java.sql.SQLException;
import javafx.scene.control.Alert;

/**
 *
 * @author Christopher
 */
public class ErrorHandler {
    
    public static void JDBCError(SQLException sqlEx) {
    
        String error = sqlEx.toString();
        Alert alert = new Alert(Alert.AlertType.WARNING);
        
        if (error.contains("SQLITE_CONSTRAINT_UNIQUE")) {
            alert.setTitle("SQLITE_CONSTRAINT_UNIQUE");
            alert.setHeaderText("Warning: You are adding to a day with hours planned!");
            alert.setContentText("There was already an entry in the database\n"
                    + "for the date being added");
            
        } else {
            alert.setTitle(error);
            alert.setHeaderText("Warning: Database Error");
            alert.setContentText(error);
        }
        
        alert.showAndWait();
    }
     
    public static void classForNameError(Exception cfnError) {
        
        String error = cfnError.toString();
        Alert alert = new Alert(Alert.AlertType.WARNING);
        
        if (error.contains("LinkageError")) {
            alert.setTitle("LinkageError");
            alert.setHeaderText("Warning: DB Linkage failed");
            alert.setContentText("Unable to link to Database");
        } else if (error.contains("ExceptionInInitializerError")) {
            alert.setTitle("ExceptionInInitializerError");
            alert.setHeaderText("Warning: DB could not be initialized");
            alert.setContentText("Unable to initialize the Database");             
        } else if (error.contains("ClassNotFoundException")) {
            alert.setTitle("ClassNotFoundException");
            alert.setHeaderText("Warning: DB class not found");
            alert.setContentText("Unable to locate the class loader");             
        } 
        
         alert.showAndWait();

    }
    
    public static void closeConnectionError(Exception closeError) {
        
        String error = closeError.toString();
        Alert alert = new Alert(Alert.AlertType.WARNING);
        
        alert.setTitle(error);
        alert.setHeaderText("Warning: Error closing Database Connection");
        alert.setContentText(error);
        alert.showAndWait();
        
    }
    
    
    
    
    
}
