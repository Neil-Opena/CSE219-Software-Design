package dataprocessors;

import javafx.geometry.Point2D;
import javafx.scene.chart.XYChart;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

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

	public static class InvalidDataNameException extends Exception {

		private static final String NAME_ERROR_MSG = "All data instance names must start with the @ character.";

		public InvalidDataNameException(String name) {
			super(String.format("Invalid name '%s'. " + NAME_ERROR_MSG, name));
		}
	}

	public static class DuplicateNameException extends Exception {

		private static final String message = "Duplicate name = "; //FIXMElater

		public DuplicateNameException(String name) {
			super(message + name);
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
		dataLabels = new HashMap<>();
		dataPoints = new HashMap<>();
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
					errorMessage.append("Line " + (dataPoints.size() + 1) + ": ");
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
