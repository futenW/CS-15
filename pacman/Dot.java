package pacman;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import java.util.ArrayList;

// dot object pacman can eat and earn points
public class Dot implements Collidable {
    private Pane _root;
    private Circle _body; // javaFX shape
    private ArrayList<Collidable> _elements;
    private Label _scoreLabel;

    // create dot shape, add to root
    public Dot(Pane root, int centerX, int centerY, ArrayList<Collidable> elements, Label scoreLabel) {
        _root = root;
        _elements = elements;
        _scoreLabel = scoreLabel;

        // create dot shape
        _body = new Circle(centerX + Const.SQ_WIDTH /2, centerY + Const.SQ_WIDTH /2,
                Const.SQ_WIDTH /8, Color.rgb(255, 255, 255));
        _root.getChildren().add(_body);
    }

    // remove dot and add 10 to score upon collision
    public void collide() {
        // update score
        Game._score = Game._score + 10;
        _scoreLabel.setText("Score: " + Game._score);

        // remove dot logically and graphically
        _root.getChildren().remove(_body);
        _elements.remove(this);

        Game._numDotsAndEnergizers--; // for checking end-game
    }
}
