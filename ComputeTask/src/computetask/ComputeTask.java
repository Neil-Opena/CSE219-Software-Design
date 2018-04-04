package computetask;


import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.concurrent.Task;

/**
 * Extension of javafx.concurrent.Task that implements the notion of a compute
 * task that runs iteratively, has a state that is updated in each iteration,
 * and from which a partial result can be extracted at each iteration.
 * Although the task state is mutable, it is important that partial results
 * be immutable (or at least that they not be mutated by the task), because
 * these are made available to the JavaFX thread for use in updating the
 * display, and if they could be concurrently mutated by the task there would
 * be the possibility of race conditions.
 * 
 * @author E. Stark
 * @version 20180330
 */
public class ComputeTask<V> extends Task<V> {

    /** Object that encapsulates the state of this task. */
    private ComputeTaskState<V> taskState;
    
    /**
     * The maximum number of iterations for which this task will run,
     * or 0 if the task will run until explicitly cancelled.
     */
    private final long iterations;
    
    /**
     * The number of iterations between GUI updates.
     */
    private final long updateInterval;
    
    /**
     * A wrapper object from which can be extracted a ReadOnlyProperty
     * that can be observed in order to periodically update the GUI with
     * the current partial result.
     */
    private final ReadOnlyObjectWrapper<V> partialResultWrapper;
    
    /**
     * Initialize a ComputeTask.
     * 
     * @param initialState  An object that encapsulates the initial state of the task.
     * @param iterations  The maximum number of iterations for which this task will run,
     * or 0 if the task is to run until explicitly cancelled.
     * @param updateInterval The number of iterations between GUI updates.
     */
    public ComputeTask(ComputeTaskState<V> initialState, long iterations, long updateInterval) {
        taskState = initialState;
        partialResultWrapper = new ReadOnlyObjectWrapper<>(initialState.getResult());
        this.iterations = iterations;
        this.updateInterval = updateInterval;
    }
    
    /**
     * The entry point for the task.
     * 
     * @return  The final result of the task, upon completion
     * @throws Exception if an exception is thrown by any of the task state
     * methods called here.
     */
    @Override
    protected V call()  {
        for (long i = 0; iterations == 0 || i < iterations; i++) {
            if (isCancelled()) {
                cancelled();
                break;
            }
            taskState.update(i);
            if (i % updateInterval == 0) {
                Platform.runLater(() -> partialResultWrapper.set(taskState.getResult()));
                updateMessage("Iteration " + i);
                updateProgress(iterations == 0 ? -1 : i, iterations);
                Thread.yield();
            }
        }
        if (!isCancelled()) {
            updateProgress(iterations, iterations);
        }
        final ComputeTaskState<V> finalState = taskState;
        Platform.runLater(() -> partialResultWrapper.set(finalState.getResult()));
        return taskState.getResult();
    }
    
    @Override
    protected void cancelled() {
        updateMessage("Cancelled");
    }

    @Override
    protected void failed() {
        updateMessage("Failed");
    }

    @Override
    protected void succeeded() {
        updateMessage("Done");
    }
    
    /**
     * Get the maximum number of iterations for which this task is to run.
     * 
     * @return the maximum number of iteration for which this task is to run,
     * or 0 if the task is to run until explicitly cancelled.
     */
    public long getIterations() {
        return iterations;
    }
    
    /**
     * Get the number of iterations between GUI updates.
     * 
     * @return the number of iterations between GUI updates.
     */
    public long getUpdateInterval() {
        return updateInterval;
    }
    
    /**
     * Get the current partial result of the task.  This method is intended
     * to be used before execution of this task has begun.  If this task
     * is currently being run by a thread it is not safe to call this method
     * from some other thread.
     *
     * @return the current partial result of the task.
     */
    public V getPartialResult() {
        return taskState.getResult();
    }

    /**
     * Get a ReadOnlyObjectProperty that can be observed for changes in the
     * current partial result of this task.  Registering an event listener
     * with this property is a safe way to be informed about updates to the
     * partial result while this task is being executed.
     * 
     * @return a ReadOnlyObjectProperty that can be observed for changes in
     * the current partial result of this task.
     */
    public ReadOnlyObjectProperty<V> getPartialResultProperty() {
        return partialResultWrapper.getReadOnlyProperty();
    }
    
}
