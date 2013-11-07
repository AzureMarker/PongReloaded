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
public class MultiplayerMenu implements Screen {
	// Buttons
	Rectangle connectButton = new Rectangle(50, 170, 100, 25);
    Rectangle hostButton = new Rectangle(250, 140, 100, 25);
    Rectangle multiToMainButton = new Rectangle(25, 250, 100, 25);
    
    // Hover
    boolean connectHover;
    boolean hostHover;
    boolean multiToMainHover;
    
    // Game
    int GWIDTH;
    int GHEIGHT;
    boolean isHost = false;
	
	public MultiplayerMenu(int GWIDTH, int GHEIGHT) {
		this.GWIDTH = GWIDTH;
		this.GHEIGHT = GHEIGHT;
	}
	
	public Screens getScreenType() {
		return Screens.MULTIMENU;
	}
	
	public Screen getScreen() {
		return this;
	}
	
	public void displayOutput(Graphics g) {
		// Multiplayer Header
        g.setFont(new Font("Arial", Font.BOLD, 26));
        g.setColor(Color.WHITE);
        g.drawString("Multiplayer", 130, 75);
        
        // IP Address Input
        g.setFont(new Font("Arial", Font.BOLD, 12));
        g.drawString("Enter Ip Address & Port", 37, 100);
        
        // Connect Button
        if(!connectHover)
            g.setColor(Color.CYAN);
        else
            g.setColor(Color.PINK);
        g.fillRect(connectButton.x, connectButton.y, connectButton.width, connectButton.height);
        g.setColor(Color.GRAY);
        g.drawString("Connect", connectButton.x+26, connectButton.y+17);
        
        // Host Port Input
        //g.setColor(Color.WHITE);
        //g.drawString("Enter Port Number", 250, 100);
        
        // Host Button
        //if(!hostHover)
        //    g.setColor(Color.CYAN);
        //else
        //    g.setColor(Color.PINK);
        //g.fillRect(hostButton.x, hostButton.y, hostButton.width, hostButton.height);
        //g.setColor(Color.GRAY);
        //g.drawString("Host", hostButton.x+35, hostButton.y+17);
        
        // Back Button (Multiplayer to Main)
        if(!multiToMainHover)
            g.setColor(Color.CYAN);
        else
            g.setColor(Color.PINK);
        g.fillRect(multiToMainButton.x, multiToMainButton.y, multiToMainButton.width, multiToMainButton.height);
        g.setColor(Color.GRAY);
        g.drawString("Back", multiToMainButton.x+35, multiToMainButton.y+17);
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
		
		// Check if Hovering over Connect Button
        if(mx > connectButton.x && mx < connectButton.x+connectButton.width && my > connectButton.y && my < connectButton.y+connectButton.height)
            connectHover = true;
        else
            connectHover = false;
        
        // Check if Hovering over Host Button
        if(mx > hostButton.x && mx < hostButton.x+hostButton.width && my > hostButton.y && my < hostButton.y+hostButton.height)
            hostHover = true;
        else
            hostHover = false;
        
        // Check if Hovering over Multiplayer To Main Button
        if(mx > multiToMainButton.x && mx < multiToMainButton.x+multiToMainButton.width && my > multiToMainButton.y && my < multiToMainButton.y+multiToMainButton.height)
            multiToMainHover = true;
        else
            multiToMainHover = false;
        
        return this;
	}
	
	public Screen respondToUserInputClick(MouseEvent mouse) {
		int mx = mouse.getX();
        int my = mouse.getY();
        
        // Check if just Pressed Connect Button
        if(mx > connectButton.x && mx < connectButton.x+connectButton.width && my > connectButton.y && my < connectButton.y+connectButton.height) {
            if(!isHost) {
                return new MultiplayerGame(Pong.ipText.getText(), Integer.parseInt(Pong.connectPortText.getText()));
            }
            else
                Pong.ipText.setText("You are host");
        }
        
        // Check if just Pressed Host Button
        //if(mx > hostButton.x && mx < hostButton.x+hostButton.width && my > hostButton.y && my < hostButton.y+hostButton.height)
        //	return new MultiplayerGame(Integer.parseInt(Pong.hostPortText.getText()));
        
        // Check if just Pressed Multiplayer Menu To Main Button
        if(mx > multiToMainButton.x && mx < multiToMainButton.x+multiToMainButton.width && my > multiToMainButton.y && my < multiToMainButton.y+multiToMainButton.height)
            return new MainMenu(GWIDTH, GHEIGHT);
        
        return this;
	}
	
	public Screen respondToUserInput(MouseWheelEvent mouse) {
		return this;
	}
}
