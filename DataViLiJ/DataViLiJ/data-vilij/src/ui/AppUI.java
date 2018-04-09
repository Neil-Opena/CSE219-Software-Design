//Neil Opena 110878452
package ui;

import static java.io.File.separator;
import java.io.IOException;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import vilij.propertymanager.PropertyManager;
import vilij.templates.ApplicationTemplate;
import vilij.templates.UITemplate;
import static vilij.settings.PropertyTypes.CSS_RESOURCE_PATH;
import static vilij.settings.PropertyTypes.GUI_RESOURCE_PATH;
import static vilij.settings.PropertyTypes.ICONS_RESOURCE_PATH;
import static settings.AppPropertyTypes.*;
import actions.AppActions;
import dataprocessors.AppData;
import java.util.ArrayList;
import java.util.List;
import javafx.animation.Animation;
import javafx.animation.RotateTransition;
import javafx.collections.ObservableList;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

/**
 * This is the application's user interface implementation.
 *
 * @author Ritwik Banerjee
 */
public final class AppUI extends UITemplate {

	/**
	 * The application to which this class of actions belongs.
	 */
	ApplicationTemplate applicationTemplate;
	private PropertyManager manager;

	private ConfigWindow configWindow;

	@SuppressWarnings("FieldCanBeLocal")
	private VBox inputRegion; // container for input region
	private Label inputTitle; // title for input region
	private VBox typeContainer; // container that houses algorithm types options
	private VBox algorithms; // container for the algorithms

	private TextArea textArea;       // text area for new data input
	private LineChart<Number, Number> chart;          // the chart where data will be displayed
	private Button scrnshotButton; // toolbar button to take a screenshot of the data
	private Button editToggleButton; // button that toggles between edit and done
	private Button runButton; // button for running alogrithm
	private Button classificationButton;
	private Button clusteringButton;
	private Button displayButton;
	private Label typesTitle;
	private Label algorithmType;
	private Label displayInfo;
	private List<AlgorithmUI> clusteringAlgorithms;
	private List<AlgorithmUI> classificationAlgorithms;
	private ToggleGroup clusteringRadios;
	private ToggleGroup classificationRadios;

	private boolean hasNewText;     // whether or not the text area has any new data since last display
	public String iconsPath;

	private String lastDisplayedText; // text to check if current text matches saved text

	public AppUI(Stage primaryStage, ApplicationTemplate applicationTemplate) {
		super(primaryStage, applicationTemplate);
		this.applicationTemplate = applicationTemplate;
		cssPath = "/" + String.join(separator,
                                    manager.getPropertyValue(GUI_RESOURCE_PATH.name()),
                                    manager.getPropertyValue(CSS_RESOURCE_PATH.name()),
                                    manager.getPropertyValue(CSS_FILE.name()));
		this.getPrimaryScene().getStylesheets().add(getClass().getResource(cssPath).toExternalForm());

		primaryStage.setTitle("Data ViLiJ"); // FIXME
	}

	@Override
	protected void setResourcePaths(ApplicationTemplate applicationTemplate) {
		super.setResourcePaths(applicationTemplate);
	}

	@Override
	protected void setToolBar(ApplicationTemplate applicationTemplate) {
		super.setToolBar(applicationTemplate);
		manager = applicationTemplate.manager;
		iconsPath = "/" + String.join(separator, manager.getPropertyValue(GUI_RESOURCE_PATH.name()), manager.getPropertyValue(ICONS_RESOURCE_PATH.name()));
		String scrnshotIconPath = iconsPath + separator + manager.getPropertyValue(SCREENSHOT_ICON.name());

		scrnshotButton = setToolbarButton(scrnshotIconPath, manager.getPropertyValue(SCREENSHOT_TOOLTIP.name()), true);
		newButton.setDisable(false);
		toolBar.getItems().add(scrnshotButton);
		toolBar.getStyleClass().add("toolbar"); // FIXME
	}

