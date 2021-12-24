package simulation.gui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import simulation.Animal;
import simulation.IMapElement;

import java.io.FileInputStream;
import java.io.InputStream;

public class GuiElementBox {
    VBox box;
    Button button;
    DataTracking dataTracking;

    public GuiElementBox(IMapElement object) throws Exception{
        // import image
        InputStream stream = new FileInputStream(object.getPath());
        Image objectImage = new Image(stream, 15, 15, true, true);
        ImageView objectImageView = new ImageView(objectImage);

        // create virtual box and image
        this.box = new VBox (objectImageView);

        // crate label if object is an animal and make it a button
        if (object instanceof Animal){
            Label position = new Label(String.valueOf(((Animal) object).energy));
            box.getChildren().add(position);

            button = new Button();
            button.setStyle("-fx-border-color: transparent");
            button.setStyle("-fx-background-color: transparent");
            button.setGraphic(box);

            // add action for button
            button.setOnAction(event -> dataTracking.drawAnimalStats((Animal) object));
        }

        box.setAlignment(Pos.CENTER);
    }

    public void highlight(){
        button.setStyle("-fx-border-color: purple");
    }
}
