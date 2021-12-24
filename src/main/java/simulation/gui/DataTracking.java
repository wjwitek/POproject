package simulation.gui;

import javafx.geometry.Pos;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import simulation.AbstractWorldMap;
import simulation.Animal;

import java.util.Set;
import java.util.TreeSet;

public class DataTracking {
    private int day = 0;
    private final XYChart.Series<Number, Number> animalSeries = new XYChart.Series<>();
    private final XYChart.Series<Number, Number> grassSeries = new XYChart.Series<>();
    private final XYChart.Series<Number, Number> averageEnergySeries = new XYChart.Series<>();
    private final XYChart.Series<Number, Number> averageLifeSpan = new XYChart.Series<>();
    private final XYChart.Series<Number, Number> averageChildrenNum = new XYChart.Series<>();
    private final AbstractWorldMap map;
    private Set<String> modes = new TreeSet<>();
    private final GridPane animalStatsGrid;
    private boolean trackingAnimal = false;
    private int dayOfDeath;

    public DataTracking(AbstractWorldMap newMap, GridPane general){
        map = newMap;
        animalSeries.setName("Animals count");
        grassSeries.setName("Grasses count");
        averageEnergySeries.setName("Average energy of animal");
        averageLifeSpan.setName("Average life span of dead animal");
        averageChildrenNum.setName("Average number of children per animal");
        animalStatsGrid = general;
    }

    public void updateData(){
        day += 1;
        animalSeries.getData().add(new XYChart.Data<>(day, map.animals.size()));
        grassSeries.getData().add(new XYChart.Data<>(day, map.grasses.size()));
        double animalsNum = 0;
        if (map.animals.size() != 0){
            animalsNum = map.animals.size();
        }
        averageEnergySeries.getData().add(new XYChart.Data<>(day, (double) map.totalEnergy / animalsNum));
        double temp = 0;
        if (map.totalDead != 0){
            temp = (double) map.totalLifeSpan / (double) map.totalDead;
        }
        averageLifeSpan.getData().add(new XYChart.Data<>(day, temp));
        modes = map.getMode();
        averageChildrenNum.getData().add(new XYChart.Data<>(day, (double) map.totalChildren / animalsNum));
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

    public void modes(VBox vBox){
        vBox.getChildren().clear();
        // add all modes to vBox
        for (String signature : modes){
            vBox.getChildren().add(getLabel(signature));
        }
    }

    private Label getLabel(String text){
        Label num = new Label(text);
        num.setAlignment(Pos.CENTER);
        return num;
    }

    // draw statistics of an animal
    public void drawAnimalStats(Animal animal){
        if (!(map.active)){
            animalStatsGrid.getChildren().clear();
            Label test = new Label(animal.genome.toString());
            animalStatsGrid.add(test, 0, 0, 1, 1);
            trackingAnimal = true;
            map.trackedAnimal = animal;
            animal.isTracked = true;
            map.resetTracking();
            dayOfDeath = -1;
        }
    }

    public void updateAnimalStats(){
        if (trackingAnimal){
            animalStatsGrid.getChildren().clear();
            animalStatsGrid.add(new Label(map.trackedAnimal.genome.toString()), 0, 0, 1, 1);
            animalStatsGrid.add(new Label(String.valueOf(map.childrenOfTracked)), 0, 1, 1, 1);
            animalStatsGrid.add(new Label(String.valueOf(map.offspringOfTracked)), 0, 2, 1, 1);
            if (map.trackedAnimalDied){
                if (dayOfDeath == -1){
                    dayOfDeath = day;
                }
                animalStatsGrid.add(new Label(String.valueOf(dayOfDeath)), 0, 3, 1, 1);
            }
        }
    }

    public LineChart<Number, Number> drawAverageChildren(){
        // define axes
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Simulation day");
        yAxis.setLabel("Average number of children");
        // create chart
        LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Average number of children per animal");
        // add data to chart
        lineChart.getData().add(averageChildrenNum);
        return lineChart;
    }
}
