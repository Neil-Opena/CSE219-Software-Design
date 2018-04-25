package bargraph;

/**
 * This class implements a linear conversion strategy between data values
 * and pixels.
 *
 * @author E. Stark
 * @version 20180407
 */
public class LinearScalingStrategy implements ScalingStrategy {
    
    private final double scale;
    
    /**
     * Initialize a LinearScalingStrategy with a specified scale factor.
     * 
     * @param scale  The scale factor, in pixels per data unit.
     */
    public LinearScalingStrategy(double scale) {
        this.scale = scale;
    }

    @Override
    public double dataToPixels(double data) {
        return data * scale;
    }

    @Override
    public double pixelsToData(double pixels) {
        return pixels / scale;
   }
    
}
