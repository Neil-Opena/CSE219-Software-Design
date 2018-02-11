package ui;

import actions.AppActions;
import dataprocessors.AppData;
import static java.io.File.separator;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import vilij.templates.ApplicationTemplate;
import vilij.templates.UITemplate;

import static settings.AppPropertyTypes.*;
import static vilij.settings.PropertyTypes.GUI_RESOURCE_PATH;
import static vilij.settings.PropertyTypes.ICONS_RESOURCE_PATH;

/**
 * This is the application's user interface implementation.
 *
 * @author Ritwik Banerjee
 */
public final class AppUI extends UITemplate {

    /** The application to which this class of actions belongs. */
    ApplicationTemplate applicationTemplate;

    @SuppressWarnings("FieldCanBeLocal")
    private Button                       scrnshotButton; // toolbar button to take a screenshot of the data
    private ScatterChart<Number, Number> chart;          // the chart where data will be displayed
    private Button                       displayButton;  // workspace button to display data on the chart
    private TextArea                     textArea;       // text area for new data input
    private boolean                      hasNewText;     // whether or not the text area has any new data since last display

    private VBox 			 inputRegion;
    private Label		 	 inputTitle;
    private String			 data;

    public ScatterChart<Number, Number> getChart() { return chart; }

    public AppUI(Stage primaryStage, ApplicationTemplate applicationTemplate) {
        super(primaryStage, applicationTemplate);
        this.applicationTemplate = applicationTemplate;
    }

    @Override
    protected void setResourcePaths(ApplicationTemplate applicationTemplate) {
        super.setResourcePaths(applicationTemplate);
    }

    @Override
    protected void setToolBar(ApplicationTemplate applicationTemplate) {
        // TODO for homework 1
	super.setToolBar(applicationTemplate);

	String iconsPath = "/" + String.join(separator, applicationTemplate.manager.getPropertyValue(GUI_RESOURCE_PATH.name()),applicationTemplate.manager.getPropertyValue(ICONS_RESOURCE_PATH.name()));
	String scrnshotIconPath = iconsPath + separator + applicationTemplate.manager.getPropertyValue(SCREENSHOT_ICON.name());

	scrnshotButton = setToolbarButton(scrnshotIconPath,applicationTemplate.manager.getPropertyValue(SCREENSHOT_TOOLTIP.name()),true);
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
    }

    @Override
    public void initialize() {
        layout();
        setWorkspaceActions();
    }

    @Override
    public void clear() {
        // TODO for homework 1
	((AppData) applicationTemplate.getDataComponent()).clear();
	textArea.clear();
	newButton.setDisable(true);
    }

    private void layout() {
        // TODO for homework 1
	
	workspace = new FlowPane();

	inputRegion = new VBox();
	inputRegion.setPrefWidth(300);
	inputRegion.setAlignment(Pos.TOP_CENTER);

	inputTitle = new Label("Data File");
	inputTitle.setAlignment(Pos.CENTER);
	inputTitle.getStyleClass().add("chart-title");
	textArea = new TextArea();
	textArea.setPrefHeight(130);
	textArea.setWrapText(true);
	VBox.setMargin(textArea, new Insets(10));
	displayButton = new Button("Display");
	inputRegion.getChildren().addAll(inputTitle, textArea, displayButton);

	chart = new ScatterChart<>(new NumberAxis(), new NumberAxis());
	chart.setTitle("Data Visualization");
	chart.setPrefSize(700, 500);

	workspace.getChildren().addAll(inputRegion, chart);
	appPane.getChildren().add(workspace);
    }

    private void setWorkspaceActions() {
        // TODO for homework 1
	applicationTemplate.setDataComponent(new AppData(applicationTemplate));

	//if textArea has content, enable newbutton
	textArea.textProperty().addListener(e -> {
		if(textArea.getText().isEmpty()){
			newButton.setDisable(true);
			saveButton.setDisable(true);
		}else{
			newButton.setDisable(false);
			saveButton.setDisable(false);
		} //TODO if one line, just update if statement
	}); 

	displayButton.setOnAction(event -> {
		String test = textArea.textProperty().getValue().trim();
		hasNewText = !test.equals(data);
		if(hasNewText){
			data = textArea.getText();
			((AppData) applicationTemplate.getDataComponent()).clear();
			((AppData) applicationTemplate.getDataComponent()).loadData(data);
		}
	});
    }
}
