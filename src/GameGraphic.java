import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Objects;

public class GameGraphic extends JPanel implements KeyListener, Runnable {
    private static final int CELL_SIZE = 20;
    private static final int COLS = 30;
    private static final int ROWS = 25;

    private final Image headImg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/head.png"))).getImage();
    private final Image bodyImg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/body.png"))).getImage();
    private final Image ballImg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/ball.png"))).getImage();
    private final Image obstacleImg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/obstacle.png"))).getImage();

    private GameLogic logic;
    private Thread loopThread;
    private volatile boolean running;

    public GameGraphic() {
        logic = new GameLogic(COLS, ROWS);
        setPreferredSize(new Dimension(COLS * CELL_SIZE, ROWS * CELL_SIZE));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
    }

    public void start() {
        if (loopThread == null || !loopThread.isAlive()) {
            running = true;
            loopThread = new Thread(this);
            loopThread.start();
        }
        requestFocusInWindow();
    }

    @Override
    public void run() {
        while (running) {
            logic.update();
            repaint();
            try {
                Thread.sleep(getDelayForLevel(logic.getLevel()));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private int getDelayForLevel(int level) {
        return switch (level) {
            case 1 -> 120;
            case 2 -> 100;
            case 3 -> 90;
            case 4 -> 80;
            default -> 70;
        };
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawGrid(g);
        drawSnake(g);
        drawBall(g);
        drawObstacles(g);
        drawHud(g);
        if (logic.isGameOver()) {
            drawGameOver(g);
        }
    }

    private void drawGrid(Graphics g) {
        g.setColor(Color.DARK_GRAY);
        for (int x = 0; x <= COLS; x++) {
            g.drawLine(x * CELL_SIZE, 0, x * CELL_SIZE, ROWS * CELL_SIZE);
        }
        for (int y = 0; y <= ROWS; y++) {
            g.drawLine(0, y * CELL_SIZE, COLS * CELL_SIZE, y * CELL_SIZE);
        }
    }

    private void drawSnake(Graphics g) {
        java.util.Iterator<Point> it = logic.getSnake().getBody().iterator();
        if (it.hasNext()) {
            Point head = it.next();
            g.drawImage(headImg, head.x * CELL_SIZE, head.y * CELL_SIZE, CELL_SIZE, CELL_SIZE, this);
        }
        while (it.hasNext()) {
            Point p = it.next();
            g.drawImage(bodyImg, p.x * CELL_SIZE, p.y * CELL_SIZE, CELL_SIZE, CELL_SIZE, this);
        }
    }

    private void drawBall(Graphics g) {
        Point p = logic.getBall().getPosition();
        g.drawImage(ballImg, p.x * CELL_SIZE, p.y * CELL_SIZE, CELL_SIZE, CELL_SIZE, this);
    }

    private void drawObstacles(Graphics g) {
        for (Point p : logic.getObstacles()) {
            g.drawImage(obstacleImg, p.x * CELL_SIZE, p.y * CELL_SIZE, CELL_SIZE, CELL_SIZE, this);
        }
    }

    private void drawHud(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.drawString("Score: " + logic.getScore(), 10, 20);
        g.drawString("Level: " + logic.getLevel(), 10, 40);
        if (logic.isEndless()) {
            g.drawString("Time: ∞", 10, 60);
        } else {
            g.drawString("Time: " + logic.getTimeRemaining(), 10, 60);
        }
    }

    private void drawGameOver(Graphics g) {
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 36));
        String msg = "Game Over";
        int width = g.getFontMetrics().stringWidth(msg);
        g.drawString(msg, (getWidth() - width) / 2, getHeight() / 2);
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        String restart = "Press R to Restart";
        int w2 = g.getFontMetrics().stringWidth(restart);
        g.drawString(restart, (getWidth() - w2) / 2, getHeight() / 2 + 30);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP -> logic.getSnake().setDirection(Snake.Direction.UP);
            case KeyEvent.VK_DOWN -> logic.getSnake().setDirection(Snake.Direction.DOWN);
            case KeyEvent.VK_LEFT -> logic.getSnake().setDirection(Snake.Direction.LEFT);
            case KeyEvent.VK_RIGHT -> logic.getSnake().setDirection(Snake.Direction.RIGHT);
            case KeyEvent.VK_R -> {
                if (logic.isGameOver()) {
                    restartGame();
                }
            }
        }
    }

    private void restartGame() {
        running = false;
        if (loopThread != null) {
            try {
                loopThread.join();
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
        }
        logic = new GameLogic(COLS, ROWS);
        start();
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}

}
