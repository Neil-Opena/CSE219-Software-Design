package dataprocessors;

import actions.AppActions;
import algorithms.Algorithm;
import algorithms.AlgorithmTypes;
import classification.RandomClassifier;
import clustering.RandomClustering;
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

	private int numClassificationAlgorithms;
	private int numClusteringAlgorithms;

	private AlgorithmTypes algorithmType;
	private int algorithmIndex;
	private Algorithm algorithmToRun; //current algorithm queued up to run
	private Config configuration;

	/*
	every algorithm has an index
	*/

	private boolean isRunning; //test if algorithm is running
	private boolean fromFile;

	public AppData(ApplicationTemplate applicationTemplate) {
		this.processor = new TSDProcessor();
		this.applicationTemplate = applicationTemplate;
		this.algorithmIndex = -1;

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

	/**
	 * Load data from a given string input
	 * @param dataString String representation of the data to be loaded
	 */
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
		algorithmType = null;
		algorithmIndex = -1;
		algorithmToRun = null;
		configuration = null;
		data = null;
		fromFile = false;
	}

	/**
	 * Display the current data stored in the chart
	 */
	public void displayData() {
		if(appUI.isDifferentFromDisplayed()){
			appUI.setDisplayedText();
			processor.toChartData(appUI.getChart());
		}
	}

	/**
	 * Gets the labels of the current data
	 * @return Set of data labels
	 */
	public Set getLabels(){
		return labels;
	}

	/**
	 * Indicates whether the given data is loaded from a file
	 * @return true if data is from a file
	 */
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

	private void setUpAlgorithm(){
		if(algorithmType.equals(AlgorithmTypes.CLASSIFICATION)){
			switch(algorithmIndex){
				case (0) :
					algorithmToRun = new RandomClassifier(data, configuration.getMaxIterations(), configuration.getUpdateInterval(), configuration.getToContinue(), appUI.getChart(), this);
					break;
			}
		}else{
			switch(algorithmIndex){
				case (0) : 
					algorithmToRun = new RandomClustering(data, configuration.getMaxIterations(), configuration.getUpdateInterval(), configuration.getToContinue(), configuration.getNumLabels());
					break;
			}
		}
	}

	/**
	 * Starts the algorithm
	 */
	public void startAlgorithm(){
		setUpAlgorithm();
		displayData();
		algorithmToRun.startAlgorithm();
		appUI.disableRun();
		isRunning = true;
	}

	public void alertUI(){
		appUI.enableRun();
	}

	public boolean isRunning(){
		return isRunning;
	}

	/**
	 * Continue the algorithm
	 */
	public void continueAlgorithm(){
		algorithmToRun.continueAlgorithm();
	}

	/**
	 * Returns the number of classification algorithms available
	 * @return number of classification algorithms
	 */
	public int getNumClassificationAlgorithms(){
		return numClassificationAlgorithms;
	}

	/**
	 * Returns the number of clustering algorithms available
	 * @return number of clustering algorithms
	 */
	public int getNumClusteringAlgorithms(){
		return numClusteringAlgorithms;
	}

	/**
	 * Sets the algorithm to run
	 * @param index of the algorithm to be run by the application
	 */
	public void setAlgorithmToRun(int index){
		algorithmIndex = index;
	}

	/**
	 * Set the type of algorithm to be run
	 * @param type of algorithm from AlgorithmTypes enum
	 */
	public void setAlgorithmType(AlgorithmTypes type){
		algorithmType = type;
	}

	/**
	 * Sets the configuration of the current selected algorithm
	 * @param config configuration to be set
	 */
	public void setConfiguration(Config config){
		configuration = config;
	}

	/*
	when user clicks algorithm --> it sets the algorithm to run
	if there's no configuration --> can't actually run
	*/

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
		labels.remove(manager.getPropertyValue(NULL.name()));
	}

}
