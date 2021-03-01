/*
 * Christopher Burgess
 */
package myabsences;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import static myabsences.MyAbsences.cboYear;
import static myabsences.MyAbsences.btnsTop;
import org.json.simple.JSONObject;

/**
 *
 * @author Christopher
 */
public class TopPaneBuilder {
    
    HBox topLineHBox = new HBox();              // Top Title pane with year selector
    String year = "";   // calendar year
    ArrayList<String> years = new ArrayList<>();     // years to put in ComboBox
    
    public TopPaneBuilder (String year){

        this.year = year;     
        years = Database.getYears();
    }
    
    SimpleDateFormat FORMAT_YEAR = new SimpleDateFormat("yyyy");       
    String thisYear = FORMAT_YEAR.format(Calendar.getInstance().getTime());
    
    /* public buildTopPane
     *
     *
     *
    */
    public HBox buildTopPane () {
        
        HBox buttonHBox = new HBox();           // Button Pane - put in Left VBox
        VBox middleVBox = new VBox();           // Middle Box for Title and Combobox
        VBox leftVBox = new VBox();             // Tob Left box for buttonHBox and warningBox
        VBox warningVBox = new VBox();          // Warning box to be put in leftVBox

        /* Set middleVBox attributes */
        middleVBox.setMaxHeight(30);     
        middleVBox.setSpacing(10);
        middleVBox.setPadding(new Insets(5, 5, 0, 5));   // top, right, bottom, left
        middleVBox.getStyleClass().add("bpane");
        middleVBox.setAlignment(Pos.CENTER);  // center the children in the pane
        
        /* Set leftVBox attributes */
        leftVBox.setMaxHeight(30);     
        leftVBox.setSpacing(10);
        leftVBox.setPadding(new Insets(5, 5, 0, 5));   // top, right, bottom, left
        leftVBox.getStyleClass().add("bpane");
        leftVBox.setAlignment(Pos.TOP_LEFT);  // center the children in the pane
        
        /* Set warningVBox attributes */
        warningVBox.setMaxHeight(30);     
        warningVBox.setSpacing(10);
        warningVBox.setPadding(new Insets(5, 5, 0, 5));   // top, right, bottom, left
        warningVBox.getStyleClass().add("bpane");
        warningVBox.setAlignment(Pos.CENTER_LEFT);  // center the children in the pane  
        
        // Set imageView attributes
        ImageView imageView = new ImageView("monica-sauro-small.jpg");
        imageView.setFitHeight(75);
        imageView.setFitWidth(75);
        imageView.setEffect(new DropShadow(10, Color.WHITE));  // blue dropshadow image border
        
        /* Set topLineHBox Atributes */
        topLineHBox.setMaxHeight(30); 
        topLineHBox.setSpacing(130);
        topLineHBox.setPadding(new Insets(5, 5, 5, 5));
        topLineHBox.getStyleClass().add("bpane");
        HBox.setMargin(imageView, new Insets(10,50,0,0));
        HBox.setMargin(middleVBox, new Insets(10,130,0,0));
        topLineHBox.setAlignment(Pos.TOP_CENTER);  // center the children in the pane         
        
        /* Set buttonHBox attributes */ 
        buttonHBox.setAlignment(Pos.TOP_LEFT);
        buttonHBox.setPadding(new Insets(0, 5, 0, 5));
        buttonHBox.setSpacing(2);
        buttonHBox.getStyleClass().add("bpane");
       
        // Create top left buttons
        String[] navButtons = new String[]{"Setup","Enter Balances","List View","Exit"};
        //Button[] btnsTop = new Button[navButtons.length];
        for (int numBtn=0; numBtn < navButtons.length; numBtn++) {
            btnsTop[numBtn] = new Button(navButtons[numBtn]); 
            btnsTop[numBtn].getStyleClass().add(navButtons[numBtn]);
            btnsTop[numBtn].setPadding(new Insets(2, 6, 3, 6));   // top, right, bottom, left
            btnsTop[numBtn].setMinWidth(25);  // force buttons to be same size
            buttonHBox.getChildren().add(btnsTop[numBtn]); // add to the buttonHBox
        }
        
        // Put the warnings in the warningHBox if current year
        if (year.equals(thisYear)) {
            ArrayList<JSONObject> warnings = Database.getWarnings();
            for (int i = 0; i < warnings.size(); i++) {
                if (((String)warnings.get(i).get("Warning_Name")).equals("MAX_ACCRUAL")) {
                        if ( !((String)warnings.get(i).get("Date")).equals(" ") ) {
                        String absenceType = (String)warnings.get(i).get("Absence_Type");
                        //String color = ((String)warnings.get(i).get("Color")).toLowerCase();
                        String calDate = (String)warnings.get(i).get("Cal_Date");
                        Text warning = new Text(absenceType + " Max Accrual on " + calDate);
                        warning.getStyleClass().add("txtwarning");        // flag accrual max within two weeks
                        warningVBox.getChildren().add(warning);
                    }
                }
            }
        }
        
        /* Set Title attributes */
        Text title = new Text();      // Main Title Text                                                        
        title.setX(20.0f);
        title.setY(65.0f);
        title.setText("My Absences");                                                                                 
        title.getStyleClass().add("title");    
        
        // Set the Year combo box attributes
        if (cboYear.getItems().isEmpty()) {          // Add years from database only if empty (to not add years again on year refresh)  
            for(int numYear=0; numYear < years.size(); numYear++) {
                cboYear.getItems().add(years.get(numYear));                                               
            }
        }  
        cboYear.setValue(year);   // Set year combobox to current selected year

        // Add components to leftVBox
        leftVBox.getChildren().addAll(buttonHBox,warningVBox);       

        // Add components to middleVBox
        middleVBox.getChildren().addAll(title, cboYear);     // add components 

        // Add components to toplineHBox
        topLineHBox.getChildren().addAll(leftVBox,middleVBox, imageView);  //build top pane
        
        return topLineHBox;
    
    }
}

