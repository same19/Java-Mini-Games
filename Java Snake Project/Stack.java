import java.util.*;
import java.lang.*;
public class Stack<T> {
	private static final int INIT_CAPACITY = 10;
	private int capacity;
	private Object[] arr;
	private int top;
	public Stack() {
		capacity = INIT_CAPACITY;
		arr = new Object[capacity];
		top = -1;
	}
	public T push() {
		if (top >= capacity -1) {
			System.out.println("oops over capacity");
			capacity += INIT_CAPACITY;
			Object[] arr2 = new Object[capacity];
			for (int i=0;i<arr.length;i++) {
				arr2[i] = arr[i];
			}
			arr = arr2;
		}
		top++;
		return peek();
	}
	public T push(T t) {
		push();
		arr[top] = (Object) t;
		if (top != capacity - 1) {
			for (int i=top+1;i<capacity;i++) {
				arr[i] = null;
			}
		}
		return t;
	}
	public T peek() {
		if (top >= 0) {
			return (T)arr[top];
		} else {
			return null;
		}
	}
	public T pop() {
		top--;
		if (top >= 0) {
			return (T)arr[top+1];
		} else {
			return null;
		}
	}
	public int size() {
		return top+1;
	}
	public boolean isEmpty() {
		return top < 0;
	}
	public boolean isFull() { //returns true if all indices above top correspond to null values
		if (top>=capacity) {
			return true;
		}
		for (int i=top+1;i<capacity;i++) {
			if (arr[i] != null) {
				return false;
			}
		}
		return true;
	}
	public String toString() {
		return Arrays.deepToString(arr);
	}
}