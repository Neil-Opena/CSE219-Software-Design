package dataprocessors;

import actions.AppActions;
import algorithms.Algorithm;
import algorithms.AlgorithmTypes;
import algorithms.Classifier;
import algorithms.Clusterer;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

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
	private ArrayList<Classifier> classificationAlgorithms;
	private ArrayList<Clusterer> clusteringAlgorithms;
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
		//FIXME should probably remove these
		loadAlgorithms();
	}

	/*
	TODO:
	-use reflection to load all the algorithms
	-remove the variables above
	-change get___algorithms methods
	-save the data that stark posted
	-figure out a way to properly scale the data 
	-figure out if tsd processor is even needed?
	-what if display button not needed --> what if user displayed data first and then presses the algorithm
		-that would cause 2 displays of the same data
	-set the range of chart
	-fix algorithm run window -- indicate when line is not displaying
	-standard deviation formula in dataset


	MOVE DISPLAY DATA SET HERE
	--how about change get extrema method in random clasifier
	*/

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
			displayDataSet();
		}
	}

	private void displayDataSet(){
		data.sortValues();

		NumberAxis xAxis = (NumberAxis) appUI.getChart().getXAxis();
		NumberAxis yAxis = (NumberAxis) appUI.getChart().getYAxis();
		appUI.getChart().getData().clear();
		xAxis.setAutoRanging(false);
		yAxis.setAutoRanging(false);

		Set<String> labels = new LinkedHashSet<>(data.getLabels().values());
		for (String label : labels) {
			XYChart.Series<Number, Number> series = new XYChart.Series<>();
			series.setName(label);
			data.getLabels().entrySet().stream().filter(entry -> entry.getValue().equals(label)).forEach(entry -> {
				Point2D point = data.getLocations().get(entry.getKey());
				String name = entry.getKey();
				series.getData().add(new XYChart.Data<>(point.getX(), point.getY(), name));
			});
			appUI.getChart().getData().add(series);
		}

		double minX = data.getMinX();
		double maxX = data.getMaxX();
		double minY = data.getMinY();
		double maxY = data.getMaxY();

		double xTicks = getTickMarks(maxX, minX);
		double yTicks = getTickMarks(maxY, minY);

		xAxis.setLowerBound(minX - xTicks);
		xAxis.setUpperBound(maxX + xTicks);
		yAxis.setLowerBound(minY - yTicks);
		yAxis.setUpperBound(maxY + yTicks);

		xAxis.setTickUnit(xTicks);
		yAxis.setTickUnit(yTicks);
	}

	/**
	 * Determines the best possible tick marks according to range of data
	 * @param max maximum value of axis data
	 * @param min minimum value of axis data
	 * @return best possible tick mark
	 * 
	 * StackOverflow was used to help determine this simple algorithm
	 */
	private double getTickMarks(double max, double min){
		double range = max - min;
		double power = Math.log10(range);
		double orderOfMagnitude = Math.pow(10, power);

		return orderOfMagnitude / 10;
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

	public String getAlgorithmName(int index){
		return null;
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
					//need to change constructor and pass in labels
					algorithmToRun = new RandomClustering(data, configuration.getMaxIterations(), configuration.getUpdateInterval(), configuration.getToContinue(), appUI.getChart(), this);
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
		appUI.disableBackButton();
		appUI.disableAlgorithmChanges();
		isRunning = true;

		if(!fromFile){
			//user created data
			appUI.disableEditToggle();
		}

		Platform.runLater(() -> appUI.showAlgorithmRunWindow());
	}

	public void lineNotInChart(String direction){
		String message;
		if(direction.isEmpty()){
			message = "";
		}else{
			message = "Line not in chart bounds - direction: " + direction;
		}
		appUI.appendAlgorithmRunWindow(message);
	}

	public void updateIteration(int iteration, String info){
		double percent = ((double) iteration) / configuration.getMaxIterations();
		appUI.updateAlgorithmRunWindow(percent, info);
	}

	public void stopAlgorithm(){
		algorithmStopped();
		algorithmToRun.stopAlgorithm();
	}

	public void completeAlgorithm(){
		algorithmStopped();
		appActions.showErrorDialog("Algorithm completed", "ALgorithm has finished");
	}

	private void loadAlgorithms(){
		/*
		One way to satisfy the requirement concerning the loading of algorithms 
		would be to assume that any algorithms to be used by the application would 
		have class files located in a particular resource directory. 
		Upon application startup, this directory would be scanned to retrieve a list 
		of names of the class files located there, and each class file would be loaded 
		and instantiated using reflection. Once instantiated, the name of each algorithm 
		would be obtained, either by just using the base name of the class file, or else 
		by ensuring that each algorithm class supplies a "getName" or similar method that 
		can be used to get the user-friendly name of the algorithm. These names can then 
		be registered with the user interface so that the user will see a complete list 
		of the available algorithms, without any having been hard-coded.
		*/
	}

	private void algorithmStopped(){
		isRunning = false;
		enableRun();
		appUI.enableEditToggle();
		appUI.enableBackButton();
		appUI.enableAlgorithmChanges();
		//should reset progress indicator
		Platform.runLater(() -> {
			appUI.closeAlgorithmRunWindow();
		});
	}

	public void enableRun(){
		appUI.enableRun();
	}

	public void disableRun(){
		appUI.disableRun();
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
