package pongreloaded;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Random;

/**
 * @author Mark
 */
public class Ball implements Runnable {
    // Gloabal variables
    volatile boolean isPaused = false;
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
    
    public Ball(int x, int y, boolean paddles){
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
        
        if(paddles){
            p1 = new Paddle(15, 140, 1);
            p2 = new Paddle(370, 140, 2);
        }
    }
    
    public void setXDirection(int xdir){
        xDirection = xdir;
    }
    public void setYDirection(int ydir){
        yDirection = ydir;
    }
    
    public void draw(Graphics g){
        g.setColor(Color.WHITE);
        g.fillRect(ball.x, ball.y, ball.width, ball.height);
    }
    
    public void collision(){
        if(ball.intersects(p1.paddle)){
            setXDirection(+1);
        }
        if(ball.intersects(p2.paddle)){
            setXDirection(-1);
        }
    }
    
    public void move(){
        collision();
        ball.x += xDirection;
        ball.y += yDirection;
        //Bounce the Ball When the Edge is Detected
        if(ball.x <= 0){
            setXDirection(+1);
            p2Score++;
        }
        if(ball.x >= 385){
            setXDirection(-1);
            p1Score++;
        }
        if(ball.y <= 25)
            setYDirection(+1);
        if(ball.y >= 285)
            setYDirection(-1);
    }
    
    public int getY(){
        return ball.y;
    }
    
    public void setDifficulty(int diff){
        difficulty = diff;
    }
    
    public void setWinScore(int score){
        this.winScore = score;
    }
    
    public void setPaused(boolean sp) throws InterruptedException{
        isPaused = sp;
    }
    
    @Override
    public void run(){
        try{
            while(true){
                while(!isPaused){
                    move();
                    Thread.sleep(difficulty);
                }
            }
        }
        catch(Exception e){
            System.err.println(e.getMessage());
        }
    }
}