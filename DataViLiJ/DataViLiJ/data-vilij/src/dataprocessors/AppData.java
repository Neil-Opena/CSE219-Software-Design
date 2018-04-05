package dataprocessors;

import actions.AppActions;
import data.OperatedData;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.stream.Stream;

import vilij.propertymanager.PropertyManager;
import vilij.components.DataComponent;
import vilij.templates.ApplicationTemplate;
import ui.AppUI;
import static settings.AppPropertyTypes.*;

/**
 * This is the concrete application-specific implementation of the data
 * component defined by the Vilij framework.
 *
 * @author Ritwik Banerjee
 * @see DataComponent
 */
public class AppData implements DataComponent {

	private TSDProcessor processor;
	private ApplicationTemplate applicationTemplate;
	private AppUI appUI;
	private AppActions appActions;
	private PropertyManager manager;

	private String savedData; //String to test whether the data was saved already
	private ArrayList<String> textAreaData; //helper list 
	private ArrayList<String> fullData;
	private ArrayList<String> old;

	private class AppOperatedData extends OperatedData{
		private String name;

		public AppOperatedData(int x, int y, String label, String name){
			super(x, y, label);
			this.name = name;
		}
	}

	public AppData(ApplicationTemplate applicationTemplate) {
		this.processor = new TSDProcessor();
		this.applicationTemplate = applicationTemplate;

		appUI = (AppUI) applicationTemplate.getUIComponent();
		appActions = (AppActions) applicationTemplate.getActionComponent();
		manager = applicationTemplate.manager;
	}

	public String getSavedData() {
		return savedData;
	}
	/*
	FIXME theres an error, where teh invalid data of a file that can't be load is being shown after loadNumLines
	FIXME camera should be disabled, if cant display data - because chart will be cleared
	*/
	public String loadNumLines(int n){
		String text = ((AppUI) applicationTemplate.getUIComponent()).getTextAreaText();

		if(fullData != null && textAreaData != null){
			for(int i = 0; i < n; i++){
				if(!fullData.isEmpty()){
					textAreaData.add(fullData.remove(0));
				}
			}
			((AppUI) applicationTemplate.getUIComponent()).setHiddenData(getStringRepresentation(fullData));	
			if(!textAreaData.isEmpty()){
				text = text + "\n" +  (getStringRepresentation(textAreaData));
			}
			textAreaData.clear();
		}
		return text;
	}

	@Override
	public void loadData(Path dataFilePath) {
		reset(); // reset App Data
		initializeData(dataFilePath); //initializes Data from file

		//when loading, only 10 are displayed on the text area
		if(fullData.size() > 10){
			appActions.showErrorDialog(manager.getPropertyValue(LARGE_DATA_TITLE.name()), manager.getPropertyValue(LARGE_DATA_MESSAGE_1.name()) + fullData.size() + manager.getPropertyValue(LARGE_DATA_MESSAGE_2.name()));
			for(int i = 0; i < 10; i++){
				textAreaData.add(fullData.remove(0));
			}
		}else{
			while(!fullData.isEmpty()){
				textAreaData.add(fullData.remove(0));
			}
		}

		/*
		-could be better
		-this should get num instances, get num labels, label names, 
		*/

		appUI.setTextAreaText(getStringRepresentation(textAreaData)); //sets text area
		textAreaData.clear();
		savedData = appUI.getTextAreaText().trim();
		appUI.setHiddenData(getStringRepresentation(fullData));
		
	}

	public void loadData(String dataString) {
		String testData = checkData(dataString);

		if (testData == null) {
			displayData();
		} else {
			appActions.showErrorDialog(applicationTemplate.manager.getPropertyValue(INVALID_DATA_TITLE.name()), testData);
		}
	}

	@Override
	public void saveData(Path dataFilePath) {
		try {
			String text = ((AppUI) applicationTemplate.getUIComponent()).getTextAreaText().trim();

			File file = dataFilePath.toFile();
				
			FileWriter writer = new FileWriter(file);
			savedData = text;
			writer.append(text);
			if(fullData != null && !fullData.isEmpty()){
				writer.append("\n" + getStringRepresentation(fullData));
			}
			writer.close();
			
		} catch (IOException e) {
			appActions.showErrorDialog(manager.getPropertyValue(IO_ERROR_TITLE.name()), manager.getPropertyValue(IO_SAVE_ERROR_MESSAGE.name()));
		} catch (NullPointerException e){
			//save cancelled
		}
	}

	@Override
	public void clear() {
		processor.clear();
		appUI.getChart().getData().clear();
	}

	public void displayData() {
		processor.toChartData(appUI.getChart());
	}

	public String checkData(String data) {
		try {
			processor.clear();
			processor.processString(data);
			return null;
		} catch (Exception e) {
			String message = e.getMessage();
			if (message.length() < 9) {
				message = message + applicationTemplate.manager.getPropertyValue(INVALID_DATA_MESSAGE.name());
			}
			return message;
		}
	}

	public String getFileText(Path path){ //returns full string representation of data
		/*
		Initially, hiddenDataList will contain all of the lines, when the file is loaded,
		10 will be loaded to the textAreaDataList
		*/
		initializeData(path);
		return getStringRepresentation(fullData);
	}

	/*
	reset clears everything, including the hidden data
	*/
	public void reset(){
		savedData = null;
		fullData = null;
		clear();
	}

	public void revert(){
		fullData = old;
	}

	private String getStringRepresentation(ArrayList<String> list){
		if(list.isEmpty()){
			return null;
		}
		String[] temp = new String[list.size()];
		for(int i = 0; i < list.size(); i++){
			temp[i] = list.get(i);
		}
		return String.join("\n", temp);
	}

	/*

	should parse data to determine number of instances, labels, etc. 
	*/
	private void initializeData(Path path){ //Initializes full data from the path
		File file = path.toFile();
		try{
			BufferedReader reader = new BufferedReader(new FileReader(file));
			Stream<String> dataLines = reader.lines();
			old = fullData;
			fullData = new ArrayList<>();
			textAreaData = new ArrayList<>();

			dataLines.forEach(line -> {
				fullData.add(line);
			});
		}catch(FileNotFoundException e){
			appActions.showErrorDialog(manager.getPropertyValue(FILE_NOT_FOUND_TITLE.name()), manager.getPropertyValue(FILE_NOT_FOUND_MESSAGE.name()));
			//FIXME
		}catch(IOException e){
			appActions.showErrorDialog(manager.getPropertyValue(IO_ERROR_TITLE.name()), manager.getPropertyValue(IO_LOAD_ERROR_MESSAGE.name()));
		}
	}

}
