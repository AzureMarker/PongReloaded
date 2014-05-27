package pongreloaded;

import java.awt.*;
import java.awt.event.*;

/**
 * @author Mcat12
 */
public class MainMenu implements Screen {
	// Buttons
	Button startButton;
    Button diffButton;
    Button playersButton;
    Button modeButton;
    Button scoreButton;
    Button multiButton;
    Button exitButton;
    
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
    Dimension screenSize;
    
    public MainMenu(Dimension screenSize, int ballDiff, int p2Diff, int players, int mode, int winScore, int ballX, int ballY, int p1Y, int p2Y, int xDir, int yDir, int p1Score, int p2Score) {
    	this.screenSize = screenSize;
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
    	startButton = new Button(150, 100, 100, 25, "Resume");
    	diffButton = new Button(85, 185, 100, 25, "Difficulty: ");
    	playersButton = new Button(215, 185, 100, 25, "Players: ");
    	modeButton = new Button(85, 140, 100, 25, "");
    	scoreButton = new Button(85, 225, 100, 25, "To Win: ");
    	multiButton = new Button(215, 140, 100, 25, "Multiplayer");
    	exitButton = new Button(215, 225, 100, 25, "Exit");
    	System.out.println("MainMenu Full Constructor");
    }
    
    public MainMenu(Dimension screenSize) {
    	this.screenSize = screenSize;
    	isFirstRun = true;
    	startButton = new Button(150, 100, 100, 25, "Start Game");
    	diffButton = new Button(85, 185, 100, 25, "Difficulty: ");
    	playersButton = new Button(215, 185, 100, 25, "Players: ");
    	modeButton = new Button(85, 140, 100, 25, "");
    	scoreButton = new Button(85, 225, 100, 25, "To Win: ");
    	multiButton = new Button(215, 140, 100, 25, "Multiplayer");
    	exitButton = new Button(215, 225, 100, 25, "Exit");
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
        startButton.draw(g);
        
        // Multiplayer Button
        multiButton.draw(g);
        
        // Difficulty Button
        switch(difficulty) {
            case 1:
                diffButton.setText("Difficulty: Easy");
                break;
            case 2:
                diffButton.setText("Difficulty: Mid");
                break;
            case 3:
                diffButton.setText("Difficulty: Hard");
                break;
        }
        diffButton.draw(g);
        
        // Players Button
        switch(players) {
            case 1:
                playersButton.setText("Players: Single");
                break;
            case 2:
                playersButton.setText("Players: Multi");
                break;
        }
        playersButton.draw(g);
        
        // Mode Button
        switch(mode) {
            case 1:
                modeButton.setText("Mode: Original");
                break;
            case 2:
                modeButton.setText("Mode: Reloaded");
                break;
        }
        modeButton.draw(g);
        
        // Score Button
        scoreButton.setText("Score: " + winScore);
        scoreButton.draw(g);
        
        // Exit Button
        exitButton.draw(g);
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
        startButton.adjustHover(mx, my);
        
        // Check if Hovering over Difficulty Button
        diffButton.adjustHover(mx, my);
        
        // Check if Hovering over Players Button
        playersButton.adjustHover(mx, my);
        
        // Check if Hovering over Mode Button
        modeButton.adjustHover(mx, my);
        
        // Check if Hovering over Score Button
        scoreButton.adjustHover(mx, my);
        
        // Check if Hovering over Multiplayer Button
        multiButton.adjustHover(mx, my);
        
        // Check if Hovering over Exit Button
        exitButton.adjustHover(mx, my);
        
	    return this;
	}
	
	public Screen respondToUserInputClick(MouseEvent mouse) {
		int mx = mouse.getX();
        int my = mouse.getY();
        
        // Check if Start Button was pressed
	    if(startButton.intersects(mx, my)) {
	        if(isFirstRun == true)
	        	return new LocalGame(screenSize, ballDiff, p2Diff, players, mode, winScore);
	        else
	        	return new LocalGame(screenSize, ballDiff, p2Diff, players, mode, winScore, ballX, ballY, p1Y, p2Y, xDir, yDir, p1Score, p2Score);
	    }
	    
	    // Check if Score Button was pressed
	    if(scoreButton.intersects(mx, my)) {
	        winScore++;
	        if(winScore > 100)
	            winScore = 1;
	    }
	    
	    // Check if Difficulty Button was pressed
	    if(diffButton.intersects(mx, my)) {
	        switch(difficulty) {
	            case 1:
	                ballDiff = 4;
	                if(players == 1)
	                    p2Diff = 4;
	                difficulty = 2;
	                break;
	            case 2:
	                ballDiff = 2;
	                if(players == 1)
	                    p2Diff = 2;
	                difficulty = 3;
	                break;
	            case 3:
	                ballDiff = 8;
	                if(players == 1)
	                    p2Diff = 8;
	                difficulty = 1;
	                break;
	        }
	    }
	    
	    // Check if Players Button was pressed
	    if(playersButton.intersects(mx, my)) {
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
	    if(modeButton.intersects(mx, my)) {
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
	    if(multiButton.intersects(mx, my)) {
	    	Pong.disposeMainMenu = true;
	    	return new MultiplayerMenu(screenSize, winScore);
	    }
	    
	    // Check if Exit Button was pressed
        if(exitButton.intersects(mx, my))
            Pong.quit = true;
	    
	    return this;
	}
	
	public Screen respondToUserInput(MouseWheelEvent mouseWheel) {
		int mx = mouseWheel.getX();
        int my = mouseWheel.getY();
        int mwDir = mouseWheel.getWheelRotation();
		
	    // Check if just Scrolled Over Score Button
        if(scoreButton.intersects(mx, my)) {
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
	
	public Screen windowClosingEvent(WindowEvent window) {
		return this;
	}
}
