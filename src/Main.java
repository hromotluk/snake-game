
import javax.swing.JFrame;
public class Main {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            GameGraphic graphic = new GameGraphic();
            JFrame frame = new JFrame("Snake Game");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(graphic);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            graphic.start();
        });
    }
}