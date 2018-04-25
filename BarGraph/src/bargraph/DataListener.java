package bargraph;

/**
 * Observer interface implemented by a view in the bar graph demo.
 * 
 * @author E. Stark
 * @version 20180407
 *
 */
public interface DataListener {
	
    /**
     * Method called when the data model has changed.
     * 
     * @param index  The index of the value that has changed.
     * @param value  The new value.
     */
    public void dataChanged(int index, double value);

}
