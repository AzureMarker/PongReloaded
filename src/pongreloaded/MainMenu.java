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
public class MainMenu implements Screen {
	// Buttons
	Rectangle startButton = new Rectangle(150, 100, 100, 25);
    Rectangle diffButton = new Rectangle(85, 185, 100, 25);
    Rectangle playersButton = new Rectangle(215, 185, 100, 25);
    Rectangle modeButton = new Rectangle(85, 140, 100, 25);
    Rectangle scoreButton = new Rectangle(85, 225, 100, 25);
    Rectangle exitButton = new Rectangle(215, 225, 100, 25);
    Rectangle multiButton = new Rectangle(215, 140, 100, 25);
    
    // Hover
    boolean startHover;
    boolean diffHover;
    boolean playersHover;
    boolean modeHover;
    boolean scoreHover;
    boolean exitHover;
    boolean multiHover;
    
    // Game
    boolean isFirstRun;
    int difficulty = 1;
    int ballDiff = 7;
    int p2Diff = 5;
    int players = 1;
    int mode = 2;
    int winScore = 10;
    int ballX;
    int ballY;
    int p1Y;
    int p2Y;
    int xDir;
    int yDir;
    int p1Score;
    int p2Score;
    
    // Variables for Screen Size
    int GWIDTH;
    int GHEIGHT;
    
    public MainMenu(int GWIDTH, int GHEIGHT, int ballDiff, int p2Diff, int players, int mode, int winScore, int ballX, int ballY, int p1Y, int p2Y, int xDir, int yDir, int p1Score, int p2Score) {
    	this.GWIDTH = GWIDTH;
    	this.GHEIGHT = GHEIGHT;
    	this.ballDiff = ballDiff;
    	this.p2Diff = p2Diff;
    	this.players = players;
    	this.mode = mode;
    	this.winScore = winScore;
    	this.ballX = ballX;
    	this.ballY = ballY;
    	this.p1Y = p1Y;
    	this.p2Y = p2Y;
    	this.xDir = xDir;
    	this.yDir = yDir;
    	this.p1Score = p1Score;
    	this.p2Score = p2Score;
    	isFirstRun = false;
    	System.out.println("MainMenu Full Constructor");
    }
    
    public MainMenu(int GWIDTH, int GHEIGHT) {
    	this.GWIDTH = GWIDTH;
    	this.GHEIGHT = GHEIGHT;
    	isFirstRun = true;
    	System.out.println("MainMenu First Run");
    }
	
    public Screens getScreenType() {
    	return Screens.MAINMENU;
    }
    
    public Screen getScreen() {
		return this;
	}
    
