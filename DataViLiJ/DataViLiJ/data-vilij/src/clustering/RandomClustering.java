/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clustering;

import algorithms.Clusterer;
import data.DataSet;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * @author Neil Opean
 */
public class RandomClustering extends Clusterer{
    private static final Random RAND = new Random();

    private DataSet dataset;

    private final int maxIterations;
    private final int updateInterval;

	// currently, this value does not change after instantiation
    private final AtomicBoolean tocontinue;

    @Override
    public int getMaxIterations() {
        return maxIterations;
    }

    @Override
    public int getUpdateInterval() {
        return updateInterval;
    }

    @Override
    public boolean tocontinue() {
        return tocontinue.get();
    }

    public RandomClustering(DataSet dataset,
                            int maxIterations,
                            int updateInterval,
                            boolean tocontinue) {
        this.dataset = dataset;
        this.maxIterations = maxIterations;
        this.updateInterval = updateInterval;
        this.tocontinue = new AtomicBoolean(tocontinue);

	//NULL pointer
	if(dataset != null) this.labels = (List<String>) dataset.getLabels().values();
    }

    @Override
    public void run() {
        for (int i = 1; i <= maxIterations && tocontinue(); i++) {

	    modifyLabels();
            // everything below is just for internal viewing of how the output is changing
            // in the final project, such changes will be dynamically visible in the UI
            if (i % updateInterval == 0) {
                System.out.printf("Iteration number %d: ", i); //
                flush();
            }
            if (i > maxIterations * .6 && RAND.nextDouble() < 0.05) {
                System.out.printf("Iteration number %d: ", i);
                flush();
                break;
            }
        }
    }

    public void modifyLabels(){
	int numLabels = labels.size();
	int labelIndex = RAND.nextInt(numLabels);
	String label = labels.get(labelIndex);
	
	dataset.getLabels().forEach((String name, String currLabel) -> {
    		currLabel = label;
   	 });
    }

    // for internal viewing only
    protected void flush() {
        System.out.print("label modified");
    }
}
