package classification;

import algorithms.Classifier;
import data.DataSet;
import dataprocessors.AppData;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.application.Platform;
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
	private final AppData appData;

	private final int maxIterations;
	private final int updateInterval;

	// currently, this value does not change after instantiation
	private AtomicBoolean tocontinue;
	private AtomicBoolean initContinue; //value that does not change

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

	public final boolean isInitContinue() {
		return initContinue.get();
	}

	public RandomClassifier(DataSet dataset,
		int maxIterations,
		int updateInterval,
		boolean tocontinue, XYChart chart, AppData appData) {
		this.dataset = dataset;
		this.maxIterations = maxIterations;
		this.updateInterval = updateInterval;
		algorithm = new Thread(this);
		algorithm.setName(this.getClass().toString());

		this.tocontinue = new AtomicBoolean(tocontinue);
		this.initContinue = new AtomicBoolean(tocontinue);
		this.chart = chart;
		this.appData = appData;
	}

	@Override
	public void run() {
		initLine();
		try {
			Thread.sleep(500); //display chart first 
		} catch (InterruptedException ex) {
			return;
		}
		chart.getXAxis().setAutoRanging(false);
		chart.getYAxis().setAutoRanging(false);
		for (int i = 1; i <= maxIterations && !Thread.interrupted(); i++) {
			//int xCoefficient = new Double(RAND.nextDouble() * 100).intValue();
			//int yCoefficient = new Double(RAND.nextDouble() * 100).intValue();
			//int constant = new Double(RAND.nextDouble() * 100).intValue();

			//need to modify such that new line is inside range

			int xCoefficient = new Long(-1 * Math.round((2 * RAND.nextDouble() - 2) * 10)).intValue();
			int yCoefficient = 10;
			int constant = RAND.nextInt(11);

			// this is the real output of the classifier
			output = Arrays.asList(xCoefficient, yCoefficient, constant);

			// everything below is just for internal viewing of how the output is changing
			// in the final project, such changes will be dynamically visible in the UI
			if (i % updateInterval == 0) {
				System.out.printf("Iteration number %d: ", i); //
				flush();
				try {
					Thread.sleep(1000); //simulate performing algorithm 
					updateData();
				} catch (InterruptedException ex) {
					//do nothing
					return;
				}
				if (!isInitContinue()) {
					appData.enableRun();
					tocontinue.set(false);
					while (!tocontinue()) {
						//don't do anything until it is set to continue
					}
					appData.disableRun();
				}
			}
			if (i > maxIterations * .6 && RAND.nextDouble() < 0.05) {
				System.out.printf("Iteration number %d: ", i);
				flush();
				break;
			}
		}
		//algorithm has finished
		Platform.runLater(() -> appData.completeAlgorithm());
	}

	@Override
	public void updateData() {
		int a = output.get(0);
		int b = output.get(1);
		int c = output.get(2);

		/*
		Note that if just *one* of the coefficients A and B is zero, 
		the equation Ax + By + C = 0 still determines a line.  
		It is only if *both* A and B are zero that the equation is degenerate.  
		 */
		//in order to prevent axis resizing, the line should always be inside range
		if (a == 0 && b == 0) {
			//not a valid line --> pause algorithm --> show dialog
			return;
		}

		//ax + by + c = 0
		// y = (-c -ax) / b

		/*
		If the line does not intersect the display window, 
		an appropriate action might be to provide some sort of 
		visual indication as to the direction in which the line lies, 
		relative to the displayed rectangle.
		 */
		Platform.runLater(() -> {
			XYChart.Series line = (chart.getData().get(chart.getData().size() - 1));
			Data min = (Data) line.getData().get(0);
			Data max = (Data) line.getData().get(1);
			double yVal;

			double minX = (double) min.getXValue();
			yVal = (-c - (a * minX)) / b;
			min.setYValue(yVal);

			double maxX = (double) max.getXValue();
			yVal = (-c - (a * maxX)) / b;
			max.setYValue(yVal);

			//my implementation
			double minY = dataset.getMinY();
			double maxY = dataset.getMaxY();
			
			yVal = minY + (Math.random() * maxY);
			min.setYValue(yVal);
			yVal = minY + (Math.random() * maxY);
			max.setYValue(yVal);

		});
	}

	/*
	A better idea would be to have a label in the display that shows the current iteration number that corresponds to what is showing in the chart.
	task idea!!
	*/

	private void initLine() {

		/*
		There will have to be some mechanism for querying the Dataset 
		to determine the range of data values, so that suitable 
		ranges can be set for the charts 
		 */
		dataset.sortValues();
		double min = dataset.getMinX();
		double max = dataset.getMaxX();

		/*
		Modify chart after traversing data to avoid ConcurrentModificationException
		 */
		XYChart.Series potentialLine = chart.getData().get(chart.getData().size() - 1);
		if (potentialLine.getName().equals("classification")) {
			Platform.runLater(() -> {
				chart.getData().remove(potentialLine);

			});
		}
		line = new XYChart.Series<>();
		line.setName("classification");

		XYChart.Data minX = new XYChart.Data(min, 0);
		XYChart.Data maxX = new XYChart.Data(max, 0);
		line.getData().add(minX);
		line.getData().add(maxX);

		Platform.runLater(() -> {
			chart.getData().add(line);
			line.getNode().getStyleClass().add("line");
			((XYChart.Data) line.getData().get(0)).getNode().getStyleClass().add("hide-symbol");
			((XYChart.Data) line.getData().get(1)).getNode().getStyleClass().add("hide-symbol");
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
		tocontinue.set(true);
	}

	@Override
	public void stopAlgorithm() {
		algorithm.interrupt();
	}
}
