package simulation;

import javafx.scene.layout.GridPane;
import simulation.gui.App;
import simulation.gui.DataTracking;

public class SimulationEngine implements Runnable{
    // place where both maps are created and stored, for now only bounded one
    public final RolledWorldMap rolledMap;
    public final BoundedWorldMap boundedMap;
    private final GridPane rolledGridPane;
    private final GridPane boundedGridPane;
    private final App app;

    public SimulationEngine(App newApp, GridPane newGridPane1, GridPane newGridPane2){
        app = newApp;
        rolledMap = new RolledWorldMap(app);
        boundedMap = new BoundedWorldMap(app);
        rolledGridPane = newGridPane1;
        boundedGridPane = newGridPane2;
    }

    @Override
    public void run() {
        if (rolledMap.active){
            dayOnMap(rolledMap, rolledGridPane, app.rolledDataTracker);
        }
        if (boundedMap.active){
            dayOnMap(boundedMap, boundedGridPane, app.boundedDataTracker);
        }
    }

    public void dayOnMap(AbstractWorldMap map, GridPane gridPane, DataTracking dataTracker){
        // remove all animals without energy from map
        map.killAnimals();
        // move each animal randomly
        map.moveAnimals();
        // eat grass where animal stepped yesterday
        map.eatGrass();
        // add new grass
        map.addGrass();
        // add children
        map.makeNewAnimals();
        // update data for statistics
        dataTracker.updateData();
        // draw current state of maps
        try {
            map.app.draw(gridPane, map);
        }
        catch (Exception ex){
            System.out.println(ex.getMessage());
            System.exit(1);
        }
        // decrease energy at the end of the day
        map.decreaseEnergy();
    }
}
