import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;


public class GamePanel extends JPanel implements KeyListener, ActionListener{

    // Board constants
    // They never change so they are final and static
    public static final int COLS = 10;
    public static final int ROWS = 20;
    public static final int BLOCK = 30;  // each square is 30x30 pixels

    private int score = 0;
    private int level = 1;
    private int linesCleared = 0;
    private boolean gameOver = false;

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

    // Current falling piece
    private int currentShape;       // 0-6
    private int currentRotation;   //0-3
    private int currentX;          // column of the piece origin
    private int currentY;          // Row of piece origin

    private final Random random = new Random();
    private final Timer timer;

    public GamePanel(){
        // Make the panel wider so there is space for score
        setPreferredSize(new Dimension(COLS * BLOCK + 160, ROWS * BLOCK));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        timer = new Timer(500, this);
        timer.start();

        // Spawn the first piece when the game starts
        spawnNewPiece();
    }

    // Creates new random piece at the top of the board
    private void spawnNewPiece(){
        currentShape = random.nextInt(7);  // 0 to 6
        currentRotation = 0;
        currentX = 3;            // roughly in the middle
        currentY = 0;            // top of the board

        // If the new piece is already colliding, the game is over
        if (!isValidPosition(currentX, currentY, currentRotation)){
            gameOver = true;
            timer.stop();
        }

    }

        // If the new piece is already colliding, the game is over

    /**
     * Checks if the piece would be in a valid position
     * if it was at (x, y) with the given rotation.
     *
     * Returns false if:
     * - any block goes outside the left/right walls
     * - any block goes below the floor
     * - any block hits a locked block on the board
     */

    private boolean isValidPosition(int x, int y, int rotation) {
        int [][] shape = SHAPES[currentShape][rotation];

        for (int[] block : shape) {
            int newX = x + block[0];
            int newY = y + block[1];

            // Hit left or right wall
            if (newX < 0 || newX >= COLS) return false;

            // Hit the floor
            if (newY >= ROWS) return false;

            // Hit a locked block (only checked if the block is on the board)
            if (newY >= 0 && board[newY][newX] != 0) return false;

        }
        return true;
    }

    // Locks current piece onto board permanently and spawns new piece
    private void lockPiece() {
        int[][] shape = SHAPES[currentShape][currentRotation];

        for (int[] block : shape) {
            int x = currentX + block[0];
            int y = currentY + block[1];

            if (y >= 0) {
                // Store the color index (1-7) on the board
                board[y][x] = currentShape + 1;
            }
        }

        //After locking the piece, check for full lines
        clearLines();

        // Then spawn the next piece
        spawnNewPiece();

    }

    /** Checks the board for full rows and removes them
     *Everything above a cleared row falls down
     */
    private void clearLines() {
        int linesThisTurn = 0;
        // from the bottom row to the top
        for (int row = ROWS -1; row >= 0; row--) {

            //Assume row is full until empty cell is found
            boolean isFull = true;

            for (int col = 0; col < COLS; col++) {
                if (board[row][col] == 0) {
                    isFull = false;
                    break; // no need to check rest of this row
                }
            }

            if (isFull){
                linesThisTurn++;
                // This row is completely full, remove it

                // Move every row above this one down by one
                for (int r = row; r > 0; r--) {
                    // Copy row above into current row
                    for (int c = 0; c < COLS; c++){
                        board[r][c] = board[r - 1][c];
                    }
                }

                // Clear the very top row (it is now empty)
                for (int c = 0; c < COLS; c++) {
                    board[0][c] = 0;
                }
                row++; // check the same row again
            }
        }

        // Award points if any lines are cleared
        if (linesThisTurn > 0) {
            int[] points = {0, 40, 100, 300, 1200};
            score += points[linesThisTurn] * level;

            // Level up every 10 lines
            linesCleared += linesThisTurn;

            level = (linesCleared / 10) + 1;

            // Make game faster, starts at 500ms,
            // gets 40ms faster each level, minimum 100ms
            int newDelay = Math.max(100, 500 - (level -1) * 40);
            timer.setDelay(newDelay);
        }
    }
    // Instantly drops the piece as far down as it can go and locks it
    private void hardDrop(){
        while (isValidPosition(currentX, currentY + 1, currentRotation)) {
            currentY++;
        }
        lockPiece();
    }

