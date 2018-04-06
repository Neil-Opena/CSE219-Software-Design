package dataprocessors;

import actions.AppActions;
import algorithms.Algorithm;
import algorithms.Classifier;
import algorithms.Clusterer;
import classification.RandomClassifier;
import data.DataSet;
import data.OperatedData;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
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

	private List<DataSet> data;

	private Algorithm algorithmToRun; //current algorithm queued up to run
	private ArrayList<Classifier> classificationAlgorithms; //list of classification algorithms
	private ArrayList<Clusterer> clusteringAlgorithms; //list of clustering algorithms

	private boolean isRunning; //test if algorithm is running
	private boolean isSaved; //test if current file is saved

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

		initAlgorithms();
	}

	@Override
	public void loadData(Path dataFilePath){
		clear();

		try{
			String fileData = getFileText(dataFilePath.toFile());
			try{
				processor.processString(fileData);
				data = processor.getDataPoints();

				appUI.setTextAreaText(fileData);
			}catch(Exception e){
				//FILE NOT VALID
			}
			
		}catch(FileNotFoundException e){
			appActions.showErrorDialog(manager.getPropertyValue(FILE_NOT_FOUND_TITLE.name()), manager.getPropertyValue(FILE_NOT_FOUND_MESSAGE.name()));
		}
	}

	public void loadData(String dataString) {
		String testData = validateText(dataString);

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
//			savedData = text;
//			writer.append(text);
//			if(fullData != null && !fullData.isEmpty()){
//				writer.append("\n" + getStringRepresentation(fullData));
//			}
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
		data = null;
	}

	public void displayData() {
		processor.toChartData(appUI.getChart());
	}

	/**
	 * 
	 * @param toCheck the text from the tsd file
	 * @return null if data is valid, else returns a message of the error
	 */
	public String validateText(String toCheck){
		try{
			processor.clear(); // clear current data in processor
			processor.processString(toCheck);
			return null;
		}catch(Exception e){
			String message = e.getMessage();
			if(message.length() < 9){
				message = message + applicationTemplate.manager.getPropertyValue(INVALID_DATA_MESSAGE.name());
			}
			return message;
		}
	}

	public String validateText(File file){
		try{
			String fileData = getFileText(file);

			return validateText(fileData);
		}catch(FileNotFoundException e){
			appActions.showErrorDialog(manager.getPropertyValue(FILE_NOT_FOUND_TITLE.name()), manager.getPropertyValue(FILE_NOT_FOUND_MESSAGE.name()));
			return e.getMessage();
		}

	}

	public Algorithm getAlgorithmType(){
		return null;
	}

	public void setAlgoConfig(Config config){

	}

	public void startAlgorithm(){

	}

	public void continueAlgorithm(){

	}

	private void initAlgorithms(){
		classificationAlgorithms = new ArrayList<>();
		clusteringAlgorithms = new ArrayList<>();
		
		RandomClassifier test = new RandomClassifier();
		//FIXME
	}

	private void checkLabels(){

	}

	private String getFileText(File file) throws FileNotFoundException{
		BufferedReader reader = new BufferedReader(new FileReader(file));
		Stream<String> dataLines = reader.lines();

		StringBuilder builder = new StringBuilder();

		dataLines.forEach(line ->{
			builder.append(line + "\n");
		});

		return builder.toString();
	}

}
