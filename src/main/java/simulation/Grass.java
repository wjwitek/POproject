package simulation;

public class Grass implements IMapElement {
    private Vector2D position;

    public Grass(Vector2D position){this.position = position;}

    @Override
    public Vector2D getPosition() {
        return position;
    }

    @Override
    public String getPath() {
        return "src\\main\\resources\\grass.png";
    }

    @Override
    public String toString(){
        return "Position: " + position.toString();
    }
}
