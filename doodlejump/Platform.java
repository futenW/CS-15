package doodlejump;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

// class for generic platform and all its variables, methods, etc.
public class Platform {
    private Rectangle _platform; // platform's shapes
    private boolean _alreadyHit; // whether platform has been jumped on (for score-keeping)

    // randomly generate this new platform at a certain specified location based on last platform's position
    public Platform(Pane gamePane, int lastPlatformX, int lastPlatformY) {
        // find possible X-locations to generate this platform based on last platform's location
        int low = Math.max(0, lastPlatformX - Constants.X_OFFSET); // lowest possible x-pos to generate this platform
        int high = Math.min(Constants.WINDOW_WIDTH - Constants.PLATFORM_WIDTH, lastPlatformX + Constants.X_OFFSET); // highest

        // create platform shape at a constant height above the last platform and variable vertical distance from last platform
        _platform = new Rectangle(low + (int)(Math.random() * (high - low)),
                lastPlatformY - Constants.Y_DISTANCE, Constants.PLATFORM_WIDTH, Constants.PLATFORM_HEIGHT);
        _platform.setFill(Color.rgb(115, 115, 115));
        gamePane.getChildren().add(_platform);

        _alreadyHit = false;
    }

    // returns X-position of platform
    public int getX() {
        return (int) _platform.getX();
    }

    // returns Y-position of platform
    public int getY() {
        return (int) _platform.getY();
    }

    // setter method for platform's Y-position
    public void setY(int Y) {
        _platform.setY(Y);
    }

    // getter method for platform's rectangle shape
    public Rectangle getRectangle() {
        return _platform;
    }

    // setter method for if platform has been hit
    public void setAlreadyHit(boolean hit) {
        _alreadyHit = hit;
    }

    // getter method for whether platform has been hit
    public boolean getAlreadyHit() {
        return _alreadyHit;
    }
}
