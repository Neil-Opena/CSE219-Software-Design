package dataprocessors;

import actions.AppActions;
import algorithms.Algorithm;
import algorithms.AlgorithmTypes;
import classification.RandomClassifier;
import data.Config;
import data.DataSet;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
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

	/**
	 * current data that the application has access to
	 */
	private DataSet data;
	private Set labels;

	private Algorithm algorithmToRun; //current algorithm queued up to run
	private int algorithmIndex;
	private AlgorithmTypes algorithmType;
	private int numClassificationAlgorithms;
	private int numClusteringAlgorithms;

	/*
	every algorithm has an index
	*/

	private boolean isRunning; //test if algorithm is running
	private boolean fromFile;

	private String lastSavedText;

	public AppData(ApplicationTemplate applicationTemplate) {
		this.processor = new TSDProcessor();
		this.applicationTemplate = applicationTemplate;

		appUI = (AppUI) applicationTemplate.getUIComponent();
		appActions = (AppActions) applicationTemplate.getActionComponent();
		manager = applicationTemplate.manager;

		numClassificationAlgorithms = 1;
		numClusteringAlgorithms = 1;
	}

	@Override
	public void loadData(Path dataFilePath){
		clear();

		try{
			String fileData = getFileText(dataFilePath.toFile());
			try{
				processor.processString(fileData);
				data = DataSet.fromTSDFile(dataFilePath);
				
				labels = new LinkedHashSet(data.getLabels().values());
				int numInstances = data.getLocations().size();

				if(numInstances > 10){
					appUI.setTextAreaText(getTopTen(fileData));
				}else{
					appUI.setTextAreaText(fileData);
				}
				
				checkLabels();
				String path = dataFilePath.toString();
				appUI.displayInfo(numInstances, path);
				appUI.setUpAlgorithmTypes(labels.size());
				fromFile = true;
			}catch(Exception e){
				//FILE NOT VALID
			}
			
		}catch(FileNotFoundException e){
			appActions.showErrorDialog(manager.getPropertyValue(FILE_NOT_FOUND_TITLE.name()), manager.getPropertyValue(FILE_NOT_FOUND_MESSAGE.name()));
		}
	}

	public void loadData(String dataString) {
		fromFile = false;
		data = DataSet.fromText(dataString);
		labels = new LinkedHashSet(data.getLabels().values());
		checkLabels();
		appUI.displayInfo(data.getLocations().size(), null);
	}

	@Override
	public void saveData(Path dataFilePath) {
		try {
			String toSave = appUI.getSavedText();

			File file = dataFilePath.toFile();
				
			FileWriter writer = new FileWriter(file);
			writer.append(toSave);
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
		lastSavedText = null;
		fromFile = false;
	}

	/**
	 * Display the current data stored in the chart
	 */
	public void displayData() {
		processor.toChartData(appUI.getChart());
	}

//	public DataSet getData(){
//		return this.data;
//	}

	public Set getLabels(){
		return labels;
	}

	public boolean isFromFile(){
		return fromFile;
	}

	/**
	 * Checks whether the input String is valid based on the tsd requirements
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

	/**
	 * Check whether the text within a file is valid based on the tsd requirements
	 * @param file the File to be checked
	 * @return null if data is valid, else return a message of the error
	 */
	public String validateText(File file){
		try{
			String fileData = getFileText(file);

			return validateText(fileData);
		}catch(FileNotFoundException e){
			appActions.showErrorDialog(manager.getPropertyValue(FILE_NOT_FOUND_TITLE.name()), manager.getPropertyValue(FILE_NOT_FOUND_MESSAGE.name()));
			return e.getMessage();
		}

	}

	/**
	 * Return the current algorithm to run type
	 * @return 
	 */
	public AlgorithmTypes getAlgorithmType(){
		return algorithmType;
	}

	public void startAlgorithm(){
		System.out.println("is running");
	}

	public boolean isConfigured(){
		if(algorithmToRun != null){
			return true;
		}
		return false; // algorithm has not been configured yet
	}
	
	/**
	 * Continue the algorithm
	 */
	public void continueAlgorithm(){

	}

	public int getNumClassificationAlgorithms(){
		return numClassificationAlgorithms;
	}

	public int getNumClusteringAlgorithms(){
		return numClusteringAlgorithms;
	}

	public void setAlgorithmToRun(int index){
		algorithmToRun = null;
		algorithmIndex = index;
	}

	public void setAlgorithmType(AlgorithmTypes type){
		algorithmType = type;
	}

	/*
	What would happen if user configed without selecting algorithm?
	*/
	public void setConfiguration(Config config){
			if(algorithmType.equals(AlgorithmTypes.CLASSIFICATION)){
				switch(algorithmIndex){
					case 0: 
						algorithmToRun = new RandomClassifier(data, config.getMaxIterations(), config.getUpdateInterval(), config.getToContinue());
						break;
				}
			}else{
				switch(algorithmIndex){
					case 0:
						//algorithmToRun = new RandomClustering(data, config.getMaxIterations(), config.getUpdateInterval(), config.getToContinue());
						//might how to use num labels shit
						break;
				}
			}

	}

	/**
	 * Returns the text within a given file
	 * @param file file to extract the text
	 * @return string representation of the text inside the file
	 * @throws FileNotFoundException file is not found
	 */
	private String getFileText(File file) throws FileNotFoundException{
		BufferedReader reader = new BufferedReader(new FileReader(file));
		Stream<String> dataLines = reader.lines();

		StringBuilder builder = new StringBuilder();

		dataLines.forEach(line ->{
			builder.append(line + "\n");
		});

		return builder.toString();
	}

	/**
	 * Returns the string representation of the first ten lines
	 * @param data full text of a tsd file
	 * @return te top ten lines of the text
	 */
	private String getTopTen(String data){
		List temp = Arrays.asList(data.split("\n"));

		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < 10; i++){
			builder.append(temp.get(i) + "\n");
		}
		return builder.toString();
	}

	/**
	 * Removes the null label from the labels set
	 */
	private void checkLabels() {
		labels.remove("null");
	}

}
