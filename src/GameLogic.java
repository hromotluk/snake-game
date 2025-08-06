import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class GameLogic {

    private static final int MAX_LEVEL = 5;
    private static final int[] LEVEL_TIME = {100, 100, 100, 100}; // level 1–4 in real‑time seconds

    private final int cols;
    private final int rows;
    private final Snake snake;
    private final Ball ball;
    private final List<Point> obstacles;

    private int score;
    private int level;
    private int timeRemaining;
    private boolean endless;
    private boolean gameOver;

    public GameLogic(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;
        snake = new Snake(new Point(cols / 2, rows / 2));
        ball = new Ball();
        ball.relocate(cols, rows);
        obstacles = new ArrayList<>();
        score = 0;
        level = 1;
        endless = false;
        timeRemaining = LEVEL_TIME[0];
        gameOver = false;
    }

    public int getCols() { return cols; }
    public int getRows() { return rows; }
    public Snake getSnake() { return snake; }
    public Ball getBall() { return ball; }
    public int getScore() { return score; }
    public int getLevel() { return level; }
    public int getTimeRemaining() { return timeRemaining; }
    public boolean isEndless() { return endless; }
    public boolean isGameOver() { return gameOver; }
    public List<Point> getObstacles() { return obstacles; }

    public void update() {
        if (gameOver) return;
        snake.move();
        checkCollisions();
        if (!endless) {
            timeRemaining--;
            if (timeRemaining <= 0) {
                gameOver = true;
            }
        }
    }

    private void checkCollisions() {
        Point head = snake.getHead();
        if (head.x < 0 || head.x >= cols || head.y < 0 || head.y >= rows) {
            gameOver = true;
            return;
        }
        if (snake.getBody().stream().skip(1).anyMatch(p -> p.equals(head))) {
            gameOver = true;
            return;
        }
        for (Point obstacle : obstacles) {
            if (obstacle.equals(head)) {
                gameOver = true;
                return;
            }
        }
        if (head.equals(ball.getPosition())) {
            snake.grow();
            score++;
            ball.relocate(cols, rows);
            ensureBallSafe();
            // LEVEL UP every 2 points (multiples of two)
            if (score % 2 == 0 && level < MAX_LEVEL) {
                levelUp();
            }
        }
    }

    private void ensureBallSafe() {
        while (snake.contains(ball.getPosition()) || obstacles.contains(ball.getPosition())) {
            ball.relocate(cols, rows);
        }
    }

    private void levelUp() {
        level++;
        if (level == MAX_LEVEL) {
            endless = true;
        } else {
            timeRemaining = LEVEL_TIME[level - 1];
        }
        generateObstacles();
    }

    private void generateObstacles() {
        obstacles.clear();
        int count = 8 + (level - 1) * 4; // moderate growth per level
        for (int i = 0; i < count; i++) {
            Point p = new Point((int) (Math.random() * cols), (int) (Math.random() * rows));
            if (!snake.contains(p) && !p.equals(ball.getPosition())) {
                obstacles.add(p);
            }
        }
    }
}
