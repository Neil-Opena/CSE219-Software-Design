package bargraph;

/**
 * Interface implemented by strategies for converting between data values
 * and pixel values.
 * 
 * @author E. Stark
 * @version 20180407
 */
public interface ScalingStrategy {
    
    /**
     * Convert a data value to a pixel value.
     * 
     * @param data  The data value.
     * @return  the corresponding pixel value.
     */
    public double dataToPixels(double data);
    
    /**
     * Convert a pixel value to a data value.
     * 
     * @param pixels  The pixel value.
     * @return  the corresponding data value.
     */
    public double pixelsToData(double pixels);
    
}
