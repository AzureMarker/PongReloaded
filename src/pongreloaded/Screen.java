package pongreloaded;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.WindowEvent;

/**
 * @author Mcat12
 */
public interface Screen {
    void displayOutput(Graphics g);
    
    Screens getScreenType();

    Screen respondToUserInput(KeyEvent key);
    
    Screen respondToUserInputReleased(KeyEvent key);
    
    Screen respondToUserInputHover(MouseEvent mouse);
    
    Screen respondToUserInputClick(MouseEvent mouse);
    
    Screen respondToUserInput(MouseWheelEvent mouse);
    
    Screen windowClosingEvent(WindowEvent window);
}
