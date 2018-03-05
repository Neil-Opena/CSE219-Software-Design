package actions;

import dataprocessors.AppData;
import java.io.File;
import vilij.components.ActionComponent;
import vilij.templates.ApplicationTemplate;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.Chart;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javax.imageio.ImageIO;
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

	private FileChooser tsdFileChooser;
	private FileChooser screenShotChooser;
	private PropertyManager manager;

	private AppUI appUI;

	/**
	 * Path to the data file currently active.
	 */
	Path dataFilePath;

	public AppActions(ApplicationTemplate applicationTemplate) {
		this.applicationTemplate = applicationTemplate;
		manager = applicationTemplate.manager;

		//set up file chooser for tsd files
		this.tsdFileChooser = new FileChooser();
		tsdFileChooser.getExtensionFilters().add(new ExtensionFilter(manager.getPropertyValue(DATA_FILE_EXT_DESC.name()), manager.getPropertyValue(DATA_FILE_EXT.name())));
		Path current = Paths.get(".").toAbsolutePath();
		Path dataDirectory = current.resolve(manager.getPropertyValue(DATA_RESOURCE_PATH.name()));
		tsdFileChooser.setInitialDirectory(new File(dataDirectory.toString()));

		//set up file chooser for screenshots
		this.screenShotChooser = new FileChooser();
		screenShotChooser.getExtensionFilters().add(new ExtensionFilter(manager.getPropertyValue(PNG_EXT_DESC.name()), manager.getPropertyValue(PNG_EXT.name())));
		Path screenshotDirectory = current.resolve(manager.getPropertyValue(SCREENSHOT_RESOURCE_PATH.name()));
		screenShotChooser.setInitialDirectory(new File(screenshotDirectory.toString()));

		appUI = (AppUI) applicationTemplate.getUIComponent();
	}

	@Override
	public void handleNewRequest() {
		try {
			if (promptToSave()) {
				((AppData) applicationTemplate.getDataComponent()).reset();
				applicationTemplate.getUIComponent().clear();
				dataFilePath = null;
				appUI.disableSaveButton(); //disable Save Button
			}
		} catch (IOException e) {
			appUI.showErrorDialog(manager.getPropertyValue(IO_ERROR_TITLE.name()), manager.getPropertyValue(IO_SAVE_ERROR_MESSAGE.name()));
		}
	}

	@Override
	public void handleSaveRequest() {
		String testData = ((AppData) applicationTemplate.getDataComponent()).checkData(((AppUI) applicationTemplate.getUIComponent()).getTextAreaText().trim());
		if(testData == null){
			if(dataFilePath == null){ //no save file yet
				try{
					showSaveDialog();
				}catch(IOException e){
					appUI.showErrorDialog(manager.getPropertyValue(IO_ERROR_TITLE.name()),manager.getPropertyValue(IO_SAVE_ERROR_MESSAGE.name()));
				}
			}
			((AppData) applicationTemplate.getDataComponent()).saveData(dataFilePath);
			appUI.disableSaveButton(); //disable Save Button
		}else{
			appUI.showErrorDialog(manager.getPropertyValue(SAVE_ERROR_TITLE.name()), manager.getPropertyValue(SAVE_ERROR_MESSAGE.name()) + testData); //Invalid Data --> will not save
		}
	}

	@Override
	public void handleLoadRequest() {
		File file = tsdFileChooser.showOpenDialog(applicationTemplate.getUIComponent().getPrimaryWindow());
		AppData appData = (AppData) applicationTemplate.getDataComponent();
		try {
			String fileData = appData.getFileText(file.toPath());
			String testData = appData.checkData(fileData);
			if(testData == null){
				applicationTemplate.getUIComponent().clear();
				((AppData) applicationTemplate.getDataComponent()).loadData(file.toPath());
				dataFilePath = file.toPath();
			} else {
				//REVERT to old data
				appData.revert();
				appUI.showErrorDialog(manager.getPropertyValue(LOAD_ERROR_TITLE.name()), manager.getPropertyValue(LOAD_ERROR_MESSAGE.name()) + testData); //Invalid Data --> will not load
			}
			appUI.disableSaveButton(); //disable save button
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
		Chart chart = ((AppUI) applicationTemplate.getUIComponent()).getChart();
		File file = screenShotChooser.showSaveDialog(applicationTemplate.getUIComponent().getPrimaryWindow());
		try{
			WritableImage image = new WritableImage((int) chart.getWidth(), (int) chart.getHeight());
			WritableImage screenshot = chart.snapshot(new SnapshotParameters(), image);
			ImageIO.write(SwingFXUtils.fromFXImage(screenshot, null), manager.getPropertyValue(SCREENSHOT_TYPE.name()), file);

		}catch(IllegalArgumentException e){
			//save cancelled
		}
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
				String testData = ((AppData) applicationTemplate.getDataComponent()).checkData(((AppUI) applicationTemplate.getUIComponent()).getTextAreaText().trim());
				if(testData == null){
					return showSaveDialog();
				}else{
					appUI.showErrorDialog(manager.getPropertyValue(SAVE_ERROR_TITLE.name()), manager.getPropertyValue(SAVE_ERROR_MESSAGE.name()) + testData);
					return false;
				}
			}
			return true;
		}
	}
	

	private boolean showSaveDialog() throws IOException {
		File saveFile = tsdFileChooser.showSaveDialog(applicationTemplate.getUIComponent().getPrimaryWindow());
		try {
			saveFile.createNewFile();
			dataFilePath = saveFile.toPath();
			((AppData) applicationTemplate.getDataComponent()).saveData(dataFilePath);
			appUI.disableSaveButton(); //disable save button
		} catch (NullPointerException e) {
			return false; //save cancelled
		}
		return true;
	}
}
