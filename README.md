# My-Absences

My Absences is a desktop program that lets an employee of a company track and plan their absence hours. 
It is coded in JavaFX.

![image](https://user-images.githubusercontent.com/66129215/114072809-a4963a80-9870-11eb-8622-9f480e639076.png)

This application uses an SQLite database file, created for the user at start-up. The database contains 6 tables.

![image](https://user-images.githubusercontent.com/66129215/114212432-d4f0de00-992f-11eb-8f89-4bcb72495a16.png)

ABSENCE_TYPES: Stores the user-defined absence types.  They are given an absence ID, a color, and an Accrual Rate value
to determine if balances are Accrued, Fixed, or Added-In. If accrued, the table stores a Max Accrual value. 

STARTING_BALANCES: Stores the starting balances for each type ID, for each year as the application is used.
 
ABSENCES: Stores the Date as primary key for each day that has absence hours planned. It stores the day Title, Submitted status, 
Notes, and the name of the Absence group the day is part of if it is part of a group. 

HOURS: Stores the hours planned on each day by Absence ID and Date. Date is a foreign key to Absences, Absence ID is the key to Absence_Types. 
This table also stores the Absence group name if the day that the hours are planned on is part of an absence group. 

SETTINGS: Stores settings for the user's work schedule, Hours in Day, Days in Week, and if they work weekends.  Also stores the 
look ahead period for warning about the day that accrued types will reach Max accrual. 

WARNINGS: Stores data for messages that will appear in the top-left window of the main screen for Max Accrual dates, Settings 
needing to be added, or balances that need attention. 

STRUCTURE:

MyAbsences main screen is composed of a Top, Middle and Bottom Pane.
 
TOP: 
The Left pane is a Vertical box containing the buttons in a Horizontal box, and a message window in a Vertical box. 
The Middle Pane is a Vertical box containing the title and a year combobox. 
The Right Pane is an image view containing an image. 

MIDDLE: 
Is a year calendar Gridpane composed of month Gridpanes. Each month Gridpane is composed of date buttons. 

BOTTOM: 
Is a Gridpane of summary data about the hours planned for each absence types for the year. 

CLASSES:

SetupForm is a secondary window class launched from the Setup button for the user to enter and define their absence types. 
BalancesForm is a secondary window class launched from the Enter Balances button for the user to enter balances for their absence types for the year.
ListView is a secondary window class launched from the List View button for the user to see all year absences and data in a single list view. 
DayEntry is a secondary window class launched from clicking a calendar day button. User enters data for the absence day, which contains the 
    hours for each absence type that is planned, as well as submitted status, a title, and notes. Groups of absence days can be created in this form as well.
Settings is a secondary window class launched from clicking the Settings button. This allows users to edit the settings used for working hours and Max Accrual warnings.  

All forms are live-editable to change the data or add data after the initial submit. 

Database contains static methods to query and update the database, as well as a method to create the tables. Most data transfers from the query methods
   return an ArrayList of JSON Objects. 
Validate contains static methods to validate inputs and get validation from the user for decisions. 
ErrorHandler contains static methods to handle various caught error types. 
Warnings contains static methods to add, update or remove warnings (messages) data from the Warnings table. These are converted to string warnings in the 
    getMessages method that are displayed in the top-left message box of the Top Pane. 
JsonMatch contains static methods to find a match for data in the ArrayLists of JSON Objects, or to confirm if data is in the JSON Objects.  

TopPaneBuilder, CalanderBuilder, and SummaryReportBuilder classes build the panes and return them to the main class to build the primary Stage Borderpane. 

Changing the year combobox above the calendar changes the calendar year. Users have access to past years, the current year, and next year calendars. 
Changing the Calculation type combobox in the summary report window changes the queries for the report to include or not include hours that are not submitted yet. 

The StyleSheet contains the styles for display of Titles, labels, buttons and pane attributes. 