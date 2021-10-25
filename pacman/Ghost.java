package pacman;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import java.util.LinkedList;
import java.util.Queue;

// Ghost objects that chase pacman (blinky, inky, pinky, & clyde)
public class Ghost implements Collidable {
    private Pane _root;
    private Rectangle _body; // ghost's javaFX shape

    private boolean _isInPen; // internal record of whether ghost is in pen
    private boolean _scatterReached;
    private int _startOffset; // blinky's offset location for when he resets back in the pen

    private SmartSquare[][] _board; // from Game class
    private GhostPen _ghostPen; // from Game class
    private Label _scoreLabel;; // from Game class
    private Label _livesLabel;; // from Game class

    private Ghost _ghost1;; // from Game class (the other three ghosts)
    private Ghost _ghost2;; // from Game class
    private Ghost _ghost3;; // from Game class
    private Pacman _pacman;; // from Game class

    private int _i; private int _j; // ghost's current position
    private int _lastI; private int _lastJ; // ghost's last position
    private int _startI; private int _startJ; // ghost's reset position

    // instantiate variables, create ghost character, update internal record of position(s)
    public Ghost(Pane root, int centerX, int centerY, Paint fill, SmartSquare[][] board, boolean isInPen,
                 Label scoreLabel, Label livesLabel, GhostPen ghostPen, int startOffset) {
        _root = root;
        _board = board;
        _isInPen = isInPen;
        _scatterReached = false;
        _scoreLabel = scoreLabel;
        _livesLabel = livesLabel;
        _ghostPen = ghostPen;
        _startOffset = startOffset;

        // create ghost character
        _body = new Rectangle(centerX*Const.SQ_WIDTH, centerY*Const.SQ_WIDTH, Const.SQ_WIDTH, Const.SQ_WIDTH);
        _body.setFill(fill);
        _root.getChildren().add(_body);

        // update internal record of position(s)
        _i = centerY;
        _j = centerX;
        _lastI = centerY;
        _lastJ = centerX + 1;
    }

    // let this ghost (know) about other ghosts for resetting positions after colliding w/ pacman
    public void meetOtherGhosts(Ghost ghost1, Ghost ghost2, Ghost ghost3) { // (can't do in constructor or null pointer exception)
        _ghost1 = ghost1;
        _ghost2 = ghost2;
        _ghost3 = ghost3;
    }

    // let this ghost know about pacman for resetting his position (can't do in constructor or null pointer)
    public void meetPacman(Pacman pacman) {
        _pacman = pacman;
    }

    // get whether the ghost has reached their scatter positions
    public boolean isScatterReached() {
        return _scatterReached;
    }

    // update whether the ghost has reached their scatter position
    public void setScatterReached(boolean isScatterReached) {
        _scatterReached = isScatterReached;
    }

    // update whether ghost is currently in pen
    public void setIsInPen(boolean isInPen) {
        _isInPen = isInPen;
    }

    // get offset distance for when ghost goes back to pen (zero except for blinky)
    public int getStartOffset() {
        return _startOffset;
    }

    // get the ghost's starting row
    public int getStartI() {
        return _startI;
    }

    // get the ghost's starting column
    public int getStartJ() {
        return _startJ;
    }

    // take note of ghost's starting row
    public void setStartI(int i) {
        _startI = i;
    }

    // take note of ghost's starting column
    public void setStartJ(int j) {
        _startJ = j;
    }

    // update ghost's last row
    public void setLastI(int i) {
        _lastI = i;
    }

