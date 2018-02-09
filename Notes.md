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
__ActionComponent__ <br />

__ConfirmationDialog__ <br />

__DataComponent__ <br />

__Dialog__ <br />

__ErrorDialog__ <br />

__UIComponent__ <br />
### propertymanager

### settings

### templates


## data-vlij Module
