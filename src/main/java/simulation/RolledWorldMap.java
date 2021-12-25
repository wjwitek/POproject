package simulation;

import simulation.gui.App;

public class RolledWorldMap extends AbstractWorldMap{
    public RolledWorldMap(App app) {
        super(app);
    }

    /* For each animal randomize type of movement. */
    public void moveAnimals(){
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
        updateAnimals();
    }
}
