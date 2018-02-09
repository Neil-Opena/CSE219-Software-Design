# Notes
Created to help me understand the frameworks provided
<br />

## xmlutil Module
### xmlutil
__XMLUtilities__ <br />
_Provides methods for interaction with XML data_
1. __validateXML(__ _URL xmlFilePath, URL xmlSchemapath_ __)__
    - Returns true if the xml file is valid according the the schema, else false
2. __loadXMLDocument(__ _URL xmlFilePath, URL schemaFileURL_ __)__
    - Returns org.w3c.com.Document object with data from the original xml file
3. __getNodeWithName(__ _Document doc, String tagName_ __)__
    - Returns (1st) Node in the Document specified by tag name, else null
4. __getTextData(__ _Document doc, String tagName_ __)__
    - Returns String representation of Node in the Document specified by tag name, else null
5. __getChildrenWithName(__ _Node parent, String tagName_ __)__
    - Returns all children of the specified parent node with the specified tag name, 
    
__InvalidXMLFileFormatException__
    - Exception class that occurs when file is not valid according to the defined schema
    
## vlij Module

### components 
__ActionComponent__ [Interface] <br />
_Defines (minimal) behavior of core actions_
1. void handleNewRequest()
2. void handleSaveRequest()
3. void handleLoadRequest()
4. void handleExitRequest()
5. void handlePrintRequest()

__ConfirmationDialog__ (extends --> Stage, implements --> Dialog) <br />
_Provides template for displaying 3 way confirmation messages_
_Essentially this class is like a stage that set allows you to customize the title and the message_
1. enum Option{ YES, NO, CANCEL } 
2. __(static) getDialog()__
    - Returns dialog
3. __init(__ _Stage owner_ __)__
    - Completely initializes the dialog to be used, owner = window on top of which dialog will be displayed
4. __show(__ _String dialogTitle, String message_ __)__
    - Loads the specified title and message into the dialog and then displays the dialog
5. __getSelectedOption()__
    - Returns the Option selected

__DataComponent__ [Interface] <br />
_Defines (minimal) methods for data management_
1. void loadData(Path dataFilePath)
2. void saveData(Path dataFilePath)
3. clear()

__Dialog__ [Interface] <br />
_Defines (minimal) behavior of pop up dialogs_
1. enum DialogType{ ERROR, CONFIRMATION }
2. void show(String title, String message)
3. void init(Stage owner)

__ErrorDialog__ <br />
_Provides the template for displaying error messages, only has close button_
_Essentially this class is another stage that allows you to customize the title and the message_
2. __init(__ _Stage owner_ __)__
    - Completely initializes the error dialog 
3. __show(__ _String errorDialogTitle, String errorMessage_ __)__
    - Loads the specified title and message into the dialog and then displays the dialog

__UIComponent__ [Interface] <br />
_Defines (minimal) functionality of graphical user interface of a ViliJ application
1. Stage getPrimaryWindow()
2. Scene getPrimaryScene()
3. String getTitle()
4. void initialize()
5. void clear()

### propertymanager


### settings

### templates


## data-vlij Module
