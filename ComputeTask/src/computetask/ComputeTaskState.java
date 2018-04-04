package computetask;

/**
 * BAbstract base class whose subclasses are intended to be used to maintain
 * the state for a ComputeTask.
 * 
 * @author E. Stark
 * @version 20180330
 */
public abstract class ComputeTaskState<V> {
    
    /**
     * Perform the state update for a single iteration.
     * 
     * @param iteration The current iteration number.
     */
    protected abstract void update(long iteration);
    
    /**
     * Get the result value corresponding to the current state.
     * 
     * @return the result value corresponding to the current state.
     */
    public abstract V getResult();
    
}
