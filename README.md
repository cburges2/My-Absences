# My-Absences

My Absences is a desktop program that lets an employee of a company track and plan their absence hours. 
It is coded in JavaFX.

![image](https://user-images.githubusercontent.com/66129215/114072809-a4963a80-9870-11eb-8622-9f480e639076.png)

This application uses an SQLite database file, created for the user at start-up. The database contains 6 tables.

![image](https://user-images.githubusercontent.com/66129215/114210160-6f9bed80-992d-11eb-9f3b-78c7a3604b3d.png)

ABSENCE_TYPES: Stores the user-defined absence types.  They are given an absence ID, a color, and an Accrual Rate value
to determine if balances are Accrued, Fixed, or Added-In. If accrued, the table stores a Max Accrual value. 

STARTING_BALANCES: Stores the starting balances for each type ID, for each year as the application is used.
 
ABSENCES: Stores the Date as primary key for each day that has absence hours planned. Stores the day Title, Submitted status, 
Notes, and the name of the Absence group the day is part of if it is part of a group. 

HOURS: Stores the hours planned on each day by Absence ID. Date is a foreign key to Absences, Absence ID is the key to Absence_Types. 
Also stores the Absence group name if the day hours are planned on is part of an absence group. 

SETTINGS: Stores settings for the user's work schedule, Hours in Day, Days in Week, and if they work weekends.  Also stores the 
look ahead period for warning about the day that accrued types will reach Max accrual. 

WARNINGS: Stores data for messages that will appear in the top-left window of the main screen for Max Accrual dates, Settings 
needing to be added, or balances that need attention. 

