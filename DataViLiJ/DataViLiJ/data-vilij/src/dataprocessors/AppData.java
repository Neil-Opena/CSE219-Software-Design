package dataprocessors;

import actions.AppActions;
import algorithms.Algorithm;
import algorithms.Classifier;
import algorithms.Clusterer;
import classification.RandomClassifier;
import data.DataSet;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
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
	private List<Classifier> classificationAlgorithms; //list of classification algorithms
	private List<Clusterer> clusteringAlgorithms; //list of clustering algorithms

	private boolean isRunning; //test if algorithm is running
	private boolean isSaved; //test if current file is saved

	private String lastSavedText;

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
				data = DataSet.fromTSDFile(dataFilePath);
				
				labels = new LinkedHashSet(data.getLabels().values());
				int numInstances = data.getLocations().size();

				if(numInstances > 10){
					appUI.setTextAreaText(getTopTen(fileData));
				}else{
					appUI.setTextAreaText(fileData);
				}
				
				checkLabels();
				List test = Arrays.asList(labels.toArray());
				appUI.displayInfo(numInstances, labels.size(), test, dataFilePath.toFile().getName());
			}catch(Exception e){
				//FILE NOT VALID
			}
			
		}catch(FileNotFoundException e){
			appActions.showErrorDialog(manager.getPropertyValue(FILE_NOT_FOUND_TITLE.name()), manager.getPropertyValue(FILE_NOT_FOUND_MESSAGE.name()));
		}
	}

	public void loadData(String dataString) {
		data = DataSet.fromText(dataString);
		lastSavedText = dataString;
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
		lastSavedText = null;
	}

	/**
	 * Display the current data stored in the chart
	 */
	public void displayData() {
		processor.toChartData(appUI.getChart());
	}

	public DataSet getData(){
		return this.data;
	}

	public boolean isSaved(){
		return this.isSaved;
	}

	public boolean isModified(){
		String curr = appUI.getTextAreaText().trim();
		if(curr.equals(lastSavedText) || curr.isEmpty() || data != null){
			return false;
		}
		return true;
	}

	public int clusteringAlgorithmsSize(){
		return this.clusteringAlgorithms.size();
	}

	public int classificationAlgorithmsSize(){
		return this.classificationAlgorithms.size();
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
	public Algorithm getAlgorithmType(){
		return null;
	}

	/**
	 * Set the current algorithm based on the Config object passed in
	 * @param config Config object that hold configurations of the algorithm
	 */
	public void setAlgoConfig(Config config){

	}

	/**
	 * Start the algorithm
	 */
	public void startAlgorithm(){

	}
	
	/**
	 * Continue the algorithm
	 */
	public void continueAlgorithm(){

	}

	/**
	 * Add algorithms to the list of algorithms
	 */
	private void initAlgorithms(){
		classificationAlgorithms = new ArrayList<>();
		clusteringAlgorithms = new ArrayList<>();
		
		// create algorithm objects with default values
		classificationAlgorithms.add(new RandomClassifier(null, -1, -1, false));
		classificationAlgorithms.add(new RandomClassifier(null, -1, -1, false));
		classificationAlgorithms.add(new RandomClassifier(null, -1, -1, false));

		clusteringAlgorithms.add(null);
		clusteringAlgorithms.add(null);
		clusteringAlgorithms.add(null);
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
	 * Returns the string represetation of the first ten lines
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
