import java.awt.Point;
import java.util.Random;

public class Ball {
    private Point position;
    private final Random random;

    public Ball() {
        random = new Random();
        position = new Point(0, 0);
    }

    public Point getPosition() {
        return position;
    }

    public void relocate(int width, int height) {
        position = new Point(random.nextInt(width), random.nextInt(height));
    }
}