	@Override
	protected void setToolbarHandlers(ApplicationTemplate applicationTemplate) {
		applicationTemplate.setActionComponent(new AppActions(applicationTemplate));
		newButton.setOnAction(e -> applicationTemplate.getActionComponent().handleNewRequest());
		saveButton.setOnAction(e -> applicationTemplate.getActionComponent().handleSaveRequest());
		loadButton.setOnAction(e -> applicationTemplate.getActionComponent().handleLoadRequest());
		exitButton.setOnAction(e -> applicationTemplate.getActionComponent().handleExitRequest());
		printButton.setOnAction(e -> applicationTemplate.getActionComponent().handlePrintRequest());
		scrnshotButton.setOnAction(e -> {
			try {
				((AppActions) applicationTemplate.getActionComponent()).handleScreenshotRequest();
			} catch (IOException ex) {
				//ERROR occurred 
			}
		});
	}

	@Override
	public void initialize() {
		layout();
		setWorkspaceActions();
		initAlgorithms();
	}

	@Override
	public void clear() {
		((AppData) applicationTemplate.getDataComponent()).clear();
		textArea.clear();
		scrnshotButton.setDisable(true);
	}

	/**
	 * Returns the text within the text area
	 * @return the text area text
	 */
	public String getTextAreaText(){
		return textArea.getText();
	}

	/**
	 * Sets the text area with the given parameter
	 * Only called when data is loaded
	 * @param text text to set the text area
	 */
	public void setTextAreaText(String text){
		showTextArea();
		setReadOnly(true);
		textArea.setText(text);
	} 
	/*
	should set text to read me
	*/
	
	/**
	 * Returns the chart inside the UI
	 * @return chart
	 */
	public LineChart<Number, Number> getChart() {
		return chart;
	}

	/**
	 * Disables the save button in the UI
	 */
	public void disableSaveButton(){
		saveButton.setDisable(true);
	}

	/**
	 * Sets the display information when a data file is loaded
	 * @param numInstances the number of instances or data points to be plotted
	 * @param numLabels the number of labels of the given data set
	 * @param labelNames the names of the labels
	 * @param source the source of the file (fileName)
	 */
	public void displayInfo(int numInstances, String source){
		AppData appData = (AppData) applicationTemplate.getDataComponent();
		displayInfo.setText(numInstances + " instances with " + appData.getLabels().size() + " labels loaded from " + source + ". The labels are:\n");

		StringBuilder builder = new StringBuilder();
		appData.getLabels().forEach(label -> {
			builder.append("\t- " + label.toString() + "\n");
		});
		displayInfo.setText(displayInfo.getText() + builder.toString() + "\n");

		inputRegion.getChildren().add(displayInfo);
		setUpAlgorithmTypes(appData.getLabels().size());
	}

	/**
	 * Displays the input region to the user
	 */
	public void showTextArea(){
		resetInputRegion();
		inputRegion.getChildren().addAll(inputTitle, textArea);
	}

	

	/**
	 * Displays the edit toggle button to the user
	 */
	public void showEditToggle(){
		inputRegion.getChildren().add(editToggleButton);
		editToggleButton.setText("Done");
		setReadOnly(false);
	}

	/**
	 * Hides the edit toggle button 
	 */
	public void hideEditToggle(){

	}

	/**
	 * Hides the algorithm types
	 */
	private void hideAlgorithmTypes(){
		inputRegion.getChildren().remove(typeContainer);
	}

	/**
	 * Shows the possible clustering algorithms
	 */
	public void showClusteringAlgorithms(){
		algorithmType.setText("Clustering");
		algorithms.getChildren().add(algorithmType);
		for(int i = 0; i < clusteringAlgorithms.size(); i++){
			algorithms.getChildren().add(clusteringAlgorithms.get(i));
		}
		algorithms.getChildren().add(runButton);
		inputRegion.getChildren().add(algorithms);
	}

	/**
	 * Shows the possible classification algorithms
	 */
	public void showClassificationAlgorithms(){
		algorithmType.setText("Classification");
		algorithms.getChildren().add(algorithmType);
		for(int i = 0; i < classificationAlgorithms.size(); i++){
			algorithms.getChildren().add(classificationAlgorithms.get(i));
		}
		algorithms.getChildren().add(runButton);
		inputRegion.getChildren().add(algorithms);
	}

