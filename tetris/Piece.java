package tetris;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.Pane;
import javafx.util.Duration;


// overarching piece class for all seven configurations
public class Piece {
    private Pane _root;
    private TetrisSquare[][] _board;
    private TetrisSquare[] _fourSquares; // keeps track of this piece's four squares
    private String _color;
    private boolean _isFinished; // checks whether piece is done moving
    private String _type; // instance var for getter method
    private Timeline _timeline; // instance var for getter method

    // initializes four tetris pieces, sets positions, and initiates falling animation
    public Piece(Pane root, TetrisSquare[][] board) {
        _root = root;
        _board = board;
        _isFinished = false;

        // initialize the four tetris squares
        _fourSquares = new TetrisSquare[4];
        for (int i = 0; i < _fourSquares.length; i++) {
            _fourSquares[i] = new TetrisSquare(_root);
        }

        // position pieces
        int[][] coords = this.pickPiece();
        for (int i = 0; i < _fourSquares.length; i++) {
            _fourSquares[i].setRowPos(coords[i][0] / Constants.SQ_WIDTH + 1);
            _fourSquares[i].setColPos(coords[i][1] / Constants.SQ_WIDTH + Constants.COL_NUM/2);
            _fourSquares[i].setStyle(_color);
        }

        this.setupTimeline(); // initiate falling animation
    }

    // start timeline for falling animation
    private void setupTimeline() {
        _timeline = new Timeline(new KeyFrame(Duration.seconds(.25), new FallHandler()));
        _timeline.setCycleCount(Animation.INDEFINITE);
        _timeline.play();
    }

    // return whether piece is finished falling
    public boolean getIsFinished() {
        return _isFinished;
    }

    // returns the type of configuration for the piece
    public String getType() {
        return _type;
    }

    // returns timeline responsible for falling animations
    public Timeline getTimeline() {
        return _timeline;
    }

    // returns array of piece's four squares
    public TetrisSquare[] getFourSquares() {
        return _fourSquares;
    }

    // randomly picks which type of piece to generate and their respective color
    private int[][] pickPiece() {
        int whichPiece = (int)(Math.random() * 7);
        int[][] coords = null;
        switch (whichPiece) {
            case 0:
                coords = Constants.I_PIECE_COORDS;
                _color = "-fx-fill: red; -fx-stroke: black; -fx-stroke-width: 3;";
                break;
            case 1:
                coords = Constants.O_PIECE_COORDS;
                _color = "-fx-fill: magenta; -fx-stroke: black; -fx-stroke-width: 3;";
                _type = "O"; // to indicate in Game class that it shouldn't rotate
                break;
            case 2:
                coords = Constants.T_PIECE_COORDS;
                _color = "-fx-fill: orange; -fx-stroke: black; -fx-stroke-width: 3;";
                break;
            case 3:
                coords = Constants.L1_PIECE_COORDS;
                _color = "-fx-fill: yellow; -fx-stroke: black; -fx-stroke-width: 3;";
                break;
            case 4:
                coords = Constants.L2_PIECE_COORDS;
                _color = "-fx-fill: green; -fx-stroke: black; -fx-stroke-width: 3;";
                break;
            case 5:
                coords = Constants.S1_PIECE_COORDS;
                _color = "-fx-fill: blue; -fx-stroke: black; -fx-stroke-width: 3;";
                break;
            default:
                coords = Constants.S2_PIECE_COORDS;
                _color = "-fx-fill: aqua; -fx-stroke: black; -fx-stroke-width: 3;";
                break;
        }
        return coords;
    }

    // animations for the piece to fall
    private class FallHandler implements EventHandler<ActionEvent> {
        // if it can move down, graphically move the piece down
        @Override
        public void handle(ActionEvent event) {
            if (canMoveDown() == true) {
                for (int i = 0; i < _fourSquares.length; i++) {
                    _fourSquares[i].setRowPos(_fourSquares[i].getRowPos() + 1);
                }
            } else { // else, it can't move
                _isFinished = true;
                for (int n = 0; n < _fourSquares.length; n++) { // add piece's squares to board
                    int i = _fourSquares[n].getRowPos();
                    int j = _fourSquares[n].getColPos();

                    _board[i][j] = _fourSquares[n];
                    _timeline.stop(); // stop moving
                }
            }
        }

        // checks whether piece can move down
        private boolean canMoveDown() {
            boolean canMoveDown = true;
            for (int i = 0; i < _fourSquares.length; i++) {
                if (_board[_fourSquares[i].getRowPos() + 1][_fourSquares[i].getColPos()] != null) {
                    canMoveDown = false;
                }
            }
            return canMoveDown;
        }
    }
}
