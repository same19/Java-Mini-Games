import java.lang.*;
import java.util.*;
public class Vector {
	public double x;
	public double y;
	public Vector() {
		this(0,0);
	}
	public Vector(double x) {
		this(x,x);
	}
	public Vector(Pair p) {
		this.x = p.x;
		this.y = p.y;
	}
	public Vector(double x, double y) {
		this.x = x;
		this.y = y;
	}
	public Vector add(Vector o) {
		return new Vector(x+o.x, y+o.y);
	}
	public Vector subtract(Vector o) {
		return new Vector(x-o.x, y-o.y);
	}
	public Vector multiply(double n) {
		return new Vector(x*n, y*n);
	}
	public double dot(Vector o) {
		return x*o.x + y*o.y;
	}
	public double norm() {
		return magnitude();
	}
	public double magnitude() {
		return Math.sqrt(magnitudeSq());
	}
	public double magnitudeSq() {
		return x*x+y*y;
	}
	public String toString() {
		return "("+x+", "+y+")";
	}
	public Pair toPair() {
		return new Pair((int)x, (int)y);
	}
}