package bargraph;

import java.util.List;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * Application to demonstrate Model/View/Controller architecture with
 * Observer and Strategy design patterns.  The application displays two
 * views linked to an underlying data model: a text-based view, and a
 * bar-graph view.  Changes to either view change the model and cause the
 * other view to update to correspond.
 * 
 * @author E. Stark
 * @version 20180407
 */

public class BarGraphDemo extends Application {
    
    private final int SCALE = 25;

    private TextView textView;
    private BarGraphView graphView;
    private DataModel data;
	
    @Override
    public void start(Stage primaryStage) throws Exception {
        List<String> args = getParameters().getRaw();
        double[] dataValues = new double[args.size()];
        for (int i = 0; i < args.size(); i++) {
            dataValues[i] = Double.parseDouble(args.get(i));
        }
        data = new DataModel(dataValues);
        
        primaryStage.setTitle("Text View");
        BorderPane content = new BorderPane();
        primaryStage.setScene(new Scene(content));
        textView = new TextView(data);
        content.setCenter(textView);
        primaryStage.show();
        
        Stage secondaryStage = new Stage();
        secondaryStage.setTitle("Bar Graph View");
        content = new BorderPane();
        secondaryStage.setScene(new Scene(content));
            MenuBar menuBar = new MenuBar();
        content.setTop(menuBar);
        Menu menu = new Menu("Scale");
        menuBar.getMenus().add(menu);
        MenuItem linearScaleItem = new MenuItem("Linear");
        ScalingStrategy linearScale = new LinearScalingStrategy(SCALE);
        menu.getItems().addAll(linearScaleItem);
        graphView = new BarGraphView(data, SCALE, SCALE*20, SCALE);
        content.setCenter(graphView);
        
        secondaryStage.show();
    }
	
    public static void main(String[] args) {
        launch(new String[] { "1", "2", "3", "4", "5" });
    }

}
