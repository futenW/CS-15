package tetris;

import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

// individual squares of each tetris piece (or board border)
public class TetrisSquare {
    private Pane _root;
    private Rectangle _square;

    private int _rowPos; // keeps track of piece's position within the board
    private int _colPos;

    // instantiates piece's square body and adds it to the root pane
    public TetrisSquare(Pane root) {
        _root = root;
        _square = new Rectangle(Constants.SQ_WIDTH, Constants.SQ_WIDTH);
        _root.getChildren().add(_square);
    }

    // update row position graphically and logically
    public void setRowPos(int rowPos) {
        _rowPos = rowPos;
        _square.setLayoutY(_rowPos * Constants.SQ_WIDTH);
    }

    // update column position graphically and logically
    public void setColPos(int colPos) {
        _colPos = colPos;
        _square.setLayoutX(_colPos * Constants.SQ_WIDTH);
    }

    // returns the logical row position
    public int getRowPos() {
        return _rowPos;
    }

    // returns the logical column position
    public int getColPos() {
        return _colPos;
    }

    // returns the square's graphical body
    public Rectangle getSquare() {
        return _square;
    }

    // change the formatting style of the square
    public void setStyle(String style) {
        _square.setStyle(style);
    }
}
