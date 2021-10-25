package pacman;

import cs15.fnl.pacmanSupport.SquareType;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.event.ActionEvent;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import cs15.fnl.pacmanSupport.SupportMap;
import javafx.util.Duration;
import javafx.scene.paint.Color;
import javafx.scene.control.Label;
import java.util.ArrayList;

// logic class to run semantics of the game
public class Game {
    private Pane _root;
    private Pane _listenerPane; // dummy pane to listen to key events bc root is not able to listen
    private SmartSquare[][] _board; // keeps track of objects at every board location
    private GhostPen _ghostPen; // pen to house ghosts, FIFO
    private int _counter; // counts ghost iterations to time scatter/chase modes

    static boolean _inFrightMode; // track whether game is currently in fright mode, static bc used by energizer class
    private int _frightModeCount; // counter to time fright mode
    private Timeline _ghostTarget; // timeline for ghost BFS, instance var bc need to stop() later
    private Timeline _releaseGhosts; // timeline to periodically release ghosts from pen, instance var bc need to stop() later
    private boolean _gameIsOver; // instance var bc need to reference from multiple contexts

    private Pacman _pacman; // main character
    private Ghost _blinky; // individual ghosts
    private Ghost _clyde;
    private Ghost _pinky;
    private Ghost _inky;

    static int _score; // static bc each dot/energizer needs to reference the same number (object),
    static int _lives; // else will keep starting from zero if passed in as parameter
    static int _numDotsAndEnergizers; // counts number of dots/energizers left to check for end-game
    private Label _scoreLabel; // label showing score, used by other classes
    private Label _livesLabel; // label showing lives left, used by other classes

    // instantiates characters, board, variables, counters, game mechanics, etc.
    public Game(Pane root) {
        _root = root;
        _board = new SmartSquare[Const.NUM_SQ][Const.NUM_SQ];
        _ghostPen = new GhostPen();

        _counter = 0;
        _frightModeCount = 0;
        _inFrightMode = false;
        _numDotsAndEnergizers = 0;
        _gameIsOver = false;

        this.setupScoreAndLives(); // set up score and lives label, initialize numbers and show them

        this.populateBoard(); // add smartsquares to board
        this.makeSquares(); // read through SquaresMap and populate board with appropriate locations

        // must add to root after initializing board to show up on top
        _root.getChildren().add(_scoreLabel);
        _root.getChildren().add(_livesLabel);

        this.setupKeyHandling(); // set up pacman's movement

        this.getGhostsToTarget(); // set up BFS algorithm (chase, scatter, and when appropriate, fright)
        this.releaseGhosts(); // periodically release ghosts from pen, if not empty
        this.checkCollisions(); // check if pacman is in each square and run appropriate collision action
    }

    // set up timeline for ghost BFS (chase, scatter, and when appropriate, fright)
    private void getGhostsToTarget() {
        _ghostTarget = new Timeline(new KeyFrame(Duration.seconds(.25), new GhostTargeter()));
        _ghostTarget.setCycleCount(Animation.INDEFINITE);
        _ghostTarget.play();
    }

    // periodically releases ghosts from pen, if not empty
    private void releaseGhosts() {
        _releaseGhosts = new Timeline(new KeyFrame(Duration.seconds(3), new GhostReleaser()));
        _releaseGhosts.setCycleCount(Animation.INDEFINITE);
        _releaseGhosts.play();
    }

    // set up pacman movement key-handling
    private void setupKeyHandling() {
        _listenerPane = new Pane(); // dummy pane b/c root pane doesn't "listen" for key-handling
        _root.getChildren().add(_listenerPane);
        _listenerPane.addEventHandler(KeyEvent.KEY_PRESSED, new KeyHandler());
        _listenerPane.requestFocus();
        _listenerPane.setFocusTraversable(true);
    }

    // set up score and lives
    private void setupScoreAndLives() {
        _score = 0;
        _lives = 3;

        // format
        _scoreLabel = new Label("Score: " + _score);
        _livesLabel = new Label("Lives: " + _lives);
        _scoreLabel.setStyle("-fx-font-size: 20");
        _livesLabel.setStyle("-fx-font-size: 20");

        // center
        _scoreLabel.setLayoutX(Const.BOARD_WIDTH / 2 - Const.SQ_WIDTH);
        _scoreLabel.setLayoutY(Const.BOARD_WIDTH - Const.SQ_WIDTH);
        _livesLabel.setLayoutX(Const.SQ_WIDTH * 3);
        _livesLabel.setLayoutY(Const.BOARD_WIDTH - Const.SQ_WIDTH);
    }