    // ======== TIMER (automatic falling) ========
    @Override
    public void actionPerformed(ActionEvent e) {
        // Try to move the piece one row down
        if (isValidPosition(currentX, currentY + 1, currentRotation)) {
            currentY++;
        }else {
            // Cannot move down, lock it
            lockPiece();
        }
        repaint();
    }

    // Method is called whenever java panel needs to be redrawn
    @Override
    protected void paintComponent(Graphics g) {

        //Always has to be called first, clears previous frame (prevents ghosting)
        super.paintComponent(g);

        // Draw the locked blocks on the board
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                int value = board[row][col];

                if (value != 0) {

                    g.setColor(COLORS[value]);
                    g.fillRect(col * BLOCK + 1, row * BLOCK + 1, BLOCK - 2, BLOCK - 2);

                    // simple border
                    g.setColor(COLORS[value].darker());
                    g.drawRect(col * BLOCK + 1, row * BLOCK + 1, BLOCK - 2, BLOCK - 2);
                } else {
                    //Empty cell grid
                    g.setColor(new Color(40, 40, 40));
                    g.drawRect(col * BLOCK, row * BLOCK, BLOCK, BLOCK);
                }
            }
        }

            // Draw current falling piece
            int[][] shape = SHAPES[currentShape][currentRotation];
            for (int[] block : shape) {
                int x = currentX + block[0];
                int y = currentY + block[1];

                //Only draw if it's on the visible board
                if (y > +0) {
                    drawBlock(g, x, y, COLORS[currentShape + 1]);
                }
            }
        }



        // Helper method to not repeat the same drawing code.
        private void drawBlock(Graphics g, int col, int row, Color color) {
            g.setColor(color);
            g.fillRect(col * BLOCK + 1, row * BLOCK + 1, BLOCK - 2, BLOCK - 2);
            g.setColor(color.darker());
            g.drawRect(col * BLOCK + 1, row * BLOCK + 1, BLOCK - 2, BLOCK - 2);


            // ====== SIDE PANEL (Score/ Level / Lines) ======
            int panelX = COLS * BLOCK + 20;

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 18));

            g.drawString("Score: " + score, panelX, 40);
            g.drawString("Level: " + level, panelX, 70);
            g.drawString("Lines: " + linesCleared, panelX, 100);


            // ====== GAME OVER SCREEN =======
            if (gameOver){
                // Dark Transparent Overlay
                g.setColor(new Color(0, 0,0, 180));
                g.fillRect(0, 0, COLS * BLOCK, ROWS * BLOCK);

                g.setColor(Color.RED);
                g.setFont(new Font("Arial", Font.BOLD, 36));
                g.drawString("Game Over", 40, ROWS * BLOCK / 2 - 20);

                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.PLAIN, 16));
                g.drawString("Press R to Restart", 55, ROWS * BLOCK / 2 + 20);

            }

        }

    // ======= KEYBOARD =======

    @Override
    public void keyPressed(KeyEvent e) {
        // If the game is over, only allow Restart
        if (gameOver) {
            if (e.getKeyCode() == KeyEvent.VK_R) {
                // Reset everything
                for (int r = 0; r < ROWS; r++) {
                    for (int c = 0; c < COLS; c++) {
                        board[r][c] = 0;
                    }
                }
                score = 0;
                level = 1;
                linesCleared = 0;
                gameOver = false;
                timer.setDelay(500);
                spawnNewPiece();
                timer.start();
                repaint();
            }
            return;
        }

        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                if (isValidPosition(currentX - 1, currentY, currentRotation)){
                    currentX--;
                }
                break;

            case KeyEvent.VK_RIGHT:
                if (isValidPosition(currentX + 1, currentY, currentRotation)) {
                    currentX++;
                }
                break;

            case KeyEvent.VK_DOWN:
                if (isValidPosition(currentX, currentY + 1, currentRotation)) {
                    currentY++;
                }
                break;

            case KeyEvent.VK_UP:
                int newRotation = (currentRotation + 1) % 4;
                if (isValidPosition(currentX, currentY, newRotation)) {
                    currentRotation = newRotation;
                }
                break;

            case KeyEvent.VK_SPACE:
                hardDrop();
                break;
        }
        repaint(); // redraw after every move
    }
    // Methods must be implemented even if not used
    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}

}

