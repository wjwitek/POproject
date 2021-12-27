package simulation;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import simulation.gui.App;

import java.util.*;

public class AbstractWorldMap {
    // Vectors defining map bounds
    public final Vector2D leftCorner = new Vector2D(0, 0);
    public final Vector2D rightCorner;
    public final Vector2D jungleLeftCorner;
    public final Vector2D jungleRightCorner;
    // objects on map
    public MultiValuedMap<Vector2D, Animal> animals = new ArrayListValuedHashMap<>();
    public LinkedHashMap<Vector2D, Grass> grasses = new LinkedHashMap<>();
    // to take parameters from app, instead of passing them as arguments
    public App app;
    // lists of free cells, to speed up spawning new grass
    protected ArrayList<Vector2D> freeJungle = new ArrayList<>();
    protected ArrayList<Vector2D> freeSteppe = new ArrayList<>();
    // statistics fo tracking data
    public int totalEnergy = 0;
    public int totalLifeSpan = 0;
    public int totalDead = 0;
    public int totalChildren = 0;
    // for tracking current mode
    public LinkedHashMap<String, Integer> signatures = new LinkedHashMap<>();
    // statistics about specific animal that is tracked
    public int childrenOfTracked = 0;
    public int offspringOfTracked = 0;
    public boolean trackedAnimalDied = false;
    public Animal trackedAnimal;
    // information for simulation engine
    public boolean highlight = false;
    public boolean active = true;
    public int copies = 0;

    public AbstractWorldMap(App newApp){
        app = newApp;
        rightCorner = new Vector2D(app.width, app.height);
        // calculate jungle corners based on jungleRatio
        int jungleWidth = (int)((double) app.width * app.jungleRatio);
        int jungleHeight = (int)((double) app.height * app.jungleRatio);
        jungleLeftCorner = new Vector2D((app.width - jungleWidth) / 2, (app.height - jungleHeight) / 2);
        jungleRightCorner = new Vector2D((app.width + jungleWidth) / 2, (app.height + jungleHeight) / 2);
        // generate initial animals
        genStartingAnimals();
        totalEnergy += animals.size() * app.startingEnergy;
        // generate free positions on map
        for (int i= leftCorner.x; i< rightCorner.x; i++){
            for (int j= leftCorner.y; j< rightCorner.y; j++){
                Vector2D position = new Vector2D(i, j);
                if (!(animals.containsKey(position))){
                    if (isCellInJungle(position)){
                        freeJungle.add(position);
                    }
                    else {
                        freeSteppe.add(position);
                    }
                }
            }
        }
    }

    /* Decrease energy for each animal and note that they lived one day more. */
    public void decreaseEnergy(){
        for (Animal animal : animals.values()){
            animal.energy -= app.moveEnergy;
            totalEnergy -= app.moveEnergy;
            animal.lifeSpan += 1;
        }
    }

    /* Generate starting set of animals. */
    public void genStartingAnimals(){
        // generate set of initial positions
        Set<Vector2D> initialPositions = new HashSet<>();
        Vector2D randGenerator = new Vector2D(0, 0);
        while (initialPositions.size() < app.startingAnimals){
            Vector2D temp = randGenerator.randomVectorWithin(leftCorner, rightCorner);
            initialPositions.add(temp);
            takeCell(temp);
        }
        // initialize animal for each position
        for (Vector2D position: initialPositions){
            Animal newAnimal = new Animal(position.copy(), app.startingEnergy, this);
            animals.put(position, newAnimal);
            String temp = newAnimal.genome.toString();
            // add genome to mode tracking
            if (signatures.containsKey(temp)){
                signatures.put(temp, signatures.get(temp) + 1);
            }
            else {
                signatures.put(temp, 1);
            }
        }
    }

    /* Check if given cell is in jungle. */
    public boolean isCellInJungle(Vector2D cell){
        return cell.follows(jungleLeftCorner) && cell.precedes(jungleRightCorner);
    }

    /* Add one grass in jungle and one on steppe. */
    public void addGrass(){
        Random rand = new Random();
        // add grass in jungle
        if (!(freeJungle.isEmpty())){
            Vector2D position = freeJungle.get(rand.nextInt(freeJungle.size()));
            grasses.put(position, new Grass(position.copy()));
            freeJungle.remove(position);
        }
        // add grass in steppe
        if (!(freeSteppe.isEmpty())){
            Vector2D position = freeSteppe.get(rand.nextInt(freeSteppe.size()));
            grasses.put(position, new Grass(position.copy()));
            freeSteppe.remove(position);
        }
    }

    /* Add cell to set of free cells. */
    private void freeCell(Vector2D position){
        if (isCellInJungle(position)){
            freeJungle.add(position);
        }
        else {
            freeSteppe.add(position);
        }
    }

    /* Note that cell is taken by grass or animal. */
    private void takeCell(Vector2D position){
        if (isCellInJungle(position)){
            freeJungle.remove(position);
        }
        else {
            freeSteppe.remove(position);
        }
    }

    /* Move animals to different keys if their position has changed. */
    protected void updateAnimals(){
        Iterator<Map.Entry<Vector2D, Animal>> iter = animals.entries().iterator();
        MultiValuedMap<Vector2D, Animal> temp = new ArrayListValuedHashMap<>();
        while (iter.hasNext()){
            Map.Entry<Vector2D, Animal> entry = iter.next();
            if (!(entry.getKey().equals(entry.getValue().position))){
                if (!(animals.containsKey(entry.getKey()))){
                    freeCell(entry.getKey());
                }
                iter.remove();
                temp.put(entry.getValue().getPosition().copy(), entry.getValue());
                takeCell(entry.getValue().getPosition());
            }
        }
        animals.putAll(temp);
    }

