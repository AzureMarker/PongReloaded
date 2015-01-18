package pongreloaded;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * @author Mcat12
 */
public class LocalGame implements Screen {
	// Buttons
	private Button 
		returnButton = new Button(100, 75, 200, 25, "Return to Game"),
    	mainMenuButton = new Button(100, 125, 200, 25, "Main Menu");
	
	// Game
	private Ball b;
	private boolean isPaused = false;
	private int winScore;
	
    //Threads
	private Thread 
		ball,
		p1,
		p2;
	
	private Dimension screenSize;
    
    public LocalGame(Dimension screenSize, 
    		int ballDiff, 
    		int p2Diff, 
    		int players, 
    		int mode, 
    		int winScore, 
    		int ballX, 
    		int ballY, 
    		int p1Y, 
    		int p2Y, 
    		int xDir, 
    		int yDir, 
    		int p1Score, 
    		int p2Score) {
    	this.screenSize = screenSize;
    	this.winScore = winScore;
    	b = new Ball(ballX, ballY, p1Y, p2Y, xDir, yDir, p1Score, p2Score, this);
    	b.setDifficulty(ballDiff);
    	b.p1.setDifficulty(p2Diff);
    	b.p2.setDifficulty(p2Diff);
    	b.p2.setPlayers(players);
    	b.p1.setMode(mode);
    	b.p2.setMode(mode);
    	b.setWinScore(winScore);
    	b.setP1Score(p1Score);
    	b.setP2Score(p2Score);
    	ball = new Thread(b);
    	p1 = new Thread(b.p1);
    	p2 = new Thread(b.p2);
    	System.out.println("Starting Full LocalGame");
    	startLocalGame();
    }
    
    public LocalGame(Dimension screenSize, int ballDiff, int p2Diff, int players, int mode, int winScore) {
    	this.screenSize = screenSize;
    	this.winScore = winScore;
    	b = new Ball(193, 143, true, this);
    	ball = new Thread(b);
    	p1 = new Thread(b.p1);
    	p2 = new Thread(b.p2);
    	b.setDifficulty(ballDiff);
    	b.p1.setDifficulty(p2Diff);
    	b.p2.setDifficulty(p2Diff);
    	b.p2.setPlayers(players);
    	b.p1.setMode(mode);
    	b.p2.setMode(mode);
    	System.out.println("Starting Fresh LocalGame");
    	startLocalGame();
    }
    
    public boolean isFinished() {
    	return b.getP1Score() >= winScore || b.getP2Score() >= winScore ? true : false;
    }
    
    public int getWinner() {
    	if(b.getP1Score() >= winScore)
            return 1;
        if(b.getP2Score() >= winScore)
            return 2;
        return 0;
    }
    
    public int getBallY() {
    	return b.getY();
    }
    
	public void startLocalGame() {
        ball.start();
        p1.start();
        p2.start();
    }
	
	private void drawLocalGame(Graphics g) {
		// Give Screen to Paddles
		b.p1.setScreen(this);
		b.p2.setScreen(this);
		
		// Game
        b.draw(g);
        b.p1.draw(g);
        b.p2.draw(g);
        
        // Score
        g.setColor(Color.WHITE);
        g.drawString(""+b.getP1Score(), 15, 50);
        g.drawString(""+b.getP2Score(), 370, 50);
        
        // Check if Anyone Won
        if(isFinished() == true) {
        	Pong.winID = getWinner();
        	String name = JOptionPane.showInputDialog(null,
  				  "What is your name?",
  				  "Enter your name",
  				  JOptionPane.QUESTION_MESSAGE);
        	if(Pong.winID == 1)
        		Pong.winner = new Winner(name, b.getP1Score());
        	else
        		Pong.winner = new Winner(name, b.getP2Score());
        	Pong.isFinished = true;
        }
	}
	
	public void displayOutput(Graphics g) {
		drawLocalGame(g);
		
		if(isPaused == true) {
			// Shade Background
			g.setColor(new Color(0f,0f,0f,0.3f));
	        g.fillRect(0,0, screenSize.width, screenSize.height);
	        
	        // Buttons
	        returnButton.draw(g);
	        mainMenuButton.draw(g);
		}
	}
	
	public Screens getScreenType() {
		return isPaused == true ? Screens.LOCALPAUSE : Screens.LOCALGAME;
	}
	
	public Screen getScreen() {
		return this;
	}
	
	public void menuKeyHandler(KeyEvent key) throws InterruptedException{
        if(key.getKeyCode() == KeyEvent.VK_ESCAPE)
            switchLocalPause();
    }
	
	public void switchLocalPause() throws InterruptedException{
        if(isPaused == false) {
        	isPaused = true;
        	b.setPaused(true);
        	b.p1.setPaused(true);
        	b.p2.setPaused(true);
        }
        else {
        	isPaused = false;
        	b.setPaused(false);
        	b.p1.setPaused(false);
        	b.p2.setPaused(false);
        }
    }
	
	public Screen respondToUserInput(KeyEvent key) {
		b.p1.keyPressed(key);
        b.p2.keyPressed(key);
        try {
            menuKeyHandler(key);
        }
        catch(InterruptedException ie) {
            System.out.println("Error: " + ie.getMessage());
            System.exit(-1);
        }
        
        return this;
	}
	
	public Screen respondToUserInputReleased(KeyEvent key) {
		b.p1.keyReleased(key);
        b.p2.keyReleased(key);
        
        return this;
	}
	
	public Screen respondToUserInputHover(MouseEvent mouse) {
		if(isPaused == true) {
			int mx = mouse.getX();
	        int my = mouse.getY();
	        
            returnButton.adjustHover(mx, my);
            mainMenuButton.adjustHover(mx, my);
		}
		
		return this;
	}
	
	public Screen respondToUserInputClick(MouseEvent mouse) {
		if(isPaused == true) {
			int mx = mouse.getX();
	        int my = mouse.getY();
            
            if(returnButton.intersects(mx, my)) {
                try{
                    switchLocalPause();
                }
                catch(InterruptedException ie) {
                    System.out.println("Error: " + ie.getMessage());
                    System.exit(-1);
                }
            }
            
            if(mainMenuButton.intersects(mx, my)) {
            	b.p1.stop();
            	b.p2.stop();
            	b.stop();
                return new MainMenu(screenSize, 
                		b.getDifficulty(), 
                		b.p2.difficulty, 
                		b.p2.players, 
                		b.p1.mode, 
                		b.getWinScore(), 
                		b.getX(), 
                		b.getY(), 
                		b.p1.getY(), 
                		b.p2.getY(), 
                		b.getXDirection(), 
                		b.getYDirection(), 
                		b.getP1Score(), 
                		b.getP2Score());
            }
		}
		
		return this;
	}
	
	public Screen respondToUserInput(MouseWheelEvent mouse) {
		return this;
	}
	
	public Screen windowClosingEvent(WindowEvent window) {
		return this;
	}
}
