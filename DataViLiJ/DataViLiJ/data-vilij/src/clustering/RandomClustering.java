/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clustering;

import algorithms.Clusterer;
import data.DataSet;
import dataprocessors.AppData;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * @author Neil Opena
 */
public class RandomClustering extends Clusterer { //problem with CLuster

	private static final Random RAND = new Random();

	@SuppressWarnings("FieldCanBeLocal")
	private DataSet dataset;

	private final Thread algorithm;
	private final AppData appData;

	private final int maxIterations;
	private final int updateInterval;

	// currently, this value does not change after instantiation
	private AtomicBoolean tocontinue;
	private boolean isContinuous; //value that does not change

	@Override
	public int getMaxIterations() {
		return maxIterations;
	}

	@Override
	public int getUpdateInterval() {
		return updateInterval;
	}

	@Override
	public final boolean tocontinue() {
		return tocontinue.get();
	}

	public RandomClustering(DataSet dataset, int maxIterations, int updateInterval, int numberOfClusters, boolean tocontinue, AppData appData) {
		super(numberOfClusters);
		this.dataset = dataset;
		this.maxIterations = maxIterations;
		this.updateInterval = updateInterval;

		algorithm = new Thread(this);
		algorithm.setName(getName());

		this.tocontinue = new AtomicBoolean(tocontinue);
		this.isContinuous = tocontinue;
		this.appData = appData;
	}

	@Override
	public void run() {
		// time for original chart to show
		try {
			Thread.sleep(750);
		} catch (InterruptedException ex) {
			return;
		}

		int iteration = 0;
		while (iteration++ < maxIterations && !Thread.interrupted()) {
			appData.showCurrentIteration(iteration);
			if(iteration % updateInterval == 0){
				appData.updateChart();
				if (!isContinuous) {
					appData.enableRun();
					tocontinue.set(false);
					while (!tocontinue()) { //wait until play is clicked
						if (Thread.interrupted()) {
							return;
						}
					}
					appData.disableRun();
				}
			}
			try {
				Thread.sleep(750);
			} catch (InterruptedException ex) {
				return;
			}
		}
		appData.completeAlgorithm();
	}

	@Override
	public void startAlgorithm() {
		algorithm.start();
	}

	@Override
	public void continueAlgorithm() {
		tocontinue.set(true);
	}

	@Override
	public void stopAlgorithm() {
		algorithm.interrupt();
	}

	@Override
	public String getName() {
		return "RandomClustering";
	}
}
