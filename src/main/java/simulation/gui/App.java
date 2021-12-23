package simulation.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import simulation.*;


public class App extends Application {
    public int width; // number of columns
    public int height; // number of rows
    public float jungleRatio;
    public int startingEnergy;
    public int moveEnergy;
    public int plantEnergy;
    public int startingAnimals;
    private final int SCREEN_WIDTH = 750;
    private final int SCREEN_HEIGHT = 750;
    private final int CELL_WIDTH = 20;
    private final int CELL_HEIGHT = 20;
    private SimulationEngine engine;
    public DataTracking boundedDataTracker;
    public DataTracking rolledDataTracker;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Simulation");

        settingMenu(primaryStage);
    }

    public void settingMenu(Stage primaryStage){
        GridPane gridPane = new GridPane();
        gridPane.setHgap(8);
        gridPane.setVgap(2);

        // add fields to type in simulation settings
        TextField widthField = addTextField(gridPane, "Map width", 0);
        TextField heightField = addTextField(gridPane, "Map height", 3);
        TextField startEnergyField = addTextField(gridPane, "Starting energy", 6);
        TextField moveEnergyField = addTextField(gridPane, "Move energy", 9);
        TextField plantEnergyField = addTextField(gridPane, "Plant energy", 12);
        TextField jungleRatioField = addTextField(gridPane, "Jungle ratio", 15);
        TextField startAnimals = addTextField(gridPane, "Starting animals", 18);

        // set default values
        widthField.setText("20");
        heightField.setText("20");
        startEnergyField.setText("100");
        moveEnergyField.setText("5");
        plantEnergyField.setText("50");
        jungleRatioField.setText("0.2");
        startAnimals.setText("10");

        // button to save settings and change scene
        createStartButton(gridPane, widthField, heightField, startEnergyField, moveEnergyField, plantEnergyField,
                jungleRatioField, startAnimals, primaryStage);

        Scene scene = new Scene(gridPane, 240, 260);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void mapStage(Stage primaryStage) {
        // initialize GridPanes for both maps
        GridPane rolledGridPane = initGridPane();
        GridPane boundedGridPane = initGridPane();
        // create general layout of scene
        GridPane layout = new GridPane();
        layout.setHgap(20);
        layout.setVgap(20);
        // add grids to layout
        layout.add(rolledGridPane, 0, 0, 1, 1);
        layout.add(boundedGridPane, 0, 1, 1, 1);

        // start simulation
        engine = new SimulationEngine(this, rolledGridPane, boundedGridPane);

        // add buttons to layout
        createStopStartButton(layout, engine.rolledMap, 0);
        createStopStartButton(layout, engine.boundedMap, 1);

        // start tracking data
        boundedDataTracker = new DataTracking(engine.boundedMap);
        rolledDataTracker = new DataTracking(engine.rolledMap);

        // add charts to grid
        layout.add(rolledDataTracker.drawAnimalGrassChart(), 2, 0, 1, 1);
        layout.add(rolledDataTracker.drawAverageEnergy(), 3, 0, 1, 1);
        layout.add(rolledDataTracker.drawAverageLifeSpan(), 4, 0, 1, 1);

        layout.add(boundedDataTracker.drawAnimalGrassChart(), 2, 1, 1, 1);
        layout.add(boundedDataTracker.drawAverageEnergy(), 3, 1, 1, 1);
        layout.add(boundedDataTracker.drawAverageLifeSpan(), 4, 1, 1 , 1);

        // find and display mode


        Scene scene = new Scene(layout, SCREEN_WIDTH, SCREEN_HEIGHT);

        Thread engineThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    System.out.println(ex.getMessage());
                    System.exit(1);
                }

                // UI update is run on the Application thread
                Platform.runLater(engine);
            }
        });

        engineThread.setDaemon(true);
        engineThread.start();

        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
    }

    private void getSimParams(TextField widthField, TextField heightField, TextField startEnergyField,
                              TextField moveEnergyField, TextField plantEnergyField, TextField jungleRatioField, TextField startAnimals){
        height = Integer.parseInt(heightField.getText());
        width = Integer.parseInt(widthField.getText());
        startingEnergy = Integer.parseInt(startEnergyField.getText());
        moveEnergy = Integer.parseInt(moveEnergyField.getText());
        plantEnergy = Integer.parseInt(plantEnergyField.getText());
        jungleRatio = Float.parseFloat(jungleRatioField.getText());
        startingAnimals = Integer.parseInt(startAnimals.getText());
        if (height < 0 || width < 0 || startingEnergy <= 0 || moveEnergy < 0 || plantEnergy <= 0 || jungleRatio <= 0 || jungleRatio > 1 || startingAnimals <= 0){
            throw new IllegalArgumentException("Incorrect range of values.");
        }
    }

    // add text field with description to grid
    private TextField addTextField(GridPane gridPane, String description, int row){
        TextField newTextField = new TextField();
        newTextField.setPrefWidth(50.0);
        Label label = new Label(description);
        gridPane.add(newTextField, 8, row, 1, 1);
        gridPane.add(label, 0, row, 1, 1);
        return newTextField;
    }

    private void createStartButton(GridPane gridPane, TextField widthField, TextField heightField,
                                     TextField startEnergyField, TextField moveEnergyField, TextField plantEnergyField,
                                     TextField jungleRatioField, TextField startAnimals, Stage primaryStage){
        Button save = new Button("Start");
        save.setAlignment(Pos.CENTER);
        save.setDefaultButton(true); // clicking enter is equal to pressing this button
        save.setOnAction(new EventHandler<>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                // get values from text fields
                boolean correctValues = true;
                try {
                    getSimParams(widthField, heightField, startEnergyField, moveEnergyField, plantEnergyField, jungleRatioField, startAnimals);
                } catch (Exception ex) {
                    correctValues = false;
                    Alert warning = new Alert(Alert.AlertType.ERROR, "Check provided values, some of them are of incorrect type or range.");
                    warning.show();
                }
                if (correctValues) {
                    try {
                        mapStage(primaryStage);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        gridPane.add(save, 1, 21, 2, 1);
    }

    public void createStopStartButton(GridPane gridPane, AbstractWorldMap map, int row){
        Button stopStart = new Button("Stop");
        stopStart.setAlignment(Pos.CENTER);
        stopStart.setDefaultButton(true); // clicking enter is equal to pressing this button
        stopStart.setOnAction(new EventHandler<>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                // get values from text fields
                if (map.active){
                    map.active = false;
                    stopStart.setText("Start");
                }
                else {
                    map.active = true;
                    stopStart.setText("Stop");
                }
            }
        });
        gridPane.add(stopStart, 1, row, 1, 1);
    }

    public void draw(GridPane gridPane, AbstractWorldMap map) throws Exception {
        gridPane.getChildren().clear();

        setColors(gridPane);

        drawAnimals(gridPane, map);
        drawGrass(gridPane, map);
    }

    private GridPane initGridPane(){
        GridPane gridPane = new GridPane();
        gridPane.getColumnConstraints().add(new ColumnConstraints(CELL_WIDTH));
        gridPane.getRowConstraints().add(new RowConstraints(CELL_HEIGHT));

        // add column constraints
        for (int i = 0; i < width; i++){
            ColumnConstraints column = new ColumnConstraints(CELL_WIDTH);
            gridPane.getColumnConstraints().add(column);
        }

        // add row constraints
        for (int i = 0; i < height; i++){
            RowConstraints row = new RowConstraints(CELL_HEIGHT);
            gridPane.getRowConstraints().add(row);
        }
        return gridPane;
    }

    private Label getLabel(String text){
        Label num = new Label(text);
        num.setMinHeight(CELL_HEIGHT);
        num.setMinWidth(CELL_WIDTH);
        num.setAlignment(Pos.CENTER);
        return num;
    }

    private void drawGrass(GridPane gridPane, AbstractWorldMap map) throws Exception {
        for (Vector2D position : map.grasses.keySet()){
            gridPane.add(new GuiElementBox(map.grasses.get(position)).box, position.x, position.y, 1, 1);
        }
    }

    private void drawAnimals(GridPane gridPane, AbstractWorldMap map) throws Exception{
        for (Animal animal : map.animals.values()){
            gridPane.add(new GuiElementBox(animal).box, animal.position.x, animal.position.y, 1, 1);
        }
    }

    private void setColors(GridPane gridPane){
        String jungleColor = "-fx-background-color: #ADD8E6;";
        String steppeColor = "-fx-background-color: #DBF3FA;";
        for (int i = 0; i < width + 1; i++){
            for (int j=0; j < height + 1; j++){
                Label background = new Label(" ");
                background.setPrefSize(CELL_WIDTH, CELL_HEIGHT);
                if (new Vector2D(i, j).follows(engine.rolledMap.jungleLeftCorner) && new Vector2D(i, j).precedes(engine.rolledMap.jungleRightCorner)){
                    background.setStyle(jungleColor);
                }
                else {
                    background.setStyle(steppeColor);
                }
                gridPane.add(background, i, j, 1, 1);
            }
        }
    }
}
