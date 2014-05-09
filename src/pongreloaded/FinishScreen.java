package pongreloaded;

import java.awt.*;
import java.awt.event.*;

/**
 * @author Mcat12
 */
public class FinishScreen implements Screen {
	// Button
	Button mainMenuButton;
	
	// Display
	Dimension screenSize;
	
	// Winner
	int winID;
	
	public FinishScreen(Dimension screenSize, int winID) {
		this.screenSize = screenSize;
		this.winID = winID;
		mainMenuButton = new Button(100, 125, 200, 25, "Main Menu", screenSize);
		System.out.println("Player " + winID + " Won!");
	}
	
	public void displayOutput(Graphics g) {
		// Finish Menu
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.setColor(Color.WHITE);
        g.drawString("Player " + winID + " Won the Game!", 95, 75);
        
        // Main Menu Button
        mainMenuButton.draw(g);
	}
	
	public Screens getScreenType() {
		return Screens.FINSIH;
	}
	
	public Screen getScreen() {
		return this;
	}
	
	public Screen respondToUserInput(KeyEvent key) {
		return this;
	}
	
	public Screen respondToUserInputReleased(KeyEvent key) {
		return this;
	}
	
	public Screen respondToUserInputHover(MouseEvent mouse) {
		int mx = mouse.getX();
        int my = mouse.getY();
		
		// Check if Hovering over Main Menu Button
        mainMenuButton.adjustHover(mx, my);
		
		return this;
	}
	
	public Screen respondToUserInputClick(MouseEvent mouse) {
		int mx = mouse.getX();
        int my = mouse.getY();
		
		// Check if just Pressed Main Menu Button
        if(mainMenuButton.intersects(mx, my))
            return new MainMenu(screenSize);
		return this;
	}
	
	public Screen respondToUserInput(MouseWheelEvent mouse) {
		return this;
	}
	
	public Screen windowClosingEvent(WindowEvent window) {
		return this;
	}
}
