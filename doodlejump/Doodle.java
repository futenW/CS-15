package doodlejump;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

// class for main doodle character and all its variables, methods, etc.
public class Doodle {
    private Rectangle _body;
    private int _currentVelocity;
    private int _currentY; // current y-position
    private boolean _gameStarted; // whether doodle has hit a platform already

    private Timeline _smoothRight; // timelines that animate doodle moving left/right smoothly
    private Timeline _smoothLeft; // need to make instance vars b/c need access from multiple methods

    // initialize doodle's body shape & position, set initial velocity to zero
    public Doodle(Pane doodlePane) {
        // create doodle character
        _body = new Rectangle(Constants.WINDOW_WIDTH/2 - Constants.DOODLE_WIDTH/2, // centers doodle completely
                Constants.WINDOW_HEIGHT - Constants.DOODLE_HEIGHT, Constants.DOODLE_WIDTH, Constants.DOODLE_HEIGHT);
        _body.setFill(Color.rgb(0, 216, 159));
        doodlePane.getChildren().add(_body);

        // initialize velocity and position
        _currentVelocity = 0;
        _currentY = (int) _body.getY();
        _gameStarted = false; // user has not hit a platform yet
    }

    // this method will be constantly called to inflict the effects of gravity on the doodle
    public void updateVelocity() { // enters if-condition only if user hasn't hit a platform already i.e. _gamedStarted == false
        if (_currentVelocity > 0 && _body.getY() >= Constants.WINDOW_HEIGHT - Constants.DOODLE_HEIGHT && !_gameStarted) {
            _currentVelocity = Constants.REBOUND_VELOCITY; // jump in place when game starts initially
        } else { // update velocity based on gravity acceleration constant
            _currentVelocity = (int)(_currentVelocity + Constants.GRAVITY * Constants.DURATION);
        }
    }

    // calculate "potential position" of doodle based on new velocity
    public void updatePosition() {
        _currentY = (int)(_currentY + _currentVelocity * Constants.DURATION);
    }

    // update doodle's graphical position
    public void setNewPosition() {
        _body.setY(_currentY);
    }

    // update doodle graphical position to the middle of the screen
    public void setMidPosition() {
        _currentY = Constants.WINDOW_HEIGHT / 2;
        _body.setY(_currentY);
    }

    // used to check and see if doodle has hit any platform shapes
    public boolean intersects(int x, int y, int width, int height) {
        return _body.intersects(x, y, width, height);
    }

    // getter method for doodle's current velocity
    public int getVelocity() {
        return _currentVelocity;
    }

    // setter method for doodle's velocity
    public void setVelocity(int velocity) {
        _currentVelocity = velocity;
    }

    // getter method for doodle's Y-position
    public int getCurrentY() {
        return _currentY;
    }

    // setter method for whether user has hit a platform already
    public void setGameStarted(boolean hasHitPlatform) {
        _gameStarted = hasHitPlatform;
    }

    // set up timeline that graphically moves the doodle right
    public void setupRightTimeline() {
        _smoothRight = new Timeline(new KeyFrame(Duration.seconds(Constants.DOODLE_SPEED), new MoveRightTimeline()));
        _smoothRight.setCycleCount(Animation.INDEFINITE);
        _smoothRight.play();
    }

    // set up timeline that graphically moves the doodle left
    public void setupLeftTimeline() {
        _smoothLeft = new Timeline(new KeyFrame(Duration.seconds(Constants.DOODLE_SPEED), new MoveLeftTimeline()));
        _smoothLeft.setCycleCount(Animation.INDEFINITE);
        _smoothLeft.play();
    }

    // stop the timeline that graphically moves the doodle right
    public void stopRightTimeline() {
        _smoothRight.stop();
    }

    // stop the timeline that graphically moves the doodle left
    public void stopLeftTimeline() {
        _smoothLeft.stop();
    }

    // timeline that graphically moves the doodle right
    private class MoveRightTimeline implements EventHandler<ActionEvent> {
        // shifts the doodle's X-position positively if it hasn't hit the right side yet
        @Override
        public void handle(ActionEvent event) {
            if (_body.getX() < Constants.WINDOW_WIDTH - Constants.DOODLE_WIDTH) { // if doodle hasn't hit right wall yet
                _body.setX(_body.getX() + 1);
            }
        }
    }

    // timeline that graphically moves the doodle left
    private class MoveLeftTimeline implements EventHandler<ActionEvent> {
        // shifts the doodle's X-position negatively if it hasn't hit the left side yet
        @Override
        public void handle(ActionEvent event) {
            if (_body.getX() > 0) { // if doodle hasn't hit left wall yet
                _body.setX(_body.getX() - 1);
            }
        }
    }
}