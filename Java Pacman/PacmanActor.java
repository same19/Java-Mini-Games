public class PacmanActor extends Actor {
	private int mouthOpen;
	private String currentImage;
	private Pair nextDirection;
	private int delta_imageChange;
	public PacmanActor() {
		this(new Pair(), new Pair());
	}
	public PacmanActor(Pair pos, Pair dir) {
		super(pos, dir);
		mouthOpen = 0;
		nextDirection = getDirection();
		currentImage = "pacmanclosed.png";
		delta_imageChange = 100;
	}
	public String getImage() {
		return currentImage;
	}
	public void resetImage() {
		mouthOpen = 0;
		currentImage = "pacmanclosed.png";
	}
	public void changeImage() {
		Pair dir = getDirection();
		if (mouthOpen == 0) {
			currentImage = "pacmanclosed.png";
		} else if (mouthOpen == 1 || mouthOpen == 3) {
			currentImage = "pacman"+dir.approxString()+".png";
		} else if (mouthOpen == 2) {
			currentImage = "pacman"+dir.approxString()+"open.png";
		}
	}
	public void changeMouthOpening() {
		this.changeMouthOpening(this.getDirection());
	}
	public void changeMouthOpening(Pair dir) {
		mouthOpen = (mouthOpen + 1)%4;
	}
	public Pair getNextDirection() {
		return nextDirection;
	}
	public void setNextDirection(Pair nextDirection) {
		this.nextDirection = nextDirection;
	}
	public void setDirection(Pair dir) {
		super.setDirection(dir);
	}
	public void adjustDirection(Pacman game) {
		if (game.legalMove(getNextDirection(),this)) {
			setDirection(getNextDirection());
		}
	}
	public String toString() {
		return "Pacman";
	}
}