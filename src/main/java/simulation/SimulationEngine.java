package simulation;

import javafx.scene.layout.GridPane;
import simulation.gui.App;

public class SimulationEngine implements Runnable{
    // place where both maps are created and stored, for now only bounded one
    public final RolledWorldMap rolledMap;
    private final GridPane gridPane;

    public SimulationEngine(App app, GridPane newGridPane){
        rolledMap = new RolledWorldMap(app);
        gridPane = newGridPane;
    }

    @Override
    public void run() {
        //TODO: delete dead animals
        //TODO: consume plants
        //TODO: make children
        //TODO: add new plants
        // and do it for each map if it is active
        if (rolledMap.active){
            // move/rotate animals
            rolledMap.moveAnimals();
            try {
                rolledMap.app.drawAnimals(gridPane, rolledMap);
            }
            catch (Exception ex){
                System.out.println(ex.getMessage());
                System.exit(1);
            }
            // add new grass
            rolledMap.addGrass();
            // decrease energy at the end of the day
            rolledMap.decreaseEnergy();
        }
    }
}
