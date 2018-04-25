package bargraph;


import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

/**
 * Objects of this class implement a single bar in a bar graph.
 * 
 * @author E. Stark
 * @version 20180407
 */
public class BarGraphBar extends Pane {
    
    private double value;
    private final BarGraphView view;
    private final Rectangle rect;

    /**
     * Initialize a BarGraphBar.
     * 
     * @param value  Initial data value for the bar.
     * @param view  BarGraphView in which the bar is displayed.
     * @param width  Maximum width of the bar, in pixels.
     * @param height  Height of the bar, in pixels.
     */
    public BarGraphBar(double value, BarGraphView view, int width, int height) {
        this.view = view;
        setPrefWidth(width);
        setPrefHeight(height);
        rect = new Rectangle(0, height/4, 0, height/2);
        getChildren().add(rect);
        setValue(value);
    }
    
    /**
     * Set the data value for the bar.
     * 
     * @param value  The data value to be set.
     */
    public void setValue(double value) {
        this.value = value;
        rect.setWidth(value * view.getScale());
    }

}
