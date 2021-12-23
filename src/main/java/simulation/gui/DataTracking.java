package simulation.gui;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import simulation.AbstractWorldMap;

import java.util.ArrayList;

public class DataTracking {
    private int day = 0;
    private final XYChart.Series<Number, Number> animalSeries = new XYChart.Series<>();
    private final XYChart.Series<Number, Number> grassSeries = new XYChart.Series<>();
    private final XYChart.Series<Number, Number> averageEnergySeries = new XYChart.Series<>();
    private final XYChart.Series<Number, Number> averageLifeSpan = new XYChart.Series<>();
    private final AbstractWorldMap map;

    public DataTracking(AbstractWorldMap newMap){
        map = newMap;
        animalSeries.setName("Animals count");
        grassSeries.setName("Grasses count");
        averageEnergySeries.setName("Average energy of animal");
        averageLifeSpan.setName("Average life span of dead animal");
    }

    public void updateData(){
        day += 1;
        animalSeries.getData().add(new XYChart.Data<>(day, map.animals.size()));
        grassSeries.getData().add(new XYChart.Data<>(day, map.grasses.size()));
        averageEnergySeries.getData().add(new XYChart.Data<>(day, (double) map.totalEnergy / (double) map.animals.size()));
        double temp = 0;
        if (map.totalDead != 0){
            temp = (double) map.totalLifeSpan / (double) map.totalDead;
        }
        averageLifeSpan.getData().add(new XYChart.Data<>(day, temp));
        System.out.println(temp);
        System.out.println(map.totalLifeSpan);
        System.out.println(map.totalDead);
        System.out.println("--------------");
    }

    public LineChart<Number, Number> drawAnimalGrassChart(){
        // define axes
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Simulation day");
        yAxis.setLabel("Total count");
        // create chart
        LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Animal and grass count");
        // add data to chart
        lineChart.getData().addAll(animalSeries, grassSeries);
        return lineChart;
    }

    public LineChart<Number, Number> drawAverageEnergy(){
        // define axes
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Simulation day");
        yAxis.setLabel("Average energy of animal");
        // create chart
        LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Average energy of animal over days");
        // add data to chart
        lineChart.getData().add(averageEnergySeries);
        return lineChart;
    }

    public LineChart<Number, Number> drawAverageLifeSpan(){
        // define axes
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Simulation day");
        yAxis.setLabel("Average life span");
        // create chart
        LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Average life span of dead animals");
        // add data to chart
        lineChart.getData().add(averageLifeSpan);
        return lineChart;
    }
}
