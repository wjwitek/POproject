package simulation;

public enum Orientation {
    NORTH,
    NORTHEAST,
    EAST,
    SOUTHEAST,
    SOUTH,
    SOUTHWEST,
    WEST,
    NORTHWEST;

    public Orientation rotate(int rotation){
        // TODO: write a test
        if (rotation > 0 && rotation < 4){
            return Orientation.values()[(this.ordinal() + rotation) % 8];
        }
        else if (rotation > 4 && rotation < 8){
            int temp = this.ordinal() - (8 - rotation);
            if (temp < 0) {temp += 8;}
            return Orientation.values()[temp % 8];
        }
        else{
            throw new IllegalArgumentException(rotation + " is not a legal rotation value.");
        }
    }
}
