import java.util.*;
import java.lang.*;
import java.awt.*;
public class PacmanBoard {
	public int width;
	public int height;
	private Piece[][] board;
	private Pacman game;
	private ArrayList<Pair> updatableLocations;
	public HashMap<Pair,Pair> teleports;
	private static final Piece blackSquare = new Piece() {
		public String getImage() {
			return "blackSquare.png";
		}
	};
	private static final Piece emptySquare = null;
	private static final Piece wall = new Wall();
	private static final Piece pacmanLife = new Piece() {
		public String getImage() {
			return "pacmanleft.png";
		}
	};
	public PacmanBoard() {
		;
	}
	public PacmanBoard(Pacman p) {
		game = p;
		height = 32;//so that there is room at the top for lives, etc.
		width = 28;
		board = new Piece[height][width]; 
		teleports = new HashMap<Pair,Pair>();
		teleports.put(new Pair(0,16), new Pair(27,16));
		teleports.put(new Pair(27,16), new Pair(0,16));
		generateDefaultBoard();
		updatableLocations = new ArrayList<Pair>();
		resetUpdatableLocations();

	}

	public Piece[][] getBoard() {
		return board;
	}


	private void resetUpdatableLocations() {
		updatableLocations = new ArrayList<Pair>();
		updatableLocations.add((Pair)(game.getPacman().getPosition().clone()));
		for (int i=0;i<game.getGhosts().length;i++) {
			updatableLocations.add((Pair)(game.getGhosts()[i].getPosition().clone()));
		}
	}
	public void update() {
		//System.out.println("updating board");
		for (int r = 0;r<height;r++) {
			for (int c = 0;c<width;c++) {
				if (board[r][c] instanceof Actor) {
					board[r][c] = ((Actor)board[r][c]).getPieceUnder();
				}
			}
		}
		PacmanActor pa = game.getPacman();
		Ghost[] ghosts = game.getGhosts();
		
		game.deletingPiece(board[pa.getPosition().y][pa.getPosition().x]);
		board[pa.getPosition().y][pa.getPosition().x] = pa;
		for (Ghost i : ghosts) {
			i.setPieceUnder(board[i.getPosition().y][i.getPosition().x]);
			board[i.getPosition().y][i.getPosition().x] = i;
		}
		resetUpdatableLocations();
	}
	public boolean isTeleporter(Pair p) {
		return p.equals(new Pair(0,16)) || p.equals(new Pair(27,16));
	}
	public Pair getTeleporter(Pair p) {
		if (p.equals(new Pair(0,16))) {
			return new Pair(27,16);
		} else if (p.equals(new Pair(27,16))) {
			return new Pair(0,16);
		} else {
			return null;
		}
	}
	public void updateStats(int lives, int score) {
		for (int i = 0;i<28;i++) {
			board[31][i] = blackSquare;
		}
		if (lives == 0) {
			return;
		}
		//put lives on top left
		for (int i = 0;i<lives;i++) {
			board[31][i] = pacmanLife;
		}
		//put score on top right
	}
	private void generateDefaultBoard() {
		for (int r=0;r<31;r++) {
			for (int c = 0;c<28;c++) {
				if ((r == 7) && (c==13 || c == 14)) { //where pacman starts, no dots
					continue;
				}
				board[r][c] = new Dot();
			}
		}
		board[7][1] = new Energizer();
		board[7][26] = new Energizer();
		board[27][1] = new Energizer();
		board[27][26] = new Energizer();
		for (int r = 0; r<31; r++) {
			if (r != 16) {
				board[r][0] = wall;
				board[r][27] = wall;
			}
		}
		for (int c = 0;c<28;c++) {
			board[0][c] = wall;
			board[30][c] = wall;
		}
		//emptying out squares near the next rectangle shape/ghost zone
		for (int r = 11;r<22;r++) {
			for (int c=7;c<21;c++) {
				board[r][c] = emptySquare;
			}
		}
		//Rectangle shape, columns 10-17, rows 14-18
		for (int r = 14;r<19;r++) {
			for (int c = 10;c<18;c++) {
				if (r==14 || r==18 || c==10 || c==17) {
					board[r][c] = wall;
				} else {
					board[r][c] = emptySquare;
				}
			}
		}
		for (int c=0;c<6;c++) {
			for (int r = 11;r<22;r++) {
				if (r != 16 && (r == 11 || r==15 || r == 17 || r ==  21 || c == 5 || c == 22)) {
					board[r][c] = wall;
				} else {
					board[r][c] = emptySquare;
				}
			}
		}
		
		
		for (int c=22;c<28;c++) {
			for (int r = 11;r<22;r++) {
				if (r != 16 && (r == 11 || r==15 || r == 17 || r ==  21 || c == 5 || c == 22)) {
					board[r][c] = wall;
				} else {
					board[r][c] = emptySquare;
				}
			}
		}
		//right side up T Shape, rows 2 - 6, columns 10-17
		for (int r = 2;r<7;r++) {
			for (int c = 10;c<18;c++) {
				if (r==6 || r == 5 || c==13 || c == 14 ) {
					board[r][c] = wall;
				}
			}
		}
		//right side up T shape, rows 8-12, columns 10-17
		for (int r = 8;r<13;r++) {
			for (int c = 10;c<18;c++) {
				if (r==11 || r == 12 || c==13 || c == 14 ) {
					board[r][c] = wall;
				}
			}
		}
		//right side up T shape, rows 20-24, columns 10-17
		for (int r = 20;r<25;r++) {
			for (int c = 10;c<18;c++) {
				if (r==23 || r == 24 || c==13 || c == 14 ) {
					board[r][c] = wall;
				}
			}
		}

		for (int r = 2;r<7;r++) {
			for (int c = 2;c<12;c++) {
				if (r==2 || r == 3 || c==7 || c == 8) {
					board[r][c] = wall;
				}
			}
		}
		for (int r = 2;r<7;r++) {
			for (int c = 16;c<26;c++) {
				if (r==2 || r == 3 || c==19 || c == 20) {
					board[r][c] = wall;
				}
			}
		}

		for (int r=17;r<25;r++) {
			for (int c = 16;c<21;c++) {
				if (r==20 || r == 21 || c == 20 || c == 19) {
					board[r][c] = wall;
				}
			}
		}
		for (int r=17;r<25;r++) {
			for (int c = 7;c<12;c++) {
				if (r==20 || r == 21 || c == 7 || c == 8) {
					board[r][c] = wall;
				}
			}
		}
		for (int c = 7;c<9;c++) {
			for (int r = 11;r<16;r++) {
				board[r][c] = wall;
			}
		}
		for (int c = 19;c<21;c++) {
			for (int r = 11;r<16;r++) {
				board[r][c] = wall;
			}
		}
		for (int r = 5;r<7;r++) {
			for (int c= 1;c<3;c++) {
				board[r][c] = wall;
			}
		}
		for (int r = 26;r<29;r++) {
			for (int c = 2;c<6;c++) {
				board[r][c] = wall;
			}
		}

		for (int r = 26;r<29;r++) {
			for (int c = 7;c<12;c++) {
				board[r][c] = wall;
			}
		}
		for (int r = 26;r<30;r++) {
			for (int c = 13;c<15;c++) {
				board[r][c] = wall;
			}
		}
		for (int r = 26;r<29;r++) {
			for (int c = 16;c<21;c++) {
				board[r][c] = wall;
			}
		}
		for (int r = 26;r<29;r++) {
			for (int c = 22;c<26;c++) {
				board[r][c] = wall;
			}
		}
		for (int r = 23;r<25;r++) {
			for (int c = 22;c<26;c++) {
				board[r][c] = wall;
			}
		}
		for (int r = 23;r<25;r++) {
			for (int c = 2;c<6;c++) {
				board[r][c] = wall;
			}
		}
		for (int c = 2;c<6;c++) {
			for (int r = 5;r<10;r++) {
				if (r==8 || r == 9 || c == 4 || c == 5) {
					board[r][c] = wall;
				}
			}
		}
		for (int c = 22;c<26;c++) {
			for (int r = 5;r<10;r++) {
				if (r==8 || r == 9 || c == 22 || c == 23) {
					board[r][c] = wall;
				}
			}
		}
		for (int r = 8;r<10;r++) {
			for (int c = 7;c<12;c++) {
				board[r][c] = wall;
			}
		}
		for (int r = 8;r<10;r++) {
			for (int c = 16;c<21;c++) {
				board[r][c] = wall;
			}
		}
		for (int r=5;r<7;r++) {
			for (int c = 25;c<28;c++) {
				board[r][c] = wall;
			}
		}






	}
	public boolean allDotsGone() {
		for (int r = 0;r<31;r++) {
			for (int c = 0;c<28;c++) {
				if (board[r][c] instanceof Dot || board[r][c] instanceof Energizer) {
					return false;
				}
			}
		}
		return true;
	}
}