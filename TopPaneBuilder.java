/*
 * Christopher Burgess
 */
package myabsences;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

/**
 * TopPaneBuilder Class
 * @author Christopher
 * This class builds a HBox containing all the top pane features
 */
public class TopPaneBuilder {
    
    HBox topLineHBox = new HBox();              // Top Title pane with year selector
    String year = "";   // calendar year
    ArrayList<String> years = new ArrayList<>();    // years to put in ComboBox
    ComboBox<String> cboYear = new ComboBox<>();    // year combo
    Button[] btnsTop = new Button[4];               // top pane navigattion buttons
    Tooltip[] btnTip = new Tooltip[4];              // help tooltips for buttons
    
    /* public TopPaneBuilder - Constructor
    * 
    * year - The year currently in the main calendar
    *
    * This sets the year and gets the years in the Balances table from db */
    public TopPaneBuilder (String year){

        this.year = year;     
        years = Database.getYears();
    }
    
    SimpleDateFormat FORMAT_YEAR = new SimpleDateFormat("yyyy");       
    String thisYear = FORMAT_YEAR.format(Calendar.getInstance().getTime());
    
    /* public buildTopPane
     *
     * Returns an HBox 
     *
     * HBox returned contains the buttons, warning box, Title, Year combobox, and an Image */
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
        warningVBox.setSpacing(0);
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
       
        // Create top left buttons and help tooltips
        String[] navButtons = new String[]{"Setup","Enter Balances","List View","Exit"};
        for (int numBtn=0; numBtn < navButtons.length; numBtn++) {
            if (numBtn == 0)  {btnTip[numBtn] = new Tooltip("Setup\nDefine your abasence types");}
            if (numBtn == 1)  {btnTip[numBtn] = new Tooltip("Enter Balances\nEnter this year's starting balances for your absence types");}
            if (numBtn == 2)  {btnTip[numBtn] = new Tooltip("List View\nView all absences for the year in a list");}
            if (numBtn == 3)  {btnTip[numBtn] = new Tooltip("Exit My Absences");}
            btnTip[numBtn].getStyleClass().add("ttgray");
            btnsTop[numBtn] = new Button(navButtons[numBtn]); 
            btnsTop[numBtn].setTooltip(btnTip[numBtn]);
            btnsTop[numBtn].getStyleClass().add(navButtons[numBtn]);
            btnsTop[numBtn].setPadding(new Insets(2, 6, 3, 6));   // top, right, bottom, left
            btnsTop[numBtn].setMinWidth(25);  // force buttons to be same size
            buttonHBox.getChildren().add(btnsTop[numBtn]); // add to the buttonHBox
        }
        
        // combobox help tooltip
        Tooltip cboYearTip = new Tooltip("Choose Year for Calander");
        cboYearTip.getStyleClass().add("ttgray");
        cboYear.setTooltip(cboYearTip);
        
        // Put the warnings in the warningHBox if current year
        if (year.equals(thisYear)) {
            ArrayList<String> warnings = Warnings.getWarnings();
            for (int i = 0; i < warnings.size(); i++) {
                String warningStr = warnings.get(i);
                Text warning = new Text(warningStr);
                warning.getStyleClass().add("txtwarning");   
                warningVBox.getChildren().add(warning);
                if (warningStr.contains("Setup")) {
                    btnsTop[0].getStyleClass().add("btnwarntop");
                    Platform.runLater(() -> {
                        btnsTop[0].requestFocus();
                    });    
                }
                if (warningStr.contains("Balances") || warningStr.contains("zero")) {
                    btnsTop[1].getStyleClass().add("btnwarntop");
                    Platform.runLater(() -> {
                        btnsTop[1].requestFocus();// Set focus on Balances button
                    });
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
    
    public ComboBox<String> getCboYear() {
        
        return cboYear;
        
    }
    
    public Button[] getBtnsTop() {
        
        return btnsTop;
        
    }
}

