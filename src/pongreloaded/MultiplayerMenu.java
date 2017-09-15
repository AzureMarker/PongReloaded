package pongreloaded;

import java.awt.*;
import java.awt.event.*;

/**
 * @author Mcat12
 */
public class MultiplayerMenu implements Screen {
    // Buttons
    private Button
        connectButton = new Button(50, 170, 100, 25, "Connect"),
        hostButton = new Button(250, 140, 100, 25, "Host"),
        multiToMainButton = new Button(25, 250, 100, 25, "Back");
    
    // Game
    private Dimension screenSize;
    private int winScore;

    MultiplayerMenu(Dimension screenSize, int winScore) {
        this.screenSize = screenSize;
        this.winScore = winScore;
    }

    public Screens getScreenType() {
        return Screens.MULTIMENU;
    }

    public void displayOutput(Graphics g) {
        // Multiplayer Header
        g.setFont(new Font("Arial", Font.BOLD, 26));
        g.setColor(Color.WHITE);
        g.drawString("Multiplayer", 130, 75);
        
        // IP Address Input
        g.setFont(new Font("Arial", Font.BOLD, 12));
        g.drawString("Enter Ip Address & Port", 37, 100);
        
        // Button
        connectButton.draw(g);
        
        // Host Port Input
        g.setColor(Color.WHITE);
        g.drawString("Enter Port Number", 250, 100);
        
        // Buttons
        hostButton.draw(g);
        multiToMainButton.draw(g);
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

        connectButton.adjustHover(mx, my);
        hostButton.adjustHover(mx, my);
        multiToMainButton.adjustHover(mx, my);
        
        return this;
    }

    public Screen respondToUserInputClick(MouseEvent mouse) {
        int mx = mouse.getX();
        int my = mouse.getY();
        
        if(connectButton.intersects(mx, my)) {
            return new MultiplayerGame(Pong.ipText.getText(), Integer.parseInt(Pong.connectPortText.getText()), winScore);
        }
        
        if(hostButton.intersects(mx, my)) {
            if(Pong.hostPortText.getText().equals(""))
                System.out.println("Please enter a valid port");
            else
                return new MultiplayerGame(Integer.parseInt(Pong.hostPortText.getText()), winScore);
        }
        
        if(multiToMainButton.intersects(mx, my))
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
