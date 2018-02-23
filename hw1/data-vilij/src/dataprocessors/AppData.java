package dataprocessors;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import ui.AppUI;
import vilij.components.DataComponent;
import vilij.templates.ApplicationTemplate;

import java.nio.file.Path;
import java.util.stream.Stream;
import javafx.scene.control.TextArea;
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

    private String		savedData;

    public AppData(ApplicationTemplate applicationTemplate) {
        this.processor = new TSDProcessor();
        this.applicationTemplate = applicationTemplate;
    }

    public String getSavedData(){
	    return savedData;
    }

    @Override
    public void loadData(Path dataFilePath) {
        // TODO: NOT A PART OF HW 1
	File file = dataFilePath.toFile();
	TextArea textArea = ((AppUI) applicationTemplate.getUIComponent()).getTextArea();
	try{
		clear(); // clear the chart and text area first
		textArea.clear();

		BufferedReader reader = new BufferedReader(new FileReader(file));
		Stream<String> dataLines = reader.lines();
		dataLines.forEach(line -> {
			textArea.appendText(line + "\n");
		});
		savedData = textArea.getText().trim(); //or should it be property?
		reader.close();
	}catch(FileNotFoundException e){
		System.out.println("file not found");
		e.printStackTrace();
		//FIXME
	}catch(IOException e){
		System.out.println("Something went wrong");
		e.printStackTrace();
		//FIXME
	}
    }

    public void loadData(String dataString){
        // TODO for homework 1
	try{
		processor.processString(dataString);
		displayData();
    	}catch(Exception e){
		Dialog errorDialog = applicationTemplate.getDialog(Dialog.DialogType.ERROR);
		String message = e.getMessage();
		if(message.length() < 9){
			message = message + applicationTemplate.manager.getPropertyValue(INVALID_DATA_MESSAGE.name());
		}
		errorDialog.show(applicationTemplate.manager.getPropertyValue(INVALID_DATA_TITLE.name()), message); 
		
	}

    }

    @Override
    public void saveData(Path dataFilePath) {
        // TODO: NOT A PART OF HW 1
	File file = dataFilePath.toFile();
	try{
		FileWriter writer = new FileWriter(file);
		String text = ((AppUI) applicationTemplate.getUIComponent()).getTextArea().getText();
		savedData = text.trim();
		writer.append(text);
		writer.close();
	}catch(IOException e){
		System.out.println("something went wrong");
		e.printStackTrace();
		//FIXME
	}
    }

    //if data saved is different from text area, save button should be enabled

    @Override
    public void clear() {
	savedData = null;
        processor.clear();
	((AppUI) applicationTemplate.getUIComponent()).getChart().getData().clear();
    }

    public void displayData() {
        processor.toChartData(((AppUI) applicationTemplate.getUIComponent()).getChart());
    }
}
