package pongreloaded;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Random;

/**
 * @author Mcat12
 */
public class Ball implements Runnable {
    // Global variables
    volatile boolean isPaused = false;
    volatile boolean stop = false;
    int x, y, xDirection, yDirection;
    
    // Difficulty
    int difficulty = 7;
    
    // Score
    int p1Score, p2Score;
    int winScore = 10;
    
    // Player Paddle Objects
    Paddle p1;
    Paddle p2;
    
    // Ball Object
    Rectangle ball;
    
    public Ball(int x, int y, boolean paddles, LocalGame LGscreen) {
        p1Score = p2Score = 0;
        this.x = x;
        this.y = y;
        
        // Set Ball Moving Randomly
        Random r = new Random();
        int rDir = r.nextInt(2);
        if(rDir == 0)
            rDir--;
        setXDirection(rDir);
        int yrDir = r.nextInt(2);
        if(yrDir == 0)
            yrDir--;
        setYDirection(yrDir);
        
        // Create the Ball
        ball = new Rectangle(this.x, this.y, 7, 7);
        
        if(paddles) {
            p1 = new Paddle(15, 140, 1, LGscreen);
            p2 = new Paddle(370, 140, 2, LGscreen);
        }
    }
    
    public Ball(int x, int y, boolean paddles, MultiplayerGame MGscreen) {
        p1Score = p2Score = 0;
        this.x = x;
        this.y = y;
        
        // Set Ball Moving Randomly
        Random r = new Random();
        int rDir = r.nextInt(2);
        if(rDir == 0)
            rDir--;
        setXDirection(rDir);
        int yrDir = r.nextInt(2);
        if(yrDir == 0)
            yrDir--;
        setYDirection(yrDir);
        
        // Create 'ball'
        ball = new Rectangle(this.x, this.y, 7, 7);
        
        if(paddles) {
            p1 = new Paddle(15, 140, 3, MGscreen);
            p2 = new Paddle(370, 140, 4, MGscreen);
        }
    }
    
    public Ball(int x, int y, int p1Y, int p2Y, int xDir, int yDir, int p1Score, int p2Score, LocalGame LGscreen) {
        this.x = x;
        this.y = y;
        this.p1Score = p1Score;
        this.p2Score = p2Score;
        
        setXDirection(xDir);
        setYDirection(yDir);
        
        // Create the Ball
        ball = new Rectangle(x, y, 7, 7);
        
        p1 = new Paddle(15, p1Y, 1, LGscreen);
        p2 = new Paddle(370, p2Y, 2, LGscreen);
    }
    
    public void setXDirection(int xdir) {
        xDirection = xdir;
    }
    
    public void setYDirection(int ydir) {
        yDirection = ydir;
    }
    
    public int getXDirection() {
    	return xDirection;
    }
    
    public int getYDirection() {
    	return yDirection;
    }
    
    public void draw(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRect(ball.x, ball.y, ball.width, ball.height);
    }
    
    public void collision() {
        if(ball.intersects(p1.paddle)) {
            setXDirection(+1);
        }
        if(ball.intersects(p2.paddle)) {
            setXDirection(-1);
        }
    }
    
    public void move() {
        collision();
        ball.x += xDirection;
        ball.y += yDirection;
        //Bounce the Ball When the Edge is Detected
        if(ball.x <= 0) {
            setXDirection(+1);
            p2Score++;
        }
        if(ball.x >= 385) {
            setXDirection(-1);
            p1Score++;
        }
        if(ball.y <= 25)
            setYDirection(+1);
        if(ball.y >= 285)
            setYDirection(-1);
    }
    
    public void setX(int x) {
    	this.x = x;
    }
    
    public void setY(int y) {
    	this.y = y;
    }
    
    public int getX() {
    	return ball.x;
    }
    
    public int getY() {
        return ball.y;
    }
    
    public void setDifficulty(int diff) {
        difficulty = diff;
    }
    
    public void setWinScore(int score) {
        this.winScore = score;
    }
    
    public void setPaused(boolean sp) throws InterruptedException{
        isPaused = sp;
    }
    
    
    public void stop() {
    	stop = true;
    }
    
    @Override
    public void run() {
        try{
        	while(stop == false) {
        		while(isPaused == false) {
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