import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.Objects;

public class MenuFrame extends JFrame {
    private final JButton startButton = new JButton("START GAME");
    private final JButton exitButton = new JButton("EXIT");

    public MenuFrame() {
        setTitle("Snake");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel root = new JPanel();
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setBorder(BorderFactory.createEmptyBorder(24, 32, 32, 32));
        root.setBackground(new Color(24, 26, 30));

        JLabel title = new JLabel("Snake");
        title.setAlignmentX(CENTER_ALIGNMENT);
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 36));
        title.setForeground(Color.WHITE);

        styleButtonPrimary(startButton, 260, 48);
        styleButtonSecondary(exitButton, 260, 44);

        startButton.addActionListener(this::onStart);
        exitButton.addActionListener(e -> System.exit(0));

        root.add(title);
        root.add(Box.createRigidArea(new Dimension(0, 20)));
        root.add(startButton);
        root.add(Box.createRigidArea(new Dimension(0, 10)));
        root.add(exitButton);

        setIconImage(new ImageIcon(Objects.requireNonNull(getClass().getResource("/head.png"))).getImage());
        setContentPane(root);
        pack();
        setLocationRelativeTo(null);
    }

    private void styleButtonPrimary(JButton b, int w, int h) {
        b.setAlignmentX(CENTER_ALIGNMENT);
        b.setPreferredSize(new Dimension(w, h));
        b.setMaximumSize(new Dimension(w, h));
        b.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        b.setBackground(new Color(66, 133, 244));
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
    }

    private void styleButtonSecondary(JButton b, int w, int h) {
        b.setAlignmentX(CENTER_ALIGNMENT);
        b.setPreferredSize(new Dimension(w, h));
        b.setMaximumSize(new Dimension(w, h));
        b.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
        b.setBackground(new Color(54, 57, 63));
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
    }

    private void onStart(ActionEvent e) {
        dispose();
        GameGraphic graphic = new GameGraphic();
        JFrame frame = new JFrame("Snake Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(graphic);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setIconImage(new ImageIcon(
                Objects.requireNonNull(getClass().getResource("/head.png"))
        ).getImage());
        frame.setVisible(true);
        graphic.start();
    }
}
