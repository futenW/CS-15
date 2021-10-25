package tetris;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

// runs logic of tetris game
public class Game {
    private Pane _root;
    private TetrisSquare[][] _board; // keeps track of taken up spaces within tetris game
    private Piece _currentPiece; // current piece that is falling

    private boolean _paused; // check if game is paused
    private boolean _gameOver; // check if game has ended

    private Pane _listenerPane; // event handler doesn't seem to "listen" to the root pane
    private Label _gamePaused; // instance var b/c need to access it from multiple places

    // initialize variables, board, creates first piece, timeline, and sets up keyhandling
    public Game(Pane root) {
        _root = root;
        _board = new TetrisSquare[Constants.ROW_NUM][Constants.COL_NUM];
        this.setupBorder(); // set up game border
        _currentPiece = new Piece(_root, _board);

        this.setupTimeline(); // generate new piece after last one is finished
        _listenerPane = new Pane(); // dummy pane to "listen" for key events b/c root node doesn't work
        _root.getChildren().add(_listenerPane);

        this.checkForRows(); // check if rows have been filled

        // set up key handling
        _listenerPane.addEventHandler(KeyEvent.KEY_PRESSED, new KeyHandler());
        _listenerPane.requestFocus();
        _listenerPane.setFocusTraversable(true);

        _paused = false;
        _gameOver = false;
    }

