package simulation;

public class Grass implements IMapElement {
    private final Vector2D position;

    public Grass(Vector2D position){this.position = position;}

    @Override
    public Vector2D getPosition() {
        return position;
    }

    @Override
    public String getFileName() {
        return "grass.png";
    }

    @Override
    public String toString(){
        return "Position: " + position.toString();
    }
}
