package pacman;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.control.Label;
import java.util.ArrayList;

// energizer object pacman and eat and power-up to eat ghosts
public class Energizer implements Collidable {
    private Pane _root;
    private Circle _body; // javaFX shape
    private ArrayList<Collidable> _elements;
    private Label _scoreLabel;

    // create energizer body and add to root
    public Energizer(Pane root, int centerX, int centerY, ArrayList<Collidable> elements, Label scoreLabel) {
        _root = root;
        _elements = elements;
        _scoreLabel = scoreLabel;

        // create energizer shape
        _body = new Circle(centerX+ Const.SQ_WIDTH /2, centerY+ Const.SQ_WIDTH /2,
                Const.SQ_WIDTH /4, Color.rgb(255, 255, 255));
        _root.getChildren().add(_body);
    }

    // remove energizer and add 100 to score upon collision
    public void collide() {
        // update score
        Game._score = Game._score + 100;
        _scoreLabel.setText("Score: " + Game._score);

        // remove energizer logically and graphically
        _root.getChildren().remove(_body);
        _elements.remove(this);

        Game._inFrightMode = true; // switch to fright mode

        Game._numDotsAndEnergizers--; // for checking end-game
    }
}
