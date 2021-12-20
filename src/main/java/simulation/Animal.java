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
    public ArrayList<Integer> genome = new ArrayList<Integer>();
    public int energy;
    private ArrayList<Animal> children = new ArrayList<>();
    private final AbstractWorldMap map;

    public Animal(Vector2D startingPosition, int startingEnergy, AbstractWorldMap newMap){
        // constructor for random animal
        position = startingPosition;
        randomGenome();
        orientation = Orientation.values()[genome.get(randomGene())];
        energy = startingEnergy;
        map = newMap;
    }

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
    public String getPath() {
        switch (orientation){
            case NORTH -> {return "src\\main\\resources\\north.png";}
            case EAST -> {return "src\\main\\resources\\east.png";}
            case WEST -> {return "src\\main\\resources\\west.png";}
            case SOUTH -> {return "src\\main\\resources\\south.png";}
            case NORTHEAST -> {return "src\\main\\resources\\northeast.png";}
            case NORTHWEST -> {return "src\\main\\resources\\northwest.png";}
            case SOUTHEAST -> {return "src\\main\\resources\\southeast.png";}
            case SOUTHWEST -> {return "src\\main\\resources\\southwest.png";}
            default -> throw new IllegalArgumentException(orientation.toString() + " is not a legal orientation of animal.");
        }
    }

    public void randomGenome(){
        ArrayList<Integer> helperNums = new ArrayList<Integer>();
        for (int i = 0; i<31; i++) {
            helperNums.add(i);
        }
        Collections.shuffle(helperNums);
        // assumption: starting animal has to have non-zero chance of each type of movement
        // leave only first seven values
        List<Integer> temp = helperNums.subList(0, 6);
        Collections.sort(temp);
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

    public int randomGene(){
        // chose random element of genome -> chosen random move
        Random rand = new Random();
        return rand.nextInt(32);
    }

    public void eat(int newEnergy){ energy += newEnergy; }

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

    public Animal child(Animal other){
        Random rand = new Random();
        Animal child;
        if (rand.nextInt(2) == 0) {
            child = childGenome(other);
        }
        else{
            child = other.childGenome(this);
        }
        children.add(child);
        other.children.add(child);
        return child;
    }

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
