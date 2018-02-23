package actions;

import dataprocessors.AppData;
import java.io.File;
import vilij.components.ActionComponent;
import vilij.templates.ApplicationTemplate;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import javafx.application.Platform;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import static settings.AppPropertyTypes.*;
import ui.AppUI;
import vilij.components.ConfirmationDialog;
import vilij.components.ConfirmationDialog.Option;
import vilij.components.Dialog;
import vilij.propertymanager.PropertyManager;

/**
 * This is the concrete implementation of the action handlers required by the
 * application.
 *
 * @author Ritwik Banerjee
 */
public final class AppActions implements ActionComponent {

	/**
	 * The application to which this class of actions belongs.
	 */
	private ApplicationTemplate applicationTemplate;

	private FileChooser fileChooser;
	private PropertyManager manager;

	/**
	 * Path to the data file currently active.
	 */
	Path dataFilePath;

	public AppActions(ApplicationTemplate applicationTemplate) {
		this.applicationTemplate = applicationTemplate;
		manager = applicationTemplate.manager;

		//set up file chooser
		this.fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().add(new ExtensionFilter(manager.getPropertyValue(DATA_FILE_EXT_DESC.name()), manager.getPropertyValue(DATA_FILE_EXT.name())));
		Path current = Paths.get(".").toAbsolutePath();
		Path defaultDirectory = current.resolve(manager.getPropertyValue(DATA_RESOURCE_PATH.name()));
		fileChooser.setInitialDirectory(new File(defaultDirectory.toString()));
	}

	@Override
	public void handleNewRequest() {
		try {
			if (promptToSave()) {
				applicationTemplate.getUIComponent().clear();
				dataFilePath = null;
			}
		} catch (IOException e) {
			Dialog errorDialog = applicationTemplate.getDialog(Dialog.DialogType.ERROR);
			errorDialog.show(manager.getPropertyValue(IO_ERROR_TITLE.name()), manager.getPropertyValue(IO_ERROR_MESSAGE.name()));
		}
	}

	@Override
	public void handleSaveRequest() {
		String testData = ((AppData) applicationTemplate.getDataComponent()).checkData(((AppUI) applicationTemplate.getUIComponent()).getTextArea().getText().trim());
		if(testData == null){
			if(dataFilePath == null){ //no save file yet
				try{
					showSaveDialog();
				}catch(IOException e){
					showErrorDialog(manager.getPropertyValue(IO_ERROR_TITLE.name()),manager.getPropertyValue(IO_ERROR_MESSAGE.name()));
				}
			}else{
				((AppData) applicationTemplate.getDataComponent()).saveData(dataFilePath);
			}
			((AppUI) applicationTemplate.getUIComponent()).getSaveButton().setDisable(true); //disable Save Button
		}else{
			showErrorDialog("CANNOT SAVE", "Cannot save to a .tsd file. Invalid data\n" + testData); //Invalid Data --> will not save
		}
	}

	@Override
	public void handleLoadRequest() {
		File file = fileChooser.showOpenDialog(applicationTemplate.getUIComponent().getPrimaryWindow());
		try {
			String fileData = ((AppData) applicationTemplate.getDataComponent()).getFileText(file.toPath());
			String testData = ((AppData) applicationTemplate.getDataComponent()).checkData(fileData);
			if(testData == null){
				((AppData) applicationTemplate.getDataComponent()).loadData(file.toPath());
				dataFilePath = file.toPath();
			} else {
				showErrorDialog("CANNOT LOAD", "Not a tsd file: cannot load because of invalid data \n" + testData); //Invalid Data --> will not load
			}
			((AppUI) applicationTemplate.getUIComponent()).getSaveButton().setDisable(true); //disable save button
		} catch (NullPointerException e) {
			//load cancelled
		}
	}

	@Override
	public void handleExitRequest() {
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
	 * This helper method verifies that the user really wants to save their
	 * unsaved work, which they might not want to do. The user will be
	 * presented with three options:
	 * <ol>
	 * <li><code>yes</code>, indicating that the user wants to save the work
	 * and continue with the action,</li>
	 * <li><code>no</code>, indicating that the user wants to continue with
	 * the action without saving the work, and</li>
	 * <li><code>cancel</code>, to indicate that the user does not want to
	 * continue with the action, but also does not want to save the work at
	 * this point.</li>
	 * </ol>
	 *
	 * @return <code>false</code> if the user presses the <i>cancel</i>, and
	 * <code>true</code> otherwise.
	 */
	private boolean promptToSave() throws IOException {
		ConfirmationDialog confirmDialog = (ConfirmationDialog) applicationTemplate.getDialog(Dialog.DialogType.CONFIRMATION);
		confirmDialog.show(manager.getPropertyValue(SAVE_UNSAVED_WORK_TITLE.name()), manager.getPropertyValue(SAVE_UNSAVED_WORK.name()));

		Option option = confirmDialog.getSelectedOption();

		if (option == Option.CANCEL) {
			return false;
		} else {
			if (option == Option.YES) {
				String testData = ((AppData) applicationTemplate.getDataComponent()).checkData(((AppUI) applicationTemplate.getUIComponent()).getTextArea().getText().trim());
				if(testData == null){
					return showSaveDialog();
				}else{
					showErrorDialog("CANNOT SAVE", "Cannot save to a .tsd file. Invalid data\n" + testData);
					return false;
				}
			}
			return true;
		}
	}
	

	private boolean showSaveDialog() throws IOException {
		File saveFile = fileChooser.showSaveDialog(applicationTemplate.getUIComponent().getPrimaryWindow());
		try {
			saveFile.createNewFile();
			dataFilePath = saveFile.toPath();
			((AppData) applicationTemplate.getDataComponent()).saveData(dataFilePath);
			((AppUI) applicationTemplate.getUIComponent()).getSaveButton().setDisable(true); //disable save button
		} catch (NullPointerException e) {
			return false; //save cancelled
		}
		return true;
	}

	private void showErrorDialog(String title, String message) {
		Dialog errorDialog = applicationTemplate.getDialog(Dialog.DialogType.ERROR);
		errorDialog.show(title, message);
	}
}
