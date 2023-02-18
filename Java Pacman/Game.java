import java.util.*;
import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.concurrent.*;
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
    public default void pause() {
        /* Default does nothing, if the game requires a special sequence to pause, override in the implementing class */
        return;
    }
    public void setModifiers(boolean[] values);
    public String[] getModifierNames();
    public boolean[] getModifiers();
    public default boolean sleep(int milliseconds) {
        try {
            TimeUnit.MILLISECONDS.sleep(milliseconds);
            return true;
        } catch(Throwable t) {
            return false;
        }
    }
}
