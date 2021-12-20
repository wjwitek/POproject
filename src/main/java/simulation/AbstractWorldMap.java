package simulation;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import simulation.gui.App;

import java.util.*;

public class AbstractWorldMap {
    private double jungleChance = 0.8; // chance that new grass is spawned in jungle as opposed to steppe
    protected int newGrass = 5; // amount of grass spawned each day TODO: change to app input
    protected final Vector2D leftCorner;
    protected final Vector2D rightCorner;
    private final Vector2D jungleLeftCorner;
    private final Vector2D jungleRightCorner;
    public MultiValuedMap<Vector2D, Animal> animals = new ArrayListValuedHashMap<>();
    public LinkedHashMap<Vector2D, Grass> grasses = new LinkedHashMap<>();
    public boolean active = true;
    public App app; // to take parameters from app, instead of passing them as arguments

    public AbstractWorldMap(App newApp){
        app = newApp;
        leftCorner = new Vector2D(0, 0);
        rightCorner = new Vector2D(app.width, app.height);
        jungleLeftCorner = new Vector2D(5, 5); //TODO: change to be calculated based on jungleRatio
        jungleRightCorner = new Vector2D(10, 10);
        // generate initial animals
        genStartingAnimals();
        // set starting grass
        addGrass();
    }

    public void decreaseEnergy(){
        for (Animal animal : animals.values()){
            animal.energy -= app.moveEnergy;
        }
    }

    public void genStartingAnimals(){
        // generate set of initial positions
        Set<Vector2D> initialPositions = new HashSet<Vector2D>();
        Vector2D randGenerator = new Vector2D(0, 0);
        while (initialPositions.size() < app.startingAnimals){
            initialPositions.add(randGenerator.randomVectorWithin(leftCorner, rightCorner));
        }
        // initialize animal for each position
        for (Vector2D position: initialPositions){
            Animal newAnimal = new Animal(new Vector2D(position.x, position.y), app.startingEnergy, this);
            animals.put(position, newAnimal);
        }
    }

    public boolean isCellFree(Vector2D cell){
        return !(animals.containsKey(cell) || grasses.containsKey(cell));
    }

    public boolean isCellInJungle(Vector2D cell){
        return cell.follows(jungleLeftCorner) && cell.precedes(jungleRightCorner);
    }

    public boolean freeCells(Vector2D left, Vector2D right){
        for (int i = left.x; i < right.x; i++){
            for (int j = left.y; j < right.y; j++){
                if (isCellFree(new Vector2D(i, j))){
                    return true;
                }
            }
        }
        return false;
    }

    public void addGrass(){
        int counter = 0;
        Random rand = new Random();
        while (freeCells(leftCorner, rightCorner) && counter < newGrass){
            // decide whether new grass should be in jungle or steppe
            if (freeCells(jungleLeftCorner, jungleRightCorner) && rand.nextInt(100) < (app.jungleRatio * 100)){
                int x = rand.nextInt(jungleRightCorner.x - jungleLeftCorner.x) + jungleLeftCorner.x;
                int y = rand.nextInt(jungleRightCorner.y - jungleLeftCorner.y) + jungleLeftCorner.y;
                Vector2D position = new Vector2D(x, y);
                while (!(isCellFree(position))){
                    position.x = rand.nextInt(jungleRightCorner.x - jungleLeftCorner.x) + jungleLeftCorner.x;
                    position.y = rand.nextInt(jungleRightCorner.y - jungleLeftCorner.y) + jungleLeftCorner.y;
                }
                grasses.put(position, new Grass(position.copy()));
                counter += 1;
            }
            else {
                int x = rand.nextInt(rightCorner.x - leftCorner.x) + leftCorner.x;
                int y = rand.nextInt(rightCorner.y - leftCorner.y) + leftCorner.y;
                Vector2D position = new Vector2D(x, y);
                while (!(isCellFree(position)) && isCellInJungle(position)){
                    position.x = rand.nextInt(rightCorner.x - leftCorner.x) + leftCorner.x;
                    position.y = rand.nextInt(rightCorner.y - leftCorner.y) + leftCorner.y;
                }
                grasses.put(position, new Grass(position.copy()));
                counter += 1;
            }
        }
    }
}
