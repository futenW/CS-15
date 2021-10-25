package tetris;

// helpful constants
public class Constants {
    public static final int SQ_WIDTH = 30; // width of each square
    public static final int ROW_NUM = 22; // number of rows in board grid
    public static final int COL_NUM = 12; // number of columns in board grid
    public static final int BOARD_WIDTH = SQ_WIDTH * COL_NUM; // total width of window
    public static final int BOARD_HEIGHT = SQ_WIDTH * ROW_NUM; // total height of window

    // coordinates for squares in each tetris piece
    public static final int[][] I_PIECE_COORDS = { {0, 1*SQ_WIDTH}, {0, 2*SQ_WIDTH}, {0, 3*SQ_WIDTH}, {0, 0} };
    public static final int[][] O_PIECE_COORDS = { {1*SQ_WIDTH, 1*SQ_WIDTH}, {0, 1*SQ_WIDTH}, {1*SQ_WIDTH, 0}, {0, 0} };
    public static final int[][] T_PIECE_COORDS = { {0, 1*SQ_WIDTH}, {0, 2*SQ_WIDTH}, {1*SQ_WIDTH, 1*SQ_WIDTH}, {0, 0}};
    public static final int[][] L1_PIECE_COORDS = { {0, 1*SQ_WIDTH}, {0, 2*SQ_WIDTH}, {1*SQ_WIDTH, 0}, {0, 0} };
    public static final int[][] L2_PIECE_COORDS = { {1*SQ_WIDTH, 0}, {1*SQ_WIDTH, 1*SQ_WIDTH}, {1*SQ_WIDTH, 2*SQ_WIDTH}, {0, 0} };
    public static final int[][] S1_PIECE_COORDS = { {1*SQ_WIDTH, 1*SQ_WIDTH}, {0, 1*SQ_WIDTH}, {1*SQ_WIDTH, 2*SQ_WIDTH}, {0, 0} };
    public static final int[][] S2_PIECE_COORDS = { {0, 1*SQ_WIDTH}, {1*SQ_WIDTH, 1*SQ_WIDTH}, {0, 2*SQ_WIDTH}, {1*SQ_WIDTH, 0} };
}
