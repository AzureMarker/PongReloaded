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
    static int[] msgBody = new int[6];
    static int intMsgBody;
    org.net.Msg.Msg msg;
    MsgHandler msgHandler;
    
    
    // Client Variables
    int character;
    int pHX = 0;
    int pHY = 0;
    int[] arrayXY = new int[6];
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
		host(hostPort);
	}
	
	public void startRemoteGame(){
        bC.start();
        pC.start();
        
        // Send Variables to Player
        if(isHost)
            sendVarsToServer(bClient.p1.x, bClient.p1.y, bClient.p1Score, bClient.x, bClient.y);
    }
	
	public void host(int port){
        System.out.println("Server Initializing...");
        try {
            mH.start();
            prot = new Protocol(msgHandler);
            addMsgHandlers();
            jnm = new jnmp2p(prot, hostPort);
            connection = jnm.connect(InetAddress.getLocalHost().getHostAddress().toString());
            System.out.println("Initialized Server, recieving variables...");
            bClient.x = 100;
            while(bClient.x != 1){
                
            }
            System.out.println("Recieved variables! Sending Max Score...");
            msgHeader = "setMaxScore";
            intMsgBody = winScore;
            msg = Connection.createMsg(msgHeader, intMsgBody);
            connection.sendMsg(msg);
            System.out.println("Sent Max Score!");
            startRemoteGame();
        }
        catch (UnknownHostException ex){
            Logger.getLogger(Pong.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void connect(String ip, int port){
        System.out.println("Client Initializing...");
        try{
            mH.start();
            prot = new Protocol(msgHandler);
            addMsgHandlers();
            jnm = new jnmp2p(prot, port);
            connection = jnm.connect(ip);
            System.out.println("Initialized Client, sending variables...");
            sendVarsToServer(1, 2, 3);
            System.out.println("Sent variables!");
            startRemoteGame();
        }
        catch (Exception e) {
            Logger.getLogger(Pong.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public void sendVarsToServer(int x, int y, int score, int bx, int by){
        msgHeader = "getVars";
        msgBody[0] = x;
        msgBody[1] = y;
        msgBody[2] = bx;
        msgBody[3] = by;
        if(isHost)
            msgBody[4] = score;
        else
            msgBody[5] = score;
        msg = Connection.createMsg(msgHeader, msgBody);
        connection.sendMsg(msg);
    }
    
    public void sendVarsToServer(int x, int y, int score){
        msgHeader = "getNBVars";
        msgBody[0] = x;
        msgBody[1] = y;
        msgBody[2] = 0;
        msgBody[3] = 0;
        if(isHost)
            msgBody[4] = score;
        else
            msgBody[5] = score;
        msg = Connection.createMsg(msgHeader, msgBody);
        try {
        	connection.sendMsg(msg);
        }
        catch(NullPointerException npe) {
        	System.out.println("Could not find server!");
        }
    }
    
    public void addMsgHandlers(){
        prot.addMsgHandler("getVars", "getVariables");
        prot.addMsgHandler("getNBVars", "getNBVariables");
        prot.addMsgHandler("setMaxScore", "SetMaxScore");
    }
    
    public void closeConnection(){
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
        if(isHost)
        	sendVarsToServer(bClient.p1.x, bClient.p1.y, bClient.p1Score, bClient.x, bClient.y);
        else
        	sendVarsToServer(bClient.p2.x, bClient.p2.y, bClient.p2Score);
	}
	
	public Screens getScreenType() {
		return Screens.MULTIGAME;
	}
	
	public Screen respondToUserInput(KeyEvent key) {
		return null;
	}
	
	public Screen respondToUserInputReleased(KeyEvent key) {
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
