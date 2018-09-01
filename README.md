# BANK APP DEMO

A Banking app made for Android using Android Studio. No real money is involved, it is a project to showcase my skills in Android and Java. The Application was developed using a MVC approach, with proper programming conventions and aappropriate data encapsulation.

# ANDROID DEVELOPMENT CONCEPTS USED

- Multiple Activities: display multiple screens to the user, each serving their own banking purpose

- Intents: Passing data between activities (ie. when creating an account, the username and password that was entered by the user will be automatically entered in the login page, making the create profile-to-login experience more efficient).

- Shared Preferences: Saving the current profile (logged into by the user), and all of its general info, accounts and transactions. When initially logging into a profile, all of the data from that profile is loaded from the database (stores all profile data). This operation is performed once, in which the profile data is stored into Shared Preferences and can be updated and loaded efficiently across the different activities. JSON is involved in the reading and writing of data into Shared Preferences.

- SQLite Database: All Profile, Account, Payee and Transaction information is stored in a database. The DB consists of four tables, each with a proper primary key (composite or standard) and foreign keys when necessary. The database is stored on the user's device.

- Adapters: Personally created adapters are used to display custom information in ListView's and Spinner's. The adapters used are for accounts, as well as payments and transfers. 

- File Output: An option allows the user to output their current profile information to a text file, saved on their device

- Networking: A button allows the user to select a web browser and view my portfolio. The following URL is used: http://banksmic.dev.fast.sheridanc.on.ca/

- Fundamental Programming Concepts: ArrayList, If statements, For loops, Constructor overloading, Accessor/Mutator methods, etc.

# NOTABLE MENTIONS

- The text that the user sees is found in strings.xml, if multiple language support is added, the strings in the file can be converted to the language of preference for the user
- Each layout file uses ConstraintLayout, allowing it to properly scale depending on the size of the screen
- Runs on Android API 19 and up

# CURRENTLY IN DEVELOPMENT

- Updating the database code to have prepared staements, rather than string concatenation (defense against SQL injection)
- Have all Transactions under the same adapter and Listview in AccountActivity
- Allow 'Deposit' to be a type of transaction
- Move the 'About' Page to the login screen, have as a question mark button. Possibly use Fragment rather than separate Activity
- Clean up the code, enhance the UI, add more documentation
