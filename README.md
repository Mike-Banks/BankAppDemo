# CLICK TO WATCH DEMO OF THE APP
[![Bank App Demo Video](https://i.ytimg.com/vi/Qt9l7ImmQx8/hqdefault.jpg)](https://www.youtube.com/watch?v=Qt9l7ImmQx8&feature=youtu.be)

# PROJECT DETAILS

A Banking app made for Android using Android Studio. No real money is involved, it is a project to showcase my knowledge and practical skill in Android development with Java. The Application was developed using a MVC approach, using proper programming conventions, including documentation, error/exception handling, thorough program structure and memory efficiency.

The app starts out with a login screen, in which the user can either log in with an existing profile, or click a button and create a new profile. When signed in, the user will be brought to their dashboard page, which (when first creating a profile), will prompt them to make their first account. Additionally, there is a menu that slides from the left which includes all of the options for the app, including Dashboard, Account Overview (and subsequently Transactions), Deposits, Payments, Transfers, Profile Settings and Logout. 

# ANDROID DEVELOPMENT CONCEPTS USED

- Multiple Activities: There are two activities: one which has the fragments for logging in and creating a profile, and the other for hosting all of the features the bank app has, including account overview, payments, transactions, etc. The activites serve as containers for the different fragments throughout the application. Intents are used to pass data from one activity to another. The activities themselves do not display a view, but rather host the navigation code (among other things) to travel between fragments. 

- Multiple Fragments: What the user actually sees comes from the fragments of the application. These fragments are almost always launched from the activity that wraps them. Bundles are used to pass data from one fragment to another.

- Well-desgined UI Layouts: Multiple layout files are used, using a well-thought design that keeps the simplicity of the app, while serving optimal functionality. Most layout files are used for the fragments, while some are used for menus in the application, as well as custom layouts for dialogs.

- Custom Toolbar: With the application using AppCompat, custom toolbars are a possibility. The toolbar is consistent throughout the app, with the XML code in a styles.xml file for re-use. The toolbar has a title that changes depending on the current fragment in use, and contains options for the user (including an options menu, back navigation, drawer menu).

- DrawerLayout: The application has a DrawerLayout, which is esentially a sliding drawer that typically comes from the left slide of the screen (either by swiping near the left edge or by clicking the hamburger button in the top left of the screen). This menu hosts the different features of the application, with each option either navigation to a fragment (corresponding with the feature), or launching a dialog in some cases. The DrawerLayout is in the second Activity, which serves as the master container for most of the application's fragments.

- SQLite Database: All Profile, Account, Payee and Transaction information is stored in a database. The DB consists of four tables, each with a proper primary key (composite or standard) and foreign keys when necessary. The database is stored on the user's device.

- Shared Preferences: Saving the current profile (logged into by the user), and all of its general info, accounts and transactions. When initially logging into a profile, all of the data from that profile is loaded from the database (stores all profile data). This operation is performed once, in which the profile data is stored into Shared Preferences and can be updated and loaded efficiently across the different activities. JSON is involved in the reading and writing of data into Shared Preferences.

- Array Adapters: Custom array adapters are used to display information in ListView's and Spinner's. The adapters used are for accounts and all transaction types (deposits, transfers and payments).


# NOTABLE MENTIONS

- The app follows the Material Design guidelines, most noteably with the icons of the app. Also noticeable in the DrawerLayout, the custom tolbar and the 'Add' buttons in some of the fragments.
- Resource files are used (best practice) for strings, colors, drawables, styles.
- Runs on Android API 19 and up

# CURRENTLY IN DEVELOPMENT

While the application is completely functional, it remains somewhat of a work in progress. New libraries, frameworks and features may be implemented down the line for where I see fit. The application will not be posted on the Google Play Store, but will rather be a project I can showcase and practice Android development with Java.
