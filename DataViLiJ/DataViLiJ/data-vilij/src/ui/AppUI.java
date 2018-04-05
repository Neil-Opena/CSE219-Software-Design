//Neil Opena 110878452
package ui;

import static java.io.File.separator;
import java.io.IOException;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
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
import javafx.scene.control.TextField;

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
	private PropertyManager manager; // Property manager for accessing properties

	private ConfigWindow configWindow;

	@SuppressWarnings("FieldCanBeLocal")
	private VBox inputRegion; // container for input region
	private Label inputTitle; // title for input region
	private HBox controls; // container that houses controls

	private TextArea textArea;       // text area for new data input
	private LineChart<Number, Number> chart;          // the chart where data will be displayed
	private Button scrnshotButton; // toolbar button to take a screenshot of the data
	private Button displayButton;  // workspace button to display data on the chart
	private Button editToggleButton; // button that toggles between edit and done
	private Button runButton; // button for running alogrithm
	private Button configButton; // button for configuring algorithm
	private CheckBox readOnly; // readOnly box

	private boolean hasNewText;     // whether or not the text area has any new data since last display

	private String currentText; 
	private String hiddenData;
	/*
	Instead of displaying using text area data how aout display using app data
	*/

	public AppUI(Stage primaryStage, ApplicationTemplate applicationTemplate) {
		super(primaryStage, applicationTemplate);
		this.applicationTemplate = applicationTemplate;
		cssPath = "/" + String.join(separator,
                                    manager.getPropertyValue(GUI_RESOURCE_PATH.name()),
                                    manager.getPropertyValue(CSS_RESOURCE_PATH.name()),
                                    manager.getPropertyValue(CSS_FILE.name()));
		this.getPrimaryScene().getStylesheets().add(getClass().getResource(cssPath).toExternalForm());
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
		toolBar.getItems().add(scrnshotButton);
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
				//FIXME
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
		newButton.setDisable(true);
		scrnshotButton.setDisable(true);
		hiddenData = null;
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
	public void setDisplayInfo(int numInstances, int numLabels, List<String> labelNames, String source){
		
	}

	/**
	 * Displays the text area to the user
	 */
	public void showTextArea(){

	}

	/**
	 * Sets the text area to be read only depending on the parameter
	 * @param readOnly true to set the text area to be read only, false otherwise
	 */
	public void setReadOnly(boolean readOnly){

	}

	public void showEditToggle(){

	}

	public void hideEditToggle(){

	}

	public void showAlgorithmTypes(){

	}

	public void hideAlgorithmTypes(){

	}

	public void showCluseringAlgorithms(){
	
	}

	public void showClassificationAlgorithms(){

	}

	public void enableRun(){

	}

	public void disableRun(){

	}

	public void enableScreenshot(){

	}

	public Config getConfiguration(){
		return null;
	}


	/*
	MIGHT NOT NEED
	*/
	public void setHiddenData(String hiddenData){
		this.hiddenData = hiddenData;
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

		controls = new HBox();
		controls.setAlignment(Pos.CENTER);
		controls.setSpacing(20);
		displayButton = new Button(manager.getPropertyValue(DISPLAY_BUTTON.name()));
		readOnly = new CheckBox(manager.getPropertyValue(READ_ONLY.name()));
		controls.getChildren().addAll(displayButton, readOnly);

		inputRegion.getChildren().addAll(inputTitle, textArea, controls);

		chart = new LineChart<>(new NumberAxis(), new NumberAxis());
		chart.setTitle(manager.getPropertyValue(CHART_TITLE.name()));
		chart.setPrefSize(700, 500);

		workspace.getChildren().addAll(inputRegion, chart);
		appPane.getChildren().add(workspace);
		
		configWindow = new ConfigWindow();
	}

	/**
	 * Sets up the listeners of the controls that the user interacts with
	 */
	private void setWorkspaceActions() {

		//if textArea has content, enable newbutton
		textArea.textProperty().addListener((e, oldVal, newVal) -> {
			String savedData = ((AppData) applicationTemplate.getDataComponent()).getSavedData();
			if (savedData == null) {
				if (textArea.getText().isEmpty()) {
					newButton.setDisable(true);
					saveButton.setDisable(true);
				} else {
					newButton.setDisable(false);
					saveButton.setDisable(false);
				}
			} else {
				//current file has been saved
				String textData = textArea.getText().trim();
				newButton.setDisable(false);
				if (textData.equals(savedData)) {
					saveButton.setDisable(true);
				} else {
					saveButton.setDisable(false);
				}
				if(!textData.equals(currentText)){

					int n = textArea.getParagraphs().size();
					int toGet = 10 - n;
					if(toGet > 0){
						//update text
						newVal = ((AppData) applicationTemplate.getDataComponent()).loadNumLines(toGet);
						textArea.setText(newVal);
					}
				}
			}
		});

		displayButton.setOnAction(event -> {
			AppData appData = ((AppData) applicationTemplate.getDataComponent());

			String test = textArea.getText().trim();
			hasNewText = !test.equals(currentText);
			if (test.isEmpty() && hiddenData == null) {
				((AppActions) applicationTemplate.getActionComponent()).showErrorDialog(manager.getPropertyValue(INVALID_DATA_TITLE.name()), manager.getPropertyValue(NO_DATA_MESSAGE.name()));
			} else if (hasNewText || chart.getData().isEmpty()) {
				currentText = textArea.getText().trim();
				appData.clear();
				
				if(hiddenData != null){ //if hidden data has been instantiated
					currentText = currentText + "\n" + hiddenData;
				}

				appData.loadData(currentText); //display what was in text area and hidden
				if(chart.getData().isEmpty()){
					scrnshotButton.setDisable(true);
				}else{
					scrnshotButton.setDisable(false);
				}

				addDataPointListeners();
			}

		});

		readOnly.setOnAction(event -> {
			if (readOnly.isSelected()) {
				textArea.setEditable(false);
				//change to css CHANGE OTHERS too, like title bar and shit
				textArea.getStyleClass().add(manager.getPropertyValue(GRAY_TEXT.name()));
			} else {
				textArea.setEditable(true);
				textArea.getStyleClass().remove(manager.getPropertyValue(GRAY_TEXT.name()));
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
	
	private class ConfigWindow extends Stage{
		private TextField iterationField;
		private TextField intervalField;
		private TextField numLabelsField;
		private CheckBox continuousCheck;

		private Config config;

		public ConfigWindow(){
			layout();
		}

		public void layout(){
			iterationField = new TextField();
			intervalField = new TextField();
			numLabelsField = new TextField();
			continuousCheck = new CheckBox();

			config = null;
		}

		public Config getConig(){
			return config;
		}

		public boolean checkInput(){
			return false;
		}

		public void showLabelsField(){

		}

		private void createConfig(){
			
		}
	}
	
}