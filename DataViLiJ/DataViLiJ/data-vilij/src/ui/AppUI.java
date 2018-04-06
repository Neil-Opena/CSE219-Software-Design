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
import dataprocessors.Config;
import java.util.List;
import javafx.collections.ObservableList;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;

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

	private TextArea textArea;       // text area for new data input
	private LineChart<Number, Number> chart;          // the chart where data will be displayed
	private Button scrnshotButton; // toolbar button to take a screenshot of the data
	private Button displayButton;  // workspace button to display data on the chart
	private Button editToggleButton; // button that toggles between edit and done
	private Button runButton; // button for running alogrithm
	private Button configButton; // button for configuring algorithm
	private Button classificationButton;
	private Button clusteringButton;
	private Label typesTitle;
	private Label displayInfo;

	private boolean hasNewText;     // whether or not the text area has any new data since last display

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

		String iconsPath = "/" + String.join(separator, manager.getPropertyValue(GUI_RESOURCE_PATH.name()), manager.getPropertyValue(ICONS_RESOURCE_PATH.name()));
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
	public void displayInfo(int numInstances, int numLabels, List<String> labelNames, String source){
		displayInfo.setText(numInstances + " instances with " + numLabels + " labels loaded from " + source + ". The labels are:\n");
		String labels = "";
		for(int i = 0; i < labelNames.size(); i++){
			labels += "\t- " + labelNames.get(i) + "\n";
		}
		displayInfo.setText(displayInfo.getText() + labels + "\n");

		inputRegion.getChildren().add(displayInfo);
		setUpAlgorithmTypes(labelNames.size());
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
	public void hideAlgorithmTypes(){

	}

	/**
	 * Shows the possible clustering algorithms
	 */
	public void showCluseringAlgorithms(){
	
	}

	/**
	 * Shows the possible classification algorithms
	 */
	public void showClassificationAlgorithms(){

	}

	/**
	 * Hides the algorithms
	 */
	public void hideAlgorithms(){

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

	/**
	 * Returns the configuration obtained from the Config Window
	 * @return 
	 */
	public Config getConfiguration(){
		return null;
	}

	private void setUpAlgorithmTypes(int numLabels){
		if(numLabels < 2){
			classificationButton.setDisable(true);
		}else{
			classificationButton.setDisable(false);
		}
		inputRegion.getChildren().add(typeContainer);

		inputRegion.getChildren().add(displayButton);
	}

	private void resetInputRegion(){
		inputRegion.getChildren().clear();
	}
/**
	 * Sets the text area to be read only depending on the parameter
	 * @param readOnly true to set the text area to be read only, false otherwise
	 */
	private void setReadOnly(boolean readOnly){
		textArea.setEditable(!readOnly);
		if(readOnly){
			textArea.getStyleClass().add(manager.getPropertyValue(GRAY_TEXT.name()));
		}else{
			textArea.getStyleClass().remove(manager.getPropertyValue(GRAY_TEXT.name()));
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
		typeContainer.getChildren().addAll(typesTitle, classificationButton, clusteringButton);

		displayButton = new Button(manager.getPropertyValue(DISPLAY_BUTTON.name()));

		editToggleButton = new Button("Done");
		editToggleButton.getStyleClass().addAll("toggle-button", "types-button", "done");
		editToggleButton.setPrefWidth(100);

		chart = new LineChart<>(new NumberAxis(), new NumberAxis());
		chart.setTitle(manager.getPropertyValue(CHART_TITLE.name()));
		chart.setPrefSize(700, (getPrimaryScene().getWidth()) - 450);

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

			appData.displayData();
			if(chart.getData().isEmpty()){
				scrnshotButton.setDisable(true);
			}else{
				scrnshotButton.setDisable(false);
			}
			addDataPointListeners();

		});

		clusteringButton.setOnAction(event ->{
			System.out.println("clustering button clicked");
		});

		classificationButton.setOnAction(event ->{
			System.out.println("classification button clicked");
		});

		editToggleButton.setOnAction(event -> {
			String curr = editToggleButton.getText();
			if(curr.equals("Done")){
				editToggleButton.setText("Edit");
				setReadOnly(true);
			}else{
				editToggleButton.setText("Done");
				setReadOnly(false);
			}
		});
	}

	/**
	 * Adds listeners to the data points inside the chart
	 */
	private void addDataPointListeners(){
		for(Series series : chart.getData()){
			if(series.getName().equals(manager.getPropertyValue(AVERAGE_Y.name()))){
				Data data = (Data) series.getData().get(0);
				String averageValue = String.format(manager.getPropertyValue(DECIMAL_FORMAT.name()), Double.parseDouble(data.getExtraValue().toString()));
				Node average = series.getNode();
				Tooltip.install(average, new Tooltip(manager.getPropertyValue(AVERAGE_Y_TOOLTIP.name()) + averageValue));
				average.setOnMouseEntered(e ->{
					getPrimaryScene().setCursor(Cursor.CROSSHAIR);
				});
				average.setOnMouseExited(e ->{
					getPrimaryScene().setCursor(Cursor.DEFAULT);
				});
				
				continue;
			}
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

	/**
	 * This class is responsible for showing a window to the user
	 * The values inputted inside the window is extracted to create a Config object
	 */
	private class ConfigWindow extends Stage{

		private TextField iterationField; // TextField for entering the number of iterations
		private TextField intervalField; // TextField for entering the number of intervals
		private TextField numLabelsField; // TextField for the number of labels
		private CheckBox continuousCheck; // CheckBox whether the algorithm runs continuous or not

		private Config config; // Config object created from controls

		public ConfigWindow(){
			layout();
		}

		/**
		 *  Lays out the UI display of the Configuration Window
		 */
		public void layout(){
			iterationField = new TextField();
			intervalField = new TextField();
			numLabelsField = new TextField();
			continuousCheck = new CheckBox();

			config = null;
		}

		/**
		 * Returns the Config object associated with what the user
		 * entered in the Config Window
		 * @return 
		 */
		public Config getConig(){
			return config;
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