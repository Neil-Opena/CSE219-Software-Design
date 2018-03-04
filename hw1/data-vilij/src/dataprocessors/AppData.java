package dataprocessors;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import ui.AppUI;
import vilij.components.DataComponent;
import vilij.templates.ApplicationTemplate;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.stream.Stream;
import static settings.AppPropertyTypes.INVALID_DATA_MESSAGE;
import static settings.AppPropertyTypes.INVALID_DATA_TITLE;

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

	private String savedData; //String to test whether the data was saved already
	private ArrayList<String> textAreaData; //helper list 
	private ArrayList<String> fullData;

	public AppData(ApplicationTemplate applicationTemplate) {
		this.processor = new TSDProcessor();
		this.applicationTemplate = applicationTemplate;

		appUI = (AppUI) applicationTemplate.getUIComponent();
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
			appUI.showErrorDialog("Data Exceeded Capacity", "Loaded data consists of " + fullData.size() + " lines. Showing only the first 10 in the text area.");
			for(int i = 0; i < 10; i++){
				textAreaData.add(fullData.remove(0));
			}
		}else{
			while(!fullData.isEmpty()){
				textAreaData.add(fullData.remove(0));
			}
		}

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
			appUI.showErrorDialog(applicationTemplate.manager.getPropertyValue(INVALID_DATA_TITLE.name()), testData);
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
			System.out.println("something went wrong");
			e.printStackTrace();
			//FIXME
		} catch (NullPointerException e){
			//save canceleld
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

	private void initializeData(Path path){ //Initializes full data from the path
		File file = path.toFile();
		try{
			BufferedReader reader = new BufferedReader(new FileReader(file));
			Stream<String> dataLines = reader.lines();

			fullData = new ArrayList<>();
			textAreaData = new ArrayList<>();

			dataLines.forEach(line -> {
				fullData.add(line);
			});
		}catch(FileNotFoundException e){
			System.out.println("file not found");
			e.printStackTrace();
			//FIXME
		}catch(IOException e){
			System.out.println("Something went wrong");
			e.printStackTrace();
			//FIXME
		}
	}

}
