package pacman;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

public class Pacman {
    private Pane _root;
    private Circle _body; // pacman's jafaFX shape
    private SmartSquare[][] _board; // from Game class

    private int _i; private int _j; // pacman's current position
    private int _startingI; private int _startingJ; // pacman's reset position

    private Timeline _movePacmanRight; // instance vars used to stop() the timeline(s) later
    private Timeline _movePacmanLeft;
    private Timeline _movePacmanUp;
    private Timeline _movePacmanDown;

    private boolean _isMovingRight; // trackers for move validity
    private boolean _isMovingLeft;
    private boolean _isMovingUp;
    private boolean _isMovingDown;

    // initialize vars, create pacman character, add to root
    public Pacman(Pane root, int centerX, int centerY, SmartSquare[][] board) {
        _root = root;
        _board = board;

        _isMovingRight = false; _isMovingLeft = false; _isMovingUp = false; _isMovingDown = false;

        // create Pacman character
        _body = new Circle(centerX+Const.SQ_WIDTH/2, centerY+Const.SQ_WIDTH/2, Const.SQ_WIDTH/2, Color.rgb(250, 230, 0));
        _root.getChildren().add(_body);
    }

    // take note of pacman's starting row for resetting later
    public void setStartingI(int i) {
        _startingI = i;
    }

    // take note of pacman's starting column for resetting later
    public void setStartingJ(int j) {
        _startingJ = j;
    }

    // get pacman's javaFX shape
    public Circle getBody() {
        return _body;
    }

    // setter for row index
    public void setI(int i) {
        _i = i;
    }

    // setter for column index
    public void setJ(int j) {
        _j = j;
    }

    // getter for row index
    public int getI() {
        return _i;
    }

    // getter for column index
    public int getJ() {
        return _j;
    }

    // get pacman's move right timeline
    public Timeline getMovePacmanRight() {
        return _movePacmanRight;
    }

    // get pacman's move left timeline
    public Timeline getMovePacmanLeft() {
        return _movePacmanLeft;
    }

    // get pacman's move up timeline
    public Timeline getMovePacmanUp() {
        return _movePacmanUp;
    }

    // get pacman's move down timeline
    public Timeline getMovePacmanDown() {
        return _movePacmanDown;
    }

    // getter for whether pacman is moving right
    public boolean getIsMovingRight() {
        return _isMovingRight;
    }

    // getter for whether pacman is moving left
    public boolean getIsMovingLeft() {
        return _isMovingLeft;
    }

    // getter for whether pacman is moving up
    public boolean getIsMovingUp() {
        return _isMovingUp;
    }

    // getter for whether pacman is moving down
    public boolean getIsMovingDown() {
        return _isMovingDown;
    }

    // update whether pacman is moving right
    public void setIsMovingRight(boolean isMovingRight) {
        _isMovingRight = isMovingRight;
    }

    // update whether pacman is moving left
    public void setIsMovingLeft(boolean isMovingLeft) {
        _isMovingLeft = isMovingLeft;
    }

    // update whether pacman is moving up
    public void setIsMovingUp(boolean isMovingUp) {
        _isMovingUp = isMovingUp;
    }

    // update whether pacman is moving down
    public void setIsMovingDown(boolean isMovingDown) {
        _isMovingDown = isMovingDown;
    }

    // logically and graphically reset pacman's position
    public void resetPosition() {
        _i = _startingI; _j = _startingJ;
        _body.relocate(_j * Const.SQ_WIDTH, _i * Const.SQ_WIDTH);
    }

    // sets up timeline for pacman to move right
    public void movePacmanRight() {
        if (_board[_i][_j+1].getIsWall() == false && _isMovingLeft == false && _isMovingRight == false) {
            _movePacmanRight = new Timeline(new KeyFrame(Duration.seconds(.2), new MoveRight()));
            _movePacmanRight.setCycleCount(Animation.INDEFINITE);
            _movePacmanRight.play();
            _isMovingRight = true;
        }
    }