    /* Placeholder - each type of map executes moving animals differently, but it's useful to be able to call this
    method on AbstractWorldMap. */
    public void moveAnimals() {
    }

    /* Kill all animals with zero or negative energy. */
    public void killAnimals(){
        Iterator<Map.Entry<Vector2D, Animal>> iter = animals.entries().iterator();
        while (iter.hasNext()){
            Map.Entry<Vector2D, Animal> entry = iter.next();
            if (entry.getValue().energy < 0){
                totalEnergy -= entry.getValue().energy;
                totalDead += 1;
                totalLifeSpan += entry.getValue().lifeSpan;
                String sign = entry.getValue().genome.toString();
                if (signatures.get(sign) == 1){
                    signatures.remove(sign);
                }
                else {
                    signatures.put(sign, signatures.get(sign) - 1);
                }
                if (entry.getValue() == trackedAnimal){
                    trackedAnimalDied = true;
                }
                totalChildren -= entry.getValue().numOfChildren;
                iter.remove();
                if (!(animals.containsKey(entry.getKey()))){
                    freeCell(entry.getKey());
                }
            }
        }
    }

    /* Look for cells, where there is both animal and a grass, if so let the strongest animal it. */
    public void eatGrass(){
        for (Vector2D position : animals.keySet()){
            if (grasses.containsKey(position)){
                List<Animal> partialAnimal = (List<Animal>) animals.get(position);
                // if there is only one animal on this cell
                if (partialAnimal.size() == 1){
                    partialAnimal.get(0).energy += app.plantEnergy;
                    totalEnergy += app.plantEnergy;
                }
                // if there is more than one animal on this cell
                else{
                    ArrayList<Animal> distributeEnergy = highestEnergy(partialAnimal);
                    int energyForEach = app.plantEnergy / distributeEnergy.size();
                    for (Animal animal : distributeEnergy){
                        animal.energy += energyForEach;
                        totalEnergy += energyForEach;
                    }
                }
                // remove grass from map
                grasses.remove(position);
            }
        }
    }

    /* Returns list of animals with the highest energy on a call. */
    private ArrayList<Animal> highestEnergy(List<Animal> partialAnimal){
        // get list of animals with the highest energy
        ArrayList<Animal> answer = new ArrayList<>();
        answer.add(partialAnimal.get(0));
        for (int i=1; i < partialAnimal.size(); i++){
            if (answer.get(0).energy == partialAnimal.get(i).energy){
                answer.add(partialAnimal.get(i));
            }
            else if (answer.get(0).energy < partialAnimal.get(i).energy){
                answer.clear();
                answer.add(partialAnimal.get(i));
            }
        }
        return answer;
    }

    /* Look for cells, where there is more than one animal, if so make a new animal. */
    public void makeNewAnimals(){
        for (Vector2D position : animals.keySet()){
            if (animals.get(position).size() > 1){
                Animal mother = Collections.max(animals.get(position), Comparator.comparing(animal -> animal.energy));
                Animal father = Collections.min(animals.get(position), Comparator.comparing(animal -> animal.energy));
                for (Animal animal : animals.get(position)){
                    if (animal != mother && animal.energy > father.energy){
                        father = animal;
                    }
                }
                if (mother.energy >= app.startingEnergy / 2 && father.energy >= app.startingEnergy / 2) {
                    Animal newAnimal = mother.child(father);
                    animals.put(position, newAnimal);
                    String sign = newAnimal.genome.toString();
                    if (signatures.containsKey(sign)) {
                        signatures.put(sign, signatures.get(sign) + 1);
                    } else {
                        signatures.put(sign, 1);
                    }
                    totalChildren += 2;
                }
            }
        }
    }

    /* Get list of modes. */
    public Set<String> getMode(){
        // find most common signature
        int num = 0;
        for (String sign : signatures.keySet()){
            if (signatures.get(sign) > 0){
                num = signatures.get(sign);
            }
        }
        // make a set of all signatures with num occurrences
        Set<String> modes = new TreeSet<>();
        for (String sign : signatures.keySet()){
            if (signatures.get(sign) == num){
                modes.add(sign);
            }
        }
        return modes;
    }

    /* Rest statistics for tracked animal. */
    public void resetTracking(){
        for (Animal animal : animals.values()){
            animal.isTracked = false;
            animal.isChildOfTracked = false;
            animal.isDescendantOfTracked = false;
        }
        childrenOfTracked = 0;
        offspringOfTracked = 0;
        trackedAnimalDied = false;
    }

    /* Copy animals for magical evolution. */
    public void copyAnimals(){
        // generate set of initial positions
        Set<Vector2D> positions = new HashSet<>();
        Vector2D randGenerator = new Vector2D(0, 0);
        while (positions.size() < animals.size()){
            Vector2D temp = randGenerator.randomVectorWithin(leftCorner, rightCorner);
            if (!(animals.containsKey(temp))){
                positions.add(temp);
                takeCell(temp);
            }
        }
        // initialize animal for each position
        ArrayList<Animal> toCopy = new ArrayList<>(animals.values());
        ArrayList<Vector2D> initialPositions = new ArrayList<>(positions);
        for (int i=0; i<initialPositions.size(); i++){
            Vector2D position = initialPositions.get(i);
            Animal newAnimal = new Animal(position.copy(), app.startingEnergy, this);
            newAnimal.genome = (ArrayList<Integer>) toCopy.get(i).genome.clone();
            animals.put(position, newAnimal);
            String temp = newAnimal.genome.toString();
            if (signatures.containsKey(temp)){
                signatures.put(temp, signatures.get(temp) + 1);
            }
            else {
                signatures.put(temp, 1);
            }
        }
    }
}
