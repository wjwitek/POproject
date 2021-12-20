import org.junit.jupiter.api.Test;
import simulation.Orientation;

import static org.junit.jupiter.api.Assertions.*;

public class OrientationTest {
    @Test
    public void rotationTest(){
        // rotate left
        assertEquals(Orientation.EAST, Orientation.SOUTHEAST.rotate(7));
        assertEquals(Orientation.NORTHEAST, Orientation.SOUTHEAST.rotate(6));
        assertEquals(Orientation.NORTH, Orientation.SOUTHEAST.rotate(5));
        // rotate right
        assertEquals(Orientation.SOUTH, Orientation.SOUTHEAST.rotate(1));
        assertEquals(Orientation.SOUTHWEST, Orientation.SOUTHEAST.rotate(2));
        assertEquals(Orientation.WEST, Orientation.SOUTHEAST.rotate(3));
        // check if rotation goes over 0 correctly
        assertEquals(Orientation.NORTHWEST, Orientation.NORTHEAST.rotate(6));
        assertEquals(Orientation.NORTHEAST, Orientation.NORTHWEST.rotate(2));
    }
}
