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

    public GamePanel(){

        //Calculate the exact size the panel needs
        setPreferredSize(new Dimension(COLS * BLOCK, ROWS * BLOCK));
        setBackground(Color.BLACK);

        // Important for key presses
        setFocusable(true);

        //test blocks
        board[19][4] = 1;
        board[19][5] = 1;
        board[18][5] = 1;
        board[17][5] = 1;

    }

    // Method is called whenever java panel needs to be redrawn
    @Override
    protected void paintComponent(Graphics g){

        //Always has to be called first, clears previous frame (prevents ghosting)
        super.paintComponent(g);

        // Nested loops: for every row, go through every column
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {

                if (board[row][col] !=0) {

                    g.setColor(Color.CYAN);
                    g.fillRect(col * BLOCK, row * BLOCK, BLOCK, BLOCK);

                    g.setColor(Color.BLACK);
                    g.drawRect(col * BLOCK, row * BLOCK, BLOCK, BLOCK);
                }else{
                    //Empty cell
                    g.setColor(new Color(40, 40, 40));
                    g.drawRect(col * BLOCK, row * BLOCK, BLOCK, BLOCK);
                }
            }
        }
    }

}
