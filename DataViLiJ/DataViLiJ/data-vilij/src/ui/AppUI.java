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
import algorithms.AlgorithmTypes;
import data.Config;
import dataprocessors.AppData;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javafx.animation.Animation;
import javafx.animation.RotateTransition;
import javafx.collections.ObservableList;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
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
	private VBox classificationContainer; // container for the algorithms
	private VBox clusteringContainer;

	private TextArea textArea;       // text area for new data input
	private LineChart<Number, Number> chart;          // the chart where data will be displayed
	private Button scrnshotButton; // toolbar button to take a screenshot of the data
	private Button editToggleButton; // button that toggles between edit and done
	private Button runButton; // button for running alogrithm
	private Button classificationButton;
	private Button clusteringButton;
	private Button displayButton;
	private Button backButton;
	private Label typesTitle;
	private String algorithmType;
	private Label clusteringType;
	private Label classificationType;
	private Label displayInfo;
	private List<AlgorithmUI> clusteringAlgorithms;
	private List<AlgorithmUI> classificationAlgorithms;
	private ToggleGroup clusteringRadios;
	private ToggleGroup classificationRadios;

	private String iconsPath;

	private String displayedText; // text to check if current text matches saved text
	private String savedText;

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
		displayedText = null;
		savedText = null;
		disableSaveButton();
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

	public boolean textAreaShown(){
		return inputRegion.getChildren().contains(textArea);
	}

	public boolean isDifferentFromSaved(){
		return !textArea.getText().trim().equals(savedText);
	}

	private boolean isDifferentFromDisplayed(){
		return !textArea.getText().trim().equals(displayedText);
	}

	/**
	 * Sets the current text as the last saved text
	 */
	public void setSavedText(){
		this.savedText = textArea.getText().trim();
	}

	public String getSavedText(){
		return this.savedText;
	}
	
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
		inputRegion.getChildren().remove(displayInfo);
		AppData appData = (AppData) applicationTemplate.getDataComponent();
		StringBuilder builder = new StringBuilder();
		Set labels = appData.getLabels();

		builder.append(numInstances + " instances with " + labels.size());
		if(source != null){
			builder.append(" labels loaded from " + source +".\n");
		}else{
			builder.append(" labels.\n");
		}
		
		if(labels.size() > 0){
			builder.append("The labels are:\n");
			labels.forEach(label -> {
				builder.append("\t- " + label.toString() + "\n");
			});
		}

		displayInfo.setText(builder.toString() + "\n");

		inputRegion.getChildren().add(displayInfo);
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
	 * Hides the algorithm types
	 */
	private void hideAlgorithmTypes(){
		inputRegion.getChildren().remove(typeContainer);
	}

	/**
	 * Shows the possible clustering algorithms
	 */
	public void showClusteringAlgorithms(){
		algorithmType = "Clustering";
		((AppData) applicationTemplate.getDataComponent()).setAlgorithmType(AlgorithmTypes.CLUSTERING);
		if(clusteringContainer.getChildren().isEmpty()){
			clusteringContainer.getChildren().add(clusteringType);
			for(int i = 0; i < clusteringAlgorithms.size(); i++){
				clusteringContainer.getChildren().add(clusteringAlgorithms.get(i));
			}
		}
		inputRegion.getChildren().add(clusteringContainer);
	}

	/**
	 * Shows the possible classification algorithms
	 */
	public void showClassificationAlgorithms(){
		algorithmType = "Classification";
		((AppData) applicationTemplate.getDataComponent()).setAlgorithmType(AlgorithmTypes.CLASSIFICATION);
		if(classificationContainer.getChildren().isEmpty()){
			classificationContainer.getChildren().add(classificationType);
			for(int i = 0; i < classificationAlgorithms.size(); i++){
				classificationContainer.getChildren().add(classificationAlgorithms.get(i));
			}
		}
		inputRegion.getChildren().add(classificationContainer);
	}

	private void resetAlgorithms(){
		inputRegion.getChildren().removeAll(classificationContainer, clusteringContainer);
		classificationContainer.getChildren().clear();
		clusteringContainer.getChildren().clear();
	}

	/**
	 * Shows the run button
	 */
	public void showRun(){
		if(((AppData) applicationTemplate.getDataComponent()).getAlgorithmType().equals(AlgorithmTypes.CLASSIFICATION)){
			classificationContainer.getChildren().add(runButton);
		}else{
			clusteringContainer.getChildren().add(runButton);
		}
	}

	/**
	 * Hides the run button
	 */
	public void hideRun(){
		classificationContainer.getChildren().remove(runButton);
		clusteringContainer.getChildren().remove(runButton);
	}

	public void setUpAlgorithmTypes(int numLabels){
		if(numLabels < 2){
			classificationButton.setDisable(true);
		}else{
			classificationButton.setDisable(false);
		}
		inputRegion.getChildren().add(typeContainer);
	}

	private void showBackButton(){
		inputRegion.getChildren().add(backButton);
	}

	private void hideBackButton(){
		inputRegion.getChildren().remove(backButton);
	}

	private void resetToggles(){
		Toggle selected = classificationRadios.getSelectedToggle();
		if(selected != null){
			selected.setSelected(false);
		}
		selected = clusteringRadios.getSelectedToggle();
		if(selected != null){
			selected.setSelected(false);
		}
	}

	private void resetInputRegion(){
		classificationContainer.getChildren().clear();
		clusteringContainer.getChildren().clear();
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
		
		backButton = setToolbarButton(iconsPath + separator + "back-arrow.png", "Return to Algorithm Types", false);
		backButton.setPrefWidth(50);
		backButton.getStyleClass().add("algorithm-ui");

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


		classificationContainer = new VBox();
		classificationContainer.setAlignment(Pos.CENTER);
		classificationContainer.getStyleClass().add("algorithms");
		classificationContainer.setSpacing(20);
		VBox.setMargin(classificationContainer, new Insets(10));
		clusteringContainer = new VBox();
		clusteringContainer.setAlignment(Pos.CENTER);
		clusteringContainer.getStyleClass().add("algorithms");
		clusteringContainer.setSpacing(20);
		VBox.setMargin(clusteringContainer, new Insets(10));

		algorithmType = "";
		classificationType = new Label();
		classificationType.getStyleClass().add("algorithm-type");
		classificationType.setText("Classification");
		clusteringType = new Label();
		clusteringType.getStyleClass().add("algorithm-type");
		clusteringType.setText("Clustering");

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
			
			if(isDifferentFromDisplayed()){
				chart.getData().clear();
				appData.displayData();
				addDataPointListeners();
				displayedText = textArea.getText().trim();
			}

			if(chart.getData().isEmpty()){
				scrnshotButton.setDisable(true);
			}else{
				scrnshotButton.setDisable(false);
			}
		});

		backButton.setOnAction(event -> {
			hideBackButton();
			//reset app data algorithm type
			inputRegion.getChildren().removeAll(classificationContainer, clusteringContainer);
			inputRegion.getChildren().add(typeContainer);
			resetToggles();
			hideRun();
		});

		runButton.setOnAction(event -> {
			AppData appData = (AppData) applicationTemplate.getDataComponent();
			// will have to reset somewhere i think (when new or loaded)
			Toggle selected;

			if(appData.getAlgorithmType().equals(AlgorithmTypes.CLASSIFICATION)){
				selected = classificationRadios.getSelectedToggle();
			}else{
				selected = clusteringRadios.getSelectedToggle();
			}
			//need to call appData.isConfigured somewhere
			appData.startAlgorithm();
		});

		clusteringButton.setOnAction(event ->{
			hideAlgorithmTypes();
			showClusteringAlgorithms();
			runButton.setDisable(true);
			showBackButton();
			classificationRadios.selectToggle(null);
			// other toggle group --> deselect buttons
			/*
			bug -->run button automatically disablee
			*/
		});

		classificationButton.setOnAction(event ->{
			hideAlgorithmTypes();
			showClassificationAlgorithms();
			runButton.setDisable(true);
			showBackButton();
			clusteringRadios.selectToggle(null);
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
					setUpAlgorithmTypes(appData.getLabels().size());
				}else{
					appActions.showErrorDialog("some title", result);
				}
			}else{
				hideAlgorithmTypes();
				resetAlgorithms();
				hideBackButton();
				editToggleButton.setText("Done");
				setReadOnly(false);
			}
		});

		textArea.textProperty().addListener(event -> {
			if((savedText == null && textArea.getText().isEmpty()) || (!isDifferentFromSaved())){
				disableSaveButton();
			}else if(isDifferentFromSaved()){
				saveButton.setDisable(false);
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
		int clusteringSize = appData.getNumClusteringAlgorithms();
		clusteringAlgorithms = new ArrayList<>();
		clusteringRadios = new ToggleGroup();
		for(int i = 0; i < clusteringSize; i++){
			AlgorithmUI temp = new AlgorithmUI(i);
			clusteringAlgorithms.add(temp);
			temp.chooseAlgorithm.setToggleGroup(clusteringRadios);
		}

		// each configure button corresponds to a window
		
		int classificationSize = appData.getNumClassificationAlgorithms();
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
		private int index;

		public AlgorithmUI(int index){
			this.index = index;
			layoutAlgorithm();
			setUpActions();
		}

		private void layoutAlgorithm(){
			algorithmName = new Label("Algorithm " + (index + 1));
			algorithmName.getStyleClass().add("algorithm-name");
			configButton = setToolbarButton(iconsPath + separator + "gears.png", "Configure Algorithm", false);
			configButton.getStyleClass().add("config-button");
			chooseAlgorithm = new RadioButton();
			chooseAlgorithm.setUserData(false);
			window = new ConfigWindow();

			if(algorithmType.equals("Classification")){
				chooseAlgorithm.setToggleGroup(classificationRadios);
			}else if(algorithmType.equals("Clustering")){
				chooseAlgorithm.setToggleGroup(clusteringRadios);
			}

			this.getChildren().addAll(chooseAlgorithm, algorithmName, configButton);
			this.getStyleClass().add("algorithm-ui");
			this.setAlignment(Pos.CENTER);
			this.setSpacing(15);
		}

		private boolean isConfigured(){
			return (boolean) chooseAlgorithm.getUserData();
		}

		private void testForConfiguration(){
			if(isConfigured()){
				runButton.setDisable(false);
			}
		}

		private void setUpActions(){

			configButton.setOnAction(event -> {
				if(((AppData) applicationTemplate.getDataComponent()).getAlgorithmType().equals(AlgorithmTypes.CLASSIFICATION)){
					window.hideLabelField();
				}else{
					window.showLabelField();
				}
				window.showAndWait();
				chooseAlgorithm.setUserData(true);
				testForConfiguration();
			});

			chooseAlgorithm.setOnAction(event -> {
				((AppData) applicationTemplate.getDataComponent()).setAlgorithmToRun(index);
				if(chooseAlgorithm.isSelected()){
					showRun();
					testForConfiguration();
				}else{
					hideRun();
				}
				//might have to fix for more algorithms
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
		private BorderPane iterationContainer;

		private TextField intervalField; // TextField for entering the number of intervals
		private Label intervalLabel;
		private BorderPane intervalContainer;

		private TextField numLabelsField; // TextField for the number of labels
		private Label numLabelsLabel;
		private BorderPane numLabelsContainer;

		private CheckBox continuousCheck; // CheckBox whether the algorithm runs continuous or not
		private Label checkBoxLabel;
		private BorderPane checkBoxContainer;

		private Scene currentScene;
		private VBox container;

		private Config config;

		public ConfigWindow(){
			initModality(Modality.WINDOW_MODAL); // modal => messages are blocked from reaching other windows
        		initOwner(getPrimaryWindow());
			layout();
			setUpActions();
			this.setTitle("Configure Algorithm");
			this.setScene(currentScene);
			this.getScene().getStylesheets().add(getClass().getResource(cssPath).toExternalForm());
		}

		/**
		 *  Lays out the UI display of the Configuration Window
		 */
		private void layout(){
			container = new VBox();
			Insets insets = new Insets(20);
			container.setPadding(insets);
			container.setAlignment(Pos.CENTER);
			currentScene = new Scene(container);

			sceneHeader = new Label("Algorithm Run Configuration");
			sceneHeader.getStyleClass().add("config-window-title");

			iterationField = new TextField();
			iterationField.setPrefWidth(50);
			iterationLabel = new Label("Max Iterations:");
			iterationLabel.getStyleClass().add("field-name");
			iterationContainer = new BorderPane();
			iterationContainer.setPrefWidth(300);
			iterationContainer.setPadding(insets);
			iterationContainer.setLeft(iterationLabel);
			iterationContainer.setRight(iterationField);

			intervalField = new TextField();
			intervalField.setPrefWidth(50);
			intervalLabel = new Label("Update Interval:");
			intervalLabel.getStyleClass().add("field-name");
			intervalContainer = new BorderPane();
			intervalContainer.setPadding(insets);
			intervalContainer.setLeft(intervalLabel);
			intervalContainer.setRight(intervalField);

			numLabelsField = new TextField();
			numLabelsField.setPrefWidth(50);
			numLabelsLabel = new Label("Number of Labels:");
			numLabelsLabel.getStyleClass().add("field-name");
			numLabelsContainer = new BorderPane();
			numLabelsContainer.setPadding(insets);
			numLabelsContainer.setLeft(numLabelsLabel);
			numLabelsContainer.setRight(numLabelsField);

			continuousCheck = new CheckBox();
			checkBoxLabel = new Label("Continuous Run?");
			checkBoxLabel.getStyleClass().add("field-name");
			checkBoxContainer = new BorderPane();
			checkBoxContainer.setPadding(insets);
			checkBoxContainer.setLeft(checkBoxLabel);
			checkBoxContainer.setRight(continuousCheck);

			container.getChildren().addAll(sceneHeader, iterationContainer, intervalContainer, numLabelsContainer, checkBoxContainer);
		}

		private void setUpActions(){
			this.setOnCloseRequest(event -> {
				createConfig();
				((AppData) applicationTemplate.getDataComponent()).setConfiguration(config);
			});
		}

		private void hideLabelField(){
			container.getChildren().remove(numLabelsContainer);
		}

		private void showLabelField(){
			if(!container.getChildren().contains(numLabelsContainer)){
				container.getChildren().clear();
				container.getChildren().addAll(sceneHeader, iterationContainer, intervalContainer, numLabelsContainer, checkBoxContainer);
			}
		}


		/**
		 * Returns true if the input the user entered is valid
		 * Returns false otherwise
		 * @return if input is valid
		 */
		private boolean checkInput(AlgorithmTypes type){
			// no negative values or some shit

			//also have to check if num labels present or not
			return true;
		}

		/**
		 * Create a Configuration based on the input
		 */

		/*
		FIXME must depend on type
		*/
		private void createConfig(){
			if(((AppData) applicationTemplate.getDataComponent()).getAlgorithmType().equals(AlgorithmTypes.CLUSTERING)){
				try{
					int maxIterations = Integer.parseInt(iterationField.getText());
					int updateInterval = Integer.parseInt(intervalField.getText());
					boolean toContinue = continuousCheck.isSelected();
					int numLabels = Integer.parseInt(numLabelsField.getText());
					if(checkInput(AlgorithmTypes.CLUSTERING)){
						config = new Config(maxIterations, updateInterval, toContinue, numLabels);
						iterationField.setText("" + maxIterations);
						intervalField.setText("" + updateInterval);
						continuousCheck.setSelected(toContinue);
						numLabelsField.setText("" + numLabels);
					}else{
						handleInvalidConfig(AlgorithmTypes.CLUSTERING);
					}
				}catch(NumberFormatException e){
					handleInvalidConfig(AlgorithmTypes.CLUSTERING);
				}
			}else{
				try{
					int maxIterations = Integer.parseInt(iterationField.getText());
					int updateInterval = Integer.parseInt(intervalField.getText());
					boolean toContinue = continuousCheck.isSelected();
					if(checkInput(AlgorithmTypes.CLASSIFICATION)){
						config = new Config(maxIterations, updateInterval, toContinue);
						iterationField.setText("" + maxIterations);
						intervalField.setText("" + updateInterval);
						continuousCheck.setSelected(toContinue);
					}else{
						handleInvalidConfig(AlgorithmTypes.CLASSIFICATION);
					}
				}catch(NumberFormatException e){
					handleInvalidConfig(AlgorithmTypes.CLASSIFICATION);
				}
			}
		}

		private void handleInvalidConfig(AlgorithmTypes type){
			AppActions appActions = (AppActions) applicationTemplate.getActionComponent();
			appActions.showErrorDialog("Invalid values", "Values for configuration are invalid. Default values have been inserted.");
			int tempIteration = 1;
			int tempInterval = 1;
			boolean tempContinuous = true;
			int tempLabels = 2;
			if(type.equals(AlgorithmTypes.CLUSTERING)){
				config = new Config(tempIteration, tempInterval, tempContinuous, tempLabels);
				numLabelsField.setText("" + tempLabels);
			}else{
				config = new Config(tempIteration, tempInterval, tempContinuous);
			}
			iterationField.setText("" + tempIteration);
			intervalField.setText("" + tempInterval);
			continuousCheck.setSelected(tempContinuous);
		}
	}
	
}