import java.util.*;
import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.concurrent.*;
public interface Game {
    public void update(InputEvent e);
    public Color[][] getBoard();
    public void play();
    public String getName();
    public void restart();
    public default void pause() {
    	/* Default does nothing, if the game requires a special sequence to pause, override in the implementing class */
    	return;
    }
    public void setModifiers(boolean[] values);
    public String[] getModifierNames();
    public boolean[] getModifiers();
    public default void sleep(int milliseconds) {
        try {
            TimeUnit.MILLISECONDS.sleep(milliseconds);
        } catch(Throwable t) {
            System.out.println("ERROR: "+t.getMessage());
        }
    }
}
