import java.util.*;
import java.lang.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
public class Java_2048 implements Game, ImageBoard, ScoredGame {
	public static void main(String[] args) {
		Java_2048 j = new Java_2048();
		j.play();
	}
	private int boardSize;
	private boolean alive;
	private static int DEFAULT_BOARD_SIZE = 4;
	private ArrayList<Block> blocks;
	private String[][] imageBoard;
	private Color[][] board;
	private DisplayBoard display;
	public Java_2048() {
		this(DEFAULT_BOARD_SIZE);
	}
	public Java_2048(int boardSize) {
		this.boardSize = boardSize;
		init_vars();
		this.display = new DisplayBoard(this, 10, 10);
	}
	public void init_vars() {
		this.blocks = new ArrayList<Block>();
		this.board = new Color[boardSize][boardSize];
		this.imageBoard = new String[boardSize][boardSize];
		this.alive = true;
		addRandom(1,2);
	}
	private boolean addRandom(int n, int... values) {
		Random rSpace = new Random();
		Random rValue = new Random();
		for (int i=0;i<n;i++) {
			int val_num = (int)(rValue.nextDouble()*(values.length));
			int val = values[val_num];
			int space_num = (int)(rSpace.nextDouble()*(boardSize*boardSize-blocks.size()));
			if (boardSize*boardSize-blocks.size()<n-i) {
				//System.out.println("ADD RANDOM FALSE "+blocks);
				return false;
			}
			int counter = 0;
			for (int x=0;x<boardSize;x++) {
				for (int y = 0;y<boardSize;y++) {
					if (!blocks.contains(new Block(x,y,val))) {
						//System.out.println("ALMOST ADDING");
						if (counter == space_num) {
							System.out.println("ADDING "+x+" "+y+" "+val);
							blocks.add(new Block(x,y,val)); 
						}
						counter++;
					}
				}
			}
		}
		//System.out.println("ADD RANDOM TRUE "+blocks);
		return true;
	}
	private void draw() {
		imageBoard = new String[boardSize][boardSize];
		try {
			//System.out.println(blocks);
			for (Block b : blocks) {
				board[b.pos.y][b.pos.x] = b.color;
				imageBoard[b.pos.y][b.pos.x] = b.image_name;
			}
			//System.out.println(Arrays.deepToString(board));
			display.repaint();
		} catch(Throwable t) {
			System.out.println("ERROR DRAWING: "+t);
			System.exit(0);
		}
	}
	public void play() {
		alive = true;
		draw();
	}
	private void evolve(Pair direction) {
		ArrayList<Block> oldBlocks = new ArrayList<Block>(blocks);
		if (direction.equals(Pair.LEFT)) {
			for (int x=0;x<boardSize;x++) {
				for (int y=0;y<boardSize;y++) {
					Block b = new Block(x,y,2);
					if (blocks.contains(b)) {
						//for (int i=0;i<boardSize-1;i++) {
							moveBlock(blocks.indexOf(b), direction);
						//}
					}
				}
			}
		}	
		if (direction.equals(Pair.RIGHT)) {
			for (int x=boardSize-1;x>=0;x--) {
				for (int y=0;y<boardSize;y++) {
					Block b = new Block(x,y,2);
					if (blocks.contains(b)) {
						//for (int i=0;i<boardSize-1;i++) {
							moveBlock(blocks.indexOf(b), direction);
						//}
					}
				}
			}
		}	
		if (direction.equals(Pair.UP)) {
			for (int x=boardSize-1;x>=0;x--) {
				for (int y=boardSize-1;y>=0;y--) {
					Block b = new Block(x,y,2);
					if (blocks.contains(b)) {
						//for (int i=0;i<boardSize-1;i++) {
							moveBlock(blocks.indexOf(b), direction);
						//}
					}
				}
			}
		}	
		if (direction.equals(Pair.DOWN)) {
			for (int x=boardSize-1;x>=0;x--) {
				for (int y=0;y<boardSize;y++) {
					Block b = new Block(x,y,2);
					if (blocks.contains(b)) {
						//for (int i=0;i<boardSize-1;i++) {
							moveBlock(blocks.indexOf(b), direction);
						//}
					}
				}
			}
		}	
		setAllMergeable();
		//System.out.println(blocks);
		alive = addRandom(1,2,4);
		if (!isMovePossible(Pair.LEFT)&&!isMovePossible(Pair.RIGHT)&&!isMovePossible(Pair.UP)&&!isMovePossible(Pair.DOWN)) {
			alive = false;
		}
		draw();
		if (!alive) {
			end();
		}
	}
	public void end() {
		System.out.println("Game Over");
		display.gameOver();
	}
	private void setAllMergeable() {
		for (int i=0;i<blocks.size();i++) {
			blocks.get(i).mergeable = true;
		}
	}
	public void update(InputEvent e) {
		if (alive && e instanceof KeyEvent) {
			keyUpdate((KeyEvent) e);
		}
	}
	private boolean inBoard(Block b) {
		return b.x>=0 && b.x<boardSize && b.y>=0 && b.y<boardSize;
	}
	private boolean moveBlock(int index, Pair direction) {
		System.out.println("MOVING "+blocks.get(index));
		for (int temp = 0; temp<boardSize-1;temp++) {
			Block b = new Block(blocks.get(index));
			// System.out.println(blocks);
			// System.out.println("B1: "+b);
			b.move(direction);
			// System.out.println("B2: "+b);
			// System.out.println(blocks);
			int i = blocks.indexOf(b);
			//System.out.println(i + " "+blocks.contains(b));
			if (b.x<boardSize && b.x>=0 && b.y>=0 && b.y<boardSize && i >= 0) {
				if (blocks.get(index).getValue() == blocks.get(i).getValue() && blocks.get(index).mergeable == true && blocks.get(i).mergeable == true) {
					//System.out.println("VALUE INCREASING: "+blocks.get(index)+b);
					b.setValue(b.getValue()*2);
					b.mergeable = false;
					blocks.set(i, b);
					//MERGE
					//System.out.println("REMOVED");
					blocks.remove(index);
					return true;
				} else {
					return false;
				}
			} else if (b.x<boardSize && b.x>=0 && b.y>=0 && b.y<boardSize) {
				//System.out.println("SETTING NEW "+b);
				blocks.set(index, b);
				//return true;
				continue;
			} else {
				return false;
			}
		}
		return true;
	}
	private void keyUpdate(KeyEvent e) {
		Pair newDirection = null;
		if (alive && e.getKeyCode()==KeyEvent.VK_ESCAPE) {
			end();
		}
        if (e.getKeyCode()==KeyEvent.VK_LEFT || e.getKeyCode()==KeyEvent.VK_A) {
            newDirection = Pair.LEFT;
        }
        if (e.getKeyCode()==KeyEvent.VK_RIGHT || e.getKeyCode()==KeyEvent.VK_D) {
            newDirection = Pair.RIGHT;
        }
        if (e.getKeyCode()==KeyEvent.VK_UP || e.getKeyCode()==KeyEvent.VK_W) {
            newDirection = Pair.UP;
        }
        if (e.getKeyCode()==KeyEvent.VK_DOWN || e.getKeyCode()==KeyEvent.VK_S) {
            newDirection = Pair.DOWN;
        }
        if (isMovePossible(newDirection)) {
        	evolve(newDirection);
        }
	}
	private boolean isMovePossible(Pair direction) {
		if (direction == null) {
			return false;
		}
		if (direction.equals(Pair.LEFT) || direction.equals(Pair.RIGHT)) {
			for (int j=0;j<boardSize;j++) {
				for (int i=0;i<boardSize;i++) {
					//i is column, j is row
					if (blocks.contains(new Block(i,j)) && blocks.contains(new Block(i+1,j)) && blocks.get(blocks.indexOf(new Block(i, j))).getValue()==blocks.get(blocks.indexOf(new Block(i+1,j))).getValue()) {
						return true;
					}
					if (blocks.contains(new Block(i,j)) && !blocks.contains(new Block(i+1,j)) && direction.equals(Pair.RIGHT)) {
						return true;
					}
					if (!blocks.contains(new Block(i,j)) && blocks.contains(new Block(i+1,j)) && direction.equals(Pair.LEFT)) {
						return true;
					}
				}
			}
		} else if (direction.equals(Pair.UP) || direction.equals(Pair.DOWN)) {
			for (int j=0;j<boardSize;j++) {
				for (int i=0;i<boardSize;i++) {
					//i is row, j is column
					if (blocks.contains(new Block(j,i)) && blocks.contains(new Block(j,i+1)) && blocks.get(blocks.indexOf(new Block(j,i))).getValue()==blocks.get(blocks.indexOf(new Block(j,i+1))).getValue()) {
						return true;
					}
					if (blocks.contains(new Block(j,i)) && !blocks.contains(new Block(j,i+1)) && direction.equals(Pair.UP)) {
						return true;
					}
					if (!blocks.contains(new Block(j,i)) && blocks.contains(new Block(j,i+1)) && direction.equals(Pair.DOWN)) {
						return true;
					}
				}
			}
		}
		System.out.println("MOVE NOT POSSIBLE");
		return false;
	}
    public Color[][] getBoard() {
    	return board;
    }
    public int getScore() {
    	int sum = 0;
    	System.out.println("GETTING SCORE: "+blocks);
    	for (Block b : blocks) {
    		sum += b.getValue();
    	}
    	return sum;
    }
    public int getHighBlock() {
    	int max = 0;
    	System.out.println("GETTING BLOCK: "+blocks);
    	for (Block b : blocks) {
    		System.out.println(b);
    		max = Math.max(b.getValue(),max);
    	}
    	return max;
    }
    public String[][] getImages() {
    	return imageBoard;
    }
    public String getName() {
    	return "2048";
    }
}