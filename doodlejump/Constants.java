package doodlejump;

// constants class for frequently used values
public class Constants {

    public static final int GRAVITY = 1000; // acceleration constant (UNITS: pixels/s^2)
    public static final int REBOUND_VELOCITY = -500; // initial jump velocity (UNITS: pixels/s)
    public static final double DURATION = 0.016; // KeyFrame duration (UNITS: s)

    public static final int PLATFORM_WIDTH = 40; // (UNITS: pixels)
    public static final int PLATFORM_HEIGHT = 10; // (UNITS: pixels)
    public static final int DOODLE_WIDTH = 20; // (UNITS: pixels)
    public static final int DOODLE_HEIGHT = 40; // (UNITS: pixels)
    public static final int WINDOW_WIDTH = 400; // (UNITS: pixels)
    public static final int WINDOW_HEIGHT = 700; // (UNITS: pixels)

    public static final double DOODLE_SPEED = .006; // how fast the animations for doodle left/right movement play
    public static final int Y_DISTANCE = 75; // vertical distance between platforms (UNITS: pixels)
    public static final int X_OFFSET = 200; // maximum horizontal distance between platforms (UNITS: pixels)
}