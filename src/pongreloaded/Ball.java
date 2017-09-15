package pongreloaded;

import java.awt.*;
import java.util.*;

/**
 * @author Mcat12
 */
class Ball implements Runnable {
    private volatile boolean isPaused = false;
    private volatile boolean stop = false;
    private int x;
    private int y;
    private int xDirection;
    private int yDirection;
    private int difficulty = 7;
    private int p1Score;
    private int p2Score;
    private int winScore = 10;
    Paddle p1, p2;
    private Rectangle ball;
    
    Ball(int x, int y, LocalGame LGscreen) {
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

        // Create paddles
        p1 = new Paddle(15, 140, 1, LGscreen);
        p2 = new Paddle(370, 140, 2, LGscreen);
    }
    
    Ball(int x, int y, MultiplayerGame MGscreen) {
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

        // Create paddles
        p1 = new Paddle(15, 140, 3, MGscreen);
        p2 = new Paddle(370, 140, 4, MGscreen);
    }
    
    Ball(int x, int y, int p1Y, int p2Y, int xDir, int yDir, int p1Score, int p2Score, LocalGame LGscreen) {
        this.x = x;
        this.y = y;
        this.setP1Score(p1Score);
        this.setP2Score(p2Score);
        
        setXDirection(xDir);
        setYDirection(yDir);
        
        // Create the Ball
        ball = new Rectangle(x, y, 7, 7);
        
        p1 = new Paddle(15, p1Y, 1, LGscreen);
        p2 = new Paddle(370, p2Y, 2, LGscreen);
    }
    
    void setXDirection(int xdir) {
        xDirection = xdir;
    }
    
    void setYDirection(int ydir) {
        yDirection = ydir;
    }
    
    int getXDirection() {
        return xDirection;
    }
    
    int getYDirection() {
        return yDirection;
    }
    
    void draw(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRect(ball.x, ball.y, ball.width, ball.height);
    }
    
    private void collision() {
        if(ball.intersects(p1.paddle)) {
            setXDirection(+1);
        }
        if(ball.intersects(p2.paddle)) {
            setXDirection(-1);
        }
    }
    
    private void move() {
        collision();
        ball.x += xDirection;
        ball.y += yDirection;
        
        // Bounce the Ball When the Edge is Detected
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
    
    void setX(int x) {
        this.x = x;
    }
    
    void setY(int y) {
        this.y = y;
    }
    
    int getX() {
        return ball.x;
    }
    
    int getY() {
        return ball.y;
    }
    
    void setDifficulty(int diff) {
        difficulty = diff;
    }
    
    int getDifficulty() {
        return difficulty;
    }
    
    void setWinScore(int score) {
        this.winScore = score;
    }
    
    int getWinScore() {
        return winScore;
    }
    
    int getP1Score() {
        return p1Score;
    }
    
    void setP1Score(int p1Score) {
        this.p1Score = p1Score;
    }

    int getP2Score() {
        return p2Score;
    }

    void setP2Score(int p2Score) {
        this.p2Score = p2Score;
    }

    void setPaused(boolean sp) {
        isPaused = sp;
    }

    void stop() {
        stop = true;
    }
    
    @Override
    public void run() {
        try{
            while(!stop) {
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