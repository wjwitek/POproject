package simulation;

import javafx.application.Application;
import simulation.gui.App;

public class World {
    public World(){}

    public static void main(String[] args){
        try {
            Application.launch(App.class, args);
        }
        catch (IllegalArgumentException ex){
            System.out.println(ex.getMessage());
            System.exit(1);
        }
    }
}
