package simulation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/*
Animals position is numerated as in GridPane.
*/

public class Animal implements IMapElement{
    public Vector2D position;
    private Orientation orientation;
    public ArrayList<Integer> genome = new ArrayList<>();
    public int energy;
    private AbstractWorldMap map;
    public int lifeSpan = 1;
    public boolean isChildOfTracked = false;
    public boolean isDescendantOfTracked = false;
    public boolean isTracked = false;
    public int numOfChildren = 0;

    /* Constructor for random animal. */
    public Animal(Vector2D startingPosition, int startingEnergy, AbstractWorldMap newMap){
        position = startingPosition;
        randomGenome();
        orientation = Orientation.values()[genome.get(randomGene())];
        energy = startingEnergy;
        map = newMap;
    }

    /* Constructor for predetermined animal. */
    public Animal(int startingEnergy, AbstractWorldMap newMap){
        map = newMap;
        energy = startingEnergy;
    }

    @Override
    public String toString(){
        return "Position: " + position.toString() + "\nsimulation.Orientation: " + orientation.toString();
    }

    @Override
    public Vector2D getPosition() {
        return position;
    }

    @Override
    public String getFileName() {
        switch (orientation){
            case NORTH -> {return "north.png";}
            case EAST -> {return "east.png";}
            case WEST -> {return "west.png";}
            case SOUTH -> {return "south.png";}
            case NORTHEAST -> {return "northeast.png";}
            case NORTHWEST -> {return "northwest.png";}
            case SOUTHEAST -> {return "southeast.png";}
            case SOUTHWEST -> {return "southwest.png";}
            default -> throw new IllegalArgumentException(orientation + " is not a legal orientation of animal.");
        }
    }

    /* Build random genome. */
    public void randomGenome(){
        ArrayList<Integer> helperNums = new ArrayList<>();
        for (int i = 0; i<31; i++) {
            helperNums.add(i);
        }
        Collections.shuffle(helperNums);
        // assumption: starting animal has to have non-zero chance of each type of movement
        // leave only first seven values
        List<Integer> temp = helperNums.subList(0, 6);
        Collections.sort(temp);
        // create signature as array
        // create list representing genome
        int[] nums = {0, 1, 2, 3, 4, 5, 6, 7};
        int index = 0;
        for (int i=0; i<32; i++){
            if (index < 6 && temp.get(index) == i - 1){
                index ++;
            }
            genome.add(nums[index]);
        }
    }

    /* Chose random element of genome -> chosen random move. */
    public int randomGene(){
        Random rand = new Random();
        return rand.nextInt(32);
    }

    /* Create genome for child, based on its parents. */
    public Animal childGenome(Animal other){
        Animal child = new Animal((int) ((energy + other.energy) * 0.25), this.map);
        child.position = new Vector2D(position.x, position.y);
        // this is animal that give left part of genome
        // count where genome of first animal is cut
        int firstRatio = (int)(((float) energy / (float)(energy + other.energy)) * 32.0);
        child.genome = new ArrayList<>(genome.subList(0, firstRatio));
        child.genome.addAll(other.genome.subList(firstRatio, 32));
        return child;
    }

    /* Create child animal, based on its parents. */
    public Animal child(Animal other){
        Random rand = new Random();
        Animal child;
        if (rand.nextInt(2) == 0) {
            child = childGenome(other);
        }
        else{
            child = other.childGenome(this);
        }
        // give child orientation and energy from parents
        child.orientation = Orientation.values()[child.genome.get(child.randomGene())];
        child.energy = (int) (0.25 * (double) (energy + other.energy));
        // reduce parents energy
        energy -= (int) (0.25 * (double) energy);
        other.energy -= (int) (0.25 * (double) other.energy);
        // inherit map
        child.map = map;
        // inherit tracking
        if (isTracked || other.isTracked){
            child.isChildOfTracked = true;
            map.childrenOfTracked += 1;
        }
        else if (isChildOfTracked || isDescendantOfTracked || other.isChildOfTracked || other.isDescendantOfTracked){
            child.isDescendantOfTracked = true;
            map.offspringOfTracked += 1;
        }
        // add to children counter
        numOfChildren += 1;
        other.numOfChildren += 1;
        return child;
    }

    /* Randomly move an animal. */
    public void move(){
        // randomize gene
        Random rand = new Random();
        int randMove = genome.get(rand.nextInt(32));
        if (randMove == 0){
            position.increment(orientation, 1);
        }
        else if (randMove == 4){
            position.increment(orientation, -1);
        }
        else{
            orientation = orientation.rotate(randMove);
        }
    }
}
