package dataprocessors;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import ui.AppUI;
import vilij.components.DataComponent;
import vilij.templates.ApplicationTemplate;

import java.nio.file.Path;
import static settings.AppPropertyTypes.INVALID_DATA_MESSAGE;
import static settings.AppPropertyTypes.INVALID_DATA_TITLE;
import vilij.components.Dialog;

/**
 * This is the concrete application-specific implementation of the data component defined by the Vilij framework.
 *
 * @author Ritwik Banerjee
 * @see DataComponent
 */
public class AppData implements DataComponent {

    private TSDProcessor        processor;
    private ApplicationTemplate applicationTemplate;

    public AppData(ApplicationTemplate applicationTemplate) {
        this.processor = new TSDProcessor();
        this.applicationTemplate = applicationTemplate;
    }

    @Override
    public void loadData(Path dataFilePath) {
        // TODO: NOT A PART OF HW 1
    }

    public void loadData(String dataString){
        // TODO for homework 1
	try{
		processor.processString(dataString);
		displayData();
	}catch(Exception e){
		Dialog errorDialog = applicationTemplate.getDialog(Dialog.DialogType.ERROR);
		errorDialog.show(applicationTemplate.manager.getPropertyValue(INVALID_DATA_TITLE.name()), applicationTemplate.manager.getPropertyValue(INVALID_DATA_MESSAGE.name()));
	}

    }

    @Override
    public void saveData(Path dataFilePath) {
        // TODO: NOT A PART OF HW 1
	File file = dataFilePath.toFile();
	try{
		FileWriter writer = new FileWriter(file);
		writer.append(((AppUI) applicationTemplate.getUIComponent()).getTextAreaData());
		writer.close();
	}catch(IOException e){
		System.out.println("something went wrong");
		e.printStackTrace();
		//FIXME
	}
    }

    @Override
    public void clear() {
        processor.clear();
	((AppUI) applicationTemplate.getUIComponent()).getChart().getData().clear();
    }

    public void displayData() {
        processor.toChartData(((AppUI) applicationTemplate.getUIComponent()).getChart());
    }
}