    // constantly check for collisions throughout board w/ dots, energizers, ghosts
    private void checkCollisions() {
        Timeline checkCollisions = new Timeline(new KeyFrame(Duration.seconds(.001), new CollisionHandler()));
        checkCollisions.setCycleCount(Animation.INDEFINITE);
        checkCollisions.play();
    }

    // initialize 2D-array board of SmartSquares
    private void populateBoard() {
        for (int i = 0; i < _board.length; i++) {
            for (int j = 0; j < _board[i].length; j++) {
                _board[i][j] = new SmartSquare(_root, j * Const.SQ_WIDTH, i * Const.SQ_WIDTH);
            }
        }
    }

    // add appropriate objects to appropriate location(s)
    private void makeSquares() {
        SquareType[][] typeMap = SupportMap.getSupportMap();

        // traverse through board
        for (int i = 0; i < _board.length; i++) {
            for (int j = 0; j < _board[i].length; j++) {
                if (typeMap[i][j] == SquareType.WALL) { // add walls
                    _board[i][j].setIsWall(true);
                } else if (typeMap[i][j] == SquareType.DOT) { // add dots
                    _numDotsAndEnergizers++;
                    _board[i][j].addElement(new Dot(_root, j * Const.SQ_WIDTH, i * Const.SQ_WIDTH, _board[i][j].getElements(), _scoreLabel));
                } else if (typeMap[i][j] == SquareType.ENERGIZER) { // add energizers
                    _numDotsAndEnergizers++;
                    _board[i][j].addElement(new Energizer(_root, j * Const.SQ_WIDTH, i * Const.SQ_WIDTH, _board[i][j].getElements(), _scoreLabel));
                } else if (typeMap[i][j] == SquareType.PACMAN_START_LOCATION) { // place pacman
                    _pacman = new Pacman(_root, j*Const.SQ_WIDTH, i*Const.SQ_WIDTH, _board);
                    _pacman.setI(i); _pacman.setJ(j);
                    _pacman.setStartingI(i); _pacman.setStartingJ(j); // take note of reset location for pacman
                } else if (typeMap[i][j] == SquareType.GHOST_START_LOCATION) { // place ghosts
                    // initialize
                    _blinky = new Ghost(_root, j, (i-2), Const.BLINKY_COLOR, _board, false, _scoreLabel, _livesLabel, _ghostPen, 2*Const.SQ_WIDTH);
                    _clyde = new Ghost(_root, j, i, Const.CLYDE_COLOR, _board, true, _scoreLabel, _livesLabel, _ghostPen, 0);
                    _pinky = new Ghost(_root, (j-1), i, Const.PINKY_COLOR, _board, true, _scoreLabel, _livesLabel, _ghostPen, 0);
                    _inky = new Ghost(_root, (j+1), i, Const.INKY_COLOR, _board, true, _scoreLabel, _livesLabel, _ghostPen, 0);

                    // for reseting positions later when one collides w/ pacmcan
                    _blinky.meetOtherGhosts(_clyde, _pinky, _inky);
                    _clyde.meetOtherGhosts(_blinky, _pinky, _inky);
                    _pinky.meetOtherGhosts(_blinky, _clyde, _inky);
                    _inky.meetOtherGhosts(_blinky, _clyde, _pinky);

                    // take note of reset locations for each ghost
                    _blinky.setStartI(i); _blinky.setStartJ(j);
                    _clyde.setStartI(i); _clyde.setStartJ(j);
                    _inky.setStartI(i); _inky.setStartJ(j+1);
                    _pinky.setStartI(i); _pinky.setStartJ(j-1);

                    // add logically
                    _board[i-2][j].addElement(_blinky);
                    _board[i][j].addElement(_clyde);
                    _board[i][j-1].addElement(_pinky);
                    _board[i][j+1].addElement(_inky);

                    // add to pen
                    _ghostPen.getGhostQueue().add(_clyde);
                    _ghostPen.getGhostQueue().add(_pinky);
                    _ghostPen.getGhostQueue().add(_inky);
                }
            }
        }
        // for resetting pacman's location if collision occurs
        _blinky.meetPacman(_pacman);
        _pinky.meetPacman(_pacman);
        _clyde.meetPacman(_pacman);
        _inky.meetPacman(_pacman);
    }

