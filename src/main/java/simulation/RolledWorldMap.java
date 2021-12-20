package simulation;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import simulation.gui.App;

import java.util.Iterator;
import java.util.Map;

public class RolledWorldMap extends AbstractWorldMap{
    public RolledWorldMap(App app) {
        super(app);
    }

    public void moveAnimals(){
        //for each animal randomize type of movement, where should they be after
        for (Animal animal : animals.values()){
            animal.move();
            // handle animal going over the map
            if (animal.position.x < leftCorner.x){
                animal.position.x = 1 + rightCorner.x - (leftCorner.x - animal.position.x);
            }
            if (animal.position.y < leftCorner.y){
                animal.position.y = 1 + rightCorner.y - (leftCorner.y - animal.position.y);
            }
            if (animal.position.x > rightCorner.x){
                animal.position.x = animal.position.x - (rightCorner.x + 1) + leftCorner.x;
            }
            if (animal.position.y > rightCorner.y){
                animal.position.y = animal.position.y - (rightCorner.y + 1) + leftCorner.y;
            }
        }
        // if position has changed update animals
        Iterator<Map.Entry<Vector2D, Animal>> iter = animals.entries().iterator();
        MultiValuedMap<Vector2D, Animal> temp = new ArrayListValuedHashMap<>();
        while (iter.hasNext()){
            Map.Entry<Vector2D, Animal> entry = iter.next();
            if (!(entry.getKey().equals(entry.getValue().position))){
                iter.remove();
                temp.put(entry.getValue().getPosition().copy(), entry.getValue());
            }
        }
        animals.putAll(temp);
    }
}
