package cartoon;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

// creates a new heart
public class Heart {
    // make heart from composite shapes, pinkify it, and add it to the given pane
    public Heart(Pane heartPane, int posX, int posY, int size) {
        // make new composite shapes
        Rectangle heartCenter = new Rectangle(posX, posY,size*2,size*2); // shape 1
        Circle heartLeft = new Circle(posX+size/5, posY+size/5, size); // shape 2
        Circle heartRight = new Circle(posX+size*1.8, posY+size/5, size); // shape 3
        Circle fourthShape = new Circle(posX+size/5, posY+size/5, size); // shape 4
        Circle fifthShape = new Circle(posX+size*1.8, posY+size/5, size); // shape 5
        heartCenter.setRotate(45);

        // create pink heart from composite shapes
        Shape heart = Shape.union(heartCenter, heartLeft);
        heart = Shape.union(heart, heartRight);
        heart = Shape.union(heart, fourthShape);
        heart = Shape.union(heart, fifthShape);
        heart.setFill(Color.rgb(Constants.HEART_RED, Constants.HEART_GREEN, Constants.HEART_BLUE));
        heart.setFocusTraversable(false);

        // add to pane
        heartPane.getChildren().add(heart);
    }
}