    // key-handler for pacman's movement
    private class KeyHandler implements EventHandler<KeyEvent> {
        // move pacman left/right/up/down if possible
        @Override
        public void handle(KeyEvent e) {
            KeyCode keyPressed = e.getCode();
            if (!_gameIsOver) { // as long as game if not over yet
                if (keyPressed == KeyCode.RIGHT) {
                    if (_board[_pacman.getI()][_pacman.getJ() + 1].getIsWall() == false) {
                        if (_pacman.getIsMovingLeft()) { // stop moving left
                            _pacman.setIsMovingLeft(false);
                            _pacman.getMovePacmanLeft().stop();
                        }
                        if (_pacman.getIsMovingUp()) { // stop moving right
                            _pacman.setIsMovingUp(false);
                            _pacman.getMovePacmanUp().stop();
                        }
                        if (_pacman.getIsMovingDown()) { // stop moving down
                            _pacman.setIsMovingDown(false);
                            _pacman.getMovePacmanDown().stop();
                        }
                    }
                    _pacman.movePacmanRight(); // make pacman move right
                } else if (keyPressed == KeyCode.LEFT) {
                    if (_board[_pacman.getI()][_pacman.getJ() - 1].getIsWall() == false) {
                        if (_pacman.getIsMovingRight()) { // stop moving right
                            _pacman.setIsMovingRight(false);
                            _pacman.getMovePacmanRight().stop();
                        }
                        if (_pacman.getIsMovingUp()) { // stop moving up
                            _pacman.setIsMovingUp(false);
                            _pacman.getMovePacmanUp().stop();
                        }
                        if (_pacman.getIsMovingDown()) { // stop moving down
                            _pacman.setIsMovingDown(false);
                            _pacman.getMovePacmanDown().stop();
                        }
                    }
                    _pacman.movePacmanLeft(); // make pacman move left
                } else if (keyPressed == KeyCode.UP) {
                    if (_board[_pacman.getI() - 1][_pacman.getJ()].getIsWall() == false) {
                        if (_pacman.getIsMovingRight()) { // stop moving right
                            _pacman.setIsMovingRight(false);
                            _pacman.getMovePacmanRight().stop();
                        }
                        if (_pacman.getIsMovingLeft()) { // stop moving left
                            _pacman.setIsMovingLeft(false);
                            _pacman.getMovePacmanLeft().stop();
                        }
                        if (_pacman.getIsMovingDown()) { // stop moving down
                            _pacman.setIsMovingDown(false);
                            _pacman.getMovePacmanDown().stop();
                        }
                    }
                    _pacman.movePacmanUp(); // make pacman move up
                } else if (keyPressed == KeyCode.DOWN) {
                    if (_board[_pacman.getI() + 1][_pacman.getJ()].getIsWall() == false) {
                        if (_pacman.getIsMovingRight()) { // stop moving right
                            _pacman.setIsMovingRight(false);
                            _pacman.getMovePacmanRight().stop();
                        }
                        if (_pacman.getIsMovingLeft()) { // stop moving left
                            _pacman.setIsMovingLeft(false);
                            _pacman.getMovePacmanLeft().stop();
                        }
                        if (_pacman.getIsMovingUp()) { // stop moving up
                            _pacman.setIsMovingUp(false);
                            _pacman.getMovePacmanUp().stop();
                        }
                    }
                    _pacman.movePacmanDown(); // make pacman move down
                }
            }
            e.consume();
        }
    }

    // periodically release ghosts from pen if pen is not empty
    private class GhostReleaser implements EventHandler<ActionEvent> {
        // logically remove ghost(s) from queue and set graphical positon outside of pen
        @Override
        public void handle(ActionEvent event) {
            if(!_ghostPen.getGhostQueue().isEmpty()) {
                Ghost next = _ghostPen.getGhostQueue().remove();

                _board[next.getI() - 2][next.getJ()].addElement(next); // move along board
                _board[next.getI()][next.getJ()].removeElement(next);

                next.setLastI(next.getI()); // set ghost's internal record-keeping coordinates
                next.setI((next.getI() - 2));

                // graphically move
                int newPos = (int)next.getBody().getLayoutY() - 2 * Const.SQ_WIDTH;
                next.getBody().setLayoutY(newPos);

                next.setIsInPen(false); // set ghost's internal record-keeping
            }
        }
    }

