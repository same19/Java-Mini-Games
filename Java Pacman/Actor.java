public class Actor implements Piece {
	private Vector actualPos;
	private Pair pos;
	private Pair direction;
	private String image;
	private Piece pieceUnder;
	public Actor() {
		this(new Pair());
	}
	public Actor(Pair pos) {
		this(pos, new Pair(-1,0));
	}
	public Actor(Pair pos, Pair dir) {
		this.actualPos = new Vector(pos);
		this.pos = pos;
		this.image = "blackSquare.png";
		this.direction = dir;
	}
	public String getImage() {
		return image;
	}
	public void setActualPosition(Vector v) {
		this.actualPos = v;
	}
	public Vector getActualPosition() {
		return actualPos;
	}
	public void moveActualPosition(Vector v) {
		this.actualPos = actualPos.add(v);
	}
	public void move(Pair dir) {
		double d = dir.magnitude();
		if (d != 0) {
			direction = new Pair((int)(dir.x/d), (int)(dir.y/d));
		} else {
			direction = new Pair(1,0);
		}
		pos.x += dir.x;
		pos.y += dir.y;
	}
	public Pair getPosition() {
		return pos;
	}
	public void setPosition(Pair p) {
		this.pos = p;
	}
	public Pair getDirection() {
		return direction;
	}
	public void setDirection(Pair p) {
		this.direction = p;
	}
	public void setPieceUnder(Piece p) {
		if (!(p instanceof Actor)) {
			this.pieceUnder = p;
		} else {
			System.out.println("ACTOR COLLISION "+this.toString()+" X "+p.toString());
		}
	}
	public Piece getPieceUnder() {
		return pieceUnder;
	}
}