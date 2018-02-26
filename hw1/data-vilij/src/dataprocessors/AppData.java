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
import javafx.scene.control.TextArea;
import static settings.AppPropertyTypes.INVALID_DATA_MESSAGE;
import static settings.AppPropertyTypes.INVALID_DATA_TITLE;
import vilij.components.Dialog;

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

	private String savedData; //String to test whether the data was saved already
	private String textAreaData;
	private String hiddenData;
	private int numLines;

	public AppData(ApplicationTemplate applicationTemplate) {
		this.processor = new TSDProcessor();
		this.applicationTemplate = applicationTemplate;
	}

	public String getSavedData() {
		return savedData;
	}

	@Override
	public void loadData(Path dataFilePath) {
		AppUI appUI = ((AppUI) applicationTemplate.getUIComponent());
		TextArea textArea = appUI.getTextArea();
			
		reset(); // clear the chart and text area first
		textArea.clear();
		
		getFileText(dataFilePath); //instantiates text area and hidden data
		textArea.setText(textAreaData); //sets text area
		savedData = textArea.getText().trim();

		if(numLines > 10){
			Dialog errorDialog = applicationTemplate.getDialog(Dialog.DialogType.ERROR);
			errorDialog.show("Data Exceeded Capacity", "Loaded data consists of " + numLines + " lines. Showing only the first 10 in the text area.");
		}

		((AppUI) applicationTemplate.getUIComponent()).setHiddenData(hiddenData);
	}

	public void loadData(String dataString) {
		String testData = checkData(dataString);

		if (testData == null) {
			displayData();
		} else {
			Dialog errorDialog = applicationTemplate.getDialog(Dialog.DialogType.ERROR);
			errorDialog.show(applicationTemplate.manager.getPropertyValue(INVALID_DATA_TITLE.name()), testData);
		}
	}

	@Override
	public void saveData(Path dataFilePath) {
		try {
			String text = ((AppUI) applicationTemplate.getUIComponent()).getTextArea().getText().trim();

			File file = dataFilePath.toFile();
				
			FileWriter writer = new FileWriter(file);
			savedData = text;
			writer.append(text);
			writer.append("\n" + hiddenData); //BUG: hash set count may be different
			writer.close();
			
		} catch (IOException e) {
			System.out.println("something went wrong");
			e.printStackTrace();
			//FIXME
		}
	}

	@Override
	public void clear() {
		processor.clear();
		((AppUI) applicationTemplate.getUIComponent()).getChart().getData().clear();
	}

	public void displayData() {
		processor.toChartData(((AppUI) applicationTemplate.getUIComponent()).getChart());
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

	public String getFileText(Path path){ //returns full string representation of data, also instantiates hidden data and text area data
		File file = path.toFile();
		try{
			BufferedReader reader = new BufferedReader(new FileReader(file));
			Stream<String> dataLines = reader.lines();

			ArrayList<String> fullData = new ArrayList<>();

			dataLines.forEach(line -> {
				fullData.add(line);
			});

			StringBuilder hiddenDataBuilder = new StringBuilder();
			StringBuilder textAreaDataBuilder = new StringBuilder();
			StringBuilder fullDataBuilder = new StringBuilder();

			if(fullData.size() <= 10){
				for(String line : fullData){
					String toAdd = line + "\n";
					textAreaDataBuilder.append(toAdd);
					fullDataBuilder.append(toAdd);
				}
			}else{
				numLines = fullData.size();

				for(int i = 0; i < 10; i++){
					String toAdd = fullData.get(i) + "\n";
					textAreaDataBuilder.append(toAdd);
					fullDataBuilder.append(toAdd);
				}
				for(int i = 10; i < fullData.size(); i++){
					String toAdd = fullData.get(i) + "\n";
					hiddenDataBuilder.append(toAdd);
					fullDataBuilder.append(toAdd);
				}
				
			}
			textAreaData = textAreaDataBuilder.toString();
			hiddenData = hiddenDataBuilder.toString();

			return fullDataBuilder.toString();

		}catch(FileNotFoundException e){
			System.out.println("file not found");
			e.printStackTrace();
			//FIXME
		}catch(IOException e){
			System.out.println("Something went wrong");
			e.printStackTrace();
			//FIXME
		}
		return null;
	}

	private void reset(){
		savedData = null; //reset every helper variables
		textAreaData = null;
		hiddenData = null;
		numLines = 0;
		clear();
	}

}