    // handles ghosts' movement(s)
    private class GhostTargeter implements EventHandler<ActionEvent> {
        @Override
        // runs fright mode, scatter mode, and chase mode
        public void handle(ActionEvent event) {
            if (_inFrightMode) {
                // set fright colors
                _blinky.getBody().setFill(Const.FRIGHT_COLOR);
                _pinky.getBody().setFill(Const.FRIGHT_COLOR);
                _inky.getBody().setFill(Const.FRIGHT_COLOR);
                _clyde.getBody().setFill(Const.FRIGHT_COLOR);

                // randomly move ghosts
                this.moveGhost(_blinky, (int)(Math.random()*22), (int)(Math.random()*22));
                this.moveGhost(_clyde, (int)(Math.random()*22), (int)(Math.random()*22));
                this.moveGhost(_inky, (int)(Math.random()*22), (int)(Math.random()*22));
                this.moveGhost(_pinky, (int)(Math.random()*22), (int)(Math.random()*22));

                _frightModeCount++; // increment timer counter
                if (_frightModeCount > 28) { // 7 seconds
                    // stop and reset
                    _inFrightMode = false;
                    _frightModeCount = 0;
                }
            } else { // scatter mode and chase mode

                // reset original colors
                _blinky.getBody().setFill(Const.BLINKY_COLOR);
                _pinky.getBody().setFill(Const.PINKY_COLOR);
                _inky.getBody().setFill(Const.INKY_COLOR);
                _clyde.getBody().setFill(Const.CLYDE_COLOR);

                if (_counter <= 28) { // for the first 7 seconds

                    // target pacman
                    this.moveGhost(_blinky, _pacman.getI(), _pacman.getJ());
                    this.moveGhost(_clyde, _pacman.getI(), _pacman.getJ() + 2);
                    this.moveGhost(_inky, _pacman.getI() - 4, _pacman.getJ());
                    this.moveGhost(_pinky, _pacman.getI() + 1, _pacman.getJ() - 3);
                    _counter++;
                }
                else { // for the last 7 seconds

                        // target corners
                        if (_blinky.getI() == 1 && _blinky.getJ() == 1) {
                            _blinky.setScatterReached(true);
                        } else if (_blinky.getI() == 4 && _blinky.getJ() == 5) {
                            _blinky.setScatterReached(false);
                        }
                        if (_blinky.isScatterReached() == true) {
                            this.moveGhost(_blinky, 4, 5);
                        } else if (_blinky.isScatterReached() == false) {
                            this.moveGhost(_blinky, 0, 0);
                        }
                        if (_inky.getI() == 1 && _inky.getJ() == Const.NUM_SQ - 2) {
                            _inky.setScatterReached(true);
                        } else if (_inky.getI() == 4 && _inky.getJ() == 17) {
                            _inky.setScatterReached(false);
                        }
                        if (_inky.isScatterReached() == true) {
                            this.moveGhost(_inky, 4, 17);
                        } else if (_inky.isScatterReached() == false) {
                            this.moveGhost(_inky, 0, Const.NUM_SQ - 2);
                        }
                        if (_clyde.getI() == Const.NUM_SQ - 2 && _clyde.getJ() == 1) {
                            _clyde.setScatterReached(true);
                        } else if (_clyde.getI() == 19 && _clyde.getJ() == 9) {
                            _clyde.setScatterReached(false);

                            _board[_clyde.getI()][_clyde.getJ() - 1].addElement(_clyde);
                            _board[_clyde.getI()][_clyde.getJ()].removeElement(_clyde);

                            _clyde.setLastJ(_clyde.getJ());
                            _clyde.setJ((_clyde.getJ() - 1));

                            int newPos = (int)_clyde.getBody().getLayoutX() - Const.SQ_WIDTH;
                            _clyde.getBody().setLayoutX(newPos);
                        }
                        if (_clyde.isScatterReached() == true) {
                            this.moveGhost(_clyde, 19, 9);
                        } else if (_clyde.isScatterReached() == false) {
                            this.moveGhost(_clyde, Const.NUM_SQ - 1, 0);
                        }
                        if (_pinky.getI() == Const.NUM_SQ - 2 && _pinky.getJ() == Const.NUM_SQ - 2) {
                            _pinky.setScatterReached(true);
                        } else if (_pinky.getI() == 19 && _pinky.getJ() == 13) {
                            _pinky.setScatterReached(false);

                            _board[_pinky.getI()][_pinky.getJ() + 2].addElement(_pinky);
                            _board[_pinky.getI()][_pinky.getJ()].removeElement(_pinky);

                            _pinky.setLastJ(_pinky.getJ());
                            _pinky.setJ((_pinky.getJ() + 2));

                            int newPos = (int)_pinky.getBody().getLayoutX() + 2*Const.SQ_WIDTH;
                            _pinky.getBody().setLayoutX(newPos);
                        }
                        if (_pinky.isScatterReached() == true) {
                            this.moveGhost(_pinky, 19, 13);
                        } else if (_pinky.isScatterReached() == false) {
                            this.moveGhost(_pinky, Const.NUM_SQ - 1, Const.NUM_SQ - 1);
                        }
                    _counter++;
                    if (_counter > 56) { // once 14 seconds have been up
                        _counter = 0; // reset cycle
                    }
                }
            }

        }