    // sets up timeline for pacman to move left
    public void movePacmanLeft() {
        if (_board[_i][_j-1].getIsWall() == false && _isMovingRight == false && _isMovingLeft == false) {
            _movePacmanLeft = new Timeline(new KeyFrame(Duration.seconds(.2), new MoveLeft()));
            _movePacmanLeft.setCycleCount(Animation.INDEFINITE);
            _movePacmanLeft.play();
            _isMovingLeft = true;
        }
    }

    // sets up timeline for pacman to move up
    public void movePacmanUp() {
        if (_board[_i-1][_j].getIsWall() == false && _isMovingDown == false && _isMovingUp == false) {
            _movePacmanUp = new Timeline(new KeyFrame(Duration.seconds(.2), new MoveUp()));
            _movePacmanUp.setCycleCount(Animation.INDEFINITE);
            _movePacmanUp.play();
            _isMovingUp = true;
        }
    }

    // sets up timeline for pacman to move down
    public void movePacmanDown() {
        if (_board[_i+1][_j].getIsWall() == false && _isMovingUp == false && _isMovingDown == false) {
            _movePacmanDown = new Timeline(new KeyFrame(Duration.seconds(.2), new MoveDown()));
            _movePacmanDown.setCycleCount(Animation.INDEFINITE);
            _movePacmanDown.play();
            _isMovingDown = true;
        }
    }

    // animation that moves pacman to the right continuously
    private class MoveRight implements EventHandler<ActionEvent> {
        // logically and graphically move pacman to the right
        @Override
        public void handle(ActionEvent event) {
            if (_i == 11 && _j == 22) {
                int currentPos = (int)_body.getCenterX();
                currentPos = currentPos - currentPos + Const.SQ_WIDTH/2;
                _body.setCenterX(currentPos);

                _j = 0;
            } else if (_board[_i][_j +1].getIsWall()) {
                _movePacmanRight.stop();
                _isMovingRight = false;
            } else {
                // graphically move pacman right
                int currentPos = (int)_body.getCenterX();
                currentPos = currentPos + Const.SQ_WIDTH;
                _body.setCenterX(currentPos);

                // logically move pacman right
                _j++;
            }
        }
    }

    // animation that moves pacman to the left continuously
    private class MoveLeft implements EventHandler<ActionEvent> {
        // logically and graphically move pacman to the left
        @Override
        public void handle(ActionEvent event) {
            if (_i == 11 && _j == 0) {
                int currentPos = (int)_body.getCenterX();
                currentPos = currentPos + Const.BOARD_WIDTH - Const.SQ_WIDTH;
                _body.setCenterX(currentPos);

                _j = 22;
            } else if (_board[_i][_j - 1].getIsWall()) {
                _movePacmanLeft.stop();
                _isMovingLeft = false;
            } else {
                // move pacman left graphically
                int currentPos = (int)_body.getCenterX();
                currentPos = currentPos - Const.SQ_WIDTH;
                _body.setCenterX(currentPos);

                // move pacman left logically
                _j--;
            }
        }
    }

    // animation that moves pacman up continuously
    private class MoveUp implements EventHandler<ActionEvent> {
        // logically and graphically move pacman up
        @Override
        public void handle(ActionEvent event) {
            if (_board[_i - 1][_j].getIsWall()) {
                _movePacmanUp.stop();
                _isMovingUp = false;
            } else {
                // move pacman up graphically
                int currentPos = (int)_body.getCenterY();
                currentPos = currentPos - Const.SQ_WIDTH;
                _body.setCenterY(currentPos);

                // move pacman up logically
                _i--;
            }
        }
    }

    // animation that moves pacman down continuously
    private class MoveDown implements EventHandler<ActionEvent> {
        // logically and graphically move pacman down
        @Override
        public void handle(ActionEvent event) {
            if (_board[_i + 1][_j].getIsWall()) {
                _movePacmanDown.stop();
                _isMovingDown = false;
            } else {
                // move pacman down graphically
                int currentPos = (int)_body.getCenterY();
                currentPos = currentPos + Const.SQ_WIDTH;
                _body.setCenterY(currentPos);

                // move pacman down logically
                _i++;
            }
        }
    }
}
