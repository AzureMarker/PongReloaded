package pongreloaded;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

/**
 * @author Mcat12
 */
public class FinishScreen implements Screen {
	// Variables
	int winID;
	
	public FinishScreen(int winID) {
		this.winID = winID;
		System.out.println("Player " + winID + " Won!");
	}
	
	public void displayOutput(Graphics g) {
		// Finish Menu
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.setColor(Color.WHITE);
        g.drawString("Player "+winID+" Won the Game!", 100, 75);
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
		return this;
	}
	
	public Screen respondToUserInputClick(MouseEvent mouse) {
		return this;
	}
	
	public Screen respondToUserInput(MouseWheelEvent mouse) {
		return this;
	}
}
