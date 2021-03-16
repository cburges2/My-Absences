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
            alert.setTitle("Planned Day already contains an Absence!");
            alert.setHeaderText("Warning: Not all your absence days were saved");
            alert.setContentText("You were saving a repeating absence but there was " +
            "already an absence planned in that time period");
            
        } else {
            alert.setTitle("JDBC Error");
            alert.setHeaderText("Warning: Database Error");
            alert.setContentText(error);
        }
        
        alert.showAndWait();
}
    
    
    
    
    
    
    
}
