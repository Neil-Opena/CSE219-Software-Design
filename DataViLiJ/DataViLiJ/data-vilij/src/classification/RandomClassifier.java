package classification;

import algorithms.Classifier;
import data.DataSet;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;

/**
 * @author Ritwik Banerjee
 */
public class RandomClassifier extends Classifier {

	private static final Random RAND = new Random();

	@SuppressWarnings("FieldCanBeLocal")
	// this mock classifier doesn't actually use the data, but a real classifier will
	private DataSet dataset;
	private XYChart<Number, Number> chart;
	private XYChart.Series line;

	private final Thread algorithm;

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

	public RandomClassifier(DataSet dataset,
		int maxIterations,
		int updateInterval,
		boolean tocontinue, XYChart chart) {
		this.dataset = dataset;
		this.maxIterations = maxIterations;
		this.updateInterval = updateInterval;
		algorithm = new Thread(this);
		this.tocontinue = new AtomicBoolean(tocontinue);
		this.chart = chart;
	}

	@Override
	public void run() {
		initLine();
		for (int i = 1; i <= maxIterations && tocontinue(); i++) {
			int xCoefficient = new Double(RAND.nextDouble() * 100).intValue();
			int yCoefficient = new Double(RAND.nextDouble() * 100).intValue();
			int constant = new Double(RAND.nextDouble() * 100).intValue();

			// this is the real output of the classifier
			output = Arrays.asList(xCoefficient, yCoefficient, constant);

			// everything below is just for internal viewing of how the output is changing
			// in the final project, such changes will be dynamically visible in the UI
			if (i % updateInterval == 0) {
				System.out.printf("Iteration number %d: ", i); //
				flush();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException ex) {
					Logger.getLogger(RandomClassifier.class.getName()).log(Level.SEVERE, null, ex);
				}
				updateData();
			}
			if (i > maxIterations * .6 && RAND.nextDouble() < 0.05) {
				System.out.printf("Iteration number %d: ", i);
				flush();
				break;
			}
		}
	}
	
	@Override
	public void updateData(){
		int a = output.get(0);
		int b = output.get(1);
		int c = output.get(2);


		Platform.runLater(() -> {
			XYChart.Series line = (chart.getData().get(chart.getData().size() - 1));
			Data min = (Data) line.getData().get(0);
			Data max = (Data) line.getData().get(1);
			double yVal;
			yVal = (1.0/b) * ((a * (double) min.getXValue()) + c);
			min.setYValue(yVal);
			yVal = (1.0/b) * ((a * (double) max.getXValue()) + c);
			min.setYValue(yVal);
		});
	}

	private void initLine(){
		line = new XYChart.Series<>();
		line.setName("line");

		double min = Double.parseDouble(chart.getData().get(0).getData().get(0).getXValue().toString());
		double max = min;

		for(XYChart.Series serie : chart.getData()){
			for(XYChart.Data point : (ObservableList<XYChart.Data>) serie.getData()){

				double testXVal = Double.parseDouble(point.getXValue().toString());
				if(testXVal < min){
					min = testXVal;
				}else if(testXVal > max){
					max = testXVal;
				}
				
			}
		}

		XYChart.Data minX = new XYChart.Data(min, 0);
		XYChart.Data maxX = new XYChart.Data(max, 0);
		line.getData().add(minX);
		line.getData().add(maxX);

		Platform.runLater(() -> {
			chart.getData().add(line);
			line.getNode().getStyleClass().add("line");
		});
	}

	@Override
	public String toString() {
		return "[" + this.getClass() + ": maxIterations=" + maxIterations + ", updateInterval=" + updateInterval + ", tocontinue=" + tocontinue + "]";
	}

	// for internal viewing only
	protected void flush() {
		System.out.printf("%d\t%d\t%d%n", output.get(0), output.get(1), output.get(2));
	}

//    /** A placeholder main method to just make sure this code runs smoothly */
//    public static void main(String... args) throws IOException {
//        DataSet          dataset    = DataSet.fromTSDFile(Paths.get("/path/to/some-data.tsd"));
//        RandomClassifier classifier = new RandomClassifier(dataset, 100, 5, true);
//        classifier.run(); // no multithreading yet
//    }

	@Override
	public void startAlgorithm() {
		algorithm.start();
	}

	@Override
	public void continueAlgorithm() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
}
