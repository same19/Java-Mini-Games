import java.awt.*;
import java.util.*;
import java.lang.*;
public class Ghost extends Actor {
	private String colorName;
	private double intelligence; //higher is smarter
	private boolean active;
	private String image;
	private boolean vulnerable;
	private int vulnerabilityValue;
	private int vulnerabilityImageInt;
	public Ghost() {
		this("red", new Pair(0,0), 10);
	}
	public Ghost(String colorName, Pair pos, double intelligence) {
		super(pos);
		this.colorName = colorName;
		setPieceUnder(null);
		this.intelligence = intelligence;
		active = false;
		image = "ghost"+colorName+".png";
		vulnerable = false;
		vulnerabilityImageInt = 0;
		vulnerabilityValue = 0;
	}
	public String getImage() {
		return image;
	}
	public boolean isVulnerable() {
		return vulnerable;
	}
	public void changeVulnerableImage() {
		image = "ghostvulnerable"+vulnerabilityImageInt/2+".png";
		vulnerabilityImageInt = (vulnerabilityImageInt + 1)%4;
	}
	public void makeVulnerable() {
		vulnerabilityValue ++;
		vulnerable = true;
		image = "ghostvulnerable0.png";
		vulnerabilityImageInt = 0;
	}
	public void makeRegular() {
		if (vulnerabilityValue > 0) {
			vulnerabilityValue--;
		}
		if (vulnerabilityValue == 0) {
			vulnerable = false;
			image = "ghost"+colorName+".png";
		}
	}
	public void move(Pacman game, Pair pacmanPos) {
		if (vulnerable) {
			changeVulnerableImage();
		}
		if (!active) {
			return;
		}
		Pair bestDir = getBestDir(game, pacmanPos);
		setDirection(bestDir);
		Pair newPos = getPosition().add(bestDir);
		if (game.getPacmanBoard().isTeleporter(getPosition()) && (newPos.x <0 || newPos.y <0 || newPos.x >=game.getPacmanBoard().width || newPos.y >= game.getPacmanBoard().height)) {
			setPosition(game.getPacmanBoard().getTeleporter(getPosition()));
		} else {
			setPosition(newPos);
		}
	}
	public Pair getBestDir(Pacman game, Pair pacmanPos) {
		double bias = intelligence;
		Pair pos = getPosition();
		Pair dir = getDirection();
		Pair[] allMoves = {new Pair(0,1), new Pair(-1,0), new Pair(0,-1), new Pair(1,0)};
		ArrayList<Pair> availableMoves = new ArrayList<Pair>();
		ArrayList<Pair> possibleMoves = new ArrayList<Pair>();
		availableMoves.add(dir);
		availableMoves.add(dir.reverse());
		availableMoves.add(dir.reverse().multiply(-1));
		for (Pair w : availableMoves) {
			if (game.legalMove(w,this)) {
				possibleMoves.add(w);
			}
		}
		Pair bestDir;
		bestDir = possibleMoves.get(0);
		Random random = new Random();
		double[] weights = new double[possibleMoves.size()];
		for (int i = 0;i<weights.length;i++) {
			weights[i] = (bias+random.nextDouble())*pos.add(possibleMoves.get(i)).distance(pacmanPos);
		}
		int bestIndex = 0;
		for (int i = 1;i<weights.length;i++) {
			if (vulnerable) {
				if (weights[i]>=weights[bestIndex]) {
					bestIndex = i;
				}
			} else {
				if (weights[i]<=weights[bestIndex]) {
					bestIndex = i;
				}
			}
		}
		bestDir = possibleMoves.get(bestIndex);
		return bestDir;

	}
	public String toString() {
		return colorName+" Ghost";
	}
	public void activate() {
		active = true;
	}
	public void deactivate() {
		active = false;
	}
	public boolean isActive() {
		return active;
	}
	public double getIntelligence() {
		return intelligence;
	}
	public void setIntelligence(double d) {
		this.intelligence = d;
	}
}