	private void resetAlgorithms(){
		inputRegion.getChildren().remove(algorithms);
		algorithms.getChildren().clear();
	}

	/**
	 * Enables the run button
	 */
	public void enableRun(){

	}

	/**
	 * Disables the run button
	 */
	public void disableRun(){
	}

	/**
	 * Enables the screen shot button
	 */
	public void enableScreenshot(){

	}

	/**
	 * Disables the screen shot button
	 */
	public void disableScreenshot(){

	}

	private void setUpAlgorithmTypes(int numLabels){
		if(numLabels < 2){
			classificationButton.setDisable(true);
		}else{
			classificationButton.setDisable(false);
		}
		inputRegion.getChildren().add(typeContainer);

		//FIXME
		/*
		if the text area is modified, should hide everything and reset input region
		*/
	}


	private void resetInputRegion(){
		algorithms.getChildren().clear();
		inputRegion.getChildren().clear();
	}
/**
	 * Sets the text area to be read only depending on the parameter
	 * @param readOnly true to set the text area to be read only, false otherwise
	 */
	private void setReadOnly(boolean readOnly){
		textArea.setEditable(!readOnly);
		//when two files are loaded after one another, text is gray bruh
		textArea.getStyleClass().remove(manager.getPropertyValue(GRAY_TEXT.name()));
		if(readOnly){
			textArea.getStyleClass().add(manager.getPropertyValue(GRAY_TEXT.name()));
		}
	}

	/**
	 * Lays out the arrangement of the user interface
	 * Instantiates the graphical user interface objects for use  
	 */
	private void layout() {

		workspace = new FlowPane();

		inputRegion = new VBox();
		inputRegion.setPrefWidth(300);
		inputRegion.setAlignment(Pos.TOP_CENTER);

		inputTitle = new Label(manager.getPropertyValue(TEXT_AREA.name()));
		inputTitle.setAlignment(Pos.CENTER);
		inputTitle.getStyleClass().add(manager.getPropertyValue(TITLE_STYLE.name()));
		textArea = new TextArea();
		textArea.setPrefHeight(150);
		textArea.setWrapText(true);
		VBox.setMargin(textArea, new Insets(10));

		displayInfo = new Label();
		displayInfo.setWrapText(true);
		displayInfo.getStyleClass().add("display-info");
		displayInfo.setEllipsisString("");
		VBox.setMargin(displayInfo, new Insets(10));

		typeContainer = new VBox();
		typeContainer.setAlignment(Pos.CENTER);
		typeContainer.getStyleClass().add("type-container");
		typeContainer.setMaxWidth(200);
		VBox.setMargin(typeContainer, new Insets(10));

		displayButton = new Button("Display");
		displayButton.setPrefWidth(200);
		displayButton.getStyleClass().add("types-button");

		typesTitle = new Label();
		typesTitle.getStyleClass().add("types-title");
		typesTitle.setPrefWidth(200);
		classificationButton = new Button("Classification");
		classificationButton.getStyleClass().addAll("types-button");
		classificationButton.setPrefWidth(200);
		classificationButton.setTooltip(new Tooltip("Display Classification Algorithms"));
		clusteringButton = new Button("Clustering");
		clusteringButton.getStyleClass().addAll("types-button");
		clusteringButton.getStyleClass().add("toolbar-button");
		clusteringButton.setTooltip(new Tooltip("Display Clustering Algorithms"));
		clusteringButton.setPrefWidth(200);
		typesTitle.setText("Algorithm Type");
		typeContainer.getChildren().addAll(typesTitle, classificationButton, clusteringButton, displayButton);


		algorithms = new VBox();
		algorithms.setAlignment(Pos.CENTER);
		algorithms.getStyleClass().add("algorithms");
		algorithms.setSpacing(20);
		VBox.setMargin(algorithms, new Insets(10));

		algorithmType = new Label();
		algorithmType.getStyleClass().add("algorithm-type");
		runButton = setToolbarButton(iconsPath + separator + "play.png", "Run the algorithm", false);
		runButton.setPrefSize(40, 40);
		runButton.getStyleClass().add("run-button");

		editToggleButton = new Button("Done");
		editToggleButton.getStyleClass().addAll("toggle-button", "types-button", "done");
		editToggleButton.setPrefWidth(100);

		chart = new LineChart<>(new NumberAxis(), new NumberAxis());
		chart.setTitle(manager.getPropertyValue(CHART_TITLE.name()));
		chart.setPrefSize(700, 700);

		workspace.getChildren().addAll(inputRegion, chart);
		appPane.getChildren().add(workspace);
		
		configWindow = new ConfigWindow();
	}

