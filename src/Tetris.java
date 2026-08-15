import javax.swing.*;

public class Tetris extends JFrame {

    public Tetris() {
        setTitle("Tetris");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        add(new GamePanel());

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> new Tetris());
    }
}