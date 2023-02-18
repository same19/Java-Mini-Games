public class PositionedImage{
	private Vector position;
	private Vector size;
	private String image;
	public PositionedImage() {
		this(null, new Vector(), new Vector());
	}
	public PositionedImage(String image, Vector position, Vector size) {
		this.image = image;
		this.position = position;
		this.size = size;
	}
	public Vector getPosition() {
		return position;
	}
	public void setPosition(Vector p) {
		this.position = p;
	}
	public Vector getSize() {
		return size;
	}
	public void setSize(Vector p) {
		this.size = p;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String s) {
		this.image = s;
	}
}