package simulation;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import simulation.gui.App;

import java.util.Iterator;
import java.util.Map;

public class BoundedWorldMap extends AbstractWorldMap{
    public BoundedWorldMap(App app){super(app);}

    public void moveAnimals(){
        //for each animal randomize type of movement, where should they be after
        for (Animal animal : animals.values()){
            animal.move();
            // handle animal going over the map
            if (animal.position.x < leftCorner.x){
                animal.position.x = leftCorner.x;
            }
            if (animal.position.y < leftCorner.y){
                animal.position.y = leftCorner.y;
            }
            if (animal.position.x > rightCorner.x){
                animal.position.x = rightCorner.x;
            }
            if (animal.position.y > rightCorner.y){
                animal.position.y = rightCorner.y;
            }
        }
        // if position has changed update animals
        updateAnimals();
    }
}
