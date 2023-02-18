import java.util.*;
import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.concurrent.*;
public class Snake implements Game, ScoredGame {
    public static void main(String[] args) {
        Snake s = new Snake();
        s.play();
    }
    private static int DEFAULT_WIDTH = 20;
    private static int DEFAULT_HEIGHT = 15;
    private boolean alive;
    private boolean paused;
    private static Color DEFAULT_SNAKE_COLOR = Color.GREEN;
    private Color snake_color;
    private static Color DEFAULT_TARGET_COLOR = Color.RED;
    private Color target_color;
    private static int DEFAULT_MAX_TARGETS = 1;
    private static Block[] DEFAULT_START_TARGETS = new Block[DEFAULT_MAX_TARGETS];
    private static int DEFAULT_LEN_ADDITION = 1;
    private int len_addition;
    private int max_targets;
    private Block[] targets;
    private static int DEFAULT_START_LENGTH = 4;
    private int start_length;
    private Color[][] board;
    private int width;
    private int height;
 	private int totalMoves;
    // private int score;
	private Direction newDirection;
    private DisplayBoard display;
	private boolean start;
    //private int snake_length;
    private SnakeTrail snake; //end of LinkedList represents head, beginning represents tail
    private Direction direction;
    private static int move_delay = 100; //in milliseconds
    public Snake() {
        this(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }
    public Snake(int width, int height) {
        this.width = width;
        this.height = height;
		init_vars();
		display = new DisplayBoard(this,10,10, 25);
    }
    public void pause() {
        this.paused = !paused;
    }
    public int getScore() {
    	System.out.println(Arrays.toString(mods));
    	if (mods[2]) {
    		System.out.println("SIZE SCORE");
    		return snake.snake.size();
    	} else {
    		System.out.println("TIME SCORE");
    		return (totalMoves * move_delay) / 1000;
    	}
    }
    private void init_vars() {
    	for (int i=0;i<DEFAULT_MAX_TARGETS;i++) {
			DEFAULT_START_TARGETS[i] = new Block(3*width/4+i,height/2);
		}
        this.alive = true;
		this.start = false;
		this.start_length = DEFAULT_START_LENGTH;
        this.board = new Color[height][width];
        this.max_targets = DEFAULT_MAX_TARGETS;
        this.targets = DEFAULT_START_TARGETS;
        this.len_addition = DEFAULT_LEN_ADDITION;
        if (width > 6 && height > 3) {
            this.snake = new SnakeTrail(width/4+3,height/2,len_addition*max_targets+1,start_length);
        } else {
            this.snake = new SnakeTrail(0,0,len_addition*max_targets+1,1);
        }
        this.direction = direction.RIGHT;
		this.newDirection = this.direction;
        this.target_color = DEFAULT_TARGET_COLOR;
        this.snake_color = DEFAULT_SNAKE_COLOR;
        this.totalMoves = 0;
        this.paused = false;
        redraw();
        
    }
    public Color[][] getBoard() {
        return board;
    }
    public void restart() {
        init_vars();
        if (display.auto_restart)
        	sleep(1000);
        (new RestartThread(this)).start();
    }
    public void play() {
        redraw();
        display.repaint();
		while (!start) {
			sleep(5);
		}
        while (alive) {
            while (paused) {
                sleep(5);
            }
            evolve();
            totalMoves++;
            sleep(move_delay);
        }
        display.gameOver();
    }
    public void evolve() {
		if (newDirection != null && !newDirection.equals(new Direction(-direction.x,-direction.y))) {
            direction = newDirection;
			newDirection = null;
        }
        Block nextPosition = new Block(snake.snake.getLast().x+direction.x, snake.snake.getLast().y+direction.y);
        int add = 0;
        if (mods[0]) {
            System.out.println("WRAPPING: "+Arrays.deepToString(targets)+" "+nextPosition);
            nextPosition.x = (nextPosition.x + width)%width;
            nextPosition.y = (nextPosition.y + height)%height;
            nextPosition.pos = new Pair(nextPosition.x,nextPosition.y);
        }
        System.out.println(Arrays.deepToString(targets)+" "+nextPosition);
        if (Arrays.asList(targets).contains(nextPosition)) {
            add++;
            for (int i=0;i<targets.length;i++) {
                if (nextPosition.equals(targets[i])) {
                    targets[i] = null;
                }
            }
        }
        alive = snake.forward(add*len_addition,nextPosition, board[0].length, board.length);
        if (alive) {
            generateTargets();
            redraw();
            display.repaint();
        }
    }
    private void generateTargets() {
        int open = board.length * board[0].length - snake.snake.size();
        int targets_to_set = 0;
        for (int i=0;i<max_targets;i++) {
            if (targets[i] != null) {
                open --;
                targets_to_set ++;
            }
        }
        Random r = new Random();
        int counter = -1;
        for (int t=0;t<targets.length;t++) {
            if (targets[t] == null) {
                int spot = (int)(r.nextDouble()*open);
                for (int y=0;y<board.length;y++) {
                    for (int x=0;x<board[0].length;x++) {
                        if (!snake.snake.contains(new Block(x,y)) && !Arrays.asList(targets).contains(new Block(x,y))) {
                            counter++;
                        }
                        if (counter==spot) {
                            targets[t] = new Block(x,y);
                        }
                    }
                }
            }
        }
    }
    private void redraw() {
        Block b;
        board = new Color[board.length][board[0].length];
        for (int i=0;i<snake.snake.size();i++) {
            b = snake.snake.get(i);
            board[b.y][b.x] = snake_color;
        }
        for (int i=0;i<max_targets;i++) {
            if (targets[i] != null) {
                board[targets[i].y][targets[i].x] = target_color;
            }
        }
    }
    public void update(InputEvent e) {
		start = true;
        if (e instanceof KeyEvent) {
            keyUpdate((KeyEvent)e);
        }
    }
    public void keyUpdate(KeyEvent e) {
        if (e.getKeyCode()==KeyEvent.VK_LEFT || e.getKeyCode()==KeyEvent.VK_A) {
            newDirection = Direction.LEFT;
        }
        if (e.getKeyCode()==KeyEvent.VK_RIGHT || e.getKeyCode()==KeyEvent.VK_D) {
            newDirection = Direction.RIGHT;
        }
        if (e.getKeyCode()==KeyEvent.VK_UP || e.getKeyCode()==KeyEvent.VK_W) {
            newDirection = Direction.UP;
        }
        if (e.getKeyCode()==KeyEvent.VK_DOWN || e.getKeyCode()==KeyEvent.VK_S) {
            newDirection = Direction.DOWN;
        }
    }
    public String getName() {
        return "Snake";
    }
    private boolean[] mods = {false, false, true, false, false};
	private String[] modNames = {"Wrapping", "Scoring--[<", "Score by length", "Score by time", ">]--"};
    public void setModifiers(boolean[] values) {
    	this.mods = values;
    }
    public String[] getModifierNames() {
    	return modNames;
    }
    public boolean[] getModifiers() {
    	return mods;
    }
}
