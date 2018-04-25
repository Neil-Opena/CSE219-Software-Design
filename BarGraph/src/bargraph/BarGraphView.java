package bargraph;


import java.util.ArrayList;
import java.util.List;
import javafx.scene.layout.VBox;

/**
 * View that displays an underlying data model in the form of a bar graph.
 *
 * @author E. Stark
 * @version 20180407
 *
 */
public class BarGraphView extends VBox implements DataListener {

    private final DataModel data;
    private double scale;
    private final int barWidth;
    private final int barHeight;
    private final List<BarGraphBar> bars;

    /**
     * Initialize a BarGraphView.
     * 
     * @param data  The underlying data model.
     * @param scale  Display scale factor, in pixels per data model unit.
     * @param barWidth  Maximum width of a bar.
     * @param barHeight  Height of a bar.
     */
    public BarGraphView(DataModel data, double scale, int barWidth, int barHeight) {
        this.data = data;
        this.scale = scale;
        this.barWidth = barWidth;
        this.barHeight = barHeight;
        bars = new ArrayList<>();
        initialize();
    }

    private void initialize() {
        for (int i = 0; i < data.size(); i++) {
            double value = data.getValue(i);
            BarGraphBar bar = new BarGraphBar(value, this, barWidth, barHeight);
            final int ii = i;
            bar.setOnMouseClicked(e -> data.setValue(ii, e.getX() / scale));
            getChildren().add(bar);
            bars.add(bar);
       }
    }
    
    /**
     * Get the scale factor of this view.
     * 
     * @return the scale factor, in pixels per data model unit.
     */
    public double getScale() {
        return scale;
    }
    
    /**
     * Set the scale factor of this view.
     * 
     * @param scale  The new scale factor.
     * 
     */
    public void setScale(double scale) {
        this.scale = scale;
    }
    
    @Override
    public void dataChanged(int index, double value) {
        // TODO Fill this in.
    }

}
