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

	private String savedData;
	private ArrayList<String> textAreaData;
	private ArrayList<String> hiddenData;

	public AppData(ApplicationTemplate applicationTemplate) {
		this.processor = new TSDProcessor();
		this.applicationTemplate = applicationTemplate;
	}

	public String getSavedData() {
		return savedData;
	}

	public ArrayList<String> getTextAreaData(){
		return textAreaData;
	}

	public ArrayList<String> getHiddenData(){
		return hiddenData;
	}

	@Override
	public void loadData(Path dataFilePath) {
		TextArea textArea = ((AppUI) applicationTemplate.getUIComponent()).getTextArea();
			
		clear(); // clear the chart and text area first
		textArea.clear();
		
		textArea.setText(getFileText(dataFilePath));
		savedData = textArea.getText().trim();
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
			writer.close();
			
		} catch (IOException e) {
			System.out.println("something went wrong");
			e.printStackTrace();
			//FIXME
		}
	}

	@Override
	public void clear() {
		savedData = null;
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

	public String getFileText(Path path){

		StringBuilder text = new StringBuilder();
		ArrayList<String> lines = getFileLines(path);
		for(String line : lines){
			text.append(line + "\n");
		}

		return text.toString();
	}

	private void instantiateData(ArrayList<String> fullData){
		textAreaData = new ArrayList<>();
		hiddenData = new ArrayList<>();

		try{
			for(int i = 0; i < 10; i++){
				textAreaData.add(fullData.get(i));
				System.out.println(textAreaData.get(i));
			}
			for(int i = 10; i < fullData.size(); i++){
				hiddenData.add(fullData.get(i));
			}
		}catch(IndexOutOfBoundsException e){

		}

	}

	private ArrayList<String> getFileLines(Path path){
		File file = path.toFile();
		try{
			BufferedReader reader = new BufferedReader(new FileReader(file));
			Stream<String> dataLines = reader.lines();

			ArrayList<String> temp = new ArrayList<>();

			dataLines.forEach(line -> {
				temp.add(line);
			});
			
			instantiateData(temp);
			return textAreaData; //hiddenData is still not displaying
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

	
}
