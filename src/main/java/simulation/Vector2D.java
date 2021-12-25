package simulation;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class Vector2D {
    public int x;
    public int y;

    public Vector2D(int x, int y){
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString(){
        return "(" + this.x + ", " + this.y + ")";
    }

    public boolean precedes(Vector2D other){
        return this.x <= other.x && this.y <= other.y;
    }

    public boolean follows(Vector2D other){
        return this.x >= other.x && this.y >= other.y;
    }

    public Vector2D add(Vector2D other){
        int x = this.x + other.x;
        int y = this.y + other.y;
        return new Vector2D(x, y);
    }

    @Override
    public boolean equals(Object other){
        if (this == other)
            return true;
        if (!(other instanceof Vector2D that))
            return false;
        return this.x == that.x && this.y == that.y;
    }

    @Override
    public int hashCode(){
        return Objects.hash(this.x, this.y);
    }

    /* Modify vector, based on current orientation of an animal. */
    public void increment(Orientation orientation, int amount){
        switch (orientation){
            case NORTH -> y -= amount;
            case EAST -> x += amount;
            case SOUTHWEST -> {
                x -= amount;
                y += amount;
            }
            case SOUTH -> y += amount;
            case SOUTHEAST -> {
                y += amount;
                x += amount;
            }
            case NORTHWEST -> {
                x -= amount;
                y -= amount;
            }
            case WEST -> x -= amount;
            case NORTHEAST -> {
                x += amount;
                y -= amount;
            }
        }
    }

    /* Generate random vector that follows leftCorner and precedes rightCorner. */
    public Vector2D randomVectorWithin(Vector2D leftCorner, Vector2D rightCorner){
        int randomX = ThreadLocalRandom.current().nextInt(leftCorner.x, rightCorner.x + 1);
        int randomY = ThreadLocalRandom.current().nextInt(leftCorner.y, rightCorner.y + 1);
        return new Vector2D(randomX, randomY);
    }

    public Vector2D copy(){
        return new Vector2D(x, y);
    }
}
