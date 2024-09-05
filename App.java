import javax.swing.*;

public class App {
    public static void main(String[] args) throws Exception {

        //window variables
        int boardWidth = 1280;
        int boardHeight = 720;

        JFrame frame = new JFrame("Space Invaders");

        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        SpaceInvaders spaceInvaders = new SpaceInvaders(boardWidth, boardHeight);
        frame.add(spaceInvaders);
        frame.pack();
        spaceInvaders.requestFocus();
        frame.setVisible(true);
    }
}
