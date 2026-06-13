package spaceinvaders.ce1106.game;

// Used for testing game.

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Keyboard implements KeyListener {

    public static boolean w = false;
    public static boolean a = false;
    public static boolean s = false;
    public static boolean d = false;
    public static boolean x = false;

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W: w = true; break;
            case KeyEvent.VK_A: a = true; break;
            case KeyEvent.VK_S: s = true; break;
            case KeyEvent.VK_D: d = true; break;
            case KeyEvent.VK_X: x = true; break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W: w = false; break;
            case KeyEvent.VK_A: a = false; break;
            case KeyEvent.VK_S: s = false; break;
            case KeyEvent.VK_D: d = false; break;
            case KeyEvent.VK_X: x = false; break;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {} // Not used
}
