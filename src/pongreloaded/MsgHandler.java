package pongreloaded;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.net.p2p.*;

/**
 * @author Mcat12
 */
public class MsgHandler implements Runnable{
    boolean isRunning = true;
    
    public void tick(){
        System.out.println("Tick!");
    }
    
    public void getVariables(Connection conn, Msg msg){
        Pong.arrayXY = (int[]) msg.getContent();
        Pong.pClient.x = Pong.arrayXY[0];
        Pong.pClient.y = Pong.arrayXY[1];
        Pong.bClient.x = Pong.arrayXY[2];
        Pong.bClient.y = Pong.arrayXY[3];
        Pong.bClient.p1Score = Pong.arrayXY[4];
        Pong.bClient.p2Score = Pong.arrayXY[5];
    }
    
    public void setMaxScore(Connection conn, Msg msg){
        Pong.winScore = Integer.parseInt(msg.getContent().toString());
    }
    
    public void close(){
        isRunning = false;
    }
    
    @Override
    public void run() {
        while(isRunning){
            try {
                tick();
                Thread.sleep(50);
            }
            catch(InterruptedException ex){
                Logger.getLogger(MsgHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}