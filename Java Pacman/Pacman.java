import java.util.*;
import java.util.Timer;
import java.lang.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.concurrent.*;
public class Pacman implements Game, ScoredGame, ImageBoard {
	public static void main(String[] args) {
		Pacman pm = new Pacman();
	}
	private int height;
	private int width;
	private final int starting_lives = 3;
	private int dotsEaten;
	private long totalTime; //milliseconds
	private int lives;
	private int level;
	private PacmanActor pacman;
	private PacmanBoard board;
	private Ghost[] ghosts;
	private int delta_t; //in milliseconds
	private int delta_t_pacman;
	private int delta_t_ghost;
	private double pacmanSpeed; // in blocks (1 unit) per second
	private double ghostSpeed;
	private boolean alive;
	private int score;
	private DisplayBoard display;
	private int betweenLivesTime = 3000;
	private int betweenLevelsTime = 5000;
	private int pauseBeforeBoardReset = 1000;
	private int timeBeforeStart = 1000;
	private int timeVulnerable = 12000;
	private final Pair pacmanStart = new Pair(13,7);
	private final Pair ghostRedStart = new Pair(13,19);
	private final Pair ghostOrangeStart = new Pair(12,16);
	private final Pair ghostPinkStart = new Pair(13,16);
	private final Pair ghostBlueStart = new Pair(15,16);
	private final Pair ghostSpawn = new Pair(13,19);
	private Thread pacmanController;
	private Thread ghostController;
	private String[] modifierNames = {""};
	private boolean[] modifierValues = {};
	private boolean paused;
	private int currentVulnerableGhostScoreBoost;
	private Timer timer = new Timer("Timer");
	public Pacman() {
		init_vars();
		display = new DisplayBoard(this);
		display.repaint();
		sleep(timeBeforeStart);
		play();
	}
	public void init_vars() {
		dotsEaten = 0;
		paused = false;
		level = 0;
		totalTime = 0;
		lives = starting_lives;
		score = 0;
		alive = true;
		pacmanSpeed = 7.5;
		ghostSpeed = 6.5;
		delta_t = 1;
		delta_t_pacman = (int) (1000/pacmanSpeed);
		delta_t_ghost = (int) (1000/ghostSpeed);
		currentVulnerableGhostScoreBoost = 0;
		pacman = new PacmanActor((Pair)pacmanStart.clone(), new Pair(-1,0));
		ghosts = new Ghost[4];
		ghosts[0] = new Ghost("red", (Pair)ghostRedStart.clone(),0);
		
		ghosts[1] = new Ghost("pink", (Pair)ghostPinkStart.clone(),0.3);

		ghosts[2] = new Ghost("blue", (Pair)ghostBlueStart.clone(),0.7);
		

		ghosts[3] = new Ghost("orange", (Pair)ghostOrangeStart.clone(),1);
		// orange = new Ghost();
		// blue = new Ghost();
		// pink = new Ghost();
		board = new PacmanBoard(this);
		height = board.height;
		width = board.width;
		board.updateStats(lives, score);
		board.update();
	}
	public void restart() {
		init_vars();
		Game.super.restart();
		// sleep(timeBeforeStart);
  //       (new RestartThread(this)).start();
    }
	private void resetLevel() {
		dotsEaten = 0;
		totalTime = 0;
		level++;
		resetPositions();
		board = new PacmanBoard(this);
		board.update();

	}
	private void resetPositions() {
		pacman.setPosition((Pair)pacmanStart.clone());
		pacman.setDirection(new Pair(-1,0));
		pacman.setNextDirection(new Pair(-1,0));
		pacman.resetImage();
		ghosts[0].setPosition((Pair)ghostRedStart.clone());
		ghosts[0].makeRegular();
		ghosts[1].setPosition((Pair)ghostPinkStart.clone());
		ghosts[1].deactivate();
		ghosts[1].makeRegular();
		ghosts[2].setPosition((Pair)ghostBlueStart.clone());
		ghosts[2].deactivate();
		ghosts[2].makeRegular();
		ghosts[3].setPosition((Pair)ghostOrangeStart.clone());
		ghosts[3].deactivate();
		ghosts[3].makeRegular();
	}
	private void keyUpdate(KeyEvent e) {
		if (e == null) {
			return;
		}
		/* Set up desired direction as well as current direction, so that as soon as it can turn, it will */
		else if (e.getKeyCode()==KeyEvent.VK_W || e.getKeyCode()==KeyEvent.VK_UP) {
			pacman.setNextDirection(new Pair(0,1));
		}
		else if (e.getKeyCode()==KeyEvent.VK_A || e.getKeyCode()==KeyEvent.VK_LEFT) {
			pacman.setNextDirection(new Pair(-1,0));
		}
		else if (e.getKeyCode()==KeyEvent.VK_S || e.getKeyCode()==KeyEvent.VK_DOWN) {
			pacman.setNextDirection(new Pair(0,-1));
		}
		else if (e.getKeyCode()==KeyEvent.VK_D || e.getKeyCode()==KeyEvent.VK_RIGHT) {
			pacman.setNextDirection(new Pair(1,0));
		}
	}
	private boolean inBounds(Pair pos) {
		return pos.x >= 0 && pos.y >= 0 && pos.x < board.width && pos.y < board.height;
	}
	private void movePacman() {
		Pair nextDir = pacman.getNextDirection();
		Pair currentDir = pacman.getDirection();
		Pair dir;
		Pair oldPos = (Pair)pacman.getPosition().clone();
		if (legalMove(nextDir,pacman)) {
			pacman.setDirection(nextDir);
			dir = nextDir;
		} else if (legalMove(currentDir, pacman)) {
			dir = currentDir;
		} else {
			//System.out.println("Pacman couldn't move.");
			return;
		}
		Pair newPos = pacman.getPosition().add(dir);
		if (inBounds(newPos)) {
			pacman.move(dir);
		} else if (board.isTeleporter(oldPos)) {
			pacman.setPosition(board.getTeleporter(oldPos));
		}

	}
    public void update(InputEvent e) {
    	if (alive && e instanceof KeyEvent) {
			keyUpdate((KeyEvent) e);
		}
    	display.repaint();
    }
    
