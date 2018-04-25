package bargraph;


import javafx.scene.control.TextField;

/**
 * Objects of this class represent single fields in a textual view of a data
 * model.
 *
 * @author E. Stark
 * @version 20180407
 */
public class NumericField extends TextField {

    private double value;

    /**
     * Initialize a NumericField with a specified initial value and width.
     * @param value  The initial data value to be displayed.
     * @param width  Width of the field, in columns. 
     */
    public NumericField(double value, int cols) {
        setPrefColumnCount(cols);
        this.value = value;
        setText("" + value);
    }

}