	/**
	 * Sets up the listeners of the controls that the user interacts with
	 */
	private void setWorkspaceActions() {

		displayButton.setOnAction(event -> {
			AppData appData = ((AppData) applicationTemplate.getDataComponent());
			
			if(!textArea.getText().trim().equals(lastDisplayedText)){
				appData.displayData();
				addDataPointListeners();
				lastDisplayedText = textArea.getText().trim();
			}

			if(chart.getData().isEmpty()){
				scrnshotButton.setDisable(true);
			}else{
				scrnshotButton.setDisable(false);
			}
		});

		runButton.setOnAction(event -> {

		});

		clusteringButton.setOnAction(event ->{
			hideAlgorithmTypes();
			showClusteringAlgorithms();
			enableRun();
		});

		classificationButton.setOnAction(event ->{
			hideAlgorithmTypes();
			showClassificationAlgorithms();
			enableRun();
		});

		editToggleButton.setOnAction(event -> {
			String curr = editToggleButton.getText();
			AppData appData = ((AppData) applicationTemplate.getDataComponent());
			AppActions appActions = ((AppActions) applicationTemplate.getActionComponent());
			if(curr.equals("Done")){
				String result = checkTextAreaText();
				if(result == null){
					editToggleButton.setText("Edit");
					setReadOnly(true);
					appData.loadData(textArea.getText());
					//hide displayed algorithms
					setUpAlgorithmTypes(appData.getLabels().size());
				}else{
					appActions.showErrorDialog("some title", result);
				}
			}else{
				hideAlgorithmTypes();
				resetAlgorithms();
				editToggleButton.setText("Done");
				setReadOnly(false);
			}
		});
	}

	public boolean isModified(){
		return false;
	}

	private String checkTextAreaText(){
		AppData appData = ((AppData) applicationTemplate.getDataComponent());
		String toTest = textArea.getText().trim();
		return appData.validateText(toTest);
	}

	/**
	 * Adds listeners to the data points inside the chart
	 */
	private void addDataPointListeners(){
		for(Series series : chart.getData()){
			for(Data point : (ObservableList<Data>) series.getData()){
				Tooltip.install(point.getNode(), new Tooltip(point.getExtraValue().toString()));

				point.getNode().setOnMouseEntered(e -> {
					getPrimaryScene().setCursor(Cursor.CROSSHAIR);
				});
				point.getNode().setOnMouseExited(e -> {
					getPrimaryScene().setCursor(Cursor.DEFAULT);
				});
			}
		}
	}

	private void initAlgorithms(){
		AppData appData = ((AppData) applicationTemplate.getDataComponent());
		
		/*
		we want each algorithm type to have a list of components
		each component comprising of a radio button, label, and regular button
		*/
		int clusteringSize = appData.clusteringAlgorithmsSize();
		clusteringAlgorithms = new ArrayList<>();
		clusteringRadios = new ToggleGroup();
		for(int i = 0; i < clusteringSize; i++){
			AlgorithmUI temp = new AlgorithmUI(i);
			clusteringAlgorithms.add(temp);
			temp.chooseAlgorithm.setToggleGroup(clusteringRadios);
		}

		// each configure button corresponds to a window
		
		int classificationSize = appData.classificationAlgorithmsSize();
		classificationAlgorithms = new ArrayList<>();
		classificationRadios = new ToggleGroup();
		for(int i = 0; i < classificationSize; i++){
			AlgorithmUI temp = new AlgorithmUI(i);
			classificationAlgorithms.add(temp);
			temp.chooseAlgorithm.setToggleGroup(classificationRadios);
		}
	}