	public void displayOutput(Graphics g) {
		// Menu
        g.setFont(new Font("Arial", Font.BOLD, 26));
        g.setColor(Color.WHITE);
        g.drawString("Pong Reloaded", 108, 75);
        
        // Start Button
        if(!startHover)
            g.setColor(Color.CYAN);
        else
            g.setColor(Color.PINK);
        g.fillRect(startButton.x, startButton.y, startButton.width, startButton.height);
        g.setFont(new Font("Arial", Font.BOLD, 12));
        g.setColor(Color.GRAY);
        if(isFirstRun == true)
            g.drawString("Start Game", startButton.x+20, startButton.y+17);
        else
            g.drawString("Resume", startButton.x+27, startButton.y+17);
        
        // Multiplayer Button
        if(!multiHover)
            g.setColor(Color.CYAN);
        else
            g.setColor(Color.PINK);
        g.fillRect(multiButton.x, multiButton.y, multiButton.width, multiButton.height);
        g.setColor(Color.GRAY);
        g.drawString("Multiplayer", multiButton.x+20,  multiButton.y+17);
        
        // Difficulty Button
        if(!diffHover)
            g.setColor(Color.CYAN);
        else
            g.setColor(Color.PINK);
        
        g.fillRect(diffButton.x, diffButton.y-5, diffButton.width, diffButton.height);
        g.setColor(Color.GRAY);
        g.drawString("Difficulty:", diffButton.x+7, diffButton.y+12);
        switch(difficulty) {
            case 1:
                g.drawString("Easy", diffButton.x+63, diffButton.y+12);
                break;
            case 2:
                g.drawString("Med", diffButton.x+63, diffButton.y+12);
                break;
            case 3:
                g.drawString("Hard", diffButton.x+63, diffButton.y+12);
                break;
        }
        
        // Players Button
        if(!playersHover)
            g.setColor(Color.CYAN);
        else
            g.setColor(Color.PINK);
        g.fillRect(playersButton.x, playersButton.y-5, playersButton.width, playersButton.height);
        g.setColor(Color.GRAY);
        g.drawString("Players:", playersButton.x+8, playersButton.y+12);
        switch(players) {
            case 1:
                g.drawString("Single", playersButton.x+58, playersButton.y+12);
                break;
            case 2:
                g.drawString("Multi", playersButton.x+58, playersButton.y+12);
                break;
        }
        
        // Mode Button
        if(!modeHover)
            g.setColor(Color.CYAN);
        else
            g.setColor(Color.PINK);
        g.fillRect(modeButton.x, modeButton.y, modeButton.width, modeButton.height);
        g.setColor(Color.GRAY);
        g.drawString("Mode:", modeButton.x+5, modeButton.y+17);
        switch(mode) {
            case 1:
                g.drawString("Original", modeButton.x+45, modeButton.y+17);
                break;
            case 2:
                g.drawString("Reloaded", modeButton.x+45, modeButton.y+17);
                break;
        }
        
        // Score Button
        if(!scoreHover)
            g.setColor(Color.CYAN);
        else
            g.setColor(Color.PINK);
        g.fillRect(scoreButton.x, scoreButton.y, scoreButton.width, scoreButton.height);
        g.setColor(Color.GRAY);
        g.drawString("To Win:", scoreButton.x+15, scoreButton.y+17);
        g.drawString(""+winScore, scoreButton.x+61, scoreButton.y+17);
        
        // Exit Button
        if(!exitHover)
            g.setColor(Color.CYAN);
        else
            g.setColor(Color.PINK);
        g.fillRect(exitButton.x, exitButton.y, exitButton.width, exitButton.height);
        g.setColor(Color.GRAY);
        g.drawString("Exit", exitButton.x+38, exitButton.y+17);
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
        
        // Check if Hovering over Start Button
        if(mx > startButton.x && mx < startButton.x+startButton.width && my > startButton.y && my < startButton.y+startButton.height)
            startHover = true;
        else
            startHover = false;
        
        // Check if Hovering over Difficulty Button
        if(mx > diffButton.x && mx < diffButton.x+diffButton.width && my > diffButton.y && my < diffButton.y+diffButton.height)
            diffHover = true;
        else
            diffHover = false;
        
        // Check if Hovering over Players Button
        if(mx > playersButton.x && mx < playersButton.x+playersButton.width && my > playersButton.y && my < playersButton.y+playersButton.height)
            playersHover = true;
        else
            playersHover = false;
        
        // Check if Hovering over Mode Button
        if(mx > modeButton.x && mx < modeButton.x+modeButton.width && my > modeButton.y && my < modeButton.y+modeButton.height)
            modeHover = true;
        else
            modeHover = false;
        
        // Check if Hovering over Score Button
        if(mx > scoreButton.x && mx < scoreButton.x+scoreButton.width && my > scoreButton.y && my < scoreButton.y+scoreButton.height)
            scoreHover = true;
        else
            scoreHover = false;
        
        // Check if Hovering over Exit Button
        if(mx > exitButton.x && mx < exitButton.x+exitButton.width && my > exitButton.y && my < exitButton.y+exitButton.height)
            exitHover = true;
        else
            exitHover = false;
        
        // Check if Hovering over Multiplayer Button
        if(mx > multiButton.x && mx < multiButton.x+multiButton.width && my > multiButton.y && my < multiButton.y+multiButton.height)
            multiHover = true;
        else
            multiHover = false;
        
	    return this;
	}
	
