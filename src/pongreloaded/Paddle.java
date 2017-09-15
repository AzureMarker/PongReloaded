package pongreloaded;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;

/**
 * @author Mcat12
 */
class Paddle implements Runnable {
    private volatile boolean isPaused = false;
    private volatile boolean stop = false;
    private int yDirection;
    private int id;
    private int playerNum;
    private int difficulty = 5;
    private int mode = 2;
    private int players = 1;
    private Screen screen;
    Rectangle paddle;
    
    Paddle(int x, int y, int id, LocalGame screen) {
        this.id = id;
        this.screen = screen;
        paddle = new Rectangle(x, y, 10, 50);
    }
    
    Paddle(int x, int y, int id, MultiplayerGame screen) {
        this.id = id;
        this.screen = screen;
        playerNum = screen.getPlayerNum();
        paddle = new Rectangle(x, y, 10, 50);
    }
    
    void setScreen(Screen screen) {
        this.screen = screen;
    }
    
    void keyPressed(KeyEvent e) {
        switch(id) {
            default:
                System.out.println("Please enter a valid ID in Paddle constructor");
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
    
    void keyReleased(KeyEvent e) {
        switch(id) {
            default:
                System.out.println("Please enter a valid ID in Paddle constructor");
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
    
    void setYDirection(int yDir) {
        yDirection = yDir;
    }
    
    int getYDirection() {
        return yDirection;
    }
    
    void setPlayerNum(int playerNum) {
        this.playerNum = playerNum;
    }
    
    private void move() {
        paddle.y += yDirection;
        if(paddle.y <= 25)
            paddle.y = 25;
        if(paddle.y >= 250)
            paddle.y = 250;
        if(this.id == 2 && getPlayers() == 1) {
            collision();
            if(((LocalGame) screen).getBallY() < paddle.y+25)
                setYDirection(-1);
            if(((LocalGame) screen).getBallY() > paddle.y+25)
                setYDirection(+1);
        }
    }
    
    private void collision() {
        if(paddle.y <= 25)
            setYDirection(+1);
        if(paddle.y > 250)
            setYDirection(-1);
    }
    
    void draw(Graphics g) {
        switch(id) {
            default:
                System.out.println("Please enter a valid ID in Paddle constructor");
                break;
            case 1:
                if(getMode() == 2)
                    g.setColor(Color.CYAN);
                if(getMode() == 1)
                    g.setColor(Color.WHITE);
                g.fillRect(paddle.x, paddle.y, paddle.width, paddle.height);
                break;
            case 2:
                if(getMode() == 2)
                    g.setColor(Color.PINK);
                if(getMode() == 1)
                    g.setColor(Color.WHITE);
                g.fillRect(paddle.x, paddle.y, paddle.width, paddle.height);
                break;
            case 3:
                if(getMode() == 2)
                    g.setColor(Color.CYAN);
                if(getMode() == 1)
                    g.setColor(Color.WHITE);
                g.fillRect(paddle.x, paddle.y, paddle.width, paddle.height);
                break;
            case 4:
                if(getMode() == 2)
                    g.setColor(Color.PINK);
                if(getMode() == 1)
                    g.setColor(Color.WHITE);
                g.fillRect(paddle.x, paddle.y, paddle.width, paddle.height);
                break;
        }
    }
    
    void setY(int y) {
        paddle.y = y;
    }
    
    int getY() {
        return paddle.y;
    }
    
    void setDifficulty(int diff) {
        difficulty = diff;
    }
    
    void setPlayers(int players) {
        this.players = players;
    }
    
    void setMode(int mode) {
        this.mode = mode;
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
                    Thread.sleep(getDifficulty());
                }
            }
        }
        catch(Exception e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
    }

    public int getDifficulty() {
        return difficulty;
    }

    public int getMode() {
        return mode;
    }

    public int getPlayers() {
        return players;
    }
}