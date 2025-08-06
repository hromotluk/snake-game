import java.awt.Point;
import java.util.ArrayDeque;
import java.util.Deque;

public class Snake {
    public enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    private final Deque<Point> body;
    private Direction direction;
    private boolean grow;

    public Snake(Point start) {
        body = new ArrayDeque<>();
        body.add(start);
        direction = Direction.RIGHT;
        grow = false;
    }

    public void setDirection(Direction dir) {
        if ((direction == Direction.UP && dir == Direction.DOWN) ||
                (direction == Direction.DOWN && dir == Direction.UP) ||
                (direction == Direction.LEFT && dir == Direction.RIGHT) ||
                (direction == Direction.RIGHT && dir == Direction.LEFT)) {
            return;
        }
        direction = dir;
    }

    public Direction getDirection() {
        return direction;
    }

    public Deque<Point> getBody() {
        return body;
    }

    public Point getHead() {
        return body.peekFirst();
    }

    public void grow() {
        grow = true;
    }

    public void move() {
        Point head = getHead();
        Point newHead = new Point(head.x, head.y);
        switch (direction) {
            case UP -> newHead.y--;
            case DOWN -> newHead.y++;
            case LEFT -> newHead.x--;
            case RIGHT -> newHead.x++;
        }
        body.addFirst(newHead);
        if (!grow) {
            body.removeLast();
        } else {
            grow = false;
        }
    }

    public boolean contains(Point p) {
        for (Point segment : body) {
            if (segment.equals(p)) {
                return true;
            }
        }
        return false;
    }
}
