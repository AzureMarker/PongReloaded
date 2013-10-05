package pongreloaded;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

/**
 * @author Mcat12
 */
public interface Screen {
    public void displayOutput(Graphics g);
    
    public Screens getScreenType();
    
    public Screen respondToUserInput(KeyEvent key);
    
    public Screen respondToUserInputReleased(KeyEvent key);
    
    public Screen respondToUserInputHover(MouseEvent mouse);
    
    public Screen respondToUserInputClick(MouseEvent mouse);
    
    public Screen respondToUserInput(MouseWheelEvent mouse);
}
