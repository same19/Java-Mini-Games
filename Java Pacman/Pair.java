import java.util.*;
import java.lang.*;
public class Pair implements Cloneable{
	public int x;
	public int y;
	public Pair() {
		this(0,0);
	}
	public Pair(int x) {
		this(x,x);
	}
	public Pair(int x, int y) {
		this.x = x;
		this.y = y;
	}
	public String toString() {
		return "("+x+", "+y+")";
	}
	public double magnitude() {
		return Math.sqrt(x*x + y*y);
	}
	public Object clone() {
		try {
			return super.clone();
		} catch(Throwable t) {
			return null;
		}
	}
	public String approxString() {
		if (x == 0 && y == 0) {
			return "zero";
		}
		if (x >= 0 && y >= 0) {
			if (y>=x) {
				return "up";
			} else {
				return "right";
			}
		} else if (x >= 0 && y <= 0) {
			if (-y >= x) {
				return "down";
			} else {
				return "right";
			}
		} else if (x <= 0 && y >= 0) {
			if (y >= -x) {
				return "up";
			} else {
				return "left";
			}

		} else {
			if (-y >= -x) {
				return "down";
			} else {
				return "left";
			}
		}
	}
	public Pair add(Pair o) {
		if (o == null) {
			return null;
		}
		return new Pair(this.x+o.x, this.y+o.y);
	}
	public Pair multiply(int n) {
		return new Pair(this.x*n, this.y*n);
	}
	public boolean equals(Object o) {
		if (o==null) {
			return false;
		}
		if (!(o instanceof Pair)) {
			return false;
		}
		Pair oP = ((Pair) o);
		return oP.x == x && oP.y == y;
	}
	public double distance(Pair o) {
		return Math.sqrt((x-o.x)*(x-o.x) + (y-o.y)*(y-o.y));
	}
	public Pair reverse() {
		return new Pair(y,x);
	}
}