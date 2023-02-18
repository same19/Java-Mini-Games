import java.util.*;
import java.lang.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
public class Block {
	public Pair pos;
	public int x;
	public int y;
	private int value;
	public String image_name;
	public boolean mergeable;
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
		setColorAndImage();
	}
	public Color color;
	public Block() {
		this(new Pair(), 2);
	}
	public Block(int x, int y) {
		this(new Pair(x,y),2);
	}
	public Block(int x, int y, int value) {
		this(new Pair(x,y),value);
	}
	public Block(Block o) {
		this(o.pos.clone(), o.value);
	}
	public Block(Pair pos, int value) {
		this.pos = pos;
		this.x = pos.x;
		this.y = pos.y;
		this.value = value;
		this.mergeable = true;
		setColorAndImage();
	}
	private void setColorAndImage() {
		if (value == 2) {
			color = Color.BLUE;
		} else if (value == 4) {
			color = Color.GREEN;
		} else  if (value == 8) {
			color = Color.YELLOW;
		} else if (value == 16) {
			color = Color.ORANGE;
		} else {
			color = Color.RED;
		}
		image_name = "Images/val"+((Integer)value).toString() +".png";
	}
	public boolean equals(Object o) {
		if (o instanceof Block) {
			Block o1 = (Block) o;
			return pos.equals(o1.pos);
		}
		return false;
	}
	public void move(Pair direction) {
		// System.out.println("MOVE START");
		// System.out.println(pos+" "+x+" "+y);
		this.pos.add(direction);
		x = pos.x;
		y = pos.y;
		// System.out.println(pos+" "+x+" "+y);
		// System.out.println("MOVE END");
	}
	public String toString() {
		return pos.toString();
	}
}