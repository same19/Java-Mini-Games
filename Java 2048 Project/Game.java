import java.util.*;
import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.*;
import javax.swing.*;
public interface Game {
	public void init_vars();
    public void update(InputEvent e);
    public Color[][] getBoard();
    public void play();
    public default String getName() {
    	return "Game";
    }
    public default void restart() {
    	init_vars();
    	(new RestartThread(this)).start();
    }
    public default void sleep(int milliseconds) {
    	try {
            TimeUnit.MILLISECONDS.sleep(milliseconds);
        } catch(Throwable t) {
            System.out.println("ERROR: "+t.getMessage());
        }
    }

}
