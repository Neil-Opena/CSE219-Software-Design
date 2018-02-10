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
_PRIVATE CONSTRUCTOR_ -basically can only create instance in the ConfirmationDialog class
1. enum Option{ YES, NO, CANCEL } 
2. __(static) getDialog()__
    - Returns dialog --> this is also where the instance is created
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
_PRIVATE CONSTRUCTOR_ -basically can only create instance in the ErrorDialog class
1. __init(__ _Stage owner_ __)__
    - Completely initializes the error dialog 
2. __show(__ _String errorDialogTitle, String errorMessage_ __)__
    - Loads the specified title and message into the dialog and then displays the dialog

__UIComponent__ [Interface] <br />
_Defines (minimal) functionality of graphical user interface of a ViliJ application_
1. Stage getPrimaryWindow()
2. Scene getPrimaryScene()
3. String getTitle()
4. void initialize()
5. void clear()

### propertymanager
__PropertyManager__ <br />
_Core class that defines all the global properties to be used by the Vilij framework_
_PRIVATE CONSTRUCTOR_ -basically can only create instance in the PropertyManager class
1. Constants required to load the elements and their properties from the XML properties file
    - PROPERTY_ELEMENT = "property"
    - PROPERTY\_LIST\_ELEMENT = "property_list"
    - PROPERTY\_OPTIONS\_LIST\_ELEMENT = "property\_options\_list"
    - PROPERTY\_OPTIONS\_ELEMENT = "property_options"
    - OPTION_ELEMENT = "option"
    - NAME_ATTRIBUTE = "name"
    - VALUE_ATTRIBUTE = "value"
2. Path of the properties resource folder, relative to the root resource folder for the application
    - PROPERTIES\_RESOURCE\_RELATIVE\_PATH = "properties"
3. __(static) getManager() __
    - Returns PropertyManager --> this is where the instance is also created (can be null if the initialization xml is not validated to the initialization schema)
    - Creates HashMap of properties and another one of propertyOptions
4. __addProperty(__ _String property, String value_ __)__
    - Adds the property to PropertyManager's HashMap 
5. __getPropertyValue(__ _String property_ __)__
    - Returns property value as a String, based on the string property name
6. __getPropertyValuesAsInt(__ _String property_ __)__
    - Returns property value as an integer, based on the string property name --> can throw Exceptions (NullPointer, NumberFormat)
7. __getPropertyValueAsBoolean(__ _String property_ __)__
    - Returns true if property value is "true" (ignoring case) based on the string property name
8. __addPropertyOption(__ _String property, String option_ __)__
    - Throws exception if property does not exist (NoSuchElement)
    - Add property option to specified string property name
9. __getPropertyOptions(__ _String propterty_ __)__
    - Throws exception if property does not exist (NoSUchElement)
    - Returns a list of property options (strings)
10. __hasProperty(__ _Object property_ __)__
    - Returns true if property exists (Notice that the parameter is an Object!)
11. __loadProperties(__ _Class klass, String xmlfilename, String schemafilename_ __)__
    - Parameters are strings of the file names
    - Document created so that a propertyListNode is retrieved such that its children are the properties.
    - For each property from the XML, NamedNodeMap (kinda like a list of attributes of the property) gets the name and value pairs and puts them in the properties HashMap of the PropertyManager
    - propertyOptionsListNode retrieved, if exists, a list of the property options are retrieved
    - For each node in the property options list, the name of the property is retrieved
    - For each option of the property, the option is added to the array list of the property in the property Options HashMap 
    - Basically, this method loads all the properties and the property options for each property in the Property Manager's hashmap

### settings
__PropertyTypes__ <br />
_This enumerable type, lists the various high-level property types listed in the initial set of properties to be loaded from the global properties xml file specified by the initialization parameters_
1. It is an enum of PropertyTypes, view file for specific property types. Property types include:
    - high-level user interface properties
    - resource files and folders
    - user interface icon file names
    - tooltips for user interface buttons
    - error titles (these reflect the type of error encountered)
    - error messages for errors that require an argument
    - standard labels and titles
__InitializationParams__ <br />
_This is the set of parameters specified for the proper initialization of a Vilij application_
_Two error-specific parameters are included to handle the case when the property file(s) cannot be loaded (which are the other parameters)_
1. enum InitializationParams (Each InitializationParams has a getParameterName() method, returns string)
    - LOAD\_ERROR\_TITLE("Load Error")
    - PROPERTIES\_LOAD\_ERROR\_MESSAGE("An error occured while loading the property file")
    - PROPERTIES\_XML("properties.xml")
    - WORKSPACE\_PROPERTIES\_XML("app-properties.xml")
    - SCHEMA\_DEFINITION("property-schema.xsd")
    
### templates


## data-vlij Module