    // update ghost's last column
    public void setLastJ(int j) {
        _lastJ = j;
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

    // return the ghost's javaFX shape
    public Rectangle getBody() {
        return _body;
    }

    // reset back to pen if in fright mode, reset all characters in scatter and chase modes
    public void collide() {
        if (Game._inFrightMode) { // if in fright mode
            // update score
            Game._score = Game._score + 200;
            _scoreLabel.setText("Score: " + Game._score);

            // move ghost to pen logically and graphically
            _ghostPen.getGhostQueue().add(this);
            this.setIsInPen(true);
            _board[_startI][_startJ].addElement(this);
            _board[_i][_j].removeElement(this);
            _lastI = _i; _lastJ = _j;
            _i = _startI; _j = _startJ;

            // returns ghost to starting position
            _body.setLayoutX(0);
            _body.setLayoutY(0 + _startOffset); // offset for _blinky, who needs to start in the pen this thime
        } else { // in scatter/chase mode
            // update lives
            Game._lives = Game._lives - 1;
            _livesLabel.setText("Lives: " + Game._lives);

            // reset pieces
            this.resetGhost(this);
            _ghost1.resetGhost(_ghost1);
            _ghost2.resetGhost(_ghost2);
            _ghost3.resetGhost(_ghost3);
            _pacman.resetPosition();
        }
    }

    // move a (generic) ghost back to their starting position
    public void resetGhost(Ghost ghost) {
        _ghostPen.getGhostQueue().add(ghost); // to queue
        ghost.setIsInPen(true); // update internal record

        // update logical position
        _board[ghost.getStartI()][ghost.getStartJ()].addElement(ghost);
        _board[ghost.getI()][ghost.getJ()].removeElement(ghost);

        ghost.setLastI(ghost.getI());
        ghost.setLastJ(ghost.getJ());
        ghost.setI(ghost.getStartI());
        ghost.setJ(ghost.getStartJ());

        // update graphical position
        _body.setLayoutX(0);
        _body.setLayoutY(0 + ghost.getStartOffset());
    }

    // ghost's breadth-first search targeting algorithm
    public Direction ghostBFS(BoardCoordinate target) {
        BoardCoordinate ghost = new BoardCoordinate(_i, _j, false); // ghost's current location
        Direction[][] directions = new Direction[Const.NUM_SQ][Const.NUM_SQ]; // 2D array of directions
        Queue<BoardCoordinate> queue = new LinkedList<BoardCoordinate>(); // ghost pen queue

        BoardCoordinate closestSquare = null;
        int closestDistance = Const.BOARD_WIDTH*2; // no cell can be farther from its target than this

        // Step 0: initial population of the queue
        if (!_board[ghost.getRow()-1][ghost.getColumn()].getIsWall() && !(ghost.getRow()-1 == _lastI && ghost.getColumn() == _lastJ)) {
            queue.add(new BoardCoordinate(ghost.getRow()-1, ghost.getColumn(), false));
            directions[ghost.getRow()-1][ghost.getColumn()] = Direction.UP;
        }
        if (!_board[ghost.getRow()+1][ghost.getColumn()].getIsWall() && !(ghost.getRow()+1 == _lastI && ghost.getColumn() == _lastJ)) {
            queue.add(new BoardCoordinate(ghost.getRow()+1, ghost.getColumn(), false));
            directions[ghost.getRow()+1][ghost.getColumn()] = Direction.DOWN;
        }
        if (ghost.getRow() == 11 && ghost.getColumn() == 0) {
            queue.add(new BoardCoordinate(ghost.getRow(), 22, false));
            directions[ghost.getRow()][22] = Direction.LEFT;
        } else if (!_board[ghost.getRow()][ghost.getColumn()-1].getIsWall() && !(ghost.getRow() == _lastI && ghost.getColumn()-1 == _lastJ)) {
            queue.add(new BoardCoordinate(ghost.getRow(), ghost.getColumn()-1, false));
            directions[ghost.getRow()][ghost.getColumn()-1] = Direction.LEFT;
        }
        if (ghost.getRow() == 11 && ghost.getColumn() == 22) {
            queue.add(new BoardCoordinate(ghost.getRow(), 0, false));
            directions[ghost.getRow()][0] = Direction.RIGHT;
        } else if (!_board[ghost.getRow()][ghost.getColumn()+1].getIsWall() && !(ghost.getRow() == _lastI && ghost.getColumn()+1 == _lastJ)) {
            queue.add(new BoardCoordinate(ghost.getRow(), ghost.getColumn()+1, false));
            directions[ghost.getRow()][ghost.getColumn()+1] = Direction.RIGHT;
        }

        // Step 1: check the current cell
        while (queue.isEmpty() == false) { // while queue is not empty
            BoardCoordinate current = queue.remove();
            int currentDistance = euclideanDistance(current, target);
            if (currentDistance < closestDistance) {
                closestDistance = currentDistance;
                closestSquare = current;
            }

            // Step 2: marking the neighbors
            if (!_board[current.getRow()-1][current.getColumn()].getIsWall() && directions[current.getRow()-1][current.getColumn()] == null) {
                queue.add(new BoardCoordinate(current.getRow()-1, current.getColumn(), false));
                directions[current.getRow()-1][current.getColumn()] = directions[current.getRow()][current.getColumn()];
            }
            if (!_board[current.getRow()+1][current.getColumn()].getIsWall() && directions[current.getRow()+1][current.getColumn()] == null) {
                queue.add(new BoardCoordinate(current.getRow()+1, current.getColumn(), false));
                directions[current.getRow()+1][current.getColumn()] = directions[current.getRow()][current.getColumn()];
            }
            if (current.getRow() == 11 && current.getColumn() == 0 && directions[11][22] == null) { // left
                queue.add(new BoardCoordinate(current.getRow(), 22, false));
                directions[current.getRow()][22] = directions[current.getRow()][current.getColumn()];
            } else if (!(current.getRow() == 11 && current.getColumn() == 0) && (!_board[current.getRow()][current.getColumn()-1].getIsWall() &&
                    directions[current.getRow()][current.getColumn()-1] == null)) {
                queue.add(new BoardCoordinate(current.getRow(), current.getColumn()-1, false));
                directions[current.getRow()][current.getColumn()-1] = directions[current.getRow()][current.getColumn()];
            }
            if (current.getRow() == 11 && current.getColumn() == 22 && directions[11][0] == null) { // right
                queue.add(new BoardCoordinate(current.getRow(), 0, false));
                directions[current.getRow()][0] = directions[current.getRow()][current.getColumn()];
            } else if (!(current.getRow() == 11 && current.getColumn() == 22) && (!_board[current.getRow()][current.getColumn()+1].getIsWall() &&
                    directions[current.getRow()][current.getColumn()+1] == null)) {
                queue.add(new BoardCoordinate(current.getRow(), current.getColumn()+1, false));
                directions[current.getRow()][current.getColumn()+1] = directions[current.getRow()][current.getColumn()];
            }
        }
        int i = closestSquare.getRow();
        int j = closestSquare.getColumn();
        return directions[i][j];
    }

    // calculate euclidean distance between two points
    private int euclideanDistance(BoardCoordinate coord1, BoardCoordinate coord2) {
        int firstI = coord1.getRow() * Const.SQ_WIDTH;
        int firstJ = coord1.getColumn() * Const.SQ_WIDTH;
        int secondI = coord2.getRow() * Const.SQ_WIDTH;
        int secondJ = coord2.getColumn() * Const.SQ_WIDTH;

        int lengthA = secondI - firstI;
        int lengthB = secondJ - firstJ;
        int hypotSquared = (lengthA*lengthA) + (lengthB*lengthB);
        return (int)Math.sqrt(hypotSquared);
    }
}
