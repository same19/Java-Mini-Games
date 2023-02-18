import java.util.*;
import java.lang.*;
public class GameState {
	private ArrayList<Block> blocks;
	private int score;
	public GameState() {
		this(null, 0);
	}
	public GameState(ArrayList<Block> blocks, int score) {
		this.blocks = blocks;
		this.score = score;
	}
	public ArrayList<Block> getBlocks() {
		return blocks;
	}
	public int getScore() {
		return score;
	}
	public String toString() {
		return blocks.toString();
	}
}