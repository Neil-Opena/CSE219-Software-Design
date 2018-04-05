/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataprocessors;

/**
 *This objects of this class contain the configuration of a certain algorithm
 * @author Neil Opena
 */
public class Config {
	private int iterations;
	private int interval;
	private boolean isContinuous;
	private int numLabels;

	public Config(int iterations, int interval, boolean isContinuous){
		this(iterations, interval, isContinuous, -1);
	}

	public Config(int iterations, int interval, boolean isContinuous, int numLabels){
		this.iterations = iterations;
		this.interval = interval;
		this.isContinuous = isContinuous;
		this.numLabels = numLabels;
	}
}
