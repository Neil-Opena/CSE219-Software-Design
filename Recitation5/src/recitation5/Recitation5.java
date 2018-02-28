/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package recitation5;
import java.util.ArrayList;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.stage.Stage;

public class Recitation5 extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Line Chart Sample");

        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Number of Month");
        yAxis.setLabel("Stock Price");

        //final LineChart<Number,Number> lineChart = new LineChart(xAxis, yAxis);
	final LineChart lineChart = new LineChart(xAxis, yAxis);

        lineChart.setTitle("Stock Monitoring, 2010");
        
        XYChart.Series series = new XYChart.Series();
        series.setName("My portfolio");
        
        series.getData().add(new XYChart.Data(1, 23));
        series.getData().add(new XYChart.Data(2, 14));
        series.getData().add(new XYChart.Data(3, 15));
        series.getData().add(new XYChart.Data(4, 24));
        series.getData().add(new XYChart.Data(5, 34));
        series.getData().add(new XYChart.Data(6, 36));
        series.getData().add(new XYChart.Data(7, 22));
        series.getData().add(new XYChart.Data(8, 45));
        series.getData().add(new XYChart.Data(9, 43));
        series.getData().add(new XYChart.Data(10, 17));
        series.getData().add(new XYChart.Data(11, 29));
        series.getData().add(new XYChart.Data(12, 25));

	XYChart.Series series2 = new XYChart.Series();
	series2.setName("Series 2");
	series2.getData().add(new XYChart.Data(1, 3));
        series2.getData().add(new XYChart.Data(2, 4));
        series2.getData().add(new XYChart.Data(3, 5));
        series2.getData().add(new XYChart.Data(4, 4));
        series2.getData().add(new XYChart.Data(5, 4));
        series2.getData().add(new XYChart.Data(6, 6));
        series2.getData().add(new XYChart.Data(7, 2));
        series2.getData().add(new XYChart.Data(8, 5));
        series2.getData().add(new XYChart.Data(9, 3));
        series2.getData().add(new XYChart.Data(10, 7));
        series2.getData().add(new XYChart.Data(11, 9));
        series2.getData().add(new XYChart.Data(12, 5));	

	XYChart.Series series3 = new XYChart.Series();
	series3.setName("YValueAdded");

        Scene scene = new Scene(lineChart, 800, 600);
        lineChart.getData().add(series);
	lineChart.getData().add(series2);

	ArrayList<Double> temp = new ArrayList<>(12);
	for(int i = 0; i < 12; i++){
		temp.add(0.0);
	}

	for(Series serie : (ObservableList<Series>) lineChart.getData()){
		for(int i = 0; i < series.getData().size(); i++){
			double oldVal = temp.get(i);
			Data point = (Data) series.getData().get(i);
			double y = Double.parseDouble(point.getYValue().toString());
			temp.set(i, oldVal + y);
		}
	}

	for(int i = 0; i < 12; i++){
		series3.getData().add(new Data(i + 1, temp.get(i)));
	}
	lineChart.getData().add(series3);
	lineChart.setCreateSymbols(false);

        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}