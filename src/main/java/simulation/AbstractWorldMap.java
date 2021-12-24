package simulation;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import simulation.gui.App;

import java.util.*;

public class AbstractWorldMap {
    public final Vector2D leftCorner = new Vector2D(0, 0);
    public final Vector2D rightCorner;
    public final Vector2D jungleLeftCorner;
    public final Vector2D jungleRightCorner;
    public MultiValuedMap<Vector2D, Animal> animals = new ArrayListValuedHashMap<>();
    public LinkedHashMap<Vector2D, Grass> grasses = new LinkedHashMap<>();
    public boolean active = true;
    public App app; // to take parameters from app, instead of passing them as arguments
    protected ArrayList<Vector2D> freeJungle = new ArrayList<>();
    protected ArrayList<Vector2D> freeSteppe = new ArrayList<>();
    public int totalEnergy = 0;
    public int totalLifeSpan = 0; // total life span of dead animals
    public int totalDead = 0;
    public LinkedHashMap<String, Integer> signatures = new LinkedHashMap<>();
    public int childrenOfTracked = 0;
    public int offspringOfTracked = 0;
    public boolean trackedAnimalDied = false;
    public Animal trackedAnimal;
    public int totalChildren = 0;
    public boolean highlight = false;

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

    public void decreaseEnergy(){
        for (Animal animal : animals.values()){
            animal.energy -= app.moveEnergy;
            totalEnergy -= app.moveEnergy;
            animal.lifeSpan += 1;
        }
    }

    public void genStartingAnimals(){
        // generate set of initial positions
        Set<Vector2D> initialPositions = new HashSet<>();
        Vector2D randGenerator = new Vector2D(0, 0);
        while (initialPositions.size() < app.startingAnimals){
            initialPositions.add(randGenerator.randomVectorWithin(leftCorner, rightCorner));
        }
        // initialize animal for each position
        for (Vector2D position: initialPositions){
            Animal newAnimal = new Animal(new Vector2D(position.x, position.y), app.startingEnergy, this);
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

    public boolean isCellInJungle(Vector2D cell){
        return cell.follows(jungleLeftCorner) && cell.precedes(jungleRightCorner);
    }

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

    private void freeCell(Vector2D position){
        if (isCellInJungle(position)){
            freeJungle.add(position);
        }
        else {
            freeSteppe.add(position);
        }
    }

    private void takeCell(Vector2D position){
        if (isCellInJungle(position)){
            freeJungle.remove(position);
        }
        else {
            freeSteppe.remove(position);
        }
    }

    protected void updateAnimals(){
        Iterator<Map.Entry<Vector2D, Animal>> iter = animals.entries().iterator();
        MultiValuedMap<Vector2D, Animal> temp = new ArrayListValuedHashMap<>();
        while (iter.hasNext()){
            Map.Entry<Vector2D, Animal> entry = iter.next();
            if (!(entry.getKey().equals(entry.getValue().position))){
                iter.remove();
                if (!(animals.containsKey(entry.getKey()))){
                    freeCell(entry.getKey());
                }
                temp.put(entry.getValue().getPosition().copy(), entry.getValue());
                takeCell(entry.getValue().getPosition());
            }
        }
        animals.putAll(temp);
    }

    public void moveAnimals() {
    }

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

    public void eatGrass(){
        for (Vector2D position : animals.keySet()){
            if (grasses.containsKey(position)){
                List<Animal> partialAnimal = (List<Animal>) animals.get(position);
                if (partialAnimal.size() == 1){
                    partialAnimal.get(0).energy += app.plantEnergy;
                    totalEnergy += app.plantEnergy;
                }
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
                Animal newAnimal = mother.child(father);
                animals.put(position, newAnimal);
                String sign = newAnimal.genome.toString();
                if (signatures.containsKey(sign)){
                    signatures.put(sign, signatures.get(sign) + 1);
                }
                else {
                    signatures.put(sign, 1);
                }
                totalChildren += 2;
            }
        }
    }

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
}
