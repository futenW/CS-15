package doodlejump;

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
import java.util.ArrayList;

// main logic class, runs the "game" and all its animations, event handling, key handling
public class Game {
    private Doodle _doodle; // main character

    private ArrayList<Platform> _platformList;
    private Platform _topPlatform; // current highest platform generated

    private Pane _gamePane;
    private Timeline _timeline; // instance variable b/c need to call stop() method later on
    private boolean _keyDown; // check to see if a key is already being pressed (for smooth movement timelines)

    private int _score; // number of platforms doodle has hit
    private Label _scoreDisplay;

    // initializes doodle character & list of platforms; creates initial set of platforms, timeline, & keyhandling
    public Game(Pane game) {
        _doodle = new Doodle(game);
        _platformList = new ArrayList<Platform>();
        _gamePane = game;

        _topPlatform = new Platform(_gamePane, Constants.WINDOW_HEIGHT/2, Constants.WINDOW_HEIGHT); // very first platform
        _platformList.add(_topPlatform);

        while (_topPlatform.getY() > 0) { // create initial set of platforms
            _topPlatform = new Platform(_gamePane, _topPlatform.getX(), _topPlatform.getY()); // each new platform's position based off last platform's position
            _platformList.add(_topPlatform);
        }

        this.setupTimeline(); // set up bouncing animation

        // set up key handling
        _keyDown = false; // a key is not currently being pressed
        _gamePane.addEventHandler(KeyEvent.KEY_PRESSED, new KeyHandler());
        _gamePane.addEventHandler(KeyEvent.KEY_RELEASED, new KeyReleaser());
        _gamePane.requestFocus();
        _gamePane.setFocusTraversable(true);

        // set up score
        _score = 0;
        _scoreDisplay = new Label("Score: " + _score);
        _scoreDisplay.setStyle("-fx-font-size: 20");
        _gamePane.getChildren().add(_scoreDisplay);
        _scoreDisplay.setLayoutX(10);
        _scoreDisplay.setLayoutY(5);
    }

    // set up timeline for bouncing animations, collisions, scrolling, etc.
    private void setupTimeline() {
        _timeline = new Timeline(new KeyFrame(Duration.seconds(Constants.DURATION), new BounceHandler()));
        _timeline.setCycleCount(Animation.INDEFINITE);
        _timeline.play();
    }

    // moves doodle left and right based on left/right arrow keys
    private class KeyHandler implements EventHandler<KeyEvent> {
        // move doodle's horizontal position
        @Override
        public void handle(KeyEvent e) {
            KeyCode keyPressed = e.getCode();
            if (keyPressed == KeyCode.RIGHT && !_keyDown) { // don't start another timeline while key is still down
                _doodle.setupRightTimeline();
                _keyDown = true;
            } else if (keyPressed == KeyCode.LEFT && !_keyDown) { // don't start another timeline while key is still down
                _doodle.setupLeftTimeline();
                _keyDown = true;
            }
            e.consume();
        }
    }

    // handles making the doodle stop moving if the left/right arrow keys are released
    private class KeyReleaser implements EventHandler<KeyEvent> {
        // stops the respective timeline if right/left key is released
        @Override
        public void handle(KeyEvent e) {
            KeyCode keyReleased = e.getCode();
            if (keyReleased == KeyCode.RIGHT) {
                _doodle.stopRightTimeline();
                _keyDown = false;
            } else if (keyReleased == KeyCode.LEFT) {
                _doodle.stopLeftTimeline();
                _keyDown = false;
            }
            e.consume();
        }
    }

    // animations that makes the doodle bounce, collide with platforms, scroll up, end game, etc.
    private class BounceHandler implements EventHandler<ActionEvent> {
        // set up animations
        @Override
        public void handle(ActionEvent event) {
            _doodle.updateVelocity();
            _doodle.updatePosition(); // calculates "potential" position
            if (_doodle.getCurrentY() < Constants.WINDOW_HEIGHT/2 && _doodle.getVelocity() < 0) { // if doodle passes midpoint while moving up
                int difference = Constants.WINDOW_HEIGHT/2 - _doodle.getCurrentY();
                for (int i = 0; i < _platformList.size(); i++) { // traverses through all the platforms
                    _platformList.get(i).setY(_platformList.get(i).getY() + difference); // move all the platforms down
                }
                _doodle.setMidPosition(); // update doodle graphical position to the middle of the screen
                if (_topPlatform.getY() > 0) { // generate new platforms
                    _topPlatform = new Platform(_gamePane, _topPlatform.getX(), _topPlatform.getY());
                    _platformList.add(_topPlatform);
                }
            } else { // make doodle bounce as usual
                _doodle.setNewPosition(); // update doodle's graphical position
                if (_doodle.getCurrentY() > Constants.WINDOW_HEIGHT) { // if doodle has fallen to bottom
                    _gamePane.setOnKeyPressed(null);
                    _timeline.stop();

                    // show "Game Over" on screen
                    Label gameOver = new Label("Game Over");
                    gameOver.setStyle("-fx-font-size: 50");
                    gameOver.setTextFill(Color.RED);
                    _gamePane.getChildren().add(gameOver);
                    gameOver.setLayoutX(Constants.WINDOW_WIDTH / 4.5);
                    gameOver.setLayoutY(Constants.WINDOW_HEIGHT / 2);
                }
            }
            for (int i = 0; i < _platformList.size(); i++) { // traverses through all the platforms
                Platform temp = _platformList.get(i); // the current platform in this iteration
                if (_doodle.getVelocity() >= 0 && _doodle.intersects(temp.getX(), temp.getY(), Constants.PLATFORM_WIDTH, Constants.PLATFORM_HEIGHT)) { // collision
                    _doodle.setVelocity(Constants.REBOUND_VELOCITY); // make doodle bounce up if it hits platform
                    _doodle.setGameStarted(true); // user has hit a platform already

                    if (!temp.getAlreadyHit()) { // if platform has not been hit yet
                        _score++;
                        _scoreDisplay.setText("Score: " + _score); // update score
                        temp.setAlreadyHit(true);
                    }
                }
                if (temp.getY() > Constants.WINDOW_HEIGHT) { // remove platform if it goes below the screen
                    _gamePane.getChildren().remove(temp.getRectangle());
                    _platformList.remove(temp);
                }
            }
        }
    }
}