package pacman;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;

// square object that represents each cell in the game board grid, keeps track of what lies in each cell
public class SmartSquare {
    private Pane _root;
    private boolean _isWall; // whether this square is a wall or not
    private ArrayList<Collidable> _elements; // arrayList to keep track of what lies in this cell
    private int _posX; // column position along board
    private int _posY; // row position along board

    // intialize vars, set position
    public SmartSquare(Pane root, int posX, int posY) {
        _root = root;
        _elements = new ArrayList<Collidable>();

        _posX = posX;
        _posY = posY;
    }

    // add an element to this square logically
    public void addElement(Collidable object) {
        _elements.add(object);
    }

    // remove an element from this square logically
    public void removeElement(Collidable object) {
        _elements.remove(object);
    }

    // return this square's list of objects
    public ArrayList<Collidable> getElements() {
        return _elements;
    }

    // return whether this square is a wall
    public boolean getIsWall() {
        return _isWall;
    }

    // set whether this square is a wall
    public void setIsWall(boolean isWall) {
        _isWall = isWall;

        if (_isWall == true) { // create wall block
            Rectangle wall = new Rectangle(_posX, _posY, Const.SQ_WIDTH, Const.SQ_WIDTH);
            wall.setFill(Color.rgb(0, 170, 255));
            _root.getChildren().add(wall);
        }
    }
}
