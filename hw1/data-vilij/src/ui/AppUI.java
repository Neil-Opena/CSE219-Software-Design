//Neil Opena 110878452
package ui;

import actions.AppActions;
import dataprocessors.AppData;
import static java.io.File.separator;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import vilij.templates.ApplicationTemplate;
import vilij.templates.UITemplate;

import static settings.AppPropertyTypes.*;
import vilij.components.Dialog;
import vilij.propertymanager.PropertyManager;
import static vilij.settings.PropertyTypes.GUI_RESOURCE_PATH;
import static vilij.settings.PropertyTypes.ICONS_RESOURCE_PATH;

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

	@SuppressWarnings("FieldCanBeLocal")
	private Button scrnshotButton; // toolbar button to take a screenshot of the data
	private ScatterChart<Number, Number> chart;          // the chart where data will be displayed
	private Button displayButton;  // workspace button to display data on the chart
	private TextArea textArea;       // text area for new data input
	private boolean hasNewText;     // whether or not the text area has any new data since last display

	private VBox inputRegion;
	private Label inputTitle;
	private String data;
	private PropertyManager manager;
	private HBox controls;
	private CheckBox readOnly;

	public ScatterChart<Number, Number> getChart() {
		return chart;
	}

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

	public TextArea getTextArea() {
		return textArea;
	}

	public Button getSaveButton() {
		return saveButton;
	}

	private void layout() {
		// TODO for homework 1

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
		readOnly = new CheckBox("Read only");
		controls.getChildren().addAll(displayButton, readOnly);

		inputRegion.getChildren().addAll(inputTitle, textArea, controls);

		chart = new ScatterChart<>(new NumberAxis(), new NumberAxis());
		chart.setTitle(manager.getPropertyValue(CHART_TITLE.name()));
		chart.setPrefSize(700, 500);

		workspace.getChildren().addAll(inputRegion, chart);
		appPane.getChildren().add(workspace);
	}

	private void setWorkspaceActions() {
		// TODO for homework 1
		applicationTemplate.setDataComponent(new AppData(applicationTemplate));

		//if textArea has content, enable newbutton
		textArea.textProperty().addListener(e -> {
			//FIXME
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
				newButton.setDisable(false);
				if (textArea.getText().trim().equals(savedData)) {
					saveButton.setDisable(true);
				} else {
					saveButton.setDisable(false);
				}
			}
		});

		displayButton.setOnAction(event -> {
			String test = textArea.textProperty().getValue().trim(); //change to getText?
			hasNewText = !test.equals(data);
			if (test.isEmpty()) {
				Dialog errorDialog = applicationTemplate.getDialog(Dialog.DialogType.ERROR);
				errorDialog.show(manager.getPropertyValue(INVALID_DATA_TITLE.name()), manager.getPropertyValue(NO_DATA_MESSAGE.name()));
			} else if (hasNewText || chart.getData().isEmpty()) {
				data = textArea.getText();
				((AppData) applicationTemplate.getDataComponent()).clear();
				((AppData) applicationTemplate.getDataComponent()).loadData(data);
			}
		});

		readOnly.setOnAction(event -> {
			if (readOnly.isSelected()) {
				textArea.setEditable(false);
				textArea.setStyle("-fx-text-fill: gray");
			} else {
				textArea.setEditable(true);
				textArea.setStyle(null);
			}
		});
	}
}
