package pongreloaded;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.net.p2p.*;

/**
 * @author Mcat12
 */
public class MsgHandler implements Runnable {
    boolean isRunning = true;
    MultiplayerGame screen;
    
    public MsgHandler(MultiplayerGame mg) {
    	screen = mg;
    }
    
    public void tick() {
        System.out.println("Tick!");
    }
    
    public void getVariables(Connection conn, Msg msg) {
    	screen.arrayXY = (int[]) msg.getContent();
        screen.bClient.p2.x = screen.arrayXY[0];
        screen.bClient.p2.y = screen.arrayXY[1];
        screen.bClient.x = screen.arrayXY[2];
        screen.bClient.y = screen.arrayXY[3];
        screen.bClient.p1Score = screen.arrayXY[4];
        screen.bClient.p2Score = screen.arrayXY[5];
        System.out.println(
        		""+screen.arrayXY[0] + "\n" +
        		""+screen.arrayXY[1] + "\n" +
        		""+screen.arrayXY[2] + "\n" +
        		""+screen.arrayXY[3] + "\n" +
        		""+screen.arrayXY[4] + "\n" +
        		""+screen.arrayXY[5]);
    }
    
    public void getNBVariables(Connection conn, Msg msg) {
    	screen.arrayXY = (int[]) msg.getContent();
        screen.bClient.p1.x = screen.arrayXY[0];
        screen.bClient.p1.y = screen.arrayXY[1];
        screen.bClient.p1Score = screen.arrayXY[4];
        screen.bClient.p2Score = screen.arrayXY[5];
        System.out.println(
        		""+screen.arrayXY[0] + "\n" +
        		""+screen.arrayXY[1] + "\n" +
        		""+screen.arrayXY[2] + "\n" +
        		""+screen.arrayXY[3] + "\n" +
        		""+screen.arrayXY[4] + "\n" +
        		""+screen.arrayXY[5]);
    }
    
    @SuppressWarnings("static-access")
	public void setMaxScore(Connection conn, Msg msg) {
        screen.setWinScore(Integer.parseInt(msg.getContent().toString()));
    }
    
    public void close() {
        isRunning = false;
    }
    
    @Override
    public void run() {
        while(isRunning) {
            try {
                tick();
                Thread.sleep(50);
            }
            catch(InterruptedException ex) {
                Logger.getLogger(MsgHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}