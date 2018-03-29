package dataprocessors;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.chart.XYChart;
import javafx.geometry.Point2D;

import static settings.AppPropertyTypes.*;
import vilij.propertymanager.PropertyManager;

/**
 * The data files used by this data visualization applications follow a
 * tab-separated format, where each data point is named, labeled, and has a
 * specific location in the 2-dimensional X-Y plane. This class handles the
 * parsing and processing of such data. It also handles exporting the data to a
 * 2-D plot.
 * <p>
 * A sample file in this format has been provided in the application's
 * <code>resources/data</code> folder.
 *
 * @author Ritwik Banerjee
 * @see XYChart
 */
public final class TSDProcessor {

	private PropertyManager manager;

	public static class InvalidDataNameException extends Exception {

		private static final String NAME_ERROR_MSG = "All data instance names must start with the @ character.";

		public InvalidDataNameException(String name) {
			super(String.format("Invalid name '%s'. " + NAME_ERROR_MSG, name));
		}
	}

	public static class DuplicateNameException extends Exception {

		private static final String MSG = "Duplicate name = ";

		public DuplicateNameException(String name) {
			super(MSG + name);
		}
	}

	private class DataPoint extends Point2D{
		String name;

		public DataPoint(double x, double y, String name){
			super(x, y);
			this.name = name;
		}
	}

	private Map<String, String> dataLabels;
	private Map<String, DataPoint> dataPoints;

	public TSDProcessor() {
		dataLabels = new LinkedHashMap<>();
		dataPoints = new LinkedHashMap<>();
		manager = PropertyManager.getManager();
	}

	/**
	 * Processes the data and populated two {@link Map} objects with the
	 * data.
	 *
	 * @param tsdString the input data provided as a single {@link String}
	 * @throws Exception if the input string does not follow the
	 * <code>.tsd</code> data format
	 */
	public void processString(String tsdString) throws Exception {
		AtomicBoolean hadAnError = new AtomicBoolean(false);
		StringBuilder errorMessage = new StringBuilder();
		Stream.of(tsdString.split("\n"))
			.map(line -> Arrays.asList(line.split("\t")))
			.forEach(list -> {
				try {
					String name = checkedname(list.get(0));
					String label = list.get(1);
					String[] pair = list.get(2).split(",");
					DataPoint point = new DataPoint(Double.parseDouble(pair[0]), Double.parseDouble(pair[1]), name);
					if (dataPoints.containsKey(name)) {
						throw new DuplicateNameException(name);
					}
					dataLabels.put(name, label);
					dataPoints.put(name, point);
				} catch (Exception e) {
					errorMessage.setLength(0);
					errorMessage.append(manager.getPropertyValue(LINE.name())+ (dataPoints.size() + 1) + ": ");
					if (e instanceof InvalidDataNameException || e instanceof DuplicateNameException) {
						errorMessage.append(e.getMessage());
					}
					hadAnError.set(true);
				}
			});
		if (errorMessage.length() > 0) {
			throw new Exception(errorMessage.toString());
		}
	}

	//maybe modify the exception to add line number?
	/**
	 * Exports the data to the specified 2-D chart.
	 *
	 * @param chart the specified chart
	 */
	void toChartData(XYChart<Number, Number> chart) {
		Set<String> labels = new HashSet<>(dataLabels.values());
		for (String label : labels) {
			XYChart.Series<Number, Number> series = new XYChart.Series<>();
			series.setName(label);
			dataLabels.entrySet().stream().filter(entry -> entry.getValue().equals(label)).forEach(entry -> {
				DataPoint point = dataPoints.get(entry.getKey());
				series.getData().add(new XYChart.Data<>(point.getX(), point.getY(), point.name));
			});
			chart.getData().add(series);
		}
		displayAverageYValue(chart);
	}

	void displayAverageYValue(XYChart<Number, Number> chart){

		double sum = 0;

		Set<DataPoint> points = new HashSet<>(dataPoints.values());
		double min = Double.parseDouble(chart.getData().get(0).getData().get(0).getXValue().toString());
		double max = min;

		for(Series serie : chart.getData()){
			for(XYChart.Data point : (ObservableList<XYChart.Data>) serie.getData()){

				sum += Double.parseDouble(point.getYValue().toString());
				
				double testXVal = Double.parseDouble(point.getXValue().toString());
				if(testXVal < min){
					min = testXVal;
				}else if(testXVal > max){
					max = testXVal;
				}
				
			}
		}

		double average = sum / points.size();
		Series averageY = new Series<>();
		averageY.setName(manager.getPropertyValue(AVERAGE_Y.name()));

		Data minData = new Data(min, average, average);
		Data maxData = new Data(max, average, average);
		averageY.getData().add(minData);
		averageY.getData().add(maxData);

		chart.getData().add(averageY);

		minData.getNode().getStyleClass().add(manager.getPropertyValue(HIDE_SYMBOL.name())); 
		maxData.getNode().getStyleClass().add(manager.getPropertyValue(HIDE_SYMBOL.name()));
		averageY.getNode().getStyleClass().add(manager.getPropertyValue(DISPLAY_LINE.name()));
	}

	void clear() {
		dataPoints.clear();
		dataLabels.clear();
	}

	private String checkedname(String name) throws InvalidDataNameException {
		if (!name.startsWith("@")) {
			throw new InvalidDataNameException(name);
		}
		return name;
	}
}
