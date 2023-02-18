import java.util.*;
import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
public class SnakeTrail {
    public LinkedList<Block> snake;
    private LinkedList<Block> path;
    private static int DEFAULT_MEMORY = 10;
    private int memory; //number of moves remembered after the snake length
    public SnakeTrail() {
        this(0,0,DEFAULT_MEMORY);
    }
    public SnakeTrail(int x, int y) {
        this(x,y,DEFAULT_MEMORY);
    }
    public SnakeTrail(int x, int y, int memory) {
        this(x,y,memory,1);
    }
    public SnakeTrail(int x, int y, int memory, int start_length) {
        snake = new LinkedList<Block>();
        for (int i=0;i<start_length;i++) {
            snake.addFirst(new Block(x-i,y));
        }
        path = (LinkedList<Block>) snake.clone();
        this.memory = memory;
    }
    private boolean inBounds(Block b, int width, int height) {
        return (b.x >= 0 && b.x < width && b.y >= 0 && b.y < height);
    }
    public boolean forward(int add, Block b, int width, int height) {
        if ((!inBounds(b,width,height)) || (snake.contains(b)&& !snake.getFirst().equals(b))) {
            return false;
        }
        
        int length = snake.size();
        length += add-1; //to add to tail if needed
        snake = new LinkedList<Block>(path.subList(path.size()-length,path.size()));
        if (path.size()+1>memory+length) {
            path.removeFirst();
        }
        path.addLast(b);
        snake.addLast(b);
        return true;
    }
}
