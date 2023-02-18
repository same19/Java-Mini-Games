public class Direction {
    public static Direction LEFT = new Direction(-1,0);
    public static Direction RIGHT = new Direction(1,0);
    public static Direction UP = new Direction(0,1);
    public static Direction DOWN = new Direction(0,-1);
    public int x;
    public int y;
    public Direction() {
        this(0,0);
    }
    public Direction(Block a, Block b) {
        this(b.x-a.x, b.y-a.y);
    }
    public Direction(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public boolean equals(Object o) {
        if (o instanceof Direction) {
            Direction o1 = (Direction) o;
            return o1.x == x && o1.y == y;
        }
        return false;
    }
    public String toString() {
        return "("+x+", "+y+")";
    }
}
