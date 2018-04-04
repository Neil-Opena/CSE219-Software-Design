package computetask;


import computetask.ComputeTaskState;
import computetask.ComputeTask;

/**
 * This class defines a ComputeTask that computes the value of Pi.
 * 
 * @author E. Stark
 * @version 20180330
 */
public class PiTask extends ComputeTask<Double> {

    public PiTask() {
        super(new PiTaskState(), 0, 1000);
    }

}

/**
 * This class maintains the state information for PiTask.
 * The value of Pi is computed by summing the Leibniz series:
 *
 * pi/4 = 1 - 1/3 + 1/5 - 1/7 + ...
 * 
 * This series converges very slowly, which means that it is not a very
 * efficient way to compute pi, but it makes a good demo that can run
 * for a long time.
 * 
 * @author E. Stark
 * @version 20180330
 */
class PiTaskState extends ComputeTaskState<Double> {

    /** The current partial sum of the series. */
    private double value;
    
    /** The sign to be used for the next term. */
    private int sign;

    public PiTaskState() {
        value = 0;
        sign = 1;
    }

    @Override
    public Double getResult() {
        return value;
    }

    @Override
    protected void update(long iteration) {
        value = value + (sign * 4.0 / (2 * iteration + 1));
        sign = -sign;
    }

    @Override
    public String toString() {
        return Double.toString(value);
    }

}