	private class AlgorithmUI extends HBox{
		private Label algorithmName;
		private Button configButton;
		private RadioButton chooseAlgorithm;
		private ConfigWindow window;

		public AlgorithmUI(int num){
			layoutAlgorithm(num);
			setUpActions();
		}

		private void layoutAlgorithm(int num){
			algorithmName = new Label("Algorithm " + (num + 1));
			algorithmName.getStyleClass().add("algorithm-name");
			configButton = setToolbarButton(iconsPath + separator + "gears.png", "Configure Algorithm", false);
			configButton.getStyleClass().add("config-button");
			chooseAlgorithm = new RadioButton();
			window = new ConfigWindow();

			this.getChildren().addAll(chooseAlgorithm, algorithmName, configButton);
			this.getStyleClass().add("algorithm-ui");
			this.setAlignment(Pos.CENTER);
			this.setSpacing(15);
		}

		private void setUpActions(){

			configButton.setOnAction(event -> {
				this.window.show();
			});
			
			RotateTransition rot = new RotateTransition(Duration.seconds(2), configButton);
			rot.setCycleCount(Animation.INDEFINITE);
			rot.setByAngle(360);
			configButton.setOnMouseEntered(event -> {
				rot.play();
			});
			configButton.setOnMouseExited(event -> {
				rot.stop();
			});

		}
	}

	/**
	 * This class is responsible for showing a window to the user
	 * The values inputted inside the window is extracted to create a Config object
	 */
	private class ConfigWindow extends Stage{

		private Label sceneHeader;
		private TextField iterationField; // TextField for entering the number of iterations
		private Label iterationLabel;
		private HBox iterationContainer;

		private TextField intervalField; // TextField for entering the number of intervals
		private Label intervalLabel;
		private HBox intervalContainer;

		private TextField numLabelsField; // TextField for the number of labels
		private Label numLabelsLabel;
		private HBox numLabelsContainer;

		private CheckBox continuousCheck; // CheckBox whether the algorithm runs continuous or not
		private Label checkBoxLabel;
		private HBox checkBoxContainer;

		private Scene currentScene;
		private VBox container;

		public ConfigWindow(){
			layout();
			this.setTitle("Configure Algorithm");
			this.setScene(currentScene);
		}

		/**
		 *  Lays out the UI display of the Configuration Window
		 */
		public void layout(){
			container = new VBox();
			Insets insets = new Insets(20);
			container.setPadding(insets);
			container.setAlignment(Pos.CENTER);
			currentScene = new Scene(container);

			sceneHeader = new Label("Algorithm Run Configuration");

			iterationField = new TextField();
			iterationLabel = new Label("Max Iterations:");
			iterationContainer = new HBox();
			iterationContainer.setPadding(insets);
			iterationContainer.getChildren().addAll(iterationLabel, iterationField);

			intervalField = new TextField();
			intervalLabel = new Label("Update Interval:");
			intervalContainer = new HBox();
			intervalContainer.setPadding(insets);
			intervalContainer.getChildren().addAll(intervalLabel, intervalField);

			numLabelsField = new TextField();
			numLabelsLabel = new Label("Number of Labels:");
			numLabelsContainer = new HBox();
			numLabelsContainer.setPadding(insets);
			numLabelsContainer.getChildren().addAll(numLabelsLabel, numLabelsField);

			continuousCheck = new CheckBox();
			checkBoxLabel = new Label("Continuous Run?");
			checkBoxContainer = new HBox();
			checkBoxContainer.setPadding(insets);
			checkBoxContainer.getChildren().addAll(checkBoxLabel, continuousCheck);


			container.getChildren().addAll(sceneHeader, iterationContainer, intervalContainer, numLabelsContainer, checkBoxContainer);

			//may not need to add labels field
		}


		/**
		 * Returns true if the input the user entered is valid
		 * Returns false otherwise
		 * @return if input is valid
		 */
		public boolean checkInput(){
			return false;
		}

		/**
		 * Shows the label TextField inside the Config Window
		 */
		public void showLabelsField(){

		}

		/**
		 * Create a Configuration based on the input
		 */
		private void createConfig(){
			
		}
	}
	
}