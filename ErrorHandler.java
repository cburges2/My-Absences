/*
 * Christopher Burgess
 */
package myabsences;

import java.sql.SQLException;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

/**
 *
 * @author Christopher Burgess
 * This class handles errors from the try-catch statements used in all other classes 
 */
public class ErrorHandler {
    
    /* private exception
    *
    * This method handles any general exceptions and report them to the user */
    public static void exception(Exception ex, String action) {
    
        String error = ex.toString();
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(error);
        alert.setHeaderText("Warning: An Error Occurred while " + action);
        alert.setContentText(error);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.setAlwaysOnTop(true);           
        alert.showAndWait();
    }
    
    /* public JDBCError
    *
    * This method handles JDBC Erros from the database.  It handles a missing table as 
    * a trigger to create the database tables at start-up.  It handles a Unique Contraint specifically */
    public static void JDBCError(SQLException sqlEx) {
        
        // if missing tables (first start), create database tables
        String error = sqlEx.toString();
        if (error.contains("no such table")) {
            Database.createTables();
        } else {   // else handle other errors
    
            Alert alert = new Alert(Alert.AlertType.WARNING);

            if (error.contains("SQLITE_CONSTRAINT_UNIQUE")) {
                alert.setTitle("SQLITE_CONSTRAINT_UNIQUE");
                alert.setHeaderText("Warning: You are adding to a table with a unique contraint!");
                alert.setContentText("There was already an entry in the database\n"
                        + "for the data being added");
            } 
            else {
                alert.setTitle(error);
                alert.setHeaderText("Warning: Database Error");
                alert.setContentText(error);
            }
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.setAlwaysOnTop(true);           
            alert.showAndWait();
        }
    }
     
    /* public classForNameError
    *
    * This method handles class For Name Errors specifically for the Database methods    */
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
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.setAlwaysOnTop(true);           
        alert.showAndWait();
    }
    
    /* public closeConnectionError
    *
    * This class handles connection close errors for the Database    */
    public static void closeConnectionError(Exception closeError) {
        
        String error = closeError.toString();
        Alert alert = new Alert(Alert.AlertType.WARNING);
        
        alert.setTitle(error);
        alert.setHeaderText("Warning: Error closing Database Connection");
        alert.setContentText(error);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.setAlwaysOnTop(true);           
        alert.showAndWait();
    }
}
