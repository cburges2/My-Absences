/*
 * Christopher Burgess
* SDEV 435
 */
package myabsences;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author Christopher
 */
//Begin Subclass ListReport
public class BalancesForm extends Application {
  
    /* Instantiate new stage object */
    static Stage reportStage = new Stage();

    /* Declare fields and buttons to be displayed */

    @Override
    public void start(Stage primaryStage) throws Exception {

        /* Main pane */
        BorderPane bPane = new BorderPane();
        GridPane gPane = new GridPane();
        
        gPane.setAlignment(Pos.TOP_LEFT);
        gPane.setPadding(new Insets(5, 5, 5, 5));
        bPane.setPadding(new Insets(5, 5, 5, 5));
        gPane.setHgap(5);
        gPane.setVgap(5);

        /* Define label nodes */
        

        /* Secondary labels that will retrieve data */
        
 
        /* Add label nodes to gpane  */

     
        /* set panes in stage and show stage */
        //gPane.setStyle("-fx-border-color: red");
        bPane.setBottom(getPaneBottom());
        StackPane secondaryLayout = new StackPane();
        Scene startBalancesScene = new Scene(secondaryLayout, 300, 450);
        secondaryLayout.getChildren().addAll(gPane, bPane);
        reportStage.setTitle("Enter Startig Balances");
        reportStage.setScene(startBalancesScene);
        //Set position of second window, related to primary window.
        reportStage.show();
    }

    /**
     * Method to combine boxes
     *
     * @return vBox
     * @throws Exception
     */
    private Pane getPaneBottom() throws Exception {
        VBox vBox = new VBox();
        vBox.getChildren().addAll(getHBoxText(), getHBoxButtons());
        return vBox;
    }

    /**
     * Method to place text labels
     *
     * @return hBoxB
     */
    private HBox getHBoxText() {
        HBox hBoxB = new HBox();
        hBoxB.setAlignment(Pos.CENTER);
        return hBoxB;
    }

    /**
     * Method to add buttons
     *
     * @return hBoxB
     */
    private HBox getHBoxButtons() {
        HBox hBoxB = new HBox();
        Button btnExit = new Button("Exit");
        btnExit.setOnAction(new exitHandler());
        hBoxB.setAlignment(Pos.CENTER);
        btnExit.setMaxWidth(50);
        HBox.setMargin(btnExit, new Insets(5, 5, 5, 5));
        hBoxB.getChildren().addAll(btnExit);
        return hBoxB;
    }

    /**
     * Method to close stage and return to main without accepting data
     */
    private static class exitHandler implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            reportStage.close();
        }
    }

   

} //End Subclass ListReport