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

import javax.swing.JOptionPane;

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
    int playerNum = 3;
    int otherPlayerNum = 3;
    int[] arrayXY = new int[8];
    String TimeStamp;
	
	// Game
	Ball bClient;
	static int winScore;
	
	// Threads
	Thread bC;
    Thread pC;
    Thread pM;
	
	public MultiplayerGame(String ip, int port) {
		this.ip = ip;
		this.port = port;
		bClient = new Ball(193, 143, true, this);
		bC = new Thread(bClient);
		pC = new Thread(bClient.p1);
		pM = new Thread(bClient.p2);
		connect(ip, port);
	}
	
	public MultiplayerGame(int hostPort) {
		this.hostPort = hostPort;
		bClient = new Ball(193, 143, true, this);
		bC = new Thread(bClient);
		pC = new Thread(bClient.p1);
		pM = new Thread(bClient.p2);
		host();
	}
	
	public void startRemoteGame() {
		System.out.println("Starting Ball Thread");
        bC.start();
        System.out.println("Starting P1 Thread");
        pC.start();
        System.out.println("Starting P2 Thread");
        pM.start();
        
        // Send Variables to Player
        System.out.println("Sending variables again...");
        sendVarsToServer();
    }
	
	public boolean isFinished() {
        if(bClient.p1Score >= winScore) {
            return true;
        }
        if(bClient.p2Score >= winScore) {
            return true;
        }
        return false;
    }
    
    public int getWinner() {
    	if(bClient.p1Score >= winScore) {
            return 1;
        }
        if(bClient.p2Score >= winScore) {
            return 2;
        }
        return 0;
    }
    
    public int getBallY() {
    	return bClient.getY();
    }
	
	public void host() {
        System.out.println("Server Initializing...");
        try {
            //mH.start();
            //prot = new Protocol(msgHandler);
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
        	msgHandler = new MsgHandler(this);
            prot = new Protocol(msgHandler);
            addMsgHandlers();
            jnm = new jnmp2p(prot, port);
            connection = jnm.connect(ip);
            System.out.println("Initialized Client, enter Player number");
            getPlayerNumber();
            System.out.println("Sent numbers, waiting for other player...");
            while(otherPlayerNum == 3) { }
            System.out.println(otherPlayerNum);
            startRemoteGame();
        }
        catch (Exception e) {
            Logger.getLogger(Pong.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public void getPlayerNumber() {
    	Object[] options = {
    			"Player 1",
    			"Player 2"
    	};
		playerNum = JOptionPane.showOptionDialog(null,
				"What Player are you?",
				"Pong Reloaded - Enter Player Number",
				JOptionPane.DEFAULT_OPTION,
				JOptionPane.QUESTION_MESSAGE,
				null,
				options,
				options[0]);
		switch(playerNum) {
			case 0:
				if(otherPlayerNum != 0)
					playerNum = 0;
				else {
					JOptionPane.showMessageDialog(null, "The other player is already player 1, you are player 2 now.");
					playerNum = 1;
				}
				
				break;
			case 1:
				if(otherPlayerNum != 1)
					playerNum = 1;
				else {
					JOptionPane.showMessageDialog(null, "The other player is already player 2, you are player 1 now.");
					playerNum = 0;
				}
				break;
		}
		sendNBVarsToServer();
    }
    
    public void sendVarsToServer() {
        msgHeader = "getVars";
        if(playerNum == 0) {
        	msgBody[0] = bClient.p1.x;
        	msgBody[1] = bClient.p1.y;
        	msgBody[2] = bClient.x;
            msgBody[3] = bClient.y;
            msgBody[4] = bClient.p1Score;
        }
        else if(playerNum == 1) {
        	msgBody[0] = bClient.p2.x;
        	msgBody[1] = bClient.p2.y;
        	msgBody[2] = bClient.p2Score;
        }
        else {
        	System.out.println("Invalid Player number: " + playerNum);
        	System.out.println("Closing connection...");
        	closeConnection();
        }
        
        try {
        	msg = Connection.createMsg(msgHeader, msgBody);
        	connection.sendMsg(msg);
        }
        catch(Exception e) {
        	System.out.println("Message failed to send, shutting down connection...");
        	closeConnection();
        }
    }
    
    public void sendNBVarsToServer() {
        msgHeader = "getNBVars";
        msgBody[0] = winScore;
        msgBody[1] = playerNum;
        try {
        	msg = Connection.createMsg(msgHeader, msgBody);
        	connection.sendMsg(msg);
        }
        catch(Exception e) {
        	System.out.println("Message failed to send, shutting down connection...");
        	closeConnection();
        }
    }
    
    public void getVariables(Connection conn, org.net.Msg.Msg msg) {
    	arrayXY = (int[]) msg.getContent();
    	if(playerNum == 0) {
    		bClient.p2.x = arrayXY[0];
        	bClient.p2.y = arrayXY[1];
        	bClient.p2Score = arrayXY[2];
        	System.out.println(
            		"p2 x: "+arrayXY[0] + "\n" +
            		"p2 y: "+arrayXY[1] + "\n" +
            		"p2Score: "+arrayXY[2]);
    	}
    	else if(playerNum == 1) {
    		bClient.p1.x = arrayXY[0];
        	bClient.p1.y = arrayXY[1];
        	bClient.x = arrayXY[2];
            bClient.y = arrayXY[3];
            bClient.p1Score = arrayXY[4];
            System.out.println(
            		"p1 x: "+arrayXY[0] + "\n" +
            		"p1 y: "+arrayXY[1] + "\n" +
            		"b x: "+arrayXY[2] + "\n" +
            		"b y: "+arrayXY[3] + "\n" +
            		"p1Score: "+arrayXY[4]);
    	}
    	else {
        	System.out.println("Invalid Player number: " + playerNum);
        	System.out.println("Closing connection...");
        	closeConnection();
        }
    }
    
    public void getNBVariables(Connection conn, org.net.Msg.Msg msg) {
    	arrayXY = (int[]) msg.getContent();
        bClient.winScore = arrayXY[0];
        otherPlayerNum = arrayXY[1];
        System.out.println(
        		"winScore: "+arrayXY[0] + "\n" +
        		"Other Player Num: " + arrayXY[1]);
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
	
	public Screen getScreen() {
		return this;
	}
	
	public Screen respondToUserInput(KeyEvent key) {
		bClient.p1.keyPressed(key);
        bClient.p2.keyPressed(key);
        return this;
	}
	
	public Screen respondToUserInputReleased(KeyEvent key) {
		bClient.p1.keyReleased(key);
        bClient.p2.keyReleased(key);
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
