package pongreloaded;

import java.awt.*;
import java.awt.event.*;

/**
 * @author Mcat12
 */
public class MainMenu implements Screen {
    // Buttons
    private Button startButton,
        diffButton = new Button(85, 185, 100, 25, "Difficulty: "),
        playersButton = new Button(215, 185, 100, 25, "Players: "),
        modeButton = new Button(85, 140, 100, 25, ""),
        scoreButton = new Button(85, 225, 100, 25, "To Win: "),
        multiButton = new Button(215, 140, 100, 25, "Multiplayer"),
        exitButton = new Button(215, 225, 100, 25, "Exit");
    
    // Game
    private boolean isFirstRun;
    private int 
        difficulty = 1,
        ballDiff = 7,
        p2Diff = 5,
        players = 1,
        mode = 2,
        winScore = 10,
        ballX,
        ballY,
        p1Y,
        p2Y,
        xDir,
        yDir,
        p1Score,
        p2Score;
    
    private Dimension screenSize;
    
    MainMenu(Dimension screenSize, int ballDiff, int p2Diff, int players, int mode, int winScore, int ballX, int ballY, int p1Y, int p2Y, int xDir, int yDir, int p1Score, int p2Score) {
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
        System.out.println("MainMenu Full Constructor");
    }
    
    MainMenu(Dimension screenSize) {
        this.screenSize = screenSize;
        isFirstRun = true;
        startButton = new Button(150, 100, 100, 25, "Start");
        System.out.println("MainMenu Fresh Run");
    }

    public Screens getScreenType() {
        return Screens.MAINMENU;
    }

    public void displayOutput(Graphics g) {
        // Menu
        g.setFont(new Font("Arial", Font.BOLD, 26));
        g.setColor(Color.WHITE);
        g.drawString("Pong Reloaded", 108, 75);
        
        // Buttons
        startButton.draw(g);
        multiButton.draw(g);
        
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
        
        switch(players) {
            case 1:
                playersButton.setText("Players: Single");
                break;
            case 2:
                playersButton.setText("Players: Multi");
                break;
        }
        playersButton.draw(g);
        
        switch(mode) {
            case 1:
                modeButton.setText("Mode: Original");
                break;
            case 2:
                modeButton.setText("Mode: Reloaded");
                break;
        }
        modeButton.draw(g);
        
        scoreButton.setText("Score: " + winScore);
        scoreButton.draw(g);
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
        
        startButton.adjustHover(mx, my);
        diffButton.adjustHover(mx, my);
        playersButton.adjustHover(mx, my);
        modeButton.adjustHover(mx, my);
        scoreButton.adjustHover(mx, my);
        multiButton.adjustHover(mx, my);
        exitButton.adjustHover(mx, my);
        
        return this;
    }

    public Screen respondToUserInputClick(MouseEvent mouse) {
        int mx = mouse.getX();
        int my = mouse.getY();
        
        if(startButton.intersects(mx, my)) {
            if(isFirstRun)
                return new LocalGame(screenSize, ballDiff, p2Diff, players, mode, winScore);
            else
                return new LocalGame(screenSize, ballDiff, p2Diff, players, mode, winScore, ballX, ballY, p1Y, p2Y, xDir, yDir, p1Score, p2Score);
        }

        if(scoreButton.intersects(mx, my)) {
            winScore++;
            if(winScore > 100)
                winScore = 1;
        }

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

        if(multiButton.intersects(mx, my))
            return new MultiplayerMenu(screenSize, winScore);

        if(exitButton.intersects(mx, my))
            Pong.quit = true;

        return this;
    }

    public Screen respondToUserInput(MouseWheelEvent mouseWheel) {
        int mx = mouseWheel.getX();
        int my = mouseWheel.getY();
        int mwDir = mouseWheel.getWheelRotation();

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
