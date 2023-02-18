import java.util.*;
import java.lang.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
public class Pair implements Cloneable {
	public static Pair LEFT = new Pair(-1,0);
    public static Pair RIGHT = new Pair(1,0);
    public static Pair UP = new Pair(0,1);
    public static Pair DOWN = new Pair(0,-1);
	public int x;
	public int y;
	public Pair() {
		this(0,0);
	}
	public Pair(int x,int y) {
		this.x = x; this.y = y;
	}
	public Pair clone() {
		try {
    		return (Pair) super.clone();
    	} catch(CloneNotSupportedException e) {
    		return new Pair();
    	}
    }
	public boolean equals(Object o) {
		if (o instanceof Pair) {
			Pair o1 = (Pair) o;
			return o1.x==x && o1.y==y;
		}
		return false;
	}
	public void add(Pair o) {
		this.x += o.x;
		this.y += o.y;
	}
	public void multiply(int n) {
		this.x *= n;
		this.y *= n;
	}
	public static Pair add(Pair a, Pair b) {
		return new Pair(a.x+b.x, a.y+b.y);
	}
	public static Pair multiply(Pair a, int n) {
		return new Pair(a.x*n, a.y*n);
	}
	public String toString() {
		return "("+x+", "+y+")";
	}
}