    // sets up timeline to generate new piece if last one is finished
    private void setupTimeline() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(.1), new PieceGenerator()));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    // sets up timeline that checks if rows have been filled
    private void checkForRows() {
        Timeline rowChecker = new Timeline(new KeyFrame(Duration.seconds(.01), new RowChecker()));
        rowChecker.setCycleCount(Animation.INDEFINITE);
        rowChecker.play();
    }

    // initializes border both graphically and logically
    private void setupBorder() {
        for (int i = 0; i < _board.length; i++) {
            for (int j = 0; j < _board[i].length; j++) {
                if (i == 0 || j == 0 || i == _board.length - 1 || j == _board[i].length - 1) { // if current node is around the edge
                    _board[i][j] = new TetrisSquare(_root);
                    _board[i][j].setRowPos(i);
                    _board[i][j].setColPos(j);
                    _board[i][j].setStyle("-fx-fill: grey; -fx-stroke: black; -fx-stroke-width: 3;");
                } else { // or else set its space to null
                    _board[i][j] = null;
                }
            }
        }
    }

    // generates new piece if last one is finished, also checks for end game conditions
    private class PieceGenerator implements EventHandler<ActionEvent> {
        // generate new piece and end game are mutually exclusive
        @Override
        public void handle(ActionEvent event) {
            if (this.topRowFilled() == true || this.noRoom() == true) { // if game should end
                _currentPiece.getTimeline().stop();
                _gameOver = true; // to stop keyhandling

                // show game over text
                Label gameOver = new Label("Game Over");
                gameOver.setStyle("-fx-font-size: 35");
                gameOver.setTextFill(Color.WHITE);
                _root.getChildren().add(gameOver);
                gameOver.setLayoutX(Constants.BOARD_WIDTH / 3.5);
                gameOver.setLayoutY(Constants.BOARD_HEIGHT / 3);
            } else if (_currentPiece.getIsFinished() == true) { // if last piece is finished moving
                _currentPiece = new Piece(_root, _board); // generate new piece
            }
        }

        // check if there is a piece in the top row of the board
        private boolean topRowFilled() {
            boolean isFilled = false;
            for (int j = 1; j < _board[0].length - 1; j++) {
                if (_board[1][j] != null) {
                    isFilled = true;
                }
            }
            return isFilled;
        }

        // checks if there is no room for a new piece to appear
        private boolean noRoom() {
            boolean noRoom = false;
            int count = 0; // counts how many null spaces there are
            for (int i = 2; i < _board.length; i++) { // traverse through every row but the top
                for (int j = 1; j < _board[i].length - 1; j++) {
                    if (_board[i][j] == null) {
                        count++;
                    }
                }
            }
            if (count < 4) { // there are are less than four spaces
                noRoom = true;
            }
            return noRoom;
        }
    }

    // moves pieces right/left, rotates, moves down, drops down, or pause
    private class KeyHandler implements EventHandler<KeyEvent> {
        // check which key is pressed and initiate appropriate response
        @Override
        public void handle(KeyEvent e) {
            KeyCode keyPressed = e.getCode();
            if (_paused == false && _gameOver == false) { // if game is not paused and not over
                if (keyPressed == KeyCode.RIGHT) {
                    this.moveRight();
                } else if (keyPressed == KeyCode.LEFT) {
                    this.moveLeft();
                } else if (keyPressed == KeyCode.UP) {
                    this.rotate();
                } else if (keyPressed == KeyCode.DOWN) {
                    this.moveDown();
                } else if (keyPressed == KeyCode.SPACE) {
                    while (this.canMoveDown() == true) { // keep moving down until it cant
                        this.moveDown();
                    }
                } else if (keyPressed == KeyCode.P && !_gameOver) { // as long as game is not over
                    _currentPiece.getTimeline().pause();
                    _paused = true;

                    // show paused text
                    _gamePaused = new Label("Paused");
                    _gamePaused.setStyle("-fx-font-size: 50");
                    _gamePaused.setTextFill(Color.WHITE);
                    _root.getChildren().add(_gamePaused);
                    _gamePaused.setLayoutX(Constants.BOARD_WIDTH / 3.5);
                    _gamePaused.setLayoutY(Constants.BOARD_HEIGHT / 3);
                }
            } else if (keyPressed == KeyCode.P) { // if game is paused and "P" has been pressed
                _currentPiece.getTimeline().play();
                _paused = false;
                _root.getChildren().remove(_gamePaused);
            }

            e.consume();
        }

        // visually moves piece down
        private void moveDown() {
            TetrisSquare[] current = _currentPiece.getFourSquares();
            if (this.canMoveDown() == true) {
                for (int i = 0; i < current.length; i++) {
                    current[i].setRowPos(current[i].getRowPos() + 1);
                }
            }
        }

        // checks whether piece can move down
        private boolean canMoveDown() {
            TetrisSquare[] current = _currentPiece.getFourSquares();
            boolean canMoveDown = true;
            for (int i = 0; i < current.length; i++) {
                if (_board[current[i].getRowPos() + 1][current[i].getColPos()] != null) {
                    canMoveDown = false;
                }
            }
            return canMoveDown;
        }

        // visually rotates piece
        private void rotate() {
            TetrisSquare[] current = _currentPiece.getFourSquares();
            int centerOfRotationX = current[0].getColPos();
            int centerOfRotationY = current[0].getRowPos();
            if (this.canRotate() == true && _currentPiece.getType() != "O") {
                for (int i = 0; i < current.length; i++) {
                    int newXLocation = centerOfRotationX - centerOfRotationY + current[i].getRowPos();
                    int newYLocation = centerOfRotationY + centerOfRotationX - current[i].getColPos();

                    current[i].setRowPos(newYLocation);
                    current[i].setColPos(newXLocation);
                }
            }
        }


        // checks whether piece can rotate
        private boolean canRotate() {
            boolean canRotate = true;
            TetrisSquare[] current = _currentPiece.getFourSquares();
            int centerOfRotationX = current[0].getColPos();
            int centerOfRotationY = current[0].getRowPos();
            for (int i = 0; i < current.length; i++) {
                int newXLocation = centerOfRotationX - centerOfRotationY + current[i].getRowPos();
                int newYLocation = centerOfRotationY + centerOfRotationX - current[i].getColPos();
                if (_board[newYLocation][newXLocation] != null) {
                    canRotate = false;
                }
            }
            return canRotate;
        }


        // visually moves piece right
        private void moveRight() {
            TetrisSquare[] current = _currentPiece.getFourSquares();
            if (this.canMoveRight() == true) {
                for (int i = 0; i < current.length; i++) {
                    current[i].setColPos(current[i].getColPos() + 1);
                }
            }
        }

        // visually moves piece left
        private void moveLeft() {
            TetrisSquare[] current = _currentPiece.getFourSquares();
            if (this.canMoveLeft() == true) {
                for (int i = 0; i < current.length; i++) {
                    current[i].setColPos(current[i].getColPos() - 1);
                }
            }
        }


        // checks if piece can move right
        private boolean canMoveRight() {
            boolean canMoveRight = true;
            TetrisSquare[] current = _currentPiece.getFourSquares();
            for (int i = 0; i < current.length; i++) {
                if (_board[current[i].getRowPos()][current[i].getColPos() + 1] != null) {
                    canMoveRight = false;
                }
            }
            return canMoveRight;
        }

        // checks if piece can move left
        private boolean canMoveLeft() {
            boolean canMoveLeft = true;
            TetrisSquare[] current = _currentPiece.getFourSquares();
            for (int i = 0; i < current.length; i++) {
                if (_board[current[i].getRowPos()][current[i].getColPos() - 1] != null) {
                    canMoveLeft = false;
                }
            }
            return canMoveLeft;
        }
    }

    // timeline to check whether row(s) have been filled and removes/shifts rows
    private class RowChecker implements EventHandler<ActionEvent> {
        // handles logically and graphically removing rows and shifting all rows above down
        @Override
        public void handle(ActionEvent event) {
            for (int i = 1; i < _board.length - 1; i++) { // traverse through the rows of the board
                if (this.rowFilled(i) == true) {
                    for (int j = 1; j < _board[i].length - 1; j++) { // traverse through columns
                        _root.getChildren().remove(_board[i][j].getSquare()); // graphically remove node at that row and column
                    }
                    for (int n = i; n >= 1; n--) { // traverse through rows at and above the filled row
                        for (int m = 1; m < _board[n].length - 1; m ++) { // traverse through columns
                            if (_board[n - 1][m] != null) { // if there is a square above
                                if (n != 1) { // if it is not the border
                                    _board[n - 1][m].setRowPos(_board[n - 1][m].getRowPos() + 1); // move down one row graphically
                                }
                            }
                            if (n != 1) { // if it is not the border
                                _board[n][m] = _board[n - 1][m]; // move down one row logically
                            }
                        }
                    }
                }
            }
        }

        // checks whether rows have been filled
        private boolean rowFilled(int i) {
            boolean rowFilled = true;
            for (int j = 0; j < _board[i].length; j++) {
                if (_board[i][j] == null) {
                    rowFilled = false;
                }
            }
            return rowFilled;
        }
    }
}
