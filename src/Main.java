
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MenuFrame menu = new MenuFrame();
            menu.setVisible(true);
        });
    }
}