	private void ghostController() {
		while (alive) {
			System.out.println("GHOST");
			if (paused) {
    			continue;
    		}
			for (int i = 0;i<ghosts.length;i++) {
				ghosts[i].move(this, pacman.getPosition());
			}
			if (!sleep(delta_t_ghost)) {
    			System.out.println("ERROR");
    			break;
    		}
    	}
	}
	private void pacmanController() {
		while (alive) {
			System.out.println("PACMAN");
			if (paused) {
    			continue;
    		}
    		movePacman();
    		pacman.changeMouthOpening();
    		pacman.changeImage();
    		if (!sleep(delta_t_pacman)) {
    			System.out.println("ERROR");
    			break;
    		}
    	}
	}
	private void spawn(Ghost g) {
		g.setPieceUnder(board.getBoard()[ghostSpawn.y][ghostSpawn.x]);
		g.setPosition(ghostSpawn);
	}
    public void play() {
    	totalTime = 0;
    	//start pacmanController
    	pacmanController = new Thread(() -> pacmanController());
    	//start ghostController
    	ghostController = new Thread(() -> ghostController());
    	ghostController.start();
    	pacmanController.start();
    	int g0SpawnTime = 0;
		int g1SpawnTime = 3000;
		int g2SpawnTime = 8000;
		int g3SpawnTime = 15000;
		int delayBeforeActivation = 500;
    	while (alive) {
    		System.out.println("ALIVE");
    		if (paused) {
    			continue;
    		}
    		if (totalTime >= g0SpawnTime && totalTime < g0SpawnTime+delta_t) {
    			spawn(ghosts[0]);
    		}
    		if (totalTime >= g1SpawnTime && totalTime < g1SpawnTime+delta_t) {
    			spawn(ghosts[1]);
    		}
    		if (totalTime >= g2SpawnTime && totalTime < g2SpawnTime+delta_t) {
    			spawn(ghosts[2]);
    		}
    		if (totalTime >= g3SpawnTime && totalTime < g3SpawnTime+delta_t) {
    			spawn(ghosts[3]);
    		}
    		if (totalTime >= g0SpawnTime+delayBeforeActivation && totalTime < g0SpawnTime+delta_t+delayBeforeActivation) {
    			ghosts[0].activate();
    		}
    		if (totalTime >= g1SpawnTime + delayBeforeActivation && totalTime < g1SpawnTime+delta_t + delayBeforeActivation) {
    			ghosts[1].activate();
    		}
    		if (totalTime >= g2SpawnTime + delayBeforeActivation && totalTime < g2SpawnTime+delta_t + delayBeforeActivation) {
    			ghosts[2].activate();
    		}
    		if (totalTime >= g3SpawnTime + delayBeforeActivation && totalTime < g3SpawnTime+delta_t + delayBeforeActivation) {
    			ghosts[3].activate();
    		}
    		for (Ghost g : ghosts) {
    			g.setIntelligence(g.getIntelligence()+delta_t/50000);
    			if (pacman.getPosition().equals(g.getPosition())) {
    				if (!g.isVulnerable()) {
	    				lifeLost();
	    			} else {
	    				changeScore(currentVulnerableGhostScoreBoost);
	    				currentVulnerableGhostScoreBoost*=2;
	    				g.deactivate();
	    				g.makeRegular();
	    				g.setPosition(ghostSpawn);
	    				TimerTask task = new TimerTask() {
					        public void run() {
					            g.activate();
					        }
					    };
					    long delay = 3000;
	    				timer.schedule(task, delay);
	    			}
    			}
    		}
    		board.update();
    		display.repaint();
    		if (!sleep(delta_t)) {
    			System.out.println("ERROR");
    			break;
    		}
    		totalTime += delta_t;
    	}
    }
    private void gameOver() {
    	pacman = new PacmanActor((Pair)pacmanStart.clone(), new Pair(-1,0));
    	ghostController.stop();
    	pacmanController.stop();
    }
    public boolean legalMove(Pair dir, Actor a) {
    	Pair newPos = new Pair(a.getPosition().x+dir.x, a.getPosition().y+dir.y);
    	if (inBounds(newPos) && board.getBoard()[newPos.y][newPos.x] instanceof Wall) {
    		return false;
    	}
    	return true;
    } 
    public String getName() {
    	return "Pacman";
    }
    public int getScore() {
    	return score;
    }
    public Color[][] getBoard() {
    	return new Color[height][width];
    }
    public ArrayList<PositionedImage> getTopImages() {
    	ArrayList<PositionedImage> a = new ArrayList<PositionedImage>();
    	if (pacman.getPosition().x == pacmanStart.x && pacman.getPosition().y == pacmanStart.y) {
    		System.out.println("YES");
			a.add(new PositionedImage(pacman.getImage(), new Vector(pacman.getPosition()).add(new Vector(0.25,0.25)), new Vector(1.5)));
		} else {
    		a.add(new PositionedImage(pacman.getImage(), new Vector(pacman.getPosition()).add(new Vector(-0.25,0.25)), new Vector(1.5)));
		}
    	for (int i = 0;i<ghosts.length;i++) {
    		if ((i == 1 && ghosts[1].getPosition().equals(ghostPinkStart)) || ghosts[i].getPosition().equals(ghostSpawn)) {
    			a.add(new PositionedImage(ghosts[i].getImage(), new Vector(ghosts[i].getPosition()).add(new Vector(0.25,0.25)), new Vector(1.5)));
    		} else {
    			a.add(new PositionedImage(ghosts[i].getImage(), new Vector(ghosts[i].getPosition()).add(new Vector(-0.25,0.25)), new Vector(1.5)));
    		}
    	}
    	return a;
    }
    public String[][] getImages() {
    	//System.out.println("getting images");
    	Piece[][] p =  board.getBoard();
    	String[][] s = new String[p.length][p[0].length];
    	for (int r = 0;r<p.length;r++) {
    		for (int c = 0;c<p[0].length;c++) {
    			if (p[r][c] != null) {
    				if (p[r][c] instanceof Actor) {
    					s[r][c] = null;
    				} else {	
    					s[r][c] = p[r][c].getImage();
    				}
    			} else {
    				s[r][c] = null;
    			}
    		}
    	}
    	return s;
    }
  //   public void restart() {
  //   	lives = starting_lives;
  //   	pacman = new PacmanActor(pacmanStart, new Pair(-1,0));
		// ghosts = new Ghost[2];
		// ghosts[0] = new Ghost(Color.RED, "red", ghostRedStart,5);
		// ghosts[0].setPieceUnder(new Dot());
		// ghosts[1] = new Ghost(Color.ORANGE, "orange", ghostOrangeStart,10);
		// ghosts[1].setPieceUnder(new Dot());
  //   	board = new PacmanBoard(this);
  //   }
    private void makeGhostsRegular() {
    	currentVulnerableGhostScoreBoost = 0;
    	for (int i = 0;i<ghosts.length;i++) {
    		ghosts[i].makeRegular();
    	}
    }
    private void makeGhostsVulnerable() {
    	currentVulnerableGhostScoreBoost = 200;
    	TimerTask task = new TimerTask() {
	        public void run() {
	            makeGhostsRegular();
	        }
	    };
	    
	    long delay = timeVulnerable; // 10 seconds
	    timer.schedule(task, delay);


    	for (int i = 0;i<ghosts.length;i++) {
    		ghosts[i].makeVulnerable();
    	}
    }
    private void lifeLost() {
    	lives--;
    	board.updateStats(lives, score);
    	sleep(delta_t);
    	alive = false;
    	sleep(delta_t);
    	display.repaint();
    	sleep(pauseBeforeBoardReset);
    	if (lives > 0) {
	    	resetPositions();
	    	board.update();
	    	display.repaint();
	    	sleep(betweenLivesTime);
	    	alive = true;
	    	play();
	    } else {
	    	System.out.println("Game Over");
	    	gameOver();
	    	display.gameOver();
	    }
    }
    private void nextLevel() {
    	alive = false;
    	sleep(10);
    	display.repaint();
    	sleep(pauseBeforeBoardReset);
    	resetLevel();
    	board.update();
  		display.repaint();
    	sleep(betweenLevelsTime);
    	alive = true;
    	play();
    }
    public void ghostCollision(Ghost g, Actor a) {
    	if (a == pacman) {
    		lifeLost();
    	}
    }
    public PacmanBoard getPacmanBoard() {
    	return board;
    }
    public PacmanActor getPacman() {
    	return pacman;
    }
    public Ghost[] getGhosts() {
    	return ghosts;
    }
    public void deletingPiece(Piece p) {
    	//PacmanBoard will call this method when deleting a piece from the board, so that this class can deal with scoring and other stuff
    	if (p instanceof Dot) {
    		changeScore(10);
    		dotsEaten++;
    	} else if (p instanceof Energizer) {
    		changeScore(50);
    		dotsEaten++;
    		makeGhostsVulnerable();
    	}
    	if (board.allDotsGone()) {
    		nextLevel();
    	}
    }
    public void changeScore(int changeBy) {
    	score += changeBy;
    	board.updateStats(lives, score);
    }
    public String getImageFolder() {
    	return "Images";
    }
    public String getBackgroundImage() {
    	return "board.jpg";
    }
    public void pause() {
    	if (paused == false) {
    		paused = true;
    	} else {
    		paused = false;
    	}
    }
    public void setModifiers(boolean[] values) {
    	modifierValues = values;
    }
    public String[] getModifierNames() {
    	return modifierNames;
    }
    public boolean[] getModifiers() {
    	return modifierValues;
    }
}