	public Screen respondToUserInputClick(MouseEvent mouse) {
		int mx = mouse.getX();
        int my = mouse.getY();
        
        // Check if Start Button was pressed
	    if(mx > startButton.x && mx < startButton.x+startButton.width && my > startButton.y && my < startButton.y+startButton.height) {
	        if(isFirstRun == true)
	        	return new LocalGame(GWIDTH, GHEIGHT, ballDiff, p2Diff, players, mode, winScore);
	        else
	        	return new LocalGame(GWIDTH, GHEIGHT, ballDiff, p2Diff, players, mode, winScore, ballX, ballY, p1Y, p2Y, xDir, yDir, p1Score, p2Score);
	    }
	    
	    // Check if Score Button was pressed
	    if(mx > scoreButton.x && mx < scoreButton.x+scoreButton.width && my > scoreButton.y && my < scoreButton.y+scoreButton.height) {
	        winScore++;
	        if(winScore > 100)
	            winScore = 1;
	    }
	    
	    // Check if Difficulty Button was pressed
	    if(mx > diffButton.x && mx < diffButton.x+diffButton.width && my > diffButton.y && my < diffButton.y+diffButton.height) {
	        switch(difficulty) {
	            case 1:
	                ballDiff = 4;
	                if(players == 1)
	                    p2Diff = 6;
	                difficulty = 2;
	                break;
	            case 2:
	                ballDiff = 2;
	                if(players == 1)
	                    p2Diff = 3;
	                difficulty = 3;
	                break;
	            case 3:
	                ballDiff = 8;
	                if(players == 1)
	                    p2Diff = 9;
	                difficulty = 1;
	                break;
	        }
	    }
	    
	    // Check if Players Button was pressed
	    if(mx > playersButton.x && mx < playersButton.x+playersButton.width && my > playersButton.y && my < playersButton.y+playersButton.height) {
	        switch(players) {
	            case 1:
	                players = 2;
	                break;
	            case 2:
	                players = 1;
	                break;
	        }
	    }
	    
	    // Check if Mode Button was pressed
	    if(mx > modeButton.x && mx < modeButton.x+modeButton.width && my > modeButton.y && my < modeButton.y+modeButton.height) {
	        switch(mode) {
	            case 1:
	                mode = 2;
	                Pong.retro = false;
	                break;
	            case 2:
	                mode = 1;
	                Pong.retro = true;
	                break;
	        }
	    }
	    
	    // Check if Multiplayer Button was pressed
	    if(mx > multiButton.x && mx < multiButton.x+multiButton.width && my > multiButton.y && my < multiButton.y+multiButton.height) {
	    	Pong.disposeMainMenu = true;
	    	return new MultiplayerMenu(GWIDTH, GHEIGHT);
	    }
	    
	    // Check if Exit Button was pressed
        if(mx > exitButton.x && mx < exitButton.x+exitButton.width && my > exitButton.y && my < exitButton.y+exitButton.height)
            Pong.quit = true;
	    
	    return this;
	}
	
	public Screen respondToUserInput(MouseWheelEvent mouseWheel) {
		int mx = mouseWheel.getX();
        int my = mouseWheel.getY();
        int mwDir = mouseWheel.getWheelRotation();
		
	    // Check if just Scrolled Over Score Button
        if(mx > scoreButton.x && mx < scoreButton.x+scoreButton.width && my > scoreButton.y && my < scoreButton.y+scoreButton.height) {
            if(mwDir < 0)
                winScore++;
            else
                winScore--;
            if(winScore > 100)
                winScore = 1;
            if(winScore < 1)
                winScore = 100;
        }
        
        return this;
	}
}
