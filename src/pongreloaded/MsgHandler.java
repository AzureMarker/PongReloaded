package pongreloaded;

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
        screen.bClient.p1.x = screen.arrayXY[0];
        screen.bClient.p1.y = screen.arrayXY[1];
        screen.bClient.p2.x = screen.arrayXY[2];
        screen.bClient.p2.y = screen.arrayXY[3];
        screen.bClient.x = screen.arrayXY[4];
        screen.bClient.y = screen.arrayXY[5];
        screen.bClient.p1Score = screen.arrayXY[6];
        screen.bClient.p2Score = screen.arrayXY[7];
        System.out.println(
        		"p1 x: "+screen.arrayXY[0] + "\n" +
        		"p1 y: "+screen.arrayXY[1] + "\n" +
        		"p2 x: "+screen.arrayXY[2] + "\n" +
        		"p2 y: "+screen.arrayXY[3] + "\n" +
        		"b x: "+screen.arrayXY[4] + "\n" +
        		"b y: "+screen.arrayXY[5] + "\n" +
        		"p1Score: "+screen.arrayXY[6] + "\n" +
        		"p2Score: "+screen.arrayXY[7]);
    }
    
    public void getNBVariables(Connection conn, Msg msg) {
    	screen.arrayXY = (int[]) msg.getContent();
        screen.bClient.winScore = screen.arrayXY[0];
        System.out.println("winScore: "+screen.arrayXY[0]);
    }
    
    public void close() {
        isRunning = false;
    }
    
    @Override
    public void run() {
        while(isRunning) { }
    }
}