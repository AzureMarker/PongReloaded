package pongreloaded;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.BufferedReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.net.p2p.Connection;
import org.net.p2p.Protocol;
import org.net.p2p.jnmp2p;

/**
 * @author Mcat12
 */
public class MultiplayerGame implements Screen {
	// Multiplayer Connection
    jnmp2p jnm;
    Connection connection;
    Protocol prot;
    BufferedReader in;
    String ip;
    int port;
    int hostPort;
    static String msgHeader;
    static int[] msgBody = new int[8];
    static int intMsgBody;
    org.net.Msg.Msg msg;
    MsgHandler msgHandler;
    
    
    // Client Variables
    int character;
    int pHX = 0;
    int pHY = 0;
    int[] arrayXY = new int[8];
    char[] com = new char[3];
    boolean clientRun = true;
    boolean first = true;
    boolean isHost;
    String TimeStamp;
	
	// Game
	Ball bClient;
	static int winScore;
	
	// Threads
	Thread bC;
    Thread pC;
    Thread pM;
    Thread mH;
	
	public MultiplayerGame(String ip, int port) {
		this.ip = ip;
		this.port = port;
		isHost = false;
		bClient = new Ball(193, 143, true, this);
		bC = new Thread(bClient);
		pC = new Thread(bClient.p1);
		pM = new Thread(bClient.p2);
		mH = new Thread(msgHandler);
		connect(ip, port);
	}
	
	public MultiplayerGame(int hostPort) {
		this.hostPort = hostPort;
		isHost = true;
		bClient = new Ball(193, 143, true, this);
		bC = new Thread(bClient);
		pC = new Thread(bClient.p1);
		pM = new Thread(bClient.p2);
		mH = new Thread(msgHandler);
		host();
	}
	
	public void startRemoteGame() {
        bC.start();
        pC.start();
        
        // Send Variables to Player
        sendVarsToServer();
    }
	
	public void host() {
        System.out.println("Server Initializing...");
        try {
            mH.start();
            prot = new Protocol(msgHandler);
            addMsgHandlers();
            jnm = new jnmp2p(prot, hostPort);
            connection = jnm.connect(InetAddress.getLocalHost().getHostAddress().toString());
            System.out.println("Initialized Server, recieving variables...");
            bClient.x = 100;
            while(bClient.x != 1) {
                
            }
            System.out.println("Recieved variables! Sending Max Score...");
            msgHeader = "setMaxScore";
            intMsgBody = winScore;
            msg = Connection.createMsg(msgHeader, intMsgBody);
            connection.sendMsg(msg);
            System.out.println("Sent Max Score!");
            startRemoteGame();
        }
        catch (UnknownHostException ex) {
            Logger.getLogger(Pong.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void connect(String ip, int port) {
        System.out.println("Client Initializing...");
        try{
            mH.start();
            prot = new Protocol(msgHandler);
            addMsgHandlers();
            jnm = new jnmp2p(prot, port);
            connection = jnm.connect(ip);
            System.out.println("Initialized Client, sending variables...");
            sendNBVarsToServer();
            sendVarsToServer();
            System.out.println("Sent variables!");
            startRemoteGame();
        }
        catch (Exception e) {
            Logger.getLogger(Pong.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public void sendVarsToServer() {
        msgHeader = "getVars";
        msgBody[0] = bClient.p1.x;
        msgBody[1] = bClient.p1.y;
        msgBody[2] = bClient.p2.x;
        msgBody[3] = bClient.p2.y;
        msgBody[4] = bClient.x;
        msgBody[5] = bClient.y;
        msgBody[6] = bClient.p1Score;
        msgBody[7] = bClient.p2Score;
        msg = Connection.createMsg(msgHeader, msgBody);
        connection.sendMsg(msg);
    }
    
    public void sendNBVarsToServer() {
        msgHeader = "getNBVars";
        msgBody[0] = winScore;
        msg = Connection.createMsg(msgHeader, msgBody);
        connection.sendMsg(msg);
    }
    
    public void addMsgHandlers() {
        prot.addMsgHandler("getVars", "getVariables");
        prot.addMsgHandler("getNBVars", "getNBVariables");
    }
    
    public void closeConnection() {
            System.out.println("Closing connection...");
            jnmp2p.close(connection);
            System.out.println("Connection closed.");
    }
	
	public static void setWinScore(int score) {
		winScore = score;
	}
	
	public void displayOutput(Graphics g) {
		// Game
        bClient.draw(g);
        bClient.p1.draw(g);
        bClient.p2.draw(g);
        
        // Score
        g.setColor(Color.WHITE);
        g.drawString(""+bClient.p1Score, 15, 50);
        g.drawString(""+bClient.p2Score, 370, 50);
        
        // Send Variables to Server
        sendVarsToServer();
	}
	
	public Screens getScreenType() {
		return Screens.MULTIGAME;
	}
	
	public Screen respondToUserInput(KeyEvent key) {
		bClient.p1.keyPressed(key);
        bClient.p2.keyPressed(key);
        return this;
	}
	
	public Screen respondToUserInputReleased(KeyEvent key) {
		bClient.p1.keyPressed(key);
        bClient.p2.keyPressed(key);
        return this;
	}
	
	public Screen respondToUserInputHover(MouseEvent mouse) {
		return this;
	}
	
	public Screen respondToUserInputClick(MouseEvent mouse) {
		return this;
	}
	
	public Screen respondToUserInput(MouseWheelEvent mouse) {
		return this;
	}
}
