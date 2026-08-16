import javax.swing.*;
import java.awt.*;


public class GamePanel extends JPanel{

    // Board constants
    // They never change so they are final and static
    public static final int COLS = 10;
    public static final int ROWS = 20;
    public static final int BLOCK = 30;  // each square is 30x30 pixels

    // Actual game board
    private final int[][] board = new int[ROWS][COLS];

    // Colors for tetrominoes
    // Index 0 is unused so that board values 1-7 match cleanly
    private static final Color[] COLORS = {
            Color.BLACK,                 // 0 - not used
            new Color(0, 240, 240),       // 1 - I (cyan)
            new Color(0, 0, 240),         // 2 - J (blue)
            new Color(240, 160, 0),       // 3 - L (orange)
            new Color(240, 240, 0),       // 4 - O (yellow)
            new Color(0, 240, 0),         // 5 - S (green)
            new Color(160, 0, 240),       // 6 - T (purple)
            new Color(240, 0, 0)          // 7 - Z (red)
    };

        /**
         * SHAPES[piece][rotation][block][x or y]
         *
         * There are 7 pieces.
         * Each piece has 4 rotations.
         * Each rotation is made of 4 blocks.
         * Each block has an x and y offset from the piece origin.
         */
    private static final int[][][][] SHAPES = {
            // 0 - I piece
            {
                    {{0,1},{1,1},{2,1},{3,1}},   // rotation 0
                    {{2,0},{2,1},{2,2},{2,3}},   // rotation 1
                    {{0,2},{1,2},{2,2},{3,2}},   // rotation 2
                    {{1,0},{1,1},{1,2},{1,3}}    // rotation 3
            },
            // 1 - J piece
            {
                    {{0,0},{0,1},{1,1},{2,1}},
                    {{1,0},{2,0},{1,1},{1,2}},
                    {{0,1},{1,1},{2,1},{2,2}},
                    {{1,0},{1,1},{0,2},{1,2}}
            },
            // 2 - L piece
            {
                    {{2,0},{0,1},{1,1},{2,1}},
                    {{1,0},{1,1},{1,2},{2,2}},
                    {{0,1},{1,1},{2,1},{0,2}},
                    {{0,0},{1,0},{1,1},{1,2}}
            },
            // 3 - O piece (square – all rotations look the same)
            {
                    {{1,0},{2,0},{1,1},{2,1}},
                    {{1,0},{2,0},{1,1},{2,1}},
                    {{1,0},{2,0},{1,1},{2,1}},
                    {{1,0},{2,0},{1,1},{2,1}}
            },
            // 4 - S piece
            {
                    {{1,0},{2,0},{0,1},{1,1}},
                    {{1,0},{1,1},{2,1},{2,2}},
                    {{1,1},{2,1},{0,2},{1,2}},
                    {{0,0},{0,1},{1,1},{1,2}}
            },
            // 5 - T piece
            {
                    {{1,0},{0,1},{1,1},{2,1}},
                    {{1,0},{1,1},{2,1},{1,2}},
                    {{0,1},{1,1},{2,1},{1,2}},
                    {{1,0},{0,1},{1,1},{1,2}}
            },
            // 6 - Z piece
            {
                    {{0,0},{1,0},{1,1},{2,1}},
                    {{2,0},{1,1},{2,1},{1,2}},
                    {{0,1},{1,1},{1,2},{2,2}},
                    {{1,0},{0,1},{1,1},{0,2}}
            }
    };


    public GamePanel(){

        //Calculate the exact size the panel needs
        setPreferredSize(new Dimension(COLS * BLOCK, ROWS * BLOCK));
        setBackground(Color.BLACK);

        // Important for key presses
        setFocusable(true);
    }

    // Method is called whenever java panel needs to be redrawn
    @Override
    protected void paintComponent(Graphics g){

        //Always has to be called first, clears previous frame (prevents ghosting)
        super.paintComponent(g);

        // Draw the locked blocks on the board
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                int value = board[row][col];

                if (value !=0) {

                    g.setColor(COLORS[value]);
                    g.fillRect(col * BLOCK + 1, row * BLOCK + 1, BLOCK - 2, BLOCK - 2);

                    // simple border
                    g.setColor(COLORS[value].darker());
                    g.drawRect(col * BLOCK + 1, row * BLOCK + 1, BLOCK - 2, BLOCK - 2);
                }else{
                    //Empty cell grid
                    g.setColor(new Color(40, 40, 40));
                    g.drawRect(col * BLOCK, row * BLOCK, BLOCK, BLOCK);
                }
            }
        }
    }

}
