package computetask;


import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

/**
 * This class defines a ComputeTask that computes the Mandelbrot set.
 * More precisely, it iterates the formula z = z*z + c, where c ranges over
 * a grid of points in the complex plane, and it determines how many
 * iterations it takes for the value of z to diverge at each point.
 * The points at which divergence does not occur form an approximation to
 * the Mandelbrot set, and for the other points, the iteration numbers at
 * which the divergence threshold is reached can be used to create interesting
 * pictures.
 * 
 * @author E. Stark
 * @version 20180330
 */

public class MandelbrotTask {
    
    public MandelbrotTask() {
        this(new MandelbrotTaskState(), 10000, 100);
    }

    public MandelbrotTask(MandelbrotTaskState state, long iterations, long updateInterval) {
        // oops...
    }

}

/**
 * This class maintains the state information for MandelbrotTask.
 * 
 * @author E. Stark
 * @version 20180330
 */
class MandelbrotTaskState {

    /** Width of the rectangular grid of points. */
    private final int width;
    
    /** Height of the rectangular grid of points. */
    private final int height;
    
    /** Coordinate of the upper-left grid point. */
    private final Complex origin;
    
    /**
     * Amount by which the real parts of the coordinates increase for each
     * successive row of the grid, and the amount by which the imaginary
     * parts of the coordinates increase for each successive column of the grid.
     */
    private final double delta;

    /** An array that holds the current "z" values. */
    private final Complex[][] values;
    
    /**
     * An array that contains the iteration number at which the divergence
     * threshold was crossed at each grid point, or 0 if the divergence
     * threshold has not yet been crossed.
     */
    private final long[][] diverged;

    /** The threshold at which divergence is declared. */
    private final double DIVERGENCE = 1E20;
    
    private final Complex ZERO = new Complex(0, 0);
    
    public MandelbrotTaskState() {
        // These parameters produce an interesting display.
        // It is fun to explore other possibilities.
        this(500, 500, new Complex(-0.7463, 0.1102), 0.000005);
    }

    /**
     * Initialize a MandelbrotTask state with specified parameters.
     * 
     * @param width The width of the grid of points.
     * @param height The height of the grid of points.
     * @param center  The coordinate of the center of the grid of points.
     * @param delta  The amount by which the real part of the coordinate of
     * a point increases for each successive row of the grid, and the amount
     * by which the imaginary part of the coordinate of a point increases
     * for each successive column of the grid.
     */
    public MandelbrotTaskState(int width, int height, Complex center, double delta) {
        this.width = width;
        this.height = height;
        this.origin = new Complex(center.getRealPart() - delta * height * 0.5,
                center.getImagPart() - delta * width * 0.5);
        this.delta = delta;
 
        diverged = new long[height][width];
        values = new Complex[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                values[i][j] = ZERO;
            }
        }
    }

    /**
     * Perform the update of the state for one iteration.
     * For each point in the grid having coordinate c, the associated value
     * z is updated by the formula z = z*z + c.  If the resulting value
     * exceeds the divergence threshold, divergence at that point is
     * declared, and the current iteration number is recorded for that
     * point.
     * 
     * @param iteration The current iteration number.
     */
    protected void update(long iteration) {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (diverged[i][j] == 0) {
                    Complex v = values[i][j];
                    Complex c = valueAtPos(i, j);
                    v = v.mul(v).add(c);
                    values[i][j] = v;
                    if (v.modulus() > DIVERGENCE) {
                        diverged[i][j] = iteration;
                    }
                }
            }
        }
    }


    /**
     * Auxiliary method to compute the coordinate of the point at a
     * specified grid location.
     * 
     * @param i  The row number of the point.
     * @param j  The column number of the point.
     * @return  The coordinate of the point.
     */
    private Complex valueAtPos(int i, int j) {
        return new Complex(origin.getRealPart() + j * delta,
                origin.getImagPart() + i * delta);
    }

    /**
     * This method constructs and returns an Image that represents the
     * current task state.  The Image returned has one pixel for each
     * point in the grid.  The color of each pixel is set according to
     * the current value of the "diverged" array.  The color black is
     * used for points that have not yet diverged.  The colors of other
     * points are set according to the iteration number at which the
     * divergence threshold was crossed.
     * 
     * @return 
     */
    public Image getResult() {
        WritableImage image = new WritableImage(width, height);
        PixelWriter pw = image.getPixelWriter();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                pw.setColor(j, i, mapColor(diverged[i][j]));
            }
        }
        return image;
    }

    /**
     * Auxiliary method to map iteration numbers to colors.
     * The current version uses a gray-scale scheme in which the whiteness
     * of a pixel is set according to the ratio of the iteration number
     * to 1000, with 1.0 the maximum possible ratio.  It is possible
     * to make more interesting images by modifying this method so that
     * colors are used, rather than just gray-scale.
     * 
     * @param v
     * @return 
     */
    private Color mapColor(long v) {
        if (v == 0) {
            return Color.BLACK;
        } else {
            if (v > 1000) {
                v = 1000;
            }
            double blue = 1.0 - v / 1000.0;
            double green = 1.0 - v / 1000.0;
            double red = 1.0 - v / 1000.0;
            return new Color(red, green, blue, 1);
        }
    }
}