        // method that runs BFS and implements ghost's target direction according to BFS
        private void moveGhost(Ghost ghost, int targetI, int targetJ) {
            Direction move = ghost.ghostBFS(new BoardCoordinate(targetI, targetJ, true)); // get target

                if (move == Direction.RIGHT) {
                    if (ghost.getI() == 11 && ghost.getJ() == 22) { // if at right tunnel
                        // logically move ghost to left tunnel
                        _board[11][0].addElement(ghost);
                        _board[11][22].removeElement(ghost);

                        ghost.setLastJ(ghost.getJ());
                        ghost.setJ(0);

                        // graphically
                        ghost.getBody().relocate(0, 11 * Const.SQ_WIDTH);
                    } else { // ghost is not at tunnel
                        _board[ghost.getI()][ghost.getJ() + 1].addElement(ghost);
                        _board[ghost.getI()][ghost.getJ()].removeElement(ghost);

                        ghost.setLastJ(ghost.getJ());
                        ghost.setJ((ghost.getJ() + 1));

                        int newPos = (int)ghost.getBody().getLayoutX() + Const.SQ_WIDTH;
                        ghost.getBody().setLayoutX(newPos);
                    }
                } else if (move == Direction.LEFT) {
                    if (ghost.getI() == 11 && ghost.getJ() == 0) { // if at left tunnel
                        // logically move ghost to left tunnel
                        _board[11][22].addElement(ghost);
                        _board[11][0].removeElement(ghost);

                        ghost.setLastJ(ghost.getJ());
                        ghost.setJ(22);

                        // graphically
                        ghost.getBody().relocate(22 * Const.SQ_WIDTH, 11 * Const.SQ_WIDTH);
                    } else { // ghost is not at tunnel
                        _board[ghost.getI()][ghost.getJ() - 1].addElement(ghost);
                        _board[ghost.getI()][ghost.getJ()].removeElement(ghost);

                        ghost.setLastJ(ghost.getJ());
                        ghost.setJ((ghost.getJ() - 1));

                        int newPos = (int)ghost.getBody().getLayoutX() - Const.SQ_WIDTH;
                        ghost.getBody().setLayoutX(newPos);
                    }
                } else if (move == Direction.UP) {
                    _board[ghost.getI() - 1][ghost.getJ()].addElement(ghost);
                    _board[ghost.getI()][ghost.getJ()].removeElement(ghost);

                    ghost.setLastI(ghost.getI());
                    ghost.setI((ghost.getI() - 1));

                    int newPos = (int)ghost.getBody().getLayoutY() - Const.SQ_WIDTH;
                    ghost.getBody().setLayoutY(newPos);
                } else if (move == Direction.DOWN) {
                    _board[ghost.getI() + 1][ghost.getJ()].addElement(ghost);
                    _board[ghost.getI()][ghost.getJ()].removeElement(ghost);

                    ghost.setLastI(ghost.getI());
                    ghost.setI((ghost.getI() + 1));

                    int newPos = (int)ghost.getBody().getLayoutY() + Const.SQ_WIDTH;
                    ghost.getBody().setLayoutY(newPos);
                }
        }
    }

    // continuously check for pacman collision(s)
    private class CollisionHandler implements EventHandler<ActionEvent> {
        // implement appropriate action events depening on collision object
        @Override
        public void handle(ActionEvent event) {
            // traverse through game board
            for (int i = 0; i < _board.length; i++) {
                for (int j = 0; j < _board[i].length; j++) {
                    if (i == _pacman.getI() && j == _pacman.getJ()) { // if pacman is in the square
                        ArrayList<Collidable> elements = _board[i][j].getElements();
                        for (int n = 0; n < elements.size(); n++) { // traverse through every element in square
                            elements.get(n).collide(); // perform collision action

                            // check for game end
                            if (_numDotsAndEnergizers <= 0 || _lives <= 0) {
                                _ghostTarget.stop();
                                _releaseGhosts.stop();
                                _gameIsOver = true;

                                // show game over text
                                Label gameOver = new Label("Game Over");
                                gameOver.setStyle("-fx-font-size: 50");
                                gameOver.setTextFill(Color.RED);
                                _root.getChildren().add(gameOver);
                                gameOver.setLayoutX(Const.BOARD_WIDTH / 2 - 4 * Const.SQ_WIDTH);
                                gameOver.setLayoutY(Const.BOARD_WIDTH / 2 - Const.SQ_WIDTH);
                            }
                        }
                    }
                }
            }
        }
    }
}
