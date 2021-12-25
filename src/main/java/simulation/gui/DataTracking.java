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

import java.util.ArrayList;
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
            Label genomeInfo = new Label("Genome:");
            animalStatsGrid.add(genomeInfo,0, 0, 1, 1);
            Label genome = new Label(animal.genome.toString());
            animalStatsGrid.add(genome, 0, 1, 1, 1);
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
            animalStatsGrid.getChildren().clear();
            Label genomeInfo = new Label("Genome:");
            animalStatsGrid.add(genomeInfo,0, 0, 1, 1);
            animalStatsGrid.add(new Label(map.trackedAnimal.genome.toString()), 0, 1, 1, 1);
            animalStatsGrid.add(new Label("Number of children: " + map.childrenOfTracked), 0, 2, 1, 1);
            animalStatsGrid.add(new Label("Number of all offspring: " + map.offspringOfTracked), 0, 3, 1, 1);
            if (map.trackedAnimalDied){
                if (dayOfDeath == -1){
                    dayOfDeath = day;
                }
                animalStatsGrid.add(new Label(String.valueOf(dayOfDeath)), 0, 3, 1, 1);
            }
        }
    }

    public ArrayList<String[]> generateReport(){
        double totalAnimals = 0;
        double totalGrass = 0;
        double totalAverageEnergy = 0;
        double totalAverageLifespan = 0;
        double totalAverageChildren = 0;
        ArrayList<String[]> data = new ArrayList<>();
        for (int i=0; i<day; i++){
            String[] dayData;
            dayData = new String[5];
            totalAnimals += decodeData(animalSeries, dayData, i, 0);
            totalGrass += decodeData(grassSeries, dayData, i, 1);
            totalAverageEnergy += decodeData(averageEnergySeries, dayData, i, 2);
            totalAverageLifespan += decodeData(averageLifeSpan, dayData, i, 3);
            totalAverageChildren += decodeData(averageChildrenNum, dayData, i, 4);
            System.out.println("Hej");
            data.add(dayData);
        }
        String[] dayData = {
        String.valueOf(totalAnimals / day),
        String.valueOf(totalGrass / day),
        String.valueOf(totalAverageEnergy / day),
        String.valueOf(totalAverageLifespan / day),
        String.valueOf(totalAverageChildren / day)};
        data.add(dayData);
        return  data;
    }

    public double decodeData(XYChart.Series<Number, Number> series, String[] dayData, int i, int j){
        dayData[j] = series.getData().get(i).YValueProperty().get().toString();
        return series.getData().get(i).YValueProperty().get().doubleValue();
    }

    public LineChart<Number, Number> allChart(){
        // define axes
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Simulation day");
        yAxis.setLabel("Value");
        // create chart
        LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Animal statistics");
        // add data to chart
        lineChart.getData().addAll(animalSeries, grassSeries, averageEnergySeries, averageLifeSpan, averageChildrenNum);
        lineChart.setMaxWidth(400);
        return lineChart;
    }
}
