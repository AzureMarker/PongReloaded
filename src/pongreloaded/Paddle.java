package pongreloaded;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;

/**
 * @author Mcat12
 */
public class Paddle implements Runnable {
    // Global Variables
    volatile boolean isPaused = false;
    volatile boolean stop = false;
    int x, y, yDirection, id, playerNum;
    Screen screen;
    
    // Difficulty
    int difficulty = 5;
    
    // Mode
    int mode = 2;
    
    // Players
    int players = 1;
    
    // Paddle Object
    Rectangle paddle;
    
    public Paddle(int x, int y, int id, LocalGame screen) {
    	this.x = x;
        this.y = y;
        this.id = id;
        this.screen = screen;
        paddle = new Rectangle(x, y, 10, 50);
    }
    
    public Paddle(int x, int y, int id, MultiplayerGame screen) {
    	this.x = x;
        this.y = y;
        this.id = id;
        this.screen = screen;
        playerNum = screen.playerNum;
        paddle = new Rectangle(x, y, 10, 50);
    }
    
    public void setScreen(Screen screen) {
    	this.screen = screen;
    }
    
    public void keyPressed(KeyEvent e) {
        switch(id) {
            default:
                System.out.println("Please enter a valid ID in Paddle Constructer");
                break;
            case 1:
                if(e.getKeyCode() == KeyEvent.VK_W) {
                    setYDirection(-1);
                }
                if(e.getKeyCode() == KeyEvent.VK_S) {
                    setYDirection(+1);
                }
                break;
            case 2:
                if(e.getKeyCode() == KeyEvent.VK_UP) {
                    setYDirection(-1);
                }
                if(e.getKeyCode() == KeyEvent.VK_DOWN) {
                    setYDirection(+1);
                }
                break;
            case 3:
            	if(playerNum == 0) {
            		if(e.getKeyCode() == KeyEvent.VK_W) {
                    	setYDirection(-1);
                	}
                	if(e.getKeyCode() == KeyEvent.VK_S) {
                    	setYDirection(+1);
                	}
            	}
                break;
            case 4:
            	if(playerNum == 1) {
            		if(e.getKeyCode() == KeyEvent.VK_W) {
                    	setYDirection(-1);
                	}
                	if(e.getKeyCode() == KeyEvent.VK_S) {
                    	setYDirection(+1);
                	}
            	}
                break;
        }
    }
    
    public void keyReleased(KeyEvent e) {
        switch(id) {
            default:
                System.out.println("Please enter a valid ID in Paddle Constructer");
                break;
            case 1:
                if(e.getKeyCode() == KeyEvent.VK_W) {
                    setYDirection(0);
                }
                if(e.getKeyCode() == KeyEvent.VK_S) {
                    setYDirection(0);
                }
                break;
            case 2:
                if(e.getKeyCode() == KeyEvent.VK_UP) {
                    setYDirection(0);
                }
                if(e.getKeyCode() == KeyEvent.VK_DOWN) {
                    setYDirection(0);
                }
                break;
            case 3:
            	if(playerNum == 0) {
            		if(e.getKeyCode() == KeyEvent.VK_W) {
            			setYDirection(0);
            		}
            		if(e.getKeyCode() == KeyEvent.VK_S) {
            			setYDirection(0);
            		}
            	}
                break;
            case 4:
            	if(playerNum == 1) {
            		if(e.getKeyCode() == KeyEvent.VK_W) {
                    	setYDirection(0);
                	}
                	if(e.getKeyCode() == KeyEvent.VK_S) {
                    	setYDirection(0);
                	}
            	}
                break;
        }
    }
    
    public void setYDirection(int yDir) {
        yDirection = yDir;
    }
    
    public int getYDirection() {
    	return yDirection;
    }
    
    public void setPlayerNum(int playerNum) {
    	this.playerNum = playerNum;
    }
    
    public void move() {
        paddle.y += yDirection;
        if(paddle.y <= 25)
            paddle.y = 25;
        if(paddle.y >= 250)
            paddle.y = 250;
        if(this.id == 2 && players == 1) {
            collision();
            if(((LocalGame) screen).getBallY() < paddle.y+25)
                setYDirection(-1);
            if(((LocalGame) screen).getBallY() > paddle.y+25)
                setYDirection(+1);
        }
    }
    
    public void collision() {
        if(paddle.y <= 25)
            setYDirection(+1);
        if(paddle.y > 250)
            setYDirection(-1);
    }
    
    public void draw(Graphics g) {
        switch(id) {
            default:
                System.out.println("Please enter a valid ID in Paddle Constructer");
                break;
            case 1:
                if(mode == 2)
                    g.setColor(Color.CYAN);
                if(mode == 1)
                    g.setColor(Color.WHITE);
                g.fillRect(paddle.x, paddle.y, paddle.width, paddle.height);
                break;
            case 2:
                if(mode == 2)
                    g.setColor(Color.PINK);
                if(mode == 1)
                    g.setColor(Color.WHITE);
                g.fillRect(paddle.x, paddle.y, paddle.width, paddle.height);
                break;
            case 3:
                if(mode == 2)
                    g.setColor(Color.CYAN);
                if(mode == 1)
                    g.setColor(Color.WHITE);
                g.fillRect(paddle.x, paddle.y, paddle.width, paddle.height);
                break;
            case 4:
                if(mode == 2)
                    g.setColor(Color.PINK);
                if(mode == 1)
                    g.setColor(Color.WHITE);
                g.fillRect(paddle.x, paddle.y, paddle.width, paddle.height);
                break;
        }
    }
    
    public void setY(int y) {
    	this.y = y;
    }
    
    public int getY() {
    	return paddle.y;
    }
    
    public void setDifficulty(int diff) {
        difficulty = diff;
    }
    
    public void setPlayers(int players) {
        this.players = players;
    }
    
    public void setMode(int mode) {
        this.mode = mode;
    }
    
    public void setPaused(boolean sp) throws InterruptedException {
        isPaused = sp;
    }
    
    public void stop() {
    	stop = true;
    }
    
    @Override
    public void run() {
        try{
            while(stop == false) {
                while(!isPaused) {
                    move();
                    Thread.sleep(difficulty);
                }
            }
        }
        catch(Exception e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
    }
    
}