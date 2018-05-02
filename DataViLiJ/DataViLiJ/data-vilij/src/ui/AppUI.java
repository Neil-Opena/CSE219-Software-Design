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
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
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
	private Stage algorithmRunWindow;
	private VBox algorithmRunContainer;
	private ProgressIndicator algorithmProgress;
	private Label algorithmRunTitle;
	private Label algorithmRunInfo;
	private Label messageAlgorithmRunInfo;


	private String iconsPath;

	private String savedText;

	public AppUI(Stage primaryStage, ApplicationTemplate applicationTemplate) {
		super(primaryStage, applicationTemplate);
		this.applicationTemplate = applicationTemplate;
		cssPath = "/" + String.join(separator,
                                    manager.getPropertyValue(GUI_RESOURCE_PATH.name()),
                                    manager.getPropertyValue(CSS_RESOURCE_PATH.name()),
                                    manager.getPropertyValue(CSS_FILE.name()));
		this.getPrimaryScene().getStylesheets().add(getClass().getResource(cssPath).toExternalForm());

		primaryStage.setTitle(manager.getPropertyValue(APPLICATION_TITLE.name()));
		setAlgorithmWindow();
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
		toolBar.getStyleClass().add(manager.getPropertyValue(TOOLBAR.name()));
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
		resetInputRegion();
		resetRadioUserData();
		textArea.clear();
		scrnshotButton.setDisable(true);
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

	/**
	 * Returns if the input region is currently showing the text area
	 * @return true if text area showing
	 */
	public boolean textAreaShown(){
		return inputRegion.getChildren().contains(textArea);
	}

	/**
	 * Tests to see if the current text is different from the text
	 * that was most recently saved
	 * @return true if different from saved text
	 */
	public boolean isDifferentFromSaved(){
		/*
		text area is empty, and file has not been saved yet
		*/
		if(textArea.getText().trim().isEmpty() && savedText == null){
			return false;
		}
		return !textArea.getText().trim().equals(savedText);
	}

	/**
	 * Sets the current text as the most recent saved text
	 */
	public void setSavedText(){
		this.savedText = textArea.getText().trim();
	}

	/**
	 * Returns the text that was most recently saved
	 * @return String of most recently saved text
	 */
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
	 * @param source the source of the file (fileName)
	 */
	public void displayInfo(int numInstances, String source){
		inputRegion.getChildren().remove(displayInfo);
		AppData appData = (AppData) applicationTemplate.getDataComponent();
		StringBuilder builder = new StringBuilder();
		Set labels = appData.getLabels();

		builder.append(numInstances + manager.getPropertyValue(INFO_1.name()) + labels.size());
		if(source != null){
			builder.append(manager.getPropertyValue(INFO_2.name()) + source +".\n");
		}else{
			builder.append(manager.getPropertyValue(INFO_3.name()));
		}
		
		if(labels.size() > 0){
			builder.append(manager.getPropertyValue(INFO_4.name()));
			labels.forEach(label -> {
				builder.append("\t- " + label.toString() + "\n");
			});
		}

		displayInfo.setText(builder.toString() + "\n");

		inputRegion.getChildren().add(displayInfo);
	}

	/**
	 * Displays the text area to the user
	 * Mainly used when initiating the application
	 * Input region is reset prior to displaying the text area
	 */
	public void showTextArea(){
		inputRegion.getChildren().addAll(inputTitle, textArea);
	}


	/**
	 * Displays the edit toggle button to the user
	 * Default shows the toggle being set to DONE
	 */
	public void showEditToggle(){
		inputRegion.getChildren().add(editToggleButton);
		editToggleButton.setText(manager.getPropertyValue(DONE.name()));
		setReadOnly(false);
	}

	public void enableEditToggle(){
		editToggleButton.setDisable(false);
	}

	public void disableEditToggle(){
		editToggleButton.setDisable(true);
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
		algorithmType = AlgorithmTypes.CLUSTERING.toString();
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
		algorithmType = AlgorithmTypes.CLASSIFICATION.toString();
		((AppData) applicationTemplate.getDataComponent()).setAlgorithmType(AlgorithmTypes.CLASSIFICATION);
		if(classificationContainer.getChildren().isEmpty()){
			classificationContainer.getChildren().add(classificationType);
			for(int i = 0; i < classificationAlgorithms.size(); i++){
				classificationContainer.getChildren().add(classificationAlgorithms.get(i));
			}
		}
		inputRegion.getChildren().add(classificationContainer);
	}

	public void disableAlgorithmChanges(){
		//disable choosing another algorithm
		//disable configuring algorithm
		if(((AppData) applicationTemplate.getDataComponent()).getAlgorithmType().equals(AlgorithmTypes.CLASSIFICATION)){
			for(int i = 0; i < classificationAlgorithms.size(); i++){
				classificationAlgorithms.get(i).configButton.setDisable(true);
				classificationAlgorithms.get(i).chooseAlgorithm.setDisable(true);
			}
		}else{
			for(int i = 0; i < clusteringAlgorithms.size(); i++){
				clusteringAlgorithms.get(i).configButton.setDisable(true);
				clusteringAlgorithms.get(i).chooseAlgorithm.setDisable(true);
			}

		}
	}

	public void enableAlgorithmChanges(){
		if(((AppData) applicationTemplate.getDataComponent()).getAlgorithmType().equals(AlgorithmTypes.CLASSIFICATION)){
			for(int i = 0; i < classificationAlgorithms.size(); i++){
				classificationAlgorithms.get(i).configButton.setDisable(false);
				classificationAlgorithms.get(i).chooseAlgorithm.setDisable(false);
			}
		}else{
			for(int i = 0; i < clusteringAlgorithms.size(); i++){
				clusteringAlgorithms.get(i).configButton.setDisable(false);
				clusteringAlgorithms.get(i).chooseAlgorithm.setDisable(false);
			}

		}

	}

	/**
	 * Hides the shown algorithms and resets the algorithms of each respective
	 * type
	 */
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

	public void disableRun(){
		scrnshotButton.setDisable(true); //also disables screen shot button
		runButton.setDisable(true);
	}

	public void enableRun(){
		if(!chart.getData().isEmpty()){
			scrnshotButton.setDisable(false); //enables screen shot button if chart is not empty
		}
		runButton.setDisable(false);
	}

	/**
	 * Hides the run button
	 */
	public void hideRun(){
		classificationContainer.getChildren().remove(runButton);
		clusteringContainer.getChildren().remove(runButton);
	}

	/**
	 * Tests to see if user can select certain type based on labels of data
	 * Disables classification type if the number of labels < 2
	 * @param numLabels the number of labels of the data
	 */
	public void setUpAlgorithmTypes(int numLabels){
		if(numLabels < 2){
			classificationButton.setDisable(true);
		}else{
			classificationButton.setDisable(false);
		}
		inputRegion.getChildren().add(typeContainer);
	}

	public void disableBackButton(){
		backButton.setDisable(true);
	}

	public void enableBackButton(){
		backButton.setDisable(false);
	}

	/**
	 * Shows the back button to the user
	 */
	private void showBackButton(){
		inputRegion.getChildren().add(backButton);
	}

	/**
	 * Hides the back button
	 */
	private void hideBackButton(){
		inputRegion.getChildren().remove(backButton);
	}

	/**
	 * Reset the toggle groups by un-selecting the selected toggles. 
	 * In this case, the radio buttons
	 */
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

	private void resetRadioUserData(){
		classificationRadios.getToggles().forEach(action -> {
			action.setUserData(false);
		});
		clusteringRadios.getToggles().forEach(action -> {
			action.setUserData(false);
		});
		/*
		user data is used to check if the current radio button = algorithm has been configured
		*/
		
	}

	/**
	 * Resets the whole input region, including the text area
	 */
	private void resetInputRegion(){
		resetAlgorithms();
		resetToggles();
		//run button should be disabled or some shit
		//should reset user data of user
		classificationAlgorithms.forEach(action -> {
			action.window.resetConfigWindow();
		});
		clusteringAlgorithms.forEach(action -> {
			action.window.resetConfigWindow();
		});
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

	private void setAlgorithmWindow(){
		algorithmRunContainer = new VBox();
		algorithmRunContainer.setSpacing(20);
		algorithmRunContainer.setAlignment(Pos.CENTER);
		algorithmRunWindow = new Stage();

		algorithmRunTitle = new Label("Algorithm Iterations");
		algorithmRunTitle.getStyleClass().add(manager.getPropertyValue(CONFIG_TITLE_CSS.name()));
		algorithmProgress = new ProgressIndicator();
		algorithmProgress.getStyleClass().add("progress");
		algorithmRunInfo = new Label("");
		messageAlgorithmRunInfo = new Label("");

		algorithmRunContainer.getChildren().addAll(algorithmRunTitle,algorithmProgress, algorithmRunInfo, messageAlgorithmRunInfo);
		algorithmRunWindow.setScene(new Scene(algorithmRunContainer));
		algorithmRunWindow.getScene().getStylesheets().add(getClass().getResource(cssPath).toExternalForm());
		algorithmRunWindow.setMinHeight(250);
		algorithmRunWindow.setMinWidth(300);
		algorithmRunWindow.setResizable(false);
		algorithmRunWindow.setAlwaysOnTop(true);
		algorithmRunWindow.setTitle("Running");
	}

	private void resetAlgorithmRunWindow(){
		algorithmProgress.setProgress(0);
		algorithmRunInfo.setText("");
		messageAlgorithmRunInfo.setText("");
	};

	public void appendAlgorithmRunWindow(String message){
		messageAlgorithmRunInfo.setText(message);
	}

	public void showAlgorithmRunWindow(){
		resetAlgorithmRunWindow();
		algorithmRunWindow.show();
	}

	public void closeAlgorithmRunWindow(){
		algorithmRunWindow.close();
	}

	public void updateAlgorithmRunWindow(double percent, String info){
		algorithmProgress.setProgress(percent);
		algorithmRunInfo.setText(info);
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
		displayInfo.getStyleClass().add(manager.getPropertyValue(DISPLAY_INFO.name()));
		displayInfo.setEllipsisString("");
		VBox.setMargin(displayInfo, new Insets(10));

		typeContainer = new VBox();
		typeContainer.setAlignment(Pos.CENTER);
		typeContainer.getStyleClass().add(manager.getPropertyValue(TYPE_CONTAINER.name()));
		typeContainer.setMaxWidth(200);
		VBox.setMargin(typeContainer, new Insets(10));

		backButton = setToolbarButton(iconsPath + separator + manager.getPropertyValue(BACK_ICON.name()), manager.getPropertyValue(BACK_TOOLTIP.name()), false);
		backButton.setPrefWidth(50);
		backButton.getStyleClass().add(manager.getPropertyValue(ALGORITHM_UI.name()));

		typesTitle = new Label();
		typesTitle.getStyleClass().add(manager.getPropertyValue(TYPES_TITLE.name()));
		typesTitle.setPrefWidth(200);
		classificationButton = new Button(AlgorithmTypes.CLASSIFICATION.toString());
		classificationButton.getStyleClass().addAll(manager.getPropertyValue(TYPES_BUTTON.name()));
		classificationButton.setPrefWidth(200);
		classificationButton.setTooltip(new Tooltip(manager.getPropertyValue(CLASSIFICATION_TOOLTIP.name())));
		clusteringButton = new Button(AlgorithmTypes.CLUSTERING.toString());
		clusteringButton.getStyleClass().addAll(manager.getPropertyValue(TYPES_BUTTON.name()));
		clusteringButton.setTooltip(new Tooltip(manager.getPropertyValue(CLUSTERING_TOOLTIP.name())));
		clusteringButton.setPrefWidth(200);
		typesTitle.setText(manager.getPropertyValue(ALGORITHM_TYPE.name()));
		typeContainer.getChildren().addAll(typesTitle, classificationButton, clusteringButton);


		classificationContainer = new VBox();
		classificationContainer.setAlignment(Pos.CENTER);
		classificationContainer.getStyleClass().add(manager.getPropertyValue(ALGORITHMS_CSS.name()));
		classificationContainer.setSpacing(20);
		VBox.setMargin(classificationContainer, new Insets(10));
		clusteringContainer = new VBox();
		clusteringContainer.setAlignment(Pos.CENTER);
		clusteringContainer.getStyleClass().add(manager.getPropertyValue(ALGORITHMS_CSS.name()));
		clusteringContainer.setSpacing(20);
		VBox.setMargin(clusteringContainer, new Insets(10));

		algorithmType = "";
		classificationType = new Label();
		classificationType.getStyleClass().add(manager.getPropertyValue(ALGORITHM_TYPE_CSS.name()));
		classificationType.setText(AlgorithmTypes.CLASSIFICATION.toString());
		clusteringType = new Label();
		clusteringType.getStyleClass().add(manager.getPropertyValue(ALGORITHM_TYPE_CSS.name()));
		clusteringType.setText(AlgorithmTypes.CLUSTERING.toString());

		runButton = setToolbarButton(iconsPath + separator + manager.getPropertyValue(PLAY_ICON.name()), manager.getPropertyValue(PLAY_TOOLTIP.name()), false);
		runButton.setPrefSize(40, 40);
		runButton.getStyleClass().add(manager.getPropertyValue(RUN_BUTTON.name()));

		editToggleButton = new Button(manager.getPropertyValue(DONE.name()));
		editToggleButton.getStyleClass().addAll(manager.getPropertyValue(TOGGLE_BUTTON.name()), manager.getPropertyValue(TYPES_BUTTON.name()));
		editToggleButton.setPrefWidth(100);

		chart = new LineChart<>(new NumberAxis(), new NumberAxis());
		chart.setTitle(manager.getPropertyValue(CHART_TITLE.name()));
		chart.setPrefSize(700, 700);
		chart.setAnimated(false); //prevent line from shifting axis

		workspace.getChildren().addAll(inputRegion, chart);
		appPane.getChildren().add(workspace);
	}

	/**
	 * Sets up the listeners of the controls that the user interacts with
	 */
	private void setWorkspaceActions() {

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
			if(!appData.isRunning()){
				Toggle selected;
				if(appData.getAlgorithmType().equals(AlgorithmTypes.CLASSIFICATION)){
					selected = classificationRadios.getSelectedToggle();
				}else{
					selected = clusteringRadios.getSelectedToggle();
				}
				appData.startAlgorithm();
			}else{
				appData.continueAlgorithm();
			}

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
			if(curr.equals(manager.getPropertyValue(DONE.name()))){
				String result = checkTextAreaText();
				if(result == null){
					editToggleButton.setText(manager.getPropertyValue(EDIT.name()));
					setReadOnly(true);
					appData.loadData(textArea.getText());
					appData.displayData();
					if(chart.getData().isEmpty()){
						scrnshotButton.setDisable(true);
					}else{
						scrnshotButton.setDisable(false);
					}
					setUpAlgorithmTypes(appData.getLabels().size());
				}else{
					appActions.showErrorDialog(manager.getPropertyValue(INVALID_DATA_TITLE.name()), result);
				}
			}else{
				hideAlgorithmTypes();
				resetAlgorithms();
				hideBackButton();
				editToggleButton.setText(manager.getPropertyValue(DONE.name()));
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


	/**
	 * Validates the current text in the text area if it satisfies the
	 * specified tsd format
	 * @return 
	 */
	private String checkTextAreaText(){
		AppData appData = ((AppData) applicationTemplate.getDataComponent());
		String toTest = textArea.getText().trim();
		return appData.validateText(toTest);
	}

	/**
	 * Initiates the algorithms by forming a ui component for each one
	 */
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

	/**
	 * This objects of this class represents an algorithm that the user can 
	 * select from. Each object can be selected or configured.
	 */
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

		/**
		 * Lays out the UI display of the algorithm choice
		 */
		private void layoutAlgorithm(){
			//AppData appData = (AppData) applicationTemplate.getDataComponent();
			//appData.getAlgorithmName(index);
			algorithmName = new Label(manager.getPropertyValue(ALGORITHM.name()) + (index + 1));
			algorithmName.getStyleClass().add(manager.getPropertyValue(ALGORITHM_NAME_CSS.name()));
			configButton = setToolbarButton(iconsPath + separator + manager.getPropertyValue(GEAR_ICON.name()), manager.getPropertyValue(CONFIG_TOOLTIP.name()), false);
			configButton.getStyleClass().add(manager.getPropertyValue(CONFIG_BUTTON.name()));
			chooseAlgorithm = new RadioButton();
			chooseAlgorithm.setUserData(false);
			window = new ConfigWindow();

			if(algorithmType.equals(AlgorithmTypes.CLASSIFICATION.toString())){
				chooseAlgorithm.setToggleGroup(classificationRadios);
			}else if(algorithmType.equals(AlgorithmTypes.CLUSTERING.toString())){
				chooseAlgorithm.setToggleGroup(clusteringRadios);
			}

			this.getChildren().addAll(chooseAlgorithm, algorithmName, configButton);
			this.getStyleClass().add(manager.getPropertyValue(ALGORITHM_UI.name()));
			this.setAlignment(Pos.CENTER);
			this.setSpacing(15);
		}

		/**
		 * Returns whether the algorithm chosen has been configured
		 * @return 
		 */
		private boolean isConfigured(){
			return (boolean) chooseAlgorithm.getUserData();
		}

		/**
		 * Tests to see if the current algorithm has been configured.
		 * Affects the display of the run button
		 */
		private void testForConfiguration(){
			if(isConfigured()){
				runButton.setDisable(false);
			}
		}

		/**
		 * Sets up the event handlers of the algorithm UI display
		 */
		private void setUpActions(){
			AppData appData = (AppData) applicationTemplate.getDataComponent();
			configButton.setOnAction(event -> {
				if(appData.getAlgorithmType().equals(AlgorithmTypes.CLASSIFICATION)){
					window.hideLabelField();
				}else{
					window.showLabelField();
				}
				window.showAndWait();
				appData.setConfiguration(window.config);
				chooseAlgorithm.setUserData(true);
				testForConfiguration();
			});

			chooseAlgorithm.setOnAction(event -> {
				appData.setAlgorithmToRun(index);
				if(chooseAlgorithm.isSelected()){
					showRun();
					testForConfiguration();
					appData.displayData();
					resetAlgorithmRunWindow(); //reset for when additional information from other algorithm is shown
				}else{
					hideRun();
				}
				//might have to fix for more algorithms
				/*
				when an algorithm is selected, get the created config and pass to app data
				*/
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
			this.setTitle(manager.getPropertyValue(CONFIG_TITLE.name()));
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

			sceneHeader = new Label(manager.getPropertyValue(CONFIG_WINDOW_TITLE.name()));
			sceneHeader.getStyleClass().add(manager.getPropertyValue(CONFIG_TITLE_CSS.name()));

			iterationField = new TextField();
			iterationField.setPrefWidth(50);
			iterationLabel = new Label(manager.getPropertyValue(ITERATION_LABEL.name()));
			iterationContainer = new BorderPane();
			iterationContainer.setPrefWidth(300);
			iterationContainer.setPadding(insets);
			iterationContainer.setLeft(iterationLabel);
			iterationContainer.setRight(iterationField);

			intervalField = new TextField();
			intervalField.setPrefWidth(50);
			intervalLabel = new Label(manager.getPropertyValue(INTERVAL_LABEL.name()));
			intervalContainer = new BorderPane();
			intervalContainer.setPadding(insets);
			intervalContainer.setLeft(intervalLabel);
			intervalContainer.setRight(intervalField);

			numLabelsField = new TextField();
			numLabelsField.setPrefWidth(50);
			numLabelsLabel = new Label(manager.getPropertyValue(NUMLABELS_LABEL.name()));
			numLabelsContainer = new BorderPane();
			numLabelsContainer.setPadding(insets);
			numLabelsContainer.setLeft(numLabelsLabel);
			numLabelsContainer.setRight(numLabelsField);

			continuousCheck = new CheckBox();
			checkBoxLabel = new Label(manager.getPropertyValue(CHECKBOX_LABEL.name()));
			checkBoxContainer = new BorderPane();
			checkBoxContainer.setPadding(insets);
			checkBoxContainer.setLeft(checkBoxLabel);
			checkBoxContainer.setRight(continuousCheck);

			container.getChildren().addAll(sceneHeader, iterationContainer, intervalContainer, numLabelsContainer, checkBoxContainer);
		}

		/**
		 * Sets up the event handlers corresponding to the window
		 */
		private void setUpActions(){
			AppData appData = (AppData) applicationTemplate.getDataComponent();
			this.setOnCloseRequest(event -> {
				createConfig(appData.getAlgorithmType());
				//appData.setConfiguration(config);
			});
		}

		/*
		Each algorithm has a configuration window that can produce a config
		When the window is closed --> it produces a config object
		everytime that the window is closed --> it should set the config for the alogrithm in the data itself
		*/

		/**
		 * Hides the number of labels field in the window. 
		 */
		private void hideLabelField(){
			container.getChildren().remove(numLabelsContainer);
		}

		/**
		 * Shows the number of labels field in the window.
		 */
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
			try{
				int maxIterations = Integer.parseInt(iterationField.getText());
				int updateInterval = Integer.parseInt(intervalField.getText());
				int numLabels = Integer.parseInt(numLabelsField.getText());
				if(maxIterations < 0 || updateInterval < 0 || numLabels < 0){
					return false;
				}
			}catch(NumberFormatException e){
				return false;
			}
			return true;
		}

		/**
		 * Creates a configuration object based on the given type
		 * @param type of the algorithm
		 */
		private void createConfig(AlgorithmTypes type){
			if(!checkInput(type)){
				handleInvalidConfig(type);
				return;
			}
			int maxIterations = Integer.parseInt(iterationField.getText());
			int updateInterval = Integer.parseInt(intervalField.getText());
			boolean toContinue = continuousCheck.isSelected();
			int numLabels = 0;
			if(type.equals(AlgorithmTypes.CLUSTERING)){
				numLabels = Integer.parseInt(numLabelsField.getText());
				config = new Config(maxIterations, updateInterval, toContinue, numLabels);
				numLabelsField.setText("" + numLabels);
			}else{
				config = new Config(maxIterations, updateInterval, toContinue);
			}
			iterationField.setText("" + maxIterations);
			if(updateInterval < 2){
				intervalField.setText("" + 2);
			}else{
				intervalField.setText("" + updateInterval);
			}
			continuousCheck.setSelected(toContinue);
		}

		private void resetConfigWindow(){
			iterationField.setText("");
			intervalField.setText("");
			continuousCheck.setSelected(false);
			numLabelsField.setText("");
		}

		/**
		 * Displays an error dialog to the user and uses default values
		 * for the algorithm configuration
		 * @param type 
		 */
		private void handleInvalidConfig(AlgorithmTypes type){
			AppActions appActions = (AppActions) applicationTemplate.getActionComponent();
			appActions.showErrorDialog(manager.getPropertyValue(INVALID_CONFIG_TITLE.name()), manager.getPropertyValue(INVALID_CONFIG_MESSAGE.name()));
			int tempIteration = 15;
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