package pongreloaded;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

/**
 * @author Mcat12
 */
public class FinishScreen implements Screen {
	// Button
	Rectangle mainMenuButton = new Rectangle(100, 125, 200, 25);
	
	// Hover
	boolean mainMenuHover;
	
	// Display
	int GWIDTH;
	int GHEIGHT;
	
	// Winner
	int winID;
	
	public FinishScreen(int GWIDTH, int GHEIGHT, int winID) {
		this.GWIDTH = GWIDTH;
		this.GHEIGHT = GHEIGHT;
		this.winID = winID;
		System.out.println("Player " + winID + " Won!");
	}
	
	public void displayOutput(Graphics g) {
		// Finish Menu
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.setColor(Color.WHITE);
        g.drawString("Player " + winID + " Won the Game!", 95, 75);
        
        // Main Menu Button
        g.setFont(new Font("Arial", Font.BOLD, 12));
        if(!mainMenuHover)
            g.setColor(Color.CYAN);
        else
            g.setColor(Color.PINK);
        g.fillRect(mainMenuButton.x, mainMenuButton.y, mainMenuButton.width, mainMenuButton.height);
        g.setColor(Color.GRAY);
        g.drawString("Main Menu", mainMenuButton.x+70, mainMenuButton.y+17);
	}
	
	public Screens getScreenType() {
		return Screens.FINSIH;
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
        if(mx > mainMenuButton.x && mx < mainMenuButton.x+mainMenuButton.width && my > mainMenuButton.y && my < mainMenuButton.y+mainMenuButton.height)
            mainMenuHover = true;
        else
            mainMenuHover = false;
		
		return this;
	}
	
	public Screen respondToUserInputClick(MouseEvent mouse) {
		int mx = mouse.getX();
        int my = mouse.getY();
		
		// Check if just Pressed Main Menu Button
        if(mx > mainMenuButton.x && mx < mainMenuButton.x+mainMenuButton.width && my > mainMenuButton.y && my < mainMenuButton.y+mainMenuButton.height)
            return new MainMenu(GWIDTH, GHEIGHT);
		return this;
	}
	
	public Screen respondToUserInput(MouseWheelEvent mouse) {
		return this;
	}
}
