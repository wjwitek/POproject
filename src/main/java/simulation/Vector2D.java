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

    public Vector2D upperRight(Vector2D other){
        int x = Math.max(this.x, other.x);
        int y = Math.max(this.y, other.y);
        return new Vector2D(x, y);
    }

    public Vector2D lowerLeft(Vector2D other){
        int x = Math.min(this.x, other.x);
        int y = Math.min(this.y, other.y);
        return new Vector2D(x, y);
    }

    public Vector2D add(Vector2D other){
        int x = this.x + other.x;
        int y = this.y + other.y;
        return new Vector2D(x, y);
    }

    public Vector2D subtract(Vector2D other){
        int x = this.x - other.x;
        int y = this.y - other.y;
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

    public Vector2D opposite(){
        int x = -this.x;
        int y = -this.y;
        return new Vector2D(x, y);
    }

    public void modifyX(int newX){
        this.x = newX;
    }

    public void modifyY(int newY){
        this.y = newY;
    }

    @Override
    public int hashCode(){
        return Objects.hash(this.x, this.y);
    }

    public int compareX(Vector2D other){
        if (this.x < other.x){
            return -1;
        }
        if (this.x > other.x){
            return 1;
        }
        return Integer.compare(this.y, other.y);
    }

    public int compareY(Vector2D other){
        if (this.y < other.y){
            return -1;
        }
        if (this.y > other.y){
            return 1;
        }
        return Integer.compare(this.x, other.x);
    }

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

    // generate random vector that follows leftCorner and precedes rightCorner
    public Vector2D randomVectorWithin(Vector2D leftCorner, Vector2D rightCorner){
        int randomX = ThreadLocalRandom.current().nextInt(leftCorner.x, rightCorner.x + 1);
        int randomY = ThreadLocalRandom.current().nextInt(leftCorner.y, rightCorner.y + 1);
        return new Vector2D(randomX, randomY);
    }

    public Vector2D copy(){
        return new Vector2D(x, y);
    }
}
