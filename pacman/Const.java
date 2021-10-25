package pacman;

import javafx.scene.paint.Color;

// helpful constants
public class Const {
    public static final int SQ_WIDTH = 28; // width of each grid cell
    public static final int NUM_SQ = 23; // number of cells vertically/horizontally
    public static final int BOARD_WIDTH = SQ_WIDTH * NUM_SQ; // total width of window

    // Ghost colors
    public static final Color BLINKY_COLOR = Color.rgb(255,45,45); // blinky's standard color
    public static final Color PINKY_COLOR = Color.rgb(255, 134,236); // pinky's standard color
    public static final Color INKY_COLOR = Color.rgb(255,165,45); // inky's standard color
    public static final Color CLYDE_COLOR = Color.rgb(45,250,255); // clyde's standard color
    public static final Color FRIGHT_COLOR = Color.rgb(30,35,255); // ghosts' frightened color
}
