package actions;

import java.io.File;
import vilij.components.ActionComponent;
import vilij.templates.ApplicationTemplate;

import java.io.IOException;
import java.nio.file.Path;
import javafx.application.Platform;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import static settings.AppPropertyTypes.*;
import vilij.components.ConfirmationDialog;
import vilij.components.ConfirmationDialog.Option;
import vilij.components.Dialog;
import vilij.propertymanager.PropertyManager;

/**
 * This is the concrete implementation of the action handlers required by the application.
 *
 * @author Ritwik Banerjee
 */
public final class AppActions implements ActionComponent {

    /** The application to which this class of actions belongs. */
    private ApplicationTemplate applicationTemplate;
    
    private FileChooser fileChooser;
    private PropertyManager manager;

    /** Path to the data file currently active. */
    Path dataFilePath;

    public AppActions(ApplicationTemplate applicationTemplate) {
        this.applicationTemplate = applicationTemplate;
	manager = applicationTemplate.manager;
	this.fileChooser = new FileChooser();
	fileChooser.getExtensionFilters().add(new ExtensionFilter(manager.getPropertyValue(DATA_FILE_EXT_DESC.name()), manager.getPropertyValue(DATA_FILE_EXT.name())));
	/* String iconsPath = "/" + String.join(separator,
                                             manager.getPropertyValue(GUI_RESOURCE_PATH.name()),
                                             manager.getPropertyValue(ICONS_RESOURCE_PATH.name()));
	File a = new File(iconsPath + "/");
	    //System.out.println(a);
	    //fileChooser.setInitialDirectory(a);

	*/
    }

    @Override
    public void handleNewRequest() {
        // TODO for homework 1
	try{
		if(promptToSave()){
			applicationTemplate.getUIComponent().clear();
		}
	}catch(IOException e){
		Dialog errorDialog = applicationTemplate.getDialog(Dialog.DialogType.ERROR);
		errorDialog.show(manager.getPropertyValue(IO_ERROR_TITLE.name()), manager.getPropertyValue(IO_ERROR_MESSAGE.name()));
	}
    }

    @Override
    public void handleSaveRequest() {
        // TODO: NOT A PART OF HW 1
	try{
		promptToSave();
	}catch(IOException e){
		Dialog errorDialog = applicationTemplate.getDialog(Dialog.DialogType.ERROR);
		errorDialog.show(manager.getPropertyValue(IO_ERROR_TITLE.name()), manager.getPropertyValue(IO_ERROR_MESSAGE.name()));
	}
    }

    @Override
    public void handleLoadRequest() {
        // TODO: NOT A PART OF HW 1
    }

    @Override
    public void handleExitRequest() {
        // TODO for homework 1
	Platform.exit();
    }

    @Override
    public void handlePrintRequest() {
        // TODO: NOT A PART OF HW 1
    }

    public void handleScreenshotRequest() throws IOException {
        // TODO: NOT A PART OF HW 1
    }

    /**
     * This helper method verifies that the user really wants to save their unsaved work, which they might not want to
     * do. The user will be presented with three options:
     * <ol>
     * <li><code>yes</code>, indicating that the user wants to save the work and continue with the action,</li>
     * <li><code>no</code>, indicating that the user wants to continue with the action without saving the work, and</li>
     * <li><code>cancel</code>, to indicate that the user does not want to continue with the action, but also does not
     * want to save the work at this point.</li>
     * </ol>
     *
     * @return <code>false</code> if the user presses the <i>cancel</i>, and <code>true</code> otherwise.
     */
    private boolean promptToSave() throws IOException {
        // TODO for homework 1
        // TODO remove the placeholder line below after you have implemented this method
	ConfirmationDialog confirmDialog = (ConfirmationDialog) applicationTemplate.getDialog(Dialog.DialogType.CONFIRMATION);
	confirmDialog.show(manager.getPropertyValue(SAVE_UNSAVED_WORK_TITLE.name()), manager.getPropertyValue(SAVE_UNSAVED_WORK.name()));

	Option option = confirmDialog.getSelectedOption();

	if(option == Option.CANCEL){
		return false;
	}else{
		if(option == Option.YES){
			File saveFile = fileChooser.showSaveDialog(applicationTemplate.getUIComponent().getPrimaryWindow());
			try{
				saveFile.createNewFile();
				dataFilePath = saveFile.toPath();
			}catch(NullPointerException e){
				return false;
			}
		}
		return true;
	}

    }
}
