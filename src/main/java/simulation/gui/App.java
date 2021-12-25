package simulation.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import simulation.*;

import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.Set;


public class App extends Application {
    public int width; // number of columns
    public int height; // number of rows
    public float jungleRatio;
    public int startingEnergy;
    public int moveEnergy;
    public int plantEnergy;
    public int startingAnimals;
    private final double CELL_WIDTH = 20;
    private final double CELL_HEIGHT = 20;
    private SimulationEngine engine;
    public DataTracking boundedDataTracker;
    public DataTracking rolledDataTracker;
    private boolean magic;
    public GridPane layout;

    public VBox rBox;
    public VBox bBox;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Simulation");

        settingMenu(primaryStage);
    }

    /* Open menu with fields to fill in starting parameters. */
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
        RadioButton magicButton = new RadioButton();
        gridPane.add(magicButton, 1, 21, 1, 1);
        Label magicLabel = new Label("Magic evolution");
        gridPane.add(magicLabel, 0, 21, 1, 1);

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
                jungleRatioField, startAnimals, primaryStage, magicButton);

        Scene scene = new Scene(gridPane, 240, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /* Set main screen and add all elements of layout. */
    public void mapStage(Stage primaryStage) {
        // initialize GridPanes for both maps
        GridPane rolledGridPane = initGridPane();
        GridPane boundedGridPane = initGridPane();
        // create general layout of scene
        layout = new GridPane();
        layout.setHgap(20);
        layout.setVgap(20);
        // add grids to layout
        layout.add(rolledGridPane, 0, 1, 3, 4);
        layout.add(boundedGridPane, 0, 7, 3, 4);

        // start simulation
        engine = new SimulationEngine(this, rolledGridPane, boundedGridPane, magic);

        // add buttons to layout
        createStopStartButton(layout, engine.rolledMap, 5);
        createStopStartButton(layout, engine.boundedMap, 11);

        // add grids for animal statistics
        GridPane rolledAnimalStats = new GridPane();
        GridPane boundedAnimalStats = new GridPane();
        layout.add(rolledAnimalStats, 5, 4, 3, 2);
        layout.add(boundedAnimalStats, 5, 10, 3, 2);

        // start tracking data
        boundedDataTracker = new DataTracking(engine.boundedMap, boundedAnimalStats);
        rolledDataTracker = new DataTracking(engine.rolledMap, rolledAnimalStats);

        // add charts to grid
        layout.add(rolledDataTracker.allChart(), 3, 1, 2, 4);
        layout.add(boundedDataTracker.allChart(), 3, 7, 2, 4);

        // find and display mode
        ScrollPane rolledMode = new ScrollPane();
        rBox = new VBox();
        rolledMode.setContent(rBox);
        layout.add(rolledMode, 5, 1, 3, 3);

        ScrollPane boundedMode = new ScrollPane();
        bBox = new VBox();
        boundedMode.setContent(bBox);
        layout.add(boundedMode, 5, 7, 3, 3);

        // add buttons to highlight animals
        layout.add(startHighlight(rolledGridPane, engine.rolledMap, rolledDataTracker), 1, 5, 1, 1);
        layout.add(startHighlight(boundedGridPane, engine.boundedMap, boundedDataTracker), 1, 11, 1, 1);

        // add buttons to save files
        layout.add(saveToFile(rolledDataTracker, Paths.get("").toAbsolutePath() + "\\" + "src\\main\\java\\simulation\\raports\\rolled.csv",
                engine.rolledMap), 2, 5, 1, 1);
        layout.add(saveToFile(boundedDataTracker, Paths.get("").toAbsolutePath() + "\\" + "src\\main\\java\\simulation\\raports\\bounded.csv",
                engine.boundedMap), 2, 11, 1, 1);

        // add map labels
        Label rolledLabel = new Label("Map with teleporting to other end.");
        rolledLabel.setAlignment(Pos.CENTER);
        rolledLabel.setStyle("-fx-rotate: 90");
        layout.add(rolledLabel, 9, 1, 1, 5);

        Label boundedLabel = new Label("Map with bounds.");
        boundedLabel.setAlignment(Pos.CENTER);
        boundedLabel.setStyle("-fx-rotate: 90");
        layout.add(boundedLabel, 9, 7, 1, 5);

        int SCREEN_WIDTH = 1500;
        int SCREEN_HEIGHT = 800;

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
        primaryStage.centerOnScreen();
        primaryStage.setFullScreen(true);
    }

    /* Extract all data from strings. */
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

    /* Add text field with description to grid. */
    private TextField addTextField(GridPane gridPane, String description, int row){
        TextField newTextField = new TextField();
        newTextField.setPrefWidth(50.0);
        Label label = new Label(description);
        gridPane.add(newTextField, 8, row, 1, 1);
        gridPane.add(label, 0, row, 1, 1);
        return newTextField;
    }

    /* Button to start simulation and read params. */
    private void createStartButton(GridPane gridPane, TextField widthField, TextField heightField,
                                     TextField startEnergyField, TextField moveEnergyField, TextField plantEnergyField,
                                     TextField jungleRatioField, TextField startAnimals, Stage primaryStage, RadioButton magicButton){
        Button save = new Button("Start");
        save.setAlignment(Pos.CENTER);
        save.setDefaultButton(true); // clicking enter is equal to pressing this button
        save.setOnAction(actionEvent -> {
            // get values from text fields
            boolean correctValues = true;
            try {
                getSimParams(widthField, heightField, startEnergyField, moveEnergyField, plantEnergyField, jungleRatioField, startAnimals);
                magic = magicButton.isSelected();
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
        });
        gridPane.add(save, 1, 24, 2, 1);
    }

    /* Create button to start and stop simulation on specified map. */
    public void createStopStartButton(GridPane gridPane, AbstractWorldMap map, int row){
        Button stopStart = new Button("Stop");
        stopStart.setAlignment(Pos.CENTER);
        stopStart.setDefaultButton(true); // clicking enter is equal to pressing this button
        stopStart.setOnAction(actionEvent -> {
            // get values from text fields
            if (map.active){
                map.active = false;
                stopStart.setText("Start");
            }
            else {
                map.active = true;
                stopStart.setText("Stop");
            }
        });
        gridPane.add(stopStart, 0, row, 1, 1);
    }

    /* Draw simulation on specified map. */
    public void draw(GridPane gridPane, AbstractWorldMap map, DataTracking dataTracking) throws Exception {
        gridPane.getChildren().clear();

        setColors(gridPane);

        drawAnimals(gridPane, map, dataTracking);
        drawGrass(gridPane, map);
    }

    /* Initialize grid for tracking animals and grass. */
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

    /* Draw all grass on specified map. */
    private void drawGrass(GridPane gridPane, AbstractWorldMap map) throws Exception {
        for (Vector2D position : map.grasses.keySet()){
            gridPane.add(new GuiElementBox(map.grasses.get(position)).box, position.x, position.y, 1, 1);
        }
    }

    /* Draw all animals on specified mao. */
    private void drawAnimals(GridPane gridPane, AbstractWorldMap map, DataTracking dataTracking) throws Exception{
        Set<String> modes = map.getMode();
        for (Animal animal : map.animals.values()){
            GuiElementBox temp = new GuiElementBox(animal);
            temp.dataTracking = dataTracking;
            if (map.highlight && modes.contains(animal.genome.toString())){
                temp.highlight();
            }
            gridPane.add(temp.button, animal.position.x, animal.position.y, 1, 1);
        }
    }

    /* Set background of map. */
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

    /* Create button to start highlighting all animals whose genome is mode. */
    private Button startHighlight(GridPane gridPane, AbstractWorldMap map, DataTracking dataTracking){
        Button button = new Button();
        button.setText("Show modes");
        button.setOnAction(event -> {
            map.highlight = !map.highlight;
            try {
                drawAnimals(gridPane, map, dataTracking);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return button;
    }

    /* Create button to save current state of simulation to CSV file. */
    private Button saveToFile(DataTracking dataTracking, String filename, AbstractWorldMap map){
        Button button = new Button();
        button.setText("Save");
        button.setOnAction(event -> {
            if (!(map.active)){
                FileHandler fileHandler = new FileHandler(dataTracking);
                try {
                    fileHandler.saveToFile(filename);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
        return button;
    }

    /* Draw label that specifies how many times magical evolution occurred. */
    public void drawMagicLabel(AbstractWorldMap map, int row, GridPane layout){
        Label rolledMagic = new Label("Number of magical evolutions: " + map.copies);
        rolledMagic.setAlignment(Pos.CENTER);
        rolledMagic.setStyle("-fx-rotate: 90");
        layout.add(rolledMagic, 8, row, 1, 5);
    }
}
