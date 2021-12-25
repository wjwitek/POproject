package simulation.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Screen;
import javafx.stage.Stage;
import simulation.*;

import java.io.FileNotFoundException;
import java.util.Set;


public class App extends Application {
    public int width; // number of columns
    public int height; // number of rows
    public float jungleRatio;
    public int startingEnergy;
    public int moveEnergy;
    public int plantEnergy;
    public int startingAnimals;
    private final int CELL_WIDTH = 20;
    private final int CELL_HEIGHT = 20;
    private SimulationEngine engine;
    public DataTracking boundedDataTracker;
    public DataTracking rolledDataTracker;
    private boolean magic = false;

    public VBox rBox;
    public VBox bBox;

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

    public void mapStage(Stage primaryStage) {
        // initialize GridPanes for both maps
        GridPane rolledGridPane = initGridPane();
        GridPane boundedGridPane = initGridPane();
        // create general layout of scene
        GridPane layout = new GridPane();
        layout.setHgap(20);
        layout.setVgap(20);
        //addLayoutConstraints(layout);
        layout.setPadding(new Insets(10, 10, 10, 10));

        // add grids to layout
        layout.add(rolledGridPane, 1, 0, 1, 5);
        layout.add(boundedGridPane, 1, 6, 1, 5);

        // start simulation
        engine = new SimulationEngine(this, rolledGridPane, boundedGridPane, magic);

        // add map labels
        Label rolledLabel = new Label("Rolled map");
        rolledLabel.setAlignment(Pos.CENTER);
        rolledLabel.setStyle("-fx-rotate: -90");
        rolledLabel.minHeight(100);
        layout.add(rolledLabel, 0, 0, 1, 5);

        Label boundedLabel = new Label("Bounded map");
        boundedLabel.setAlignment(Pos.CENTER);
        boundedLabel.setStyle("-fx-rotate: -90");
        layout.add(boundedLabel, 0, 6, 1, 5);

        // add buttons to layout
        createStopStartButton(layout, engine.rolledMap, 2, 0);
        createStopStartButton(layout, engine.boundedMap, 12, 6);

        // add grids for animal statistics
        GridPane rolledAnimalStats = new GridPane();
        GridPane boundedAnimalStats = new GridPane();
        layout.add(rolledAnimalStats, 4, 3, 1, 2);
        layout.add(boundedAnimalStats, 4, 9, 1, 2);

        // start tracking data
        boundedDataTracker = new DataTracking(engine.boundedMap, boundedAnimalStats);
        rolledDataTracker = new DataTracking(engine.rolledMap, rolledAnimalStats);

        // add charts to grid
        layout.add(rolledDataTracker.allChart(), 3, 0, 2, 5);
        layout.add(boundedDataTracker.allChart(), 3, 6, 2, 5);

        // find and display mode
        ScrollPane rolledMode = new ScrollPane();
        rBox = new VBox();
        rBox.setMaxWidth(400);
        rolledMode.setContent(rBox);
        layout.add(rolledMode, 4, 0, 1, 3);

        ScrollPane boundedMode = new ScrollPane();
        bBox = new VBox();
        bBox.setMaxWidth(400);
        boundedMode.setContent(bBox);
        layout.add(boundedMode, 4, 6, 1, 3);

        // add buttons to highlight animals
        layout.add(startHighlight(rolledGridPane, engine.rolledMap, rolledDataTracker), 2, 2, 1, 1);
        layout.add(startHighlight(boundedGridPane, engine.boundedMap, boundedDataTracker), 2, 8, 1, 1);

        // add buttons to save files
        layout.add(saveToFile(rolledDataTracker, "D:\\2021_22Z\\PO_projekt\\src\\main\\resources\\raports\\rolled.csv",
                engine.rolledMap), 2, 4, 1, 1);
        layout.add(saveToFile(boundedDataTracker, "D:\\2021_22Z\\PO_projekt\\src\\main\\resources\\raports\\bounded.csv",
                engine.boundedMap), 2, 10, 1, 1);

        //Rectangle2D screenBounds = Screen.getPrimary().getBounds();
        Scene scene = new Scene(layout, 1520, 820);

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
        //primaryStage.setFullScreen(true);
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
                                     TextField jungleRatioField, TextField startAnimals, Stage primaryStage,
                                   RadioButton radioButton){
        Button save = new Button("Start");
        save.setAlignment(Pos.CENTER);
        save.setDefaultButton(true); // clicking enter is equal to pressing this button
        save.setOnAction(actionEvent -> {
            // get values from text fields
            boolean correctValues = true;
            try {
                getSimParams(widthField, heightField, startEnergyField, moveEnergyField, plantEnergyField, jungleRatioField, startAnimals);
                magic = radioButton.isSelected();
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

    public void createStopStartButton(GridPane gridPane, AbstractWorldMap map, int col, int row){
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
        gridPane.add(stopStart, col, row, 1, 1);
    }

    public void draw(GridPane gridPane, AbstractWorldMap map, DataTracking dataTracking) throws Exception {
        gridPane.getChildren().clear();

        setColors(gridPane);

        drawAnimals(gridPane, map, dataTracking);
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

    private void drawGrass(GridPane gridPane, AbstractWorldMap map) throws Exception {
        for (Vector2D position : map.grasses.keySet()){
            gridPane.add(new GuiElementBox(map.grasses.get(position)).box, position.x, position.y, 1, 1);
        }
    }

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

    private Button startHighlight(GridPane gridPane, AbstractWorldMap map, DataTracking dataTracking){
        Button button = new Button();
        button.setText("Modes");
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

    private Button saveToFile(DataTracking dataTracking, String filename, AbstractWorldMap map){
        Button button = new Button();
        button.setText("Save");
        button.setOnAction(event -> {
            if (!(map.active)){
                System.out.println(0);
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

    private void addLayoutConstraints(GridPane layout){
        // add column constraints
        ColumnConstraints col = new ColumnConstraints(40);
        layout.getColumnConstraints().add(col);
        col = new ColumnConstraints(560);
        layout.getColumnConstraints().add(col);
        col = new ColumnConstraints(60);
        layout.getColumnConstraints().add(col);
        col = new ColumnConstraints(400);
        layout.getColumnConstraints().add(col);
        col = new ColumnConstraints(400);
        layout.getColumnConstraints().add(col);

        // add row constraints
        for (int i=0; i<11; i++){
            RowConstraints row = new RowConstraints(50);
            layout.getRowConstraints().add(row);
        }
    }